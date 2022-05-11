package de.xeri.league.servlet.datatables.table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import de.xeri.league.manager.Data;
import de.xeri.league.models.league.Team;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Lara on 09.05.2022 for web
 */
@ManagedBean(eager = true)
@SessionScoped
@Getter
@Setter
public class ShortTable implements Serializable {
  private static final transient long serialVersionUID = -7441859998261775332L;

  private int tId;
  private String name;
  private String abbreviation;
  private String logoUrl;
  private String winsPerMatch;
  private String teamScore;
  private String bilance;
  private String kills;
  private String killDiff;
  private String killsPerMatch;
  private String gold;
  private String goldDiff;
  private String goldPerMatch;
  private String creeps;
  private String creepDiff;
  private String creepPerMatch;
  private String objectives;
  private String objectivesPerMatch;
  private String towers;
  private String towersPerMatch;
  private String drakes;
  private String drakesPerMatch;
  private String inhibs;
  private String inhibsPerMatch;
  private String heralds;
  private String heraldsPerMatch;
  private String barons;
  private String baronsPerMatch;
  private String matchTime;
  private String matchTimeWins;
  private String matchTimeLosses;

  private static List<TeamObject> teams;

  static {
    try {
      teams = new ArrayList<>();
      final List<Team> leagueTeams = Data.getInstance().getCurrentGroup().getTeams().stream()
          .filter(Objects::nonNull)
          .sorted((Comparator.comparingLong(Team::getScore)).reversed())
          .collect(Collectors.toList());
      int previousPlace = 1;
      int previouswins = 0;
      for (int i = 0; i < leagueTeams.size(); i++) {
        final Team team = leagueTeams.get(i);
        int wins = Integer.parseInt(team.getTeamScore().split(":")[0]);
        if (wins < previouswins) {
          previousPlace = i + 1;
          previouswins = wins;
        } else if (wins > previouswins){
          previousPlace = i + 1;
        }

        teams.add(new TeamObject(previousPlace, team.getTurneyId(), team.getTeamName(), team.getTeamAbbr(), team.getLogoUrl(), team.getWinsPerMatch(),
            team.getTeamScore(), team.getBilance(), team.getKills(), String.valueOf(team.getKillDiff()), team.getKillsPerMatch(),
            team.getGold(), String.valueOf(team.getGoldDiff()), team.getGoldPerMatch(), team.getCreeps(), String.valueOf(team.getCreepDiff()),
            team.getCreepsPerMatch(), String.valueOf(team.getObjectives()), String.valueOf(team.getObjectivesPerMatch()),
            String.valueOf(team.getTowers()), String.valueOf(team.getTowersPerMatch()), String.valueOf(team.getDrakes()),
            String.valueOf(team.getDrakesPerMatch()), String.valueOf(team.getInhibs()), String.valueOf(team.getInhibsPerMatch()),
            String.valueOf(team.getHeralds()), String.valueOf(team.getHeraldsPerMatch()), String.valueOf(team.getBarons()),
            String.valueOf(team.getBaronsPerMatch()), team.getMatchtime(), team.getWintime(), team.getLosetime()));
      }
    } catch (Exception ignored) {
      teams = Arrays.asList(
          new TeamObject(1, 169462, "BoomWADUPSRO", "BOOM", "https://cdn0.gamesports.net/league_team_logos/169000/169462.jpg?1643105360", "92%", "11:1", "5  1  0", "0:0", "0", "0:0", "0:0", "0", "0:0", "0:0", "0", "0:0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "00:00", "00:00", "00:00"),
          new TeamObject(2, 127830, "IQ Gaming", "IQG", "https://cdn0.gamesports.net/league_team_logos/127000/127830.jpg?1620660184", "83%", "10:2", "5  0  1", "0:0", "0", "0:0", "0:0", "0", "0:0", "0:0", "0", "0:0", "0:0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "00:00", "00:00", "00:00"),
          new TeamObject(3, 163535, "Antimates", "ATM", "https://cdn0.gamesports.net/league_team_logos/163000/163535.jpg?1644181571", "70%", "7:3", "3  1  1", "0:0", "0", "0:0", "0:0", "0", "0:0", "0:0", "0", "0:0", "0:0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "00:00", "00:00", "00:00"),
          new TeamObject(4, 142116, "Technical Really Unique Esports", "TRUE", "https://cdn0.gamesports.net/league_team_logos/142000/142116.jpg?1643795480", "33%", "4:8", "2  0  4", "0:0", "0", "0:0", "0:0", "0", "0:0", "0:0", "0", "0:0", "0:0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "00:00", "00:00", "00:00"),
          new TeamObject(5, 142116, "Gigachad Gaming", "GCG", "https://cdn0.gamesports.net/league_team_logos/169000/169692.jpg?1643475220", "30%", "3:7", "1  1  3", "0:0", "0", "0:0", "0:0", "0", "0:0", "0:0", "0", "0:0", "0:0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "00:00", "00:00", "00:00"),
          new TeamObject(5, 142116, "Olympus Germany", "OLG", "https://cdn0.gamesports.net/league_team_logos/169000/169002.jpg?1637601890", "0,50", "3:9", "1  1  4", "0:0", "0", "0:0", "0:0", "0", "0:0", "0:0", "0", "0:0", "0:0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "00:00", "00:00", "00:00"),
          new TeamObject(7, 133712, "Horny Klufters", "HONK", "https://cdn0.gamesports.net/league_team_logos/133000/133712.jpg?1632772598", "17%", "2:10", "1  0  5", "0:0", "0", "0:0", "0:0", "0", "0:0", "0:0", "0", "0:0", "0:0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "00:00", "00:00", "00:00"),
          new TeamObject(0, 176975, "Arktis Gaming", "AKS", "https://cdn0.gamesports.net/league_team_logos/176000/176975.jpg?1643819581", "0%", "0:0", "0  0  0", "0:0", "0", "0:0", "0:0", "0", "0:0", "0:0", "0", "0:0", "0:0", "0", "0", "0", "0", "0", "0", "0",
              "0", "0", "0", "0", "00:00", "00:00", "00:00"));
    }
  }

  public List<TeamObject> getTeams() {
    return teams;
  }
}
