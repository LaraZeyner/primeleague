package de.xeri.league.servlet.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;
import javax.inject.Named;

import de.xeri.league.models.league.League;
import de.xeri.league.models.league.TeamLeaguePerformance;

/**
 * Created by Lara on 20.04.2022 for web
 */
@Named
@ViewScoped
public class TableController {

  private League division;
  private List<TeamLeaguePerformance> teams = new ArrayList<>();

  public List<TeamLeaguePerformance> getTeams() {
    return teams;
  }

  public void setTeams(List<TeamLeaguePerformance> teams) {
    this.teams = teams;
  }

  public League getDivision() {
    return division;
  }

  public void setDivision(League division) {
    this.division = division;
  }

  @PostConstruct
  public void init() {
    /*
    final Team trueTeam = Team.find(Const.TEAMID);
    division = trueTeam.getLastLeague();

    final Set<Team> teamsOfDivision = division.getTeams();
    for (int i = 0; i < teamsOfDivision.size(); i++) {
      final Team team = division.atPlace(i + 1);
      teams.add(new TeamLeaguePerformance(division, team));
    }*/
  }
}
