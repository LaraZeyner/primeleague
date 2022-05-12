package de.xeri.prm.game;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import de.xeri.prm.models.league.Team;
import de.xeri.prm.models.match.Teamperformance;

/**
 * Created by Lara on 18.04.2022 for web
 */
public class Clash implements Serializable {
  private static final transient long serialVersionUID = 1595142171712103690L;

  private Date date;
  private Team team;
  private final Set<Teamperformance> teamperformances = new HashSet<>();

  public Clash(Date date, Team team) {
    this.date = date;
    this.team = team;
  }

  public void addGame(Teamperformance teamperformance) {
    teamperformances.add(teamperformance);
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public Team getTeam() {
    return team;
  }

  public void setTeam(Team team) {
    this.team = team;
  }

  public String getScore() {
    final int wins = (int) teamperformances.stream().filter(Teamperformance::isWin).count();
    final int loses = teamperformances.size() - wins;
    return wins + ":" + loses;
  }


  public Set<Teamperformance> getTeamperformances() {
    return teamperformances;
  }
}
