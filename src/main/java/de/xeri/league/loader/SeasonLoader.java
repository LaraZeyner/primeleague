package de.xeri.league.loader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.xeri.league.models.enums.Matchstate;
import de.xeri.league.models.enums.StageType;
import de.xeri.league.models.league.Account;
import de.xeri.league.models.league.League;
import de.xeri.league.models.league.Matchday;
import de.xeri.league.models.league.Player;
import de.xeri.league.models.league.Season;
import de.xeri.league.models.league.Stage;
import de.xeri.league.models.league.Team;
import de.xeri.league.models.league.TurnamentMatch;
import de.xeri.league.util.Const;
import de.xeri.league.util.Data;
import de.xeri.league.util.Util;
import de.xeri.league.util.io.json.HTML;
import de.xeri.league.util.io.request.RequestManager;
import de.xeri.league.util.logger.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by Lara on 05.04.2022 for web
 */
public final class SeasonLoader {

  static {
    final Logger logger = Logger.getLogger("Season-Erstellung");
    try {
      final HTML html = Data.getInstance().getRequester().requestHTML("https://www.primeleague.gg/leagues/prm/");
      final Document doc = Jsoup.parse(html.toString());

      loadSeasons(doc);

      final String[] split = doc.select("body").attr("class").split("body-");
      final short id = Short.parseShort(split[split.length - 1].split("-")[0]);
      loadSeasonStages(id, true);
      Data.getInstance().commit();

      //TODO Do this at night
      updateSeason(Season.current().getId());
      logger.info("Season wurde aktualisiert.");

    } catch (FileNotFoundException exception) {
      logger.warning("konnte nicht gefunden werden");
    } catch (IOException exception) {
      logger.throwing(exception);
    }
  }

  private static void updateSeason(short id) {
    final Season season = Season.find(id);
    final List<Team> teams = loadTeams(season);
    TeamLoader.loadMatches(teams, season);

    for (TurnamentMatch match : TurnamentMatch.get()) {
      if (!match.getState().equals(Matchstate.CLOSED) && !match.isOpen()) {
        match.setState(Matchstate.CLOSED);
      }
    }

    for (League league : League.get()) {
      for (TurnamentMatch match : league.getMatches()) {
        if (match.getHomeTeam() != null && !league.getTeams().contains(match.getHomeTeam())) {
          league.addTeam(match.getHomeTeam());
        }
        if (match.getGuestTeam() != null && !league.getTeams().contains(match.getGuestTeam())) {
          league.addTeam(match.getGuestTeam());
        }
      }
    }

    Data.getInstance().commit();
  }

  public static void load() {

    Team.get().stream().filter(Team::isValueable).forEach(team -> {
          if (!team.isScrims()) {
            for (Player player : team.getPlayers()) {
              for (Account account : player.getAccounts()) {
                account.setLastUpdate(new Date(System.currentTimeMillis() - 180 * Const.MILLIS_PER_DAY));
              }

            }
            team.setScrims(true);
          }
        }
    );

    Data.getInstance().commit();
  }

  private static void loadSeasons(Document doc) {
    for (Element season : doc.select("li.breadcrumbs-subs").select("ul").select("li")) {
      final short id = Short.parseShort(season.select("a").attr("href").split("/prm/")[1].split("-")[0]);
      if (!Season.has(id)) {
        loadSeasonStages(id, false);
      }
    }
  }

  private static void loadSeasonStages(short id, boolean last) {
    final Logger logger = Logger.getLogger("Season-Erstellung");
    try {
      final HTML seasonHTML = Data.getInstance().getRequester().requestHTML("https://www.primeleague.gg/leagues/prm/" + id);
      final Document seasonDoc = Jsoup.parse(seasonHTML.toString());
      final String seasonName = seasonDoc.select("li.breadcrumbs-subs").select("span").text();

      final Map<String, Date> dates = loadSeasonTimestamps(seasonDoc);
      final Calendar seasonStart = Util.getCalendar(dates.get("Anmeldung startet"));
      final Calendar seasonEnd = Util.getCalendar(dates.get("Saison endet"));
      final Season season = Season.get(new Season(id, seasonName, seasonStart, seasonEnd));

      if (last) {
        // Stages
        final Calendar group = Util.getCalendar(dates.get("Start von Gruppenphase"));
        group.set(Calendar.HOUR_OF_DAY, 0);
        group.set(Calendar.MINUTE, 0);
        final Calendar playoffs = Util.getCalendar(dates.get("Start von Playoffs"));
        playoffs.set(Calendar.HOUR_OF_DAY, 0);
        playoffs.set(Calendar.MINUTE, 0);
        loadCalibration(season, dates);
        loadGroups(dates, season, group, playoffs);
        loadPlayoffs(dates, season, playoffs);
      }

      logger.info("Season erstellt");
    } catch (FileNotFoundException exception) {
      logger.warning("Season konnte nicht gefunden werden");
    } catch (IOException exception) {
      logger.severe(exception.getMessage());
    }
  }

  private static List<Team> loadTeams(Season season) {
    final List<Team> teams = new ArrayList<>();
    try {
      final RequestManager requester = Data.getInstance().getRequester();
      final HTML html = requester.requestHTML("https://www.primeleague.gg/leagues/prm/" + season.getId() + "/participants");
      final Document doc = Jsoup.parse(html.toString());
      for (Element element : doc.select("section.league-participants").select("tbody").select("tr")) {
        final Elements teamElement = element.select("td");
        final String id = teamElement.select("a.table-cell-container").attr("href").split("/teams/")[1].split("-")[0];
        final String name = teamElement.select("span").get(2).text();
        final Team team = TeamLoader.handleTeam(Integer.parseInt(id), season, false, name);
        teams.add(team);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return teams;
  }


  private static Map<String, Date> loadSeasonTimestamps(Document doc) {
    final Map<String, Date> finalDates = new HashMap<>();
    boolean po = false;
    for (Element entry : doc.select("section.league-season-head").select("div.timeline").select("li.timeline-date")) {
      final Date date = new Date(Long.parseLong(entry.attr("data-date")) * 1000);
      String title = entry.attr("data-title");
      if (title.contains("Playoffs")) po = true;
      if (po && title.startsWith("Spieltag")) title = title.replace("Spieltag", "Runde");
      finalDates.put(title, date);
    }
    return finalDates.entrySet().stream().sorted(Map.Entry.comparingByValue())
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
  }

  private static void loadCalibration(Season season, Map<String, Date> dates) {
    final Calendar calibration = Util.getCalendar(dates.get("Start von Kalibrierungsphase"));
    calibration.set(Calendar.HOUR_OF_DAY, 0);
    calibration.set(Calendar.MINUTE, 0);
    final Calendar end = Calendar.getInstance();
    end.setTimeInMillis(calibration.getTime().getTime() + 172_740_000);
    final Stage calibrationStage = season.addStage(new Stage(StageType.KALIBRIERUNGSPHASE, calibration, end));
    loadMatchdaysOfCalibration(calibrationStage);
  }

  private static void loadMatchdaysOfCalibration(Stage calibrationStage) {
    final long start = calibrationStage.getStageStart().getTimeInMillis() + 51_000_000L;
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 5; j++) {
        final long startMillis = start + i * 86_400_000L + j * 4_500_000L;
        final Matchday matchday = new Matchday("Runde " + ((i * 5) + j + 1), new Date(startMillis), new Date(startMillis + 2_700_000L));
        calibrationStage.addMatchday(matchday);
        System.out.println(((i * 5) + j + 1));
      }
    }
  }

  private static void loadGroups(Map<String, Date> dates, Season season, Calendar group, Calendar playoffs) {
    final Stage groupStage = season.addStage(new Stage(StageType.GRUPPENPHASE, group, playoffs));
    loadMatchdaysOfGroup(groupStage, dates);
  }

  private static void loadPlayoffs(Map<String, Date> dates, Season season, Calendar playoffs) {
    final Calendar playoffsEnd = Util.getCalendar(playoffs.getTime());
    playoffsEnd.add(Calendar.DAY_OF_YEAR, 1);
    playoffsEnd.set(Calendar.HOUR_OF_DAY, 0);
    playoffsEnd.set(Calendar.MINUTE, 0);
    final Stage playOffStage = season.addStage(new Stage(StageType.PLAYOFFS, playoffs, playoffsEnd));

    loadMatchdaysOfPlayoffs(playOffStage, dates);
  }

  private static void loadMatchdaysOfGroup(Stage stage, Map<String, Date> dates) {
    final Map<String, Date> dateMap = getDatesOfStage(stage, dates);
    for (Map.Entry<String, Date> entry : dateMap.entrySet()) {
      final String name = entry.getKey();
      final Date date = entry.getValue();
      if (name.startsWith("Spieltag") || name.startsWith("Runde")) {
        final Date start = new Date(date.getTime() - 161 * 3_600_000L);
        final Date end = new Date(date.getTime() + 7 * 3_600_000L);
        final Matchday matchday = new Matchday(name, start, end);
        stage.addMatchday(matchday);
      }
    }
  }

  private static Map<String, Date> getDatesOfStage(Stage stage, Map<String, Date> dates) {
    return dates.entrySet().stream().filter(entry -> entry.getValue().compareTo(stage.getStageStart().getTime()) >= 0 &&
            entry.getValue().compareTo(stage.getStageEnd().getTime()) <= 0)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b));
  }


  private static void loadMatchdaysOfPlayoffs(Stage stage, Map<String, Date> dates) {
    final Map<String, Date> dateMap = getDatesOfStage(stage, dates);
    for (Map.Entry<String, Date> entry : dateMap.entrySet()) {
      final String name = entry.getKey();


      if (name.contains("Start")) {
        final Date startDate = entry.getValue();
        final Calendar start = Util.getCalendar(startDate);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);

        final Matchday m1 = addMatchday(start, Calendar.SATURDAY, 14, "Runde 1");
        stage.addMatchday(m1);

        final Matchday m2 = addMatchday(start, Calendar.SATURDAY, 18, "Runde 2");
        stage.addMatchday(m2);

        final Matchday m3 = addMatchday(start, Calendar.SUNDAY, 14, "Runde 3");
        stage.addMatchday(m3);

        final Matchday m4 = addMatchday(start, Calendar.SUNDAY, 18, "Runde 4");
        stage.addMatchday(m4);

        final Matchday m5 = addMatchday(start, Calendar.MONDAY, 20, "Runde 5");
        stage.addMatchday(m5);
      }
    }
  }

  private static Matchday addMatchday(Calendar start, int dayOfWeek, int hour, String roundName) {
    final Calendar round = Util.getCalendar(start.getTime());
    round.set(Calendar.DAY_OF_WEEK, dayOfWeek);
    round.set(Calendar.HOUR_OF_DAY, hour);
    return new Matchday(roundName, round.getTime(), new Date(round.getTime().getTime() + 3_600_000 * 3));
  }

}
