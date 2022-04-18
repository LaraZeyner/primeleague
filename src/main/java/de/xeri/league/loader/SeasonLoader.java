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

import de.xeri.league.models.enums.StageType;
import de.xeri.league.models.league.Matchday;
import de.xeri.league.models.league.Season;
import de.xeri.league.models.league.Stage;
import de.xeri.league.models.league.Team;
import de.xeri.league.util.Data;
import de.xeri.league.util.Util;
import de.xeri.league.util.io.json.HTML;
import de.xeri.league.util.io.request.RequestManager;
import de.xeri.league.util.logger.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Created by Lara on 05.04.2022 for web
 */
public final class SeasonLoader {
  private static final Logger logger = Logger.getLogger("Season-Erstellung");

  static {
    try {
      final HTML html = Data.getInstance().getRequester().requestHTML("https://www.primeleague.gg/leagues/prm/");
      final Document doc = Jsoup.parse(html.toString());

      final String currentSeasonName = doc.select("li.breadcrumbs-subs").select("span").text();
      if (Season.find(currentSeasonName) == null) {
        final String[] split = doc.select("body").attr("class").split("body-");
        final short id = Short.parseShort(split[split.length - 1].split("-")[0]);
        loadSeason(id, true);
      }
      loadSeasons(doc); // TODO: 16.04.2022 ???
      updateSeason(Season.current().getId());
      PlayerLoader.load();
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

  }

  public static void load() {
    Data.getInstance().save();
  }

  private static void loadSeasons(Document doc) {
    for (Element season : doc.select("li.breadcrumbs-subs").select("ul").select("li")) {
      final short id = Short.parseShort(season.select("a").attr("href").split("/prm/")[1].split("-")[0]);
      if (Season.find(id) == null) {
        loadSeason(id, false);
      }
    }
  }

  /**
   * @param id Turnament ID of the season
   * @return Season
   */
  private static void loadSeason(short id, boolean last) {
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
        loadGroups(seasonDoc, dates, season, group, playoffs);
        loadPlayoffs(seasonDoc, dates, season, playoffs);

        final List<Team> teams = loadTeams(season);
        TeamLoader.loadMatches(teams, season);
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
        element.select("td").select("a.table-cell-container");
        final String id = element.select("td").select("a.table-cell-container").attr("href").split("/teams/")[1].split("-")[0];
        final Team team = TeamLoader.handleTeam(Integer.parseInt(id), season);
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
    final Date start = new Date(calibrationStage.getStageStart().getTimeInMillis());
    final Date end = new Date();
    for (int i = 0; i < 2; i++) {
      start.setTime(start.getTime() + 51_000_000L + i * 86_400_000L);
      for (int j = 0; j < 5; j++) {
        end.setTime((start.getTime() + 2_700_000L));
        final Matchday matchday = new Matchday("Runde " + ((i * 5) + j + 1), start, end);
        calibrationStage.addMatchday(matchday);
        start.setTime(start.getTime() + 4_500_000L);
      }
    }
  }

  private static void loadGroups(Document doc, Map<String, Date> dates, Season season, Calendar group, Calendar playoffs) {
    final Stage groupStage = season.addStage(new Stage(StageType.GRUPPENPHASE, group, playoffs));
    loadMatchdaysOf(groupStage, dates, 161, 7);
    //loadStage(doc, "Gruppen", groupStage, season.getId());
  }

  private static void loadPlayoffs(Document doc, Map<String, Date> dates, Season season, Calendar playoffs) {
    final Calendar playoffsEnd = Util.getCalendar(playoffs.getTime());
    playoffsEnd.add(Calendar.DAY_OF_YEAR, 1);
    playoffsEnd.set(Calendar.HOUR_OF_DAY, 0);
    playoffsEnd.set(Calendar.MINUTE, 0);
    final Stage playOffStage = season.addStage(new Stage(StageType.PLAYOFFS, playoffs, playoffsEnd));
    loadMatchdaysOf(playOffStage, dates, 0, 3);
    //loadStage(doc, "Playoff", playOffStage, season.getId());
  }

  private static void loadMatchdaysOf(Stage stage, Map<String, Date> dates, int backward, int forward) {
    final Map<String, Date> dateMap = getDatesOfStage(stage, dates);
    for (Map.Entry<String, Date> entry : dateMap.entrySet()) {
      final String name = entry.getKey();
      final Date date = entry.getValue();
      if (name.startsWith("Spieltag") || name.startsWith("Runde")) {
        final Date start = new Date(date.getTime() - backward * 3_600_000L);
        final Date end = new Date(date.getTime() + forward * 3_600_000L);
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

  /*private static void loadStage(Document doc, String query, Stage stage, short seasonId) {
    for (Element element : getLeague(doc, query).select("li")) {
      final String idLink = element.select("a").attr("href");
      if (!idLink.equals("")) {
        final short id = Short.parseShort(idLink.split("/")[8].split("-")[0]);
        final String name = element.select("a").text();
        if (!name.startsWith("Division") || name.contains("Playoff") ||
            Integer.parseInt(String.valueOf(name.replace("Division ", "").charAt(0))) >= 5) {
          handleLeague(stage, id, name, seasonId);
        }
      }
    }
  }

  private static Element getLeague(Document doc, String query) {
    return doc.select("section.league-season-stage").stream()
        .filter(stage -> stage.select("div.section-title").text().contains(query))
        .findFirst().orElse(null);
  }

  private static void handleLeague(Stage stage, short id, String name, short seasonId) {
    final League league = League.get(new League(id, name));
    final String groupTypeName = stage.getStageType().getTypeName();
    try {
      final HTML html = Data.getInstance().getRequester().requestHTML("https://www.primeleague.gg/leagues/prm/" + seasonId +
          "/" + groupTypeName + "/" + stage.getStageType().getValue() + "/" + id);
      final Document doc = Jsoup.parse(html.toString());
      if (stage.getStageType().equals(StageType.GRUPPENPHASE)) {
        handleGroupLeague(stage, name, league, doc);
      } else if (stage.getStageType().equals(StageType.PLAYOFFS)) {
        doc.select("section.league-playoff-matches").select("div.section-content").select("tbody").select("tr").forEach(match -> {
          MatchLoader.loadMatch(league, null, match);
        });
      }
      logger.info(league.getName() + " erstellt");
    } catch (FileNotFoundException exception) {
      logger.warning("League konnte nicht gefunden werden");
    } catch (IOException exception) {
      logger.severe(exception.getMessage());
    }
    stage.addLeague(league);
  }

  private static void handleGroupLeague(Stage stage, String name, League league, Document doc) {
    if (!name.startsWith("Starter")) {
      doc.select("section.league-group-scores").select("table").get(0).select("tr").forEach(row -> {
        if (!row.select("td").isEmpty()) {
          final String idString = row.select("td").get(1).select("a").attr("href").split("/teams/")[1].split("-")[0];
          final String teamName = row.select("td").get(1).select("a").attr("href").split("/teams/")[1].split("-")[1];
          final Team team = TeamLoader.handleTeam(Integer.parseInt(idString), stage.getSeason(), doc, teamName);
          if (team != null) {
            league.addTeam(team);
          }
        }
      });

      final Elements days = doc.select("section.league-group-matches").select("li");
      for (int i = 0; i < days.size(); i++) {
        final String title = days.get(i).select("h3").text().replace(" ", "_").toUpperCase();
        final Matchday matchday = Matchday.get(new Matchday(MatchdayType.valueOf(title), null, null), stage);
        MatchLoader.handleMatch(league, days, i, matchday);
      }
    }
  }*/

}
