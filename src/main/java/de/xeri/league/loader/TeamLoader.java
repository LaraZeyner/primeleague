package de.xeri.league.loader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import de.xeri.league.models.enums.StageType;
import de.xeri.league.models.enums.Teamrole;
import de.xeri.league.models.league.Account;
import de.xeri.league.models.league.League;
import de.xeri.league.models.league.Matchday;
import de.xeri.league.models.league.Player;
import de.xeri.league.models.league.Season;
import de.xeri.league.models.league.Stage;
import de.xeri.league.models.league.Team;
import de.xeri.league.util.Data;
import de.xeri.league.util.Util;
import de.xeri.league.util.io.json.HTML;
import de.xeri.league.util.io.riot.RiotAccountRequester;
import de.xeri.league.util.logger.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by Lara on 04.04.2022 for web
 */
public class TeamLoader {
  private static int amount = 0;
  public static final List<Integer> notFoundTeams = new ArrayList<>();

  public static Team handleTeam(int turnamentId) {
    return handleTeam(turnamentId, Season.current());
  }

  public static Team handleTeam(int turnamentId, Season season) {
    final Logger logger = Logger.getLogger("Team-Erstellung");
    try {
      final HTML html = Data.getInstance().getRequester().requestHTML("https://www.primeleague.gg/leagues/teams/" + turnamentId);
      final Document doc = Jsoup.parse(html.toString());
      final Team team = handleTeamCore(doc, turnamentId);
      handleMembers(doc, team);
      handleSeason(season, doc, team);
      amount++;
      logger.info(amount + ". Team " + team.getTeamName() + " hinzugefügt");
      return team;
    } catch (FileNotFoundException exception) {
      notFoundTeams.add(turnamentId);
      logger.warning("Team konnte nicht gefunden werden");
    } catch (IOException exception) {
      logger.severe(exception.getMessage());
    }
    return null;
  }

  static Team handleTeamCore(Document doc, int turnamentId) {
    final String title = doc.select("div.page-title").text();
    final Elements members = doc.select("section.league-team-members").select("div.section-content").select("li");
    if (title.startsWith("[DELETED]") && members.size() < 5) {
      return null;
    }
    final String teamName = title.split(" \\(")[0];
    final String teamAbbr = title.split(" \\(")[1].split("\\)")[0];
    final Team team = Team.get(new Team(turnamentId, teamName, teamAbbr));
    final String resultString = doc.select("ul.content-icon-info").select("li").get(1).text();
    if (resultString.contains("Ergebnis:")) {
      final String result = resultString.split("Ergebnis:")[1];
      team.setTeamResult(result.contains("Rang: ") ? result.split("Rang: ")[1] : result);
    }
    return team;
  }

  static void handleMembers(Document doc, Team team) {
    final Elements members = doc.select("section.league-team-members").select("div.section-content").select("li");
    for (Element playerElement : members) {
      final Elements elements = playerElement.select("a");
      final String idString = elements.attr("href").split("/users/")[1].split("-")[0];
      final String name = elements.text().equals("") ? null : elements.text();
      final String roleString = playerElement.select("div.txt-subtitle").text();
      final String summonerName = playerElement.select("div.txt-info").select("span").get(0).text();

      if (Player.has(Integer.parseInt(idString))) {
        final Player player = Player.get(new Player(Integer.parseInt(idString), name, Teamrole.valueOf(roleString.toUpperCase())), team);
        if (!player.getTeam().equals(team)) {
          // Spieler hat gewechselt
          team.addPlayer(player);
        }

        final Set<Account> accounts = player.getAccounts();
        if (accounts.stream().noneMatch(account -> account.getName().equals(summonerName))) {
          // Account hat sich geändert
          final Account account = RiotAccountRequester.getAccountFromName(summonerName);
          if (accounts.stream().noneMatch(account1 -> account1.getPuuid().equals(account.getPuuid()))) {
            accounts.forEach(account1 -> account1.setActive(false));
            player.addAccount(account);
          }
        } else {
          // erweiterte Initialisierung
          for (Account account1 : player.getAccounts()) {
            if (account1.getPuuid() == null) {
              final Account account = RiotAccountRequester.getAccountFromName(summonerName);
              if (accounts.stream().noneMatch(account2 -> account2.getPuuid().equals(account.getPuuid()))) {
                accounts.forEach(account2 -> account1.setActive(false));
              }
              // Gleich geblieben
            } else if (account1.isValueable()) {
              RiotAccountRequester.getAccountFromName(summonerName);
            }
          }
        }
      } else {
        final Player player = team.addPlayer(new Player(Integer.parseInt(idString), name, Teamrole.valueOf(roleString.toUpperCase())));
        final Set<Account> accounts = player.getAccounts();
        if (accounts.stream().noneMatch(account -> account.getName().equals(summonerName))) {
          final Account account = Account.get(new Account(summonerName));
          player.addAccount(account);
        }
      }
    }
  }


  private static void handleSeason(Season season, Document doc, Team team) {
    for (Element stageElement : doc.select("section.league-team-stage")) {
      final String stageTypeString = stageElement.select("div.section-title").text();
      final StageType stageType = StageType.valueOf(stageTypeString.toUpperCase());
      final Stage stage = Stage.find(season, stageType);

      final Elements divisionLink = stageElement.select("ul.content-icon-info-l").select("a");
      final String idString = divisionLink.attr("href").split("/")[8].split("-")[0];
      final League league = stage.addLeague(new League(Short.parseShort(idString), divisionLink.text()));
      league.addTeam(team);
    }
  }


  static void loadMatches(List<Team> teams, Season season) {
    for (Team team : teams) {
      if (team.getTeamTid() != 0) {
        try {
          final HTML html = Data.getInstance().getRequester().requestHTML("https://www.primeleague.gg/leagues/teams/" + team.getTeamTid());
          final Document doc = Jsoup.parse(html.toString());
          loadMatchesOfTeam(doc, season);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private static void loadMatchesOfTeam(Document doc, Season season) {
    final Logger logger = Logger.getLogger("Load Match");
    for (Element stageElement : doc.select("section.league-team-stage")) {
      final String stageTypeString = stageElement.select("div.section-title").text();
      final StageType stageType = StageType.valueOf(stageTypeString.toUpperCase());
      final String matchdayPrefix = stageType.equals(StageType.GRUPPENPHASE) ? "Spieltag " : "Runde ";
      final Stage stage = Stage.find(season, stageType);

      final Elements divisionLink = stageElement.select("ul.content-icon-info-l").select("a");
      final String idString = divisionLink.attr("href").split("/")[8].split("-")[0];
      final League league = League.find(Short.parseShort(idString));

      final Elements matchElements = stageElement.select("ul.league-stage-matches").select("li");
      for (int i = 0; i < matchElements.size(); i++) {
        final Elements selectTitle = matchElements.select("div.txt-info");
        final String timeString = selectTitle.select("span.tztime").attr("data-time");
        final Date date = new Date(Long.parseLong(timeString) * 1000L);
        final Calendar calendar = Util.getCalendar(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        final Calendar calendar1 = Util.getCalendar(calendar.getTime());
        calendar1.add(Calendar.DAY_OF_YEAR, 7);
        final String titleText = selectTitle.text();
        final int id = titleText.contains("(Spieltag ") ?
            Integer.parseInt(titleText.split("Spieltag ")[1].substring(0, 1)) : i + 1;
        final Element matchElement = matchElements.get(i).selectFirst("tr");
        final Matchday neu = new Matchday(matchdayPrefix + id, calendar.getTime(), calendar1.getTime());
        final Matchday matchday = Matchday.get(neu, stage);

        if (matchElement != null) {
          MatchLoader.loadMatch(league, matchday, matchElement);
        } else {
          logger.attention("Matchlist empty for some reason");
        }
      }
    }
  }
}
