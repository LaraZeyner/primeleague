package de.xeri.prm.loader;

import de.xeri.prm.models.league.Schedule;
import de.xeri.prm.models.league.Team;
import de.xeri.prm.models.league.TurnamentMatch;
import de.xeri.prm.util.Const;
import de.xeri.prm.manager.Data;

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
