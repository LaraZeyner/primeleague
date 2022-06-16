package de.xeri.prm.loader;

import de.xeri.prm.manager.PrimeData;
import de.xeri.prm.models.league.Team;
import de.xeri.prm.models.league.TurnamentMatch;
import de.xeri.prm.util.Const;

/**
 * Created by Lara on 06.04.2022 for web
 */
public final class ScheduleLoader {

  static {
    final Team team = Team.findTid(Const.TEAMID);
    for (TurnamentMatch match : PrimeData.getInstance().getCurrentGroup().getMatches()) {
      MatchLoader.analyseMatchPage(match);
      final Team enemy = match.getOtherTeam(team);
      if (enemy != null) {
        enemy.setScrims(true);
      }
    }
  }

  public static void load() {
    PrimeData.getInstance().commit();
  }
}
