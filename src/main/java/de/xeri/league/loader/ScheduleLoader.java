package de.xeri.league.loader;

import de.xeri.league.models.league.Schedule;
import de.xeri.league.models.league.Team;
import de.xeri.league.models.league.TurnamentMatch;
import de.xeri.league.util.Const;
import de.xeri.league.util.Data;

/**
 * Created by Lara on 06.04.2022 for web
 */
public final class ScheduleLoader {

  static {
    final Team team = Team.find(Const.TEAMID);
    for (TurnamentMatch match : team.getTurnamentMatches()) {
      match.getGuestTeam().setScrims(true);
      final Schedule schedule = Schedule.get(new Schedule(match.getScheduleType(), match.getStart(),
          match.getMatchday().getStage().getStageType().name() + " - " + match.getScheduleType().name() + " gegen" +
              match.getGuestTeam().getTeamAbbr(), "Match vs. " + match.getGuestTeam().getTeamAbbr()));
      match.getGuestTeam().addSchedule(schedule);
    }
  }

  public static void load() {
    Data.getInstance().commit();
  }
}
