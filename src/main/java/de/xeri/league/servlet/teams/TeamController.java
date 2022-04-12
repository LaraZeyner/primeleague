package de.xeri.league.servlet.teams;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 * Created by Lara on 04.04.2022 for web
 */
@ManagedBean
@RequestScoped
public class TeamController {
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
    return "team";
  }
}

