package de.xeri.league.models.league;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.league.models.match.Teamperformance;
import de.xeri.league.util.Data;
import de.xeri.league.util.Util;

@Entity(name = "Team")
@Table(name = "team", indexes = {
    @Index(name = "team_tId", columnList = "team_tId", unique = true),
    @Index(name = "team_abbr", columnList = "team_abbr", unique = true),
    @Index(name = "team_name", columnList = "team_name", unique = true)
})
public class Team implements Serializable {

  @Transient
  private static final long serialVersionUID = 2656015802095877363L;

  private static Set<Team> data;

  public static void save() {
    if (data != null) data.forEach(Data.getInstance().getSession()::saveOrUpdate);
  }

  public static Set<Team> get() {
    if (data == null) data = new LinkedHashSet<>((List<Team>) Util.query("Team"));
    return data;
  }

  public static Team get(Team neu) {
    get();
    if (find(neu.getTeamTid()) == null) data.add(neu);
    return find(neu.getTeamTid());
  }

  public static Team find(int id) {
    get();
    return data.stream().filter(entry -> entry.getTeamTid() == id).findFirst().orElse(null);
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "team_id", nullable = false)
  private short id;

  @Column(name = "team_tId")
  private int teamTid;

  @Column(name = "team_name", nullable = false, length = 100)
  private String teamName;

  @Column(name = "team_abbr", nullable = false, length = 10)
  private String teamAbbr;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "league")
  private League league;

  @Column(name = "team_result", length = 15)
  private String teamResult;

  @Column(name = "scrims")
  private boolean scrims;

  @OneToMany(mappedBy = "team")
  private final Set<Teamperformance> teamperformances = new LinkedHashSet<>();

  @OneToMany(mappedBy = "enemyTeam")
  private final Set<Schedule> schedules = new LinkedHashSet<>();

  @OneToMany(mappedBy = "homeTeam")
  private final Set<TurnamentMatch> matchesHome = new LinkedHashSet<>();

  @OneToMany(mappedBy = "guestTeam")
  private final Set<TurnamentMatch> matchesGuest = new LinkedHashSet<>();

  @OneToMany(mappedBy = "team")
  private final Set<Player> players = new LinkedHashSet<>();

  @OneToMany(mappedBy = "player")
  private final Set<Matchlog> logEntries = new LinkedHashSet<>();

  // default constructor
  public Team() {
  }

  public Team(int teamTid, String teamName, String teamAbbr) {
    this.teamTid = teamTid;
    this.teamName = teamName;
    this.teamAbbr = teamAbbr;
  }

  public void addSchedule(Schedule schedule) {
    schedules.add(schedule);
    schedule.setEnemyTeam(this);
  }

  public void addMatch(TurnamentMatch match, boolean home) {
    if (home) {
      matchesHome.add(match);
      match.setHomeTeam(this);
    } else {
      matchesGuest.add(match);
      match.setGuestTeam(this);
    }
  }

  public void addPlayer(Player player) {
    players.add(player);
    player.setTeam(this);
  }

  public void addLogEntry(Matchlog entry) {
    logEntries.add(entry);
    entry.setTeam(this);
  }

  public boolean isValueable() {
    return league != null && league.getId() == find(142116).getLeague().getId() || scrims;
  }

  public String getLogoUrl() {
    return "https://cdn0.gamesports.net/league_team_logos/" + teamTid/1000  + "000/" + teamTid + ".jpg";
  }

  public Set<TurnamentMatch> getTurnamentMatches() {
    final Set<TurnamentMatch> allmatches = new LinkedHashSet<>(matchesHome);
    allmatches.addAll(matchesGuest);
    return allmatches;
  }

  //<editor-fold desc="getter and setter">
  public Set<Matchlog> getLogEntries() {
    return logEntries;
  }

  public Set<Player> getPlayers() {
    return players;
  }

  public Set<TurnamentMatch> getMatchesGuest() {
    return matchesGuest;
  }

  public Set<TurnamentMatch> getMatchesHome() {
    return matchesHome;
  }

  public Set<Schedule> getSchedules() {
    return schedules;
  }

  public Set<Teamperformance> getTeamperformances() {
    return teamperformances;
  }

  public String getTeamResult() {
    return teamResult;
  }

  public void setTeamResult(String teamResult) {
    this.teamResult = teamResult;
  }

  public League getLeague() {
    return league;
  }

  void setLeague(League teamGroup) {
    this.league = teamGroup;
  }

  public String getTeamAbbr() {
    return teamAbbr;
  }

  public void setTeamAbbr(String teamAbbr) {
    this.teamAbbr = teamAbbr;
  }

  public String getTeamName() {
    return teamName;
  }

  public void setTeamName(String teamName) {
    this.teamName = teamName;
  }

  public int getTeamTid() {
    return teamTid;
  }

  void setTeamTid(int teamTid) {
    this.teamTid = teamTid;
  }

  public short getId() {
    return id;
  }

  public void setId(short id) {
    this.id = id;
  }

  public boolean isScrims() {
    return scrims;
  }

  public void setScrims(boolean scrims) {
    this.scrims = scrims;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Team)) return false;
    final Team team = (Team) o;
    return getId() == team.getId() && getTeamTid() == team.getTeamTid() && getTeamName().equals(team.getTeamName()) && getTeamAbbr().equals(team.getTeamAbbr()) && Objects.equals(getLeague(), team.getLeague()) && Objects.equals(getTeamResult(), team.getTeamResult()) && getTeamperformances().equals(team.getTeamperformances()) && getSchedules().equals(team.getSchedules()) && getMatchesHome().equals(team.getMatchesHome()) && getMatchesGuest().equals(team.getMatchesGuest()) && getPlayers().equals(team.getPlayers());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getTeamTid(), getTeamName(), getTeamAbbr(), getLeague(), getTeamResult(), getTeamperformances(), getSchedules());
  }

  @Override
  public String toString() {
    return "Team{" +
        "id=" + id +
        ", teamTid=" + teamTid +
        ", teamName='" + teamName + '\'' +
        ", teamAbbr='" + teamAbbr + '\'' +
        ", league='" + league + '\'' +
        ", teamResult='" + teamResult + '\'' +
        '}';
  }
  //</editor-fold>
}