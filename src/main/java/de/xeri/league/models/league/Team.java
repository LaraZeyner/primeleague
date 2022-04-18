package de.xeri.league.models.league;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.league.models.enums.Lane;
import de.xeri.league.models.enums.QueueType;
import de.xeri.league.models.match.Game;
import de.xeri.league.models.match.Playerperformance;
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
    final Team team = find(neu.getTeamTid());
    if (team == null) {
      data.add(neu);
    } else {
      team.setTeamAbbr(neu.teamAbbr);
      team.setTeamName(neu.teamName);
    }
    return find(neu.getTeamTid());
  }

  public static Team find(int id) {
    get();
    return data.stream().filter(entry -> entry.getTeamTid() == id).findFirst().orElse(null);
  }

  public static Team findNext() {
    final List<Schedule> next = Schedule.next();
    if (!next.isEmpty()) {
       return next.get(0).getEnemyTeam();
    }
    final List<Schedule> last = Schedule.last();
    return last.get(last.size() - 1).getEnemyTeam();
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

  @ManyToMany(mappedBy = "teams")
  private final Set<League> leagues = new LinkedHashSet<>();

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

  public Player addPlayer(Player player) {
    return Player.get(player, this);
  }

  public void addLogEntry(Matchlog entry) {
    logEntries.add(entry);
    entry.setTeam(this);
  }

  public List<Clash> getClashDays() {
    final List<Game> clashGames = teamperformances.stream().filter(teamperformance -> teamperformance.getGame().isQueue(QueueType.CLASH))
        .map(Teamperformance::getGame).sorted(Comparator.comparingLong(game -> game.getGameStart().getTime())).collect(Collectors.toList());
    final List<Clash> clashes = new ArrayList<>();
    long millis = 0;
    for (Game game : clashGames) {
      final long gameMillis = game.getGameStart().getTime();
      final long difference = gameMillis - millis;
      if (difference > 43_200_000L) { // 12 Stunden
        clashes.add(new Clash(game.getGameStart(), this));
      }

      final Clash clash = clashes.get(clashes.size() - 1);
      final Teamperformance teamperformance = game.getPerformanceOf(this);
      clash.addGame(teamperformance);


      millis = gameMillis;
    }
    return clashes;
  }

  public List<Teamperformance> getCompetitivePerformances() {
    return Teamperformance.get().stream()
        .filter(teamperformance -> teamperformance.getTeam().equals(this) && Util.inRange(teamperformance.getGame().getGameStart()))
        .collect(Collectors.toList());
  }

  public Map<Account, Integer> getLaner(Lane lane) {
    return getCompetitivePerformances().stream()
        .flatMap(teamperformance -> teamperformance.getPlayerperformances().stream())
        .collect(Collectors.toMap(Playerperformance::getAccount,
            playerperformance -> playerperformance.getAccount().getGamesOn(lane, true).size(),
            (a, b) -> b)).entrySet().stream()
        .sorted(Map.Entry.comparingByValue())
        .collect(Collectors.toMap(Map.Entry::getKey,
            Map.Entry::getValue,
            (e1, e2) -> e1, LinkedHashMap::new));
  }

  public League getLastLeague() {
    final Date date = leagues.stream().map(league -> league.getStage().getStageStart().getTime()).max(Date::compareTo).orElse(null);
    return leagues.stream().filter(league -> league.getStage().getStageStart().getTime().equals(date)).findFirst().orElse(null);
  }

  public boolean isValueable() {
    return getLastLeague() != null && getLastLeague().getId() == find(142116).getLastLeague().getId() || scrims;
  }

  public String getLogoUrl() {
    return "https://cdn0.gamesports.net/league_team_logos/" + teamTid / 1000 + "000/" + teamTid + ".jpg";
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

  public Set<League> getLeagues() {
    return leagues;
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
    return getId() == team.getId() && getTeamTid() == team.getTeamTid() && getTeamName().equals(team.getTeamName()) && getTeamAbbr().equals(team.getTeamAbbr()) && Objects.equals(getLeagues(), team.getLeagues()) && Objects.equals(getTeamResult(), team.getTeamResult()) && getTeamperformances().equals(team.getTeamperformances()) && getSchedules().equals(team.getSchedules()) && getMatchesHome().equals(team.getMatchesHome()) && getMatchesGuest().equals(team.getMatchesGuest()) && getPlayers().equals(team.getPlayers());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getTeamTid(), getTeamName(), getTeamAbbr(), getLeagues(), getTeamResult());
  }

  @Override
  public String toString() {
    return "Team{" +
        "id=" + id +
        ", teamTid=" + teamTid +
        ", teamName='" + teamName + '\'' +
        ", teamAbbr='" + teamAbbr + '\'' +
        ", leagues='" + leagues.size() + '\'' +
        ", teamResult='" + teamResult + '\'' +
        '}';
  }
  //</editor-fold>
}