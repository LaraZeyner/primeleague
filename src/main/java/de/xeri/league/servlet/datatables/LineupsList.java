package de.xeri.league.servlet.datatables;

import java.util.List;

import javax.faces.bean.ViewScoped;
import javax.inject.Named;

import de.xeri.league.models.enums.Lane;
import de.xeri.league.models.league.Account;
import de.xeri.league.models.league.Team;
import de.xeri.league.util.Const;

/**
 * Created by Lara on 18.04.2022 for web
 */
@Named("lineups")
@ViewScoped
public class LineupsList {

  public String getSelected(String type, String lane) {
    if (type.equals("live")) {
      return getLaners(Team.findNext().getTeamTid(), lane).isEmpty() ? "null" :
          getLaners(Team.findNext().getTeamTid(), lane).get(0).getDisplayName(Lane.findLane(lane));
    }
    return getLaners(Team.find(Const.TEAMID).getTeamTid(), lane).isEmpty() ? "null" :
        getLaners(Team.find(Const.TEAMID).getTeamTid(), lane).get(0).getDisplayName(Lane.findLane(lane));
  }

  public List<Account> getLaners(String type, String lane) {
    if (type.equals("live")) {
      return getLaners(Team.findNext().getTeamTid(), lane);
    }
    return getLaners(Team.find(Const.TEAMID).getTeamTid(), lane);
  }

  public String getSelected(int teamId, String lane) {
    return getLaners(teamId, lane).isEmpty() ? "null" : getLaners(teamId, lane).get(0).getDisplayName(Lane.findLane(lane));
  }

  public List<Account> getLaners(int teamId, String lane) {
    final Team team = Team.find(teamId);
    return team.getLaner(Lane.valueOf(lane));
  }

}
