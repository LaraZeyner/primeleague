package de.xeri.league.loader;

import de.xeri.league.models.league.Schedule;
import de.xeri.league.models.league.Team;
import de.xeri.league.models.league.TurnamentMatch;
import de.xeri.league.util.Const;

/**
 * Created by Lara on 06.04.2022 for web
 */
public final class ScheduleLoader {

  static {
    final Team team = Team.find(Const.TEAMID);
    for (TurnamentMatch match : team.getTurnamentMatches()) {
      final Schedule schedule = Schedule.get(new Schedule(match.getScheduleType(), match.getStart(),
          match.getMatchday().getStage().getStageType().name() + " - " + match.getScheduleType().name() + " gegen" +
              match.getGuestTeam().getTeamAbbr(), "Match vs. " + match.getGuestTeam().getTeamAbbr()));
      match.getGuestTeam().addSchedule(schedule);
    }
  }

  public static void load() {

  }
  // TODO: 08.04.2022 If schedule add game of team -> 180 days query
}
