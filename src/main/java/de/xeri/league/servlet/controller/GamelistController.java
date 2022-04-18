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
public class GamelistController {
  private TeamEntry teamEntry;

  public TeamEntry getTeamEntry() {
    return teamEntry;
  }

  public void setTeamEntry(TeamEntry teamEntry) {
    this.teamEntry = teamEntry;
  }

  public String doLookup(int id) {
    teamEntry = new TeamBean().getTeamEntries().stream().filter(team -> id == team.getId())
        .findFirst().orElse(null);
    return "gamelist";
  }

  public String doLookup(int id, String type) {
    teamEntry = new TeamBean().getTeamEntries().stream().filter(team1 -> id == team1.getId())
        .findFirst().orElse(null);
    return "gamelist";
  }

  public String doLookup() {
    teamEntry = new TeamBean().getTeamEntries().stream().filter(team -> Const.TEAMID == team.getId())
        .findFirst().orElse(null);
    return "gamelist";
  }

  public String doLookup(String type) {
    // TODO: 18.04.2022 Type
    teamEntry = new TeamBean().getTeamEntries().stream().filter(team -> Const.TEAMID == team.getId())
        .findFirst().orElse(null);
    return "gamelist";
  }
}

