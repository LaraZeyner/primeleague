package de.xeri.league.loader;

import java.io.FileNotFoundException;
import java.io.IOException;
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
import de.xeri.league.util.Data;
import de.xeri.league.util.io.json.HTML;
import de.xeri.league.util.logger.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by Lara on 11.04.2022 for web
 */
public class MatchLoader {

  public static void handleMatch(League league, Elements days, int i, Matchday matchday) {
    days.get(i).select("tr").forEach(match -> loadMatch(league, matchday, match));
  }

  public static void loadMatch(League league, Matchday matchday, Element match) {
    final Elements matchData = match.select("td").get(1).select("a");
    final int matchId = Integer.parseInt(matchData.attr("href").split("/matches/")[1].split("-")[0]);
    if (TurnamentMatch.has(matchId)) {
      final String score = matchData.text().contains(":") ? matchData.text() : "-:-";
      boolean b = TurnamentMatch.has(matchId);
      final TurnamentMatch turnamentMatch = TurnamentMatch.get(new TurnamentMatch(matchId, score), league, matchday);

      handleTeam(match, turnamentMatch, true);
      handleTeam(match, turnamentMatch, false);
      if (!b) {
        analyseMatchPage(turnamentMatch);
      }
    }
  }

  private static void handleTeam(Element match, TurnamentMatch turnamentMatch, boolean home) {
    final Element team2Placeholder = match.select("td").get(home ? 0 : 2);
    if (!team2Placeholder.text().equals("")) {
      final String teamName = team2Placeholder.select("img").attr("title");
      final String teamAbbr = team2Placeholder.text();
      final String teamIdString = team2Placeholder.select("img").attr("data-src");
      final String teamString = teamIdString.split("/")[5].split("\\.")[0];
      try {
        if (!TeamLoader.notFoundTeams.contains(Integer.parseInt(teamString))) {
          final Team team = Team.get(new Team(Integer.parseInt(teamString), teamName, teamAbbr));
          team.addMatch(turnamentMatch, home);
        }
      } catch (NumberFormatException ex) {
        de.xeri.league.util.logger.Logger.getLogger("Match Creation").warning("Match RIP");
      }
    }
  }

  public static void analyseMatchPage(TurnamentMatch match) {
    final Logger logger = Logger.getLogger("Match-Erstellung");
    try {
      final HTML html = Data.getInstance().getRequester()
          .requestHTML("https://www.primeleague.gg/leagues/matches/" + match.getId());
      final Document doc = Jsoup.parse(html.toString());

      final String timeString = doc.select("div#league-match-time").select("span").attr("data-time");
      match.setStart(new Date(Long.parseLong(timeString) * 1000L));

      handleMatchlog(match, doc);
      logger.info("Match erstellt");
    } catch (FileNotFoundException exception) {
      logger.warning("Match konnte nicht gefunden werden");
    } catch (IOException exception) {
      logger.severe(exception.getMessage());
    }
  }

  private static void handleMatchlog(TurnamentMatch match, Document doc) {
    for (Element entry : doc.select("section.league-match-logs").select("tbody").select("tr")) {
      final Elements column = entry.select("span.table-cell-container");
      // get Date
      final String stampString = column.first().select("span").attr("data-time");
      final Date date = new Date(Long.parseLong(stampString) * 1000L);
      final Matchlog logEntry = match.addEntry(new Matchlog(date));

      final String userString = column.get(1).select("span").text();
      String userName = userString.split("\\(")[userString.split("\\(").length - 2];
      if (userName.endsWith(" ")) userName = userName.substring(0, userName.length() - 1);
      String teamIdString = userString.split("\\(")[userString.split("\\(").length - 1].replace(")", "");
      if (teamIdString.contains("Team")) {
        if (!userName.equals("System")) {
          final Player player = Player.find(userName);
          if (player != null) {
            player.addLogEntry(logEntry);
          }
        }
        teamIdString = teamIdString.replace("Team ", "");
        final int teamId = Integer.parseInt(teamIdString);
        if (isTeam(teamId, 1, match.getHomeTeam())) {
          match.getHomeTeam().addLogEntry(logEntry);
        } else if (isTeam(teamId, 2, match.getGuestTeam())) {
          match.getGuestTeam().addLogEntry(logEntry);
        } else if (Team.find(teamId) != null) {
          Team.find(teamId).addLogEntry(logEntry);
        }
      }
      logEntry.setLogAction(LogAction.getAction(column.get(2).select("span").text()));
      logEntry.setLogDetails(column.get(3).select("span").text());
    }
    final List<Matchlog> logs = match.getLogEntries().stream()
        .sorted((o1, o2) -> (int) (o1.getLogTime().getTime() - o2.getLogTime().getTime()))
        .collect(Collectors.toList());

    final Matchstate matchstate = determineMatchstate(match, logs);
    match.setState(matchstate);
  }

  private static Matchstate determineMatchstate(TurnamentMatch match, List<Matchlog> logs) {
    final Logger logger = Logger.getLogger("Update Matchstate");

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
        final boolean home = match.getHomeTeam().equals(team);
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

    final Set<Team> teamReady = logs.stream()
        .filter(log -> log.getLogAction().equals(LogAction.SUBMIT))
        .map(Matchlog::getTeam)
        .collect(Collectors.toCollection(LinkedHashSet::new));
    if (teamReady.size() == 2) return Matchstate.LINEUPS_SUBMITTED;
    if (teamReady.size() == 1) return Matchstate.LINEUP_SUBMITTED;

    if (doesHappenXTimes(logs, LogAction.CONFIRM, 1)) {
      return Matchstate.SCHEDULED;
    }

    for (final Matchlog matchlog1 : logs) {
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
