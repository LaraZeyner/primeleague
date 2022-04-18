package de.xeri.league.servlet.controller;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import de.xeri.league.servlet.teams.TeamBean;
import de.xeri.league.servlet.teams.TeamEntry;

/**
 * Created by Lara on 04.04.2022 for web
 */
@ManagedBean
@RequestScoped
public class ScoutingController {
  private TeamEntry teamEntry;

  public TeamEntry getTeamEntry() {
    return teamEntry;
  }

  public void setTeamEntry(TeamEntry teamEntry) {
    this.teamEntry = teamEntry;
  }

  public String doLookupPlayer(String id) {
    teamEntry = new TeamBean().getTeamEntries().stream().filter(team -> Integer.parseInt(id) == team.getId())
        .findFirst().orElse(null);
    return "scoutingplayer";
  }

  public String doLookupChampions(String id) {
    teamEntry = new TeamBean().getTeamEntries().stream().filter(team -> Integer.parseInt(id) == team.getId())
        .findFirst().orElse(null);
    return "scoutingchampions";
  }

  public String doLookupComposition(String id) {
    teamEntry = new TeamBean().getTeamEntries().stream().filter(team -> Integer.parseInt(id) == team.getId())
        .findFirst().orElse(null);
    return "scoutingcomposition";
  }
}

