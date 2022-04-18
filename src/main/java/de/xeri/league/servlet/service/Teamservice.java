package de.xeri.league.servlet.service;

import javax.faces.bean.SessionScoped;
import javax.inject.Named;

import de.xeri.league.models.league.Team;
import de.xeri.league.util.Util;

/**
 * Created by Lara on 18.04.2022 for web
 */
@Named
@SessionScoped
public class Teamservice {
  private Team team;

  public Teamservice() {
  }

  public Teamservice(Team team) {
    this.team = team;
  }

  public Team getTeam() {
    return team;
  }

  public void setTeam(Team team) {
    this.team = team;
  }

  public int getGames() {
    return (int) team.getCompetitivePerformances().stream()
        .filter(teamperformance -> Util.inRange(teamperformance.getGame().getGameStart()))
        .count();
  }
}
