package de.xeri.league.servlet.controller;

import java.util.List;

import javax.faces.bean.RequestScoped;
import javax.inject.Named;

import de.xeri.league.models.league.Player;
import de.xeri.league.models.league.Team;

/**
 * Created by Lara on 04.04.2022 for web
 */
@Named
@RequestScoped
public class PlayerController {
  private Team team;
  private List<Player> players;

  public Team getTeam() {
    return team;
  }

  public void setTeam(Team team) {
    this.team = team;
  }

  public List<Player> getPlayers() {
    return players;
  }

  public void setPlayers(List<Player> players) {
    this.players = players;
  }

  public String doLookup(int id) {
    team = Team.find(id);
    players = (List<Player>) team.getPlayers();
    return "player";
  }
}

