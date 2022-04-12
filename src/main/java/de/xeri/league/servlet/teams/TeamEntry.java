package de.xeri.league.servlet.teams;

import java.util.LinkedHashSet;
import java.util.Set;

import de.xeri.league.models.league.Player;
import de.xeri.league.models.league.Schedule;
import de.xeri.league.models.match.Teamperformance;
import de.xeri.league.models.league.TurnamentMatch;

/**
 * Created by Lara on 04.04.2022 for web
 */
public class TeamEntry {
  private int id;
  private String name;
  private String abbrevation;
  private String group;
  private String result;
  private String score;
  private int rating;
  private int place;
  private String scoreLong;
  private int wins;
  private int ties;
  private int losses;
  private int kills;
  private int gold;
  private int cs;
  private int objectives;
  private String matchtime;
  private final Set<Teamperformance> performances = new LinkedHashSet<>();
  private final Set<Schedule> schedules = new LinkedHashSet<>();
  private final Set<TurnamentMatch> matches = new LinkedHashSet<>();
  private final Set<Player> players = new LinkedHashSet<>();
  private boolean canEdit;

  public TeamEntry(int id, String name, String abbrevation, String group, String score, int wins, int ties, int losses, int kills, int gold,
                   int cs, int objectives, String matchtime) {
    this.id = id;
    this.name = name;
    this.abbrevation = abbrevation;
    this.group = group;
    this.kills = kills;
    this.gold = gold;
    this.cs = cs;
    this.objectives = objectives;
    this.wins = wins;
    this.ties = ties;
    this.losses = losses;
    this.matchtime = matchtime;

    if (score.contains("/")) {
      final int home = Integer.parseInt(score.split("\\(")[1].split("\\)")[0].split("/")[0]);
      final int guest = Integer.parseInt(score.split("\\(")[1].split("\\)")[0].split("/")[1]);
      this.result = 100 * home / (home + guest) + "%";
      this.rating = id + 1_000 * home + 1_000_000 * home / (home + guest);
      this.place = score.contains(".") ? Integer.parseInt(score.split("\\.")[0]) : -1;
      this.score = home + ":" + guest;
      this.scoreLong = wins + "  " + ties + "  " + losses;
    } else {
     this.score = "";
     this.scoreLong = "disquali";
    }
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getPlace() {
    return place;
  }

  public void setPlace(int place) {
    this.place = place;
  }

  public String getAbbrevation() {
    return abbrevation;
  }

  public void setAbbrevation(String abbrevation) {
    this.abbrevation = abbrevation;
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getGroup() {
    return group;
  }

  public void setGroup(String group) {
    this.group = group;
  }

  public int getRating() {
    return rating;
  }

  public void setRating(int rating) {
    this.rating = rating;
  }

  public Set<Teamperformance> getPerformances() {
    return performances;
  }

  public Set<Schedule> getSchedules() {
    return schedules;
  }

  public Set<TurnamentMatch> getMatches() {
    return matches;
  }

  public Set<Player> getPlayers() {
    return players;
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

  public void setObjectives(int objectives) {
    this.objectives = objectives;
  }

  public String getScoreLong() {
    return scoreLong;
  }

  public void setScoreLong(String scoreLong) {
    this.scoreLong = scoreLong;
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

  public boolean isCanEdit() {
    return canEdit;
  }

  public void setCanEdit(boolean canEdit) {
    this.canEdit = canEdit;
  }

  public String getMatchtime() {
    return matchtime;
  }

  public void setMatchtime(String matchtime) {
    this.matchtime = matchtime;
  }
}
