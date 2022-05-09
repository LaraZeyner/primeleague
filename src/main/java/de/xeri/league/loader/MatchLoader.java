package de.xeri.league.loader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.xeri.league.models.enums.LogAction;
import de.xeri.league.models.enums.Matchstate;
import de.xeri.league.models.league.League;
import de.xeri.league.models.league.Matchday;
import de.xeri.league.models.league.Matchlog;
import de.xeri.league.models.league.Player;
import de.xeri.league.models.league.Team;
import de.xeri.league.models.league.TurnamentMatch;
import de.xeri.league.manager.Data;
import de.xeri.league.util.logger.Logger;
import lombok.val;
import lombok.var;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by Lara on 11.04.2022 for web
 */
public final class MatchLoader {
  private static int amount = 0;
  private final static List<Integer> matchesLoaded = new ArrayList<>();

  public static void handleMatch(League league, Elements days, int i, Matchday matchday) {
    days.get(i).select("tr").forEach(match -> loadMatch(league, matchday, match));
  }

  public static void loadMatch(League league, Matchday matchday, Element match) {
    val logger = Logger.getLogger("Match-Erstellung");
    val millisString = match.select("span").attr("data-time");
    val matchElement = match.selectFirst("tr");
    val matchData = matchElement.select("td").get(1).select("a");
    val matchIdString = matchData.attr("href").split("/matches/")[1].split("-")[0];
    final int matchId = Integer.parseInt(matchIdString);

    if (!matchesLoaded.contains(matchId) || !TurnamentMatch.has(matchId)) {
      val score = matchData.text().contains(":") ? matchData.text() : "-:-";
      val turnamentMatch = TurnamentMatch.get(new TurnamentMatch(matchId, score, new Date(Long.parseLong(millisString))), league, matchday);

      handleTeam(matchElement, turnamentMatch, true, league);
      handleTeam(matchElement, turnamentMatch, false, league);
      analyseMatchPage(turnamentMatch);

      matchesLoaded.add(matchId);
      amount++;
      if (amount % 10 == 0) {
        logger.info(amount + " Matches hinzugefügt");
      }
    }
  }

  private static void handleTeam(Element match, TurnamentMatch turnamentMatch, boolean home, League league) {
    val teamPlaceholder = match.select("td").get(home ? 0 : 2);
    if (!teamPlaceholder.text().equals("")) {
      val teamName = teamPlaceholder.select("img").attr("title");
      val teamAbbr = teamPlaceholder.text();
      val teamIdString = teamPlaceholder.select("img").attr("data-src");
      val teamString = teamIdString.split("/")[5].split("\\.")[0];
      Team team = null;
      try {
        if (!TeamLoader.notFoundTeams.contains(Integer.parseInt(teamString))) {
          team = Team.get(new Team(Integer.parseInt(teamString), teamName, teamAbbr));
        }
      } catch (NumberFormatException ex) {
        team = Team.find(teamName, league);
      }
      if (team != null) {
        team.addMatch(turnamentMatch, home);
      }
    }
  }

  public static void analyseMatchPage(TurnamentMatch match) {
    val logger = Logger.getLogger("Match-Erstellung");
    try {
      val html = Data.getInstance().getRequester().requestHTML("https://www.primeleague.gg/leagues/matches/" + match.getId());
      val doc = Jsoup.parse(html.toString());

      val timeString = doc.select("div#league-match-time").select("span").attr("data-time");
      match.setStart(new Date(Long.parseLong(timeString) * 1000L));

      handleMatchlog(match, doc);
    } catch (FileNotFoundException exception) {
      logger.warning("Match konnte nicht gefunden werden");
    } catch (IOException exception) {
      logger.severe(exception.getMessage());
    }
  }

  private static void handleMatchlog(TurnamentMatch match, Document doc) {
    for (Element entry : doc.select("section.league-match-logs").select("tbody").select("tr")) {
      val column = entry.select("span.table-cell-container");
      val stampString = column.first().select("span").attr("data-time");
      val date = new Date(Long.parseLong(stampString) * 1000L);


      val userString = column.get(1).select("span").text();
      val logAction = LogAction.getAction(column.get(2).select("span").text());
      val logDetails = column.get(3).select("span").text();
      var logEntry = match.addEntry(new Matchlog(date, logAction, logDetails));

      var userName = userString.split("\\(")[userString.split("\\(").length - 2];
      if (userName.endsWith(" ")) userName = userName.substring(0, userName.length() - 1);
      var teamIdString = userString.split("\\(")[userString.split("\\(").length - 1].replace(")", "");
      if (teamIdString.contains("Team")) {
        if (!userName.equals("System")) {
          if (Player.has(userName)) {
            val player = Player.find(userName);
            player.addLogEntry(logEntry);
          }
        }
        teamIdString = teamIdString.replace("Team ", "");
        final int teamId = Integer.parseInt(teamIdString);
        if (isTeam(teamId, 1, match.getHomeTeam())) {
          match.getHomeTeam().addLogEntry(logEntry);
        } else if (isTeam(teamId, 2, match.getGuestTeam())) {
          match.getGuestTeam().addLogEntry(logEntry);
        } else if (Team.hasTId(teamId)) {
          Team.findTid(teamId).addLogEntry(logEntry);
        }
      }
    }
    val logs = match.getLogEntries().stream()
        .sorted((o1, o2) -> (int) (o1.getLogTime().getTime() - o2.getLogTime().getTime()))
        .collect(Collectors.toList());

    if (!logs.isEmpty()) {
      val matchstate = determineMatchstate(match, logs);
      match.setState(matchstate);
    } else {
      match.setState(Matchstate.CREATED);
    }
  }

  private static Matchstate determineMatchstate(TurnamentMatch match, List<Matchlog> logs) {
    val logger = Logger.getLogger("Update Matchstate");

    if (doesHappenXTimes(logs, LogAction.PLAYED, 1)) return Matchstate.CLOSED;

    if (isRecentlyLogged(logs)) {
      if (doesHappenXTimes(logs, LogAction.REPORT, 2)) {
        return Matchstate.GAME_2_ENDED;
      } else if (doesHappenXTimes(logs, LogAction.REPORT, 1)) {
        return Matchstate.GAME_1_ENDED;
      } else if (doesHappenXTimes(logs, LogAction.REQUEST, 1)) {
        return Matchstate.LOBBY_REQUESTED;
      }

    } else {
      if (doesHappenXTimes(logs, LogAction.REPORT, 2)) {
        return Matchstate.GAME_3_OPEN;
      } else if (doesHappenXTimes(logs, LogAction.REPORT, 1)) {
        return Matchstate.GAME_2_OPEN;
      } else if (doesHappenXTimes(logs, LogAction.REQUEST, 1)) {
        return Matchstate.GAME_1_OPEN;
      }
    }

    final List<Set<Player>> readyTeams = Arrays.asList(new LinkedHashSet<>(), new LinkedHashSet<>());
    for (Matchlog log : logs) {
      final Team team = log.getTeam();
      if (team != null && match.hasTeam(team)) {
        final boolean home = match.getHomeTeam() != null && match.getHomeTeam().equals(team);
        final int idInGame = home ? 0 : 1;
        if (log.getLogAction().equals(LogAction.SUBMIT)) {
          readyTeams.get(idInGame).clear();
        } else if (log.getLogAction().equals(LogAction.READY)) {
          readyTeams.get(idInGame).add(log.getPlayer());
        }
      } else if (log.getLogAction().equals(LogAction.READY) || log.getLogAction().equals(LogAction.SUBMIT)) {
        logger.severe("Team im Log nicht identifiziert.", log.toString());
      }
    }
    final int teamsReady = (int) readyTeams.stream().filter(team -> team.size() == 5).count();
    if (teamsReady == 2) return Matchstate.TEAMS_READY;
    if (teamsReady == 1) return Matchstate.TEAM_READY;

    val teamReady = logs.stream()
        .filter(log -> log.getLogAction().equals(LogAction.SUBMIT))
        .map(Matchlog::getTeam)
        .collect(Collectors.toCollection(LinkedHashSet::new));
    if (teamReady.size() == 2) return Matchstate.LINEUPS_SUBMITTED;
    if (teamReady.size() == 1) return Matchstate.LINEUP_SUBMITTED;

    if (doesHappenXTimes(logs, LogAction.CONFIRM, 1)) {
      return Matchstate.SCHEDULED;
    }

    for (Matchlog matchlog1 : logs) {
      if (matchlog1.getLogAction() != null) {
        if (matchlog1.getLogAction().equals(LogAction.SUGGEST)) {
          if (matchlog1.getTeam() != null && matchlog1.getTeam().equals(match.getGuestTeam())) return Matchstate.RESPONDED;
          if (matchlog1.getTeam() != null && matchlog1.getTeam().equals(match.getHomeTeam())) return Matchstate.SUGGESTED;
        }
      } else {
        logger.severe("Log Action nicht aufgezeichnet.");
      }
    }

    return Matchstate.CREATED;
  }

  private static boolean isRecentlyLogged(List<Matchlog> logs) {
    return logs.get(0).getLogTime().getTime() > new Date().getTime() - 600_000L;
  }

  private static boolean doesHappenXTimes(List<Matchlog> logs, LogAction played, int i) {
    return logs.stream().filter(matchlog -> matchlog.getLogAction().equals(played)).count() == i;
  }

  private static boolean isTeam(int teamId, int i, Team team) {
    return teamId == i && team != null;
  }
}
