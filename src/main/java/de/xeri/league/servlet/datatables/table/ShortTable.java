package de.xeri.league.servlet.datatables.table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Lara on 09.05.2022 for web
 */
@Named
@ViewScoped
@Getter
@Setter
public class ShortTable implements Serializable {
  private static final transient long serialVersionUID = -5602534718145399628L;

  private List<TeamObject> teams;
  private String leagueName;

  @PostConstruct
  public void init() {

    /*final List<Team> leagueTeams = Data.getInstance().getCurrentGroup().getTeams().stream()
        .filter(Objects::nonNull)
        .sorted((Comparator.comparingLong(Team::getScore)).reversed())
        .collect(Collectors.toList());
    for (Team team : leagueTeams) {
      this.teams.add(new TeamObject(team.getTurneyId(), team.getTeamAbbr(), team.getLogoUrl(), team.getWinsPerMatch(),
          team.getTeamScore(), team.getBilance()));

    }*/

    this.teams = new ArrayList<>();
    this.teams.add(
        new TeamObject(169462, "BOOM", "https://cdn0.gamesports.net/league_team_logos/169000/169462.jpg?1643105360",
            "1,83", "11:1", "5  1  0"));
    this.teams.add(
        new TeamObject(127830, "IQG", "https://cdn0.gamesports.net/league_team_logos/127000/127830.jpg?1620660184",
            "1,67", "10:2", "5  0  1"));
    this.teams.add(
        new TeamObject(163535, "ATM", "https://cdn0.gamesports.net/league_team_logos/163000/163535.jpg?1644181571",
            "1,40", "7:3", "3  1  1"));
    this.teams.add(
        new TeamObject(142116, "TRUE", "https://cdn0.gamesports.net/league_team_logos/142000/142116.jpg?1643795480",
            "0,67", "4:8", "2  0  4"));
    this.teams.add(
        new TeamObject(142116, "GCG", "https://cdn0.gamesports.net/league_team_logos/169000/169692.jpg?1643475220",
            "0,60", "3:7", "1  1  3"));
    this.teams.add(
        new TeamObject(142116, "OLG", "https://cdn0.gamesports.net/league_team_logos/169000/169002.jpg?1637601890",
            "0,50", "3:9", "1  1  4"));
    this.teams.add(
        new TeamObject(133712, "HONK", "https://cdn0.gamesports.net/league_team_logos/133000/133712.jpg?1632772598",
            "0,33", "2:10", "1  0  5"));
    this.teams.add(
        new TeamObject(176975, "AKS", "https://cdn0.gamesports.net/league_team_logos/176000/176975.jpg?1643819581",
            "-,--", "-:-", "-  -  -"));
    //this.leagueName = Data.getInstance().getCurrentGroup().getName();
  }
}
