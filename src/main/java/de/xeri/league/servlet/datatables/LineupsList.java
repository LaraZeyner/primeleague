package de.xeri.league.servlet.datatables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
      return getLaners(Team.findNext().getTeamTid(), lane).isEmpty() ? "null" : getLaners(Team.findNext().getTeamTid(), lane).get(0);
    }
    return getLaners(Team.find(Const.TEAMID).getTeamTid(), lane).isEmpty() ? "null" : getLaners(Team.find(Const.TEAMID).getTeamTid(), lane).get(0);
  }

  public List<String> getLaners(String type, String lane) {
    final Team team = type.equals("live") ? Team.find(Team.findNext().getTeamTid()) : Team.find(Const.TEAMID);
    final List<String> list = new ArrayList<>();
    final Map<Account, Integer> laner = team.getLaner(Lane.valueOf(lane));
    for (Account account : laner.keySet()) {
      final String accountName = account.getName();
      final int amount = laner.get(account);
      list.add(accountName + " - " + amount + " Spiel" + (amount != 1 ? "e" : ""));
    }
    return list;
  }

  public String getSelected(int teamId, String lane) {
    return getLaners(teamId, lane).isEmpty() ? "null" : getLaners(teamId, lane).get(0);
  }

  public List<String> getLaners(int teamId, String lane) {
    final Team team = Team.find(teamId);
    final List<String> list = new ArrayList<>();
    final Map<Account, Integer> laner = team.getLaner(Lane.valueOf(lane));
    for (Account account : laner.keySet()) {
      final String accountName = account.getName();
      final int amount = laner.get(account);
      list.add(accountName + " - " + amount + " Spiel" + (amount != 1 ? "e" : ""));
    }
    return list;
  }

}
