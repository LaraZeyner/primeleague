package de.xeri.prm.servlet.deprecated;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.bean.RequestScoped;
import javax.inject.Named;

import de.xeri.prm.models.enums.LogAction;
import de.xeri.prm.models.enums.Matchstate;
import de.xeri.prm.models.league.Matchlog;
import de.xeri.prm.models.league.Team;
import de.xeri.prm.models.league.TurnamentMatch;
import de.xeri.prm.servlet.Event;
import lombok.Getter;
import lombok.val;

/**
 * Created by Lara on 10.05.2022 for web
 */
@Named
@RequestScoped
@Getter
public class MatchView {
  private int tourneyId;

  private List<Event> events;

  @PostConstruct
  public void init() {
    events = new ArrayList<>();
    final TurnamentMatch turnamentMatch = TurnamentMatch.find(tourneyId);

    val matchstates = Arrays.asList(Matchstate.SUGGESTED, Matchstate.LINEUPS_SUBMITTED, Matchstate.LOBBY_REQUESTED,
        Matchstate.GAME_1_ENDED, Matchstate.GAME_2_ENDED);
    Set<Team> readyTeams = new LinkedHashSet<>();
    final int teamAmount = (turnamentMatch.getHomeTeam() != null ? 1 : 0) + (turnamentMatch.getGuestTeam() != null ? 1 : 0);

    final List<Matchlog> matchlogList = turnamentMatch.getLogEntries().stream()
        .sorted(Comparator.comparing(Matchlog::getLogTime))
        .collect(Collectors.toList());

    for (Matchlog logEntry : matchlogList) {
      final Date logTime = logEntry.getLogTime();

      final LogAction logAction = logEntry.getLogAction();
      if (logAction.equals(LogAction.SUBMIT) && matchstates.contains(Matchstate.LINEUPS_SUBMITTED)) {
        final Team team = logEntry.getTeam();
        if (team != null) {
          readyTeams.add(team);
          if (readyTeams.size() == teamAmount) {
            events.add(new Event(Matchstate.LINEUPS_SUBMITTED, new SimpleDateFormat("dd.MM.yyyy HH:mm").format(logTime.getTime())));
            matchstates.remove(Matchstate.LINEUPS_SUBMITTED);
          }
        }
      }


      if (matchstates.contains(Matchstate.SUGGESTED) && logAction.equals(LogAction.SUGGEST)) {
        events.add(new Event(Matchstate.SUGGESTED, new SimpleDateFormat("dd.MM.yyyy HH:mm").format(logTime.getTime())));
        matchstates.remove(Matchstate.SUGGESTED);

      } else if (logAction.equals(LogAction.CONFIRM)) {
        events.add(new Event(Matchstate.SCHEDULED, new SimpleDateFormat("dd.MM.yyyy HH:mm").format(logTime.getTime())));

      } else if (matchstates.contains(Matchstate.LINEUPS_SUBMITTED) && logAction.equals(LogAction.REQUEST)) {
        events.add(new Event(Matchstate.LOBBY_REQUESTED, new SimpleDateFormat("dd.MM.yyyy HH:mm").format(logTime.getTime())));
        matchstates.remove(Matchstate.LOBBY_REQUESTED);

      } else if (matchstates.contains(Matchstate.GAME_1_ENDED) && logAction.equals(LogAction.REPORT)) {
        events.add(new Event(Matchstate.GAME_1_ENDED, new SimpleDateFormat("dd.MM.yyyy HH:mm").format(logTime.getTime())));
        matchstates.remove(Matchstate.GAME_1_ENDED);

      } else if (matchstates.contains(Matchstate.GAME_2_ENDED) && logAction.equals(LogAction.REPORT)) {
        events.add(new Event(Matchstate.GAME_2_ENDED, new SimpleDateFormat("dd.MM.yyyy HH:mm").format(logTime.getTime())));
        matchstates.remove(Matchstate.GAME_2_ENDED);

      } else if (logAction.equals(LogAction.DISQUALIFIED) || logAction.equals(LogAction.PLAYED)) {
        events.add(new Event(Matchstate.CLOSED, new SimpleDateFormat("dd.MM.yyyy HH:mm").format(logTime.getTime())));
      }
    }
  }
}
