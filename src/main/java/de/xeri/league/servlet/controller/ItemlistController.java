package de.xeri.league.servlet.controller;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import de.xeri.league.servlet.teams.TeamBean;
import de.xeri.league.servlet.teams.TeamEntry;
import de.xeri.league.util.Const;

/**
 * Created by Lara on 04.04.2022 for web
 */
@ManagedBean
@RequestScoped
public class ItemlistController {
  private TeamEntry teamEntry;

  public TeamEntry getTeamEntry() {
    return teamEntry;
  }

  public void setTeamEntry(TeamEntry teamEntry) {
    this.teamEntry = teamEntry;
  }


  public String doLookup() {
    teamEntry = new TeamBean().getTeamEntries().stream().filter(team -> Const.TEAMID == team.getId())
        .findFirst().orElse(null);
    return "itemlist";
  }

  public String doLookup(String type) {
    // TODO: 18.04.2022 Type
    teamEntry = new TeamBean().getTeamEntries().stream().filter(team -> Const.TEAMID == team.getId())
        .findFirst().orElse(null);
    return "itemlist";
  }
}

