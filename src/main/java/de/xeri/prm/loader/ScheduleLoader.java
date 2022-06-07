package de.xeri.prm.loader;

import java.util.Date;
import java.util.Set;

import de.xeri.prm.models.league.Schedule;
import de.xeri.prm.models.league.Team;
import de.xeri.prm.models.league.TurnamentMatch;
import de.xeri.prm.util.Const;
import de.xeri.prm.manager.PrimeData;

/**
 * Created by Lara on 06.04.2022 for web
 */
public final class ScheduleLoader {

  static {
    final Set<Schedule> schedules = Schedule.get();
    schedules.stream().filter(schedule -> !schedule.getType().getDisplayname().equals("Clash") && !schedule.getType().getDisplayname().startsWith("Best of")).forEach(schedule -> schedule.getEnemyTeam().getSchedules().remove(schedule));

    final Team team = Team.findTid(Const.TEAMID);
    for (TurnamentMatch match : team.getTurnamentMatches()) {
      final Team enemy = match.getHomeTeam().equals(team) ? match.getGuestTeam() : match.getHomeTeam();
      if (match.getLeague().getStage().isInSeason(new Date())) {
        enemy.setScrims(true);
      }

      final Schedule schedule = Schedule.get(new Schedule(match.getScheduleType(), match.getStart(),
          match.getMatchday().getStage().getStageType().name() + " - " + match.getScheduleType().name() + " gegen " +
              enemy.getTeamAbbr(), "Match vs. " + enemy.getTeamAbbr()));
      enemy.addSchedule(schedule);
    }
  }

  public static void load() {
    PrimeData.getInstance().commit();
  }
}
