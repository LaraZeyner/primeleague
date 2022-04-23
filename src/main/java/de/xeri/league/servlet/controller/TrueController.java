package de.xeri.league.servlet.controller;

import javax.annotation.PostConstruct;
import javax.faces.bean.RequestScoped;
import javax.inject.Named;

import de.xeri.league.models.league.League;
import de.xeri.league.models.league.Team;
import de.xeri.league.util.Const;

/**
 * Created by Lara on 20.04.2022 for web
 */
@Named
@RequestScoped
public class TrueController {
  private League division;

  @PostConstruct
  public void init() {
    Team team = Team.find(Const.TEAMID);
    division = team.getLastLeague();
  }

  public League getDivision() {
    return division;
  }
}
