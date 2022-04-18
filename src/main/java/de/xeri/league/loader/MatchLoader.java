package de.xeri.league.loader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
    if (TurnamentMatch.find(matchId) == null) {
      final String score = matchData.text().contains(":") ? matchData.text() : "-:-";
      final TurnamentMatch turnamentMatch = TurnamentMatch.get(new TurnamentMatch(matchId, score));

      handleTeam(match, turnamentMatch, true);
      handleTeam(match, turnamentMatch, false);
      analyseMatchPage(turnamentMatch);

      if (matchday != null) matchday.addMatch(turnamentMatch);
      league.addMatch(turnamentMatch);
    }
  }

  private static void handleTeam(Element match, TurnamentMatch turnamentMatch, boolean home) {
    final Element team2Placeholder = match.select("td").get(home ? 0 : 2);
    if (team2Placeholder.is("img")) {
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

      for (Element entry : doc.select("section.league-match-logs").select("tbody").select("tr")) {
        final Elements column = entry.select("span.table-cell-container");
        // get Date
        final String stampString = column.first().select("span").attr("data-time");
        final Date date = new Date(Long.parseLong(stampString) * 1000L);
        final Matchlog logEntry = Matchlog.get(new Matchlog(date), match);

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
      final List<Matchlog> logs =
          match.getLogEntries().stream().sorted((o1, o2) -> (int) (o1.getLogTime().getTime() - o2.getLogTime().getTime())).collect(Collectors.toList());

      match.setState(Matchstate.CREATED);
      for (final Matchlog matchlog1 : logs) {
        if (matchlog1.getLogAction() == null) {
          logger.severe("Log Action nicht aufgezeichnet.");
        }
        if (matchlog1.getLogAction().equals(LogAction.SUGGEST)) {
          if (matchlog1.getTeam() != null && matchlog1.getTeam().equals(match.getHomeTeam())) match.setState(Matchstate.SUGGESTED);
          if (matchlog1.getTeam() != null && matchlog1.getTeam().equals(match.getGuestTeam())) match.setState(Matchstate.RESPONDED);
          break;
        }
      }
      anyMatch(logs, match, LogAction.CONFIRM, 1, Matchstate.SCHEDULED);
      final List<Team> teamReady = new ArrayList<>();
      logs.stream().filter(log -> log.getLogAction().equals(LogAction.SUBMIT)).filter(log -> !teamReady.contains(log.getTeam()))
          .forEach(log -> teamReady.add(log.getTeam()));
      if (teamReady.size() == 2) match.setState(Matchstate.LINEUP_SUBMITTED);
      if (teamReady.size() == 1) match.setState(Matchstate.LINEUPS_SUBMITTED);
      final List<Boolean> bools = Arrays.asList(false, false);
      final List<Integer> ints = Arrays.asList(0, 0);
      for (Matchlog entrie : logs) {
        final Team team = entrie.getTeam();
        if (team != null) {
          if (entrie.getLogAction().equals(LogAction.READY)) {
            if (team.equals(teamReady.get(0))) ints.set(0, ints.get(0) + 1);
            else if (team.equals(teamReady.get(1))) ints.set(1, ints.get(1) + 1);

          } else if (entrie.getLogAction().equals(LogAction.SUBMIT)) {
            if (teamReady.size() > 0 && team.equals(teamReady.get(0))) {
              if (ints.get(0) >= 5) bools.set(0, true);
              ints.set(0, 0);
            } else if (teamReady.size() > 1 && team.equals(teamReady.get(1))) {
              if (ints.get(1) >= 5) bools.set(1, true);
              ints.set(1, 0);
            }
          }
        } else {
          logger.warning("Team im Log nicht identifiziert.");
        }
      }

      final int boolsCount = (int) bools.stream().filter(b -> b).count();
      if (boolsCount == 2) match.setState(Matchstate.TEAMS_READY);
      if (boolsCount == 1) match.setState(Matchstate.TEAM_READY);
      if (logs.get(0).getLogTime().getTime() > new Date().getTime() - 600_000L) {
        anyMatch(logs, match, LogAction.REQUEST, 1, Matchstate.LOBBY_REQUESTED);
        anyMatch(logs, match, LogAction.REPORT, 1, Matchstate.GAME_1_ENDED);
        anyMatch(logs, match, LogAction.REPORT, 2, Matchstate.GAME_2_ENDED);

      } else {
        anyMatch(logs, match, LogAction.REQUEST, 1, Matchstate.GAME_1_OPEN);
        anyMatch(logs, match, LogAction.REPORT, 1, Matchstate.GAME_2_OPEN);
        anyMatch(logs, match, LogAction.REPORT, 2, Matchstate.GAME_3_OPEN);
      }
      anyMatch(logs, match, LogAction.PLAYED, 1, Matchstate.CLOSED);
      logger.info("Match erstellt");
    } catch (FileNotFoundException exception) {
      logger.warning("Match konnte nicht gefunden werden");
    } catch (IOException exception) {
      logger.severe(exception.getMessage());
    }
  }

  private static boolean isTeam(int teamId, int i, Team team) {
    return teamId == i && team != null;
  }

  private static void anyMatch(List<Matchlog> logs, TurnamentMatch match, LogAction action, int amount, Matchstate matchstate) {
    if (logs.stream().filter(matchlog -> matchlog.getLogAction().equals(action)).count() == amount)
        match.setState(matchstate);
  }
}
