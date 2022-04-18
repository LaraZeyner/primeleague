package de.xeri.league.servlet.controller;

import javax.faces.bean.RequestScoped;
import javax.inject.Named;

import de.xeri.league.models.league.Team;
import de.xeri.league.util.Const;
import de.xeri.league.util.Util;

/**
 * Created by Lara on 04.04.2022 for web
 */
@Named
@RequestScoped
public class TeamController {
  private Team team;

  public Team getTeam() {
    return team;
  }

  public void setTeam(Team team) {
    this.team = team;
  }

  public String doLookup(int id) {
    team = Team.find(id);
    return "team";
  }

  public String doLookup() {
    return doLookup(Const.TEAMID);
  }

  public String doLookup(String string) {
    if (string.equals("live")) {
      final Team team = Team.findNext();
      return doLookup(team.getId());
    }
    return doLookup();
  }

  public int getGames() {
    return (int) team.getCompetitivePerformances().stream()
        .filter(teamperformance -> Util.inRange(teamperformance.getGame().getGameStart()))
        .count();
  }

  public int getWins() {
    return (int) team.getCompetitivePerformances().stream()
        .filter(teamperformance -> Util.inRange(teamperformance.getGame().getGameStart()) && teamperformance.isWin())
        .count() * 100 / getGames();
  }
}
