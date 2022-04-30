package de.xeri.league.models.league;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
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

import de.xeri.league.loader.TeamLoader;
import de.xeri.league.models.enums.Lane;
import de.xeri.league.models.enums.QueueType;
import de.xeri.league.models.enums.StageType;
import de.xeri.league.models.match.Game;
import de.xeri.league.models.match.Playerperformance;
import de.xeri.league.models.match.Teamperformance;
import de.xeri.league.util.Data;
import de.xeri.league.util.HibernateUtil;
import de.xeri.league.util.Util;
import de.xeri.league.util.logger.Logger;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NamedQuery;

@Entity(name = "Team")
@Table(name = "team", indexes = {
    @Index(name = "team_tId", columnList = "team_tId", unique = true),
    @Index(name = "team_abbr", columnList = "team_abbr"),
    @Index(name = "team_name", columnList = "team_name")
})
@NamedQuery(name = "Team.findAll", query = "FROM Team t")
@NamedQuery(name = "Team.findById", query = "FROM Team t WHERE id = :pk")
@NamedQuery(name = "Team.findBy", query = "FROM Team t WHERE teamName = :name")
@NamedQuery(name = "Team.findByTId", query = "FROM Team t WHERE turneyId = :tid")
@Getter
@Setter
@NoArgsConstructor
public class Team implements Serializable {

  @Transient
  private static final long serialVersionUID = 2656015802095877363L;

  public static Set<Team> get() {
    return new LinkedHashSet<>(HibernateUtil.findList(Team.class));
  }

  public static Team get(Team neu) {
    if (hasTId(neu.getTurneyId())) {
      final Team team = findTid(neu.getTurneyId());
      team.setTeamAbbr(neu.teamAbbr);
      team.setTeamName(neu.teamName);
      return team;
    }

    Data.getInstance().save(neu);
    return neu;
  }

  public static boolean has(short id) {
    return HibernateUtil.has(Team.class, id);
  }

  public static boolean hasTId(int tid) {
    return HibernateUtil.has(Team.class, new String[]{"tid"}, new Object[]{tid}, "findByTId");
  }

  public static boolean has(String name) {
    return HibernateUtil.has(Team.class, new String[]{"name"}, new Object[]{name});
  }

  public static boolean has(String name, League league) {
    return findAll(name).stream().anyMatch(team -> team.getLeagues().contains(league));
  }

  public static Team find(String name, League league) {
    final Logger logger = Logger.getLogger("Team-Finder");
    if (has(name, league)) {
      final List<Team> teams = findAll(name).stream()
          .filter(team -> team.getLeagues().contains(league))
          .collect(Collectors.toList());
      if (teams.size() > 1) {
        logger.config("Meherere Teams gefunden");
      }
      return teams.get(0);
    }

    if (has(name)) {
      return find(name);
    } else {
      logger.warning("Team konnte nicht gefunden werden", name);
      return null;
    }
  }

  public static Team find(String name) {
    return HibernateUtil.find(Team.class, new String[]{"name"}, new Object[]{name});
  }

  public static List<Team> findAll(String name) {
    return HibernateUtil.findList(Team.class, new String[]{"name"}, new Object[]{name});
  }

  public static Team findTid(int tid) {
    return HibernateUtil.find(Team.class, new String[]{"tid"}, new Object[]{tid}, "findByTId");
  }

  public static Team find(int id) {
    return HibernateUtil.find(Team.class, id);
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
  private int turneyId;

  @Column(name = "team_name", nullable = false, length = 100)
  private String teamName;

  @Column(name = "team_abbr", nullable = false, length = 25)
  private String teamAbbr;

  @ManyToMany(mappedBy = "teams")
  private final Set<League> leagues = new LinkedHashSet<>();

  @Column(name = "team_result", length = 30)
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

  public Team(int turneyId, String teamName, String teamAbbr) {
    this.turneyId = turneyId;
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
    final List<Game> clashGames = teamperformances.stream()
        .map(Teamperformance::getGame)
        .filter(game -> game.isQueue(QueueType.CLASH))
        .sorted(Comparator.comparingLong(game -> game.getGameStart().getTime()))
        .collect(Collectors.toList());
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

  public List<Account> getLaner(Lane lane) {
    return getCompetitivePerformances().stream()
        .flatMap(teamperformance -> teamperformance.getPlayerperformances().stream())
        .map(Playerperformance::getAccount)
        .filter(account -> !account.getGamesOn(lane, true).isEmpty())
        .sorted((account1, account2) -> account2.getGamesOn(lane, true).size() - account1.getGamesOn(lane, true).size())
        .collect(Collectors.toList());

  }

  public League getLastLeague() {
    final League league = leagues.stream()
        .filter(league1 -> league1.getStage().getStageType().equals(StageType.GRUPPENPHASE))
        .max(Comparator.comparingLong(l -> l.getStage().getStageEnd().getTimeInMillis()))
        .orElse(null);
    if (league == null) {
      final Team team = TeamLoader.handleSeason(this);
      if (team == null) {
        return null;
      }
      return team.getLastLeague(1);
    }
    return league;

  }

  private League getLastLeague(int loop) {
    return leagues.stream()
        .max(Comparator.comparingLong(l -> l.getStage().getStageEnd().getTimeInMillis()))
        .orElse(null);
  }

  public boolean isValueable() {
    return Data.getInstance().getCurrentGroup().getTeams().contains(this) || scrims;
  }

  public String getLogoUrl() {
    return "https://cdn0.gamesports.net/league_team_logos/" + turneyId / 1000 + "000/" + turneyId + ".jpg";
  }

  public Set<TurnamentMatch> getTurnamentMatches() {
    final Set<TurnamentMatch> allmatches = new LinkedHashSet<>(matchesHome);
    allmatches.addAll(matchesGuest);
    return allmatches;
  }

  //<editor-fold desc="getter and setter">

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Team)) return false;
    final Team team = (Team) o;
    return getId() == team.getId() && getTurneyId() == team.getTurneyId();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getTurneyId(), getTeamName(), getTeamAbbr(), getLeagues(), getTeamResult());
  }

  @Override
  public String toString() {
    return "Team{" +
        "id=" + id +
        ", teamTid=" + turneyId +
        ", teamName='" + teamName + '\'' +
        ", teamAbbr='" + teamAbbr + '\'' +
        ", leagues='" + leagues.size() + '\'' +
        ", teamResult='" + teamResult + '\'' +
        '}';
  }
  //</editor-fold>
}