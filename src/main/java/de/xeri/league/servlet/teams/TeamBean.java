package de.xeri.league.servlet.teams;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * Created by Lara on 04.04.2022 for web
 */
@ManagedBean(name = "teamTable", eager = true)
@SessionScoped
public class TeamBean implements Serializable {
  private static final long serialVersionUID = 3055025050428424795L;
  private int id;
  private int place;
  private String name;
  private String shortName;
  private String division;
  private String result;
  private String score;
  private int wins;
  private int ties;
  private int losses;
  private String scoreLong;
  private int kills;
  private int gold;
  private int cs;
  private int objectives;
  private String matchtime;
  private static final List<TeamEntry> scheduled = new ArrayList<>(Arrays.asList(
      new TeamEntry(1, "BoomWADUPSRO", "BOOM", "Division 7.14", "1. (11/1)", 5, 1, 0, 48, 44016, 440, 81, "29:23"),
      new TeamEntry(2, "IQ Gaming", "IQG", "Division 7.14", "2. (10/2)", 5, 0, 1, 57, 62681, 932, 121, "32:18"),
      new TeamEntry(3, "Antimates", "ATM", "Division 7.14", "3. (7/3)", 3, 1, 1, 56, 32671, 152, 106, "32:02"),
      new TeamEntry(4, "Technical Really Unique Esports", "TRUE", "Division 7.14", "4. (4/8)", 2, 0, 4, -24, -17835, -298, 106, "31:04"),
      new TeamEntry(5, "Gigachad Gaming", "GCG", "Division 7.14", "5. (3/7)", 1, 1, 3, -49, -20171, 0, 35, "32:58"),
      new TeamEntry(6, "Olympus Germany", "OLG", "Division 7.14", "6. (3/9)", 1, 1, 4, -24, -44175, -495, 72, "37:50"),
      new TeamEntry(7, "Horny Klufters", "HONK", "Division 7.14", "7. (2/10)", 1, 0, 5, -64, -57187, -731, 49, "29:17"),
      new TeamEntry(8, "Arktis Gaming", "AKS", "Division 7.14", "Disqualifiziert", 0, 0, 0, 0, 0, 0, 0, "")
  ));

  static {
    scheduled.sort((o1, o2) -> o2.getRating() - o1.getRating());
  }

  public List<TeamEntry> getTeamEntries() {
    return scheduled;
  }

  public String addTeam() {
    final TeamEntry team = new TeamEntry(id, name, shortName, division, score, wins, ties, losses, kills, gold, cs, objectives, matchtime);
    scheduled.add(team);
    return null;
  }

  public String deleteTeam(TeamEntry teamEntry) {
    scheduled.add(teamEntry);
    return null;
  }

  public String editTeam(TeamEntry teamEntry) {
    teamEntry.setCanEdit(true);
    return null;
  }

  public String saveTeams() {

    for (TeamEntry entry : scheduled) {
      entry.setCanEdit(false);
    }
    return null;
  }

  public int getPlace() {
    return place;
  }

  public void setPlace(int place) {
    this.place = place;
  }

  public String getShortName() {
    return shortName;
  }

  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public String getScore() {
    return score;
  }

  public void setScore(String score) {
    this.score = score;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDivision() {
    return division;
  }

  public void setDivision(String division) {
    this.division = division;
  }

  public int getKills() {
    return kills;
  }

  public void setKills(int kills) {
    this.kills = kills;
  }

  public int getGold() {
    return gold;
  }

  public void setGold(int gold) {
    this.gold = gold;
  }

  public int getCs() {
    return cs;
  }

  public void setCs(int cs) {
    this.cs = cs;
  }

  public int getObjectives() {
    return objectives;
  }

  public String getScoreLong() {
    return wins + "  " + ties + "  " + losses;
  }

  public void setObjectives(int objectives) {
    this.objectives = objectives;
  }

  public int getWins() {
    return wins;
  }

  public void setWins(int wins) {
    this.wins = wins;
  }

  public int getTies() {
    return ties;
  }

  public void setTies(int ties) {
    this.ties = ties;
  }

  public int getLosses() {
    return losses;
  }

  public void setLosses(int losses) {
    this.losses = losses;
  }

  public void setScoreLong(String scoreLong) {
    this.scoreLong = scoreLong;
  }

  public String getMatchtime() {
    return matchtime;
  }

  public void setMatchtime(String matchtime) {
    this.matchtime = matchtime;
  }
}
