package de.xeri.prm.models.league;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
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
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.prm.game.Clash;
import de.xeri.prm.loader.TeamLoader;
import de.xeri.prm.manager.PrimeData;
import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.enums.QueueType;
import de.xeri.prm.models.enums.StageType;
import de.xeri.prm.models.match.Game;
import de.xeri.prm.models.match.Teamperformance;
import de.xeri.prm.models.match.playerperformance.Playerperformance;
import de.xeri.prm.servlet.datatables.league.LeagueTeam;
import de.xeri.prm.servlet.datatables.scheduling.InventoryStatus;
import de.xeri.prm.util.Const;
import de.xeri.prm.util.HibernateUtil;
import de.xeri.prm.util.Util;
import de.xeri.prm.util.logger.Logger;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import org.hibernate.Hibernate;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.query.Query;

@Entity(name = "Team")
@Table(name = "team", indexes = {
    @Index(name = "team_tId", columnList = "team_tId", unique = true),
    @Index(name = "team_abbr", columnList = "team_abbr"),
    @Index(name = "team_name", columnList = "team_name")
})
@NamedQuery(name = "Team.findAll", query = "FROM Team t")
@NamedQuery(name = "Team.findById", query = "FROM Team t WHERE id = :pk")
@NamedQuery(name = "Team.findBy", query = "FROM Team t WHERE teamName = :name")
@NamedQuery(name = "Team.findByScrim", query = "FROM Team t WHERE scrims = :scrims")
@NamedQuery(name = "Team.findByTId", query = "FROM Team t WHERE turneyId = :tid")
@NamedQuery(name = "Team.valueableTeams", query = "FROM Team t WHERE scrims = true OR id IN :teamIds")
@NamedQuery(name = "Team.scrimTeams", query = "FROM Team t WHERE scrims = true")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Team implements Serializable {
  @Transient
  private static final long serialVersionUID = 2656015802095877363L;

  private static List<Team> valueableTeams;
  private static List<Team> scrimTeams;

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

    PrimeData.getInstance().save(neu);
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

  public static List<Team> getValueableTeams() {
    if (valueableTeams == null) {
      valueableTeams = HibernateUtil.findList(Team.class, new String[]{"teamIds"},
          new Object[]{PrimeData.getInstance().getCurrentGroup().getTeams().stream().map(Team::getId).collect(Collectors.toList())}, "valueableTeams");
    }
    return valueableTeams;
  }

  public static List<Team> getScrimTeams() {
    if (scrimTeams == null) {
      final Query namedQuery = PrimeData.getInstance().getSession().getNamedQuery("Team.scrimTeams");
      scrimTeams = namedQuery.list();
    }
    return scrimTeams;
  }

  public static Team find(String name, League league) {
    final Logger logger = Logger.getLogger("Team-Finder");
    final List<Team> teams = findAll(name);
    if (!teams.isEmpty()) {
      if (teams.size() > 1) {
        return league.getTeams().stream().filter(teams::contains).findFirst().orElse(null);
      }
      return teams.get(0);
    }
    logger.warning("Team konnte nicht gefunden werden", name);
    return null;
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

  public static List<Team> findScrim() {
    return HibernateUtil.findList(Team.class, new String[]{"scrims"}, new Object[]{true}, "findByScrim");
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
  @LazyCollection(LazyCollectionOption.EXTRA)
  @OrderColumn
  @ToString.Exclude
  private final Set<League> leagues = new LinkedHashSet<>();

  @Column(name = "team_result", length = 30)
  private String teamResult;

  @Column(name = "scrims")
  private boolean scrims;

  @OneToMany(mappedBy = "team")
  @ToString.Exclude
  private final Set<Teamperformance> teamperformances = new LinkedHashSet<>();

  @OneToMany(mappedBy = "enemyTeam")
  @LazyCollection(LazyCollectionOption.EXTRA)
  @OrderColumn
  @ToString.Exclude
  private final Set<Schedule> schedules = new LinkedHashSet<>();

  @OneToMany(mappedBy = "homeTeam")
  @LazyCollection(LazyCollectionOption.EXTRA)
  @OrderColumn
  @ToString.Exclude
  private final Set<TurnamentMatch> matchesHome = new LinkedHashSet<>();

  @OneToMany(mappedBy = "guestTeam")
  @LazyCollection(LazyCollectionOption.EXTRA)
  @OrderColumn
  @ToString.Exclude
  private final Set<TurnamentMatch> matchesGuest = new LinkedHashSet<>();

  @OneToMany(mappedBy = "team")
  @LazyCollection(LazyCollectionOption.EXTRA)
  @OrderColumn
  @ToString.Exclude
  private final Set<Player> players = new LinkedHashSet<>();

  @OneToMany(mappedBy = "team")
  @LazyCollection(LazyCollectionOption.EXTRA)
  @OrderColumn
  @ToString.Exclude
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

  public void removeSchedule(Schedule schedule) {
    schedules.remove(schedule);
    schedule.setEnemyTeam(null);
    PrimeData.getInstance().remove(schedule);
  }

  public boolean addMatch(TurnamentMatch match, boolean home) {
    boolean changed = false;
    if (home) {
      if (!matchesHome.contains(match)) {
        changed = true;
      }
      matchesHome.add(match);
      match.setHomeTeam(this);
    } else {
      if (!matchesGuest.contains(match)) {
        changed = true;
      }
      matchesGuest.add(match);
      match.setGuestTeam(this);
    }
    return changed;
  }

  public Player addPlayer(Player player) {
    val oldTeam = player.getTeam();
    if (oldTeam != null && !this.equals(oldTeam)) {
      oldTeam.removePlayer(player);
    }
    return Player.get(player, this);
  }

  private void removePlayer(Player player) {
    players.remove(player);
    PrimeData.getInstance().save(this);
  }

  public void addLogEntry(Matchlog entry) {
    logEntries.add(entry);
    entry.setTeam(this);
  }

  public List<Clash> getClashDays() {
    val clashGames = teamperformances.stream()
        .map(Teamperformance::getGame)
        .filter(game -> game.isQueue(QueueType.CLASH))
        .sorted(Comparator.comparingLong(game -> game.getGameStart().getTime()))
        .collect(Collectors.toList());
    val clashes = new ArrayList<Clash>();
    long millis = 0;
    for (Game game : clashGames) {
      final long gameMillis = game.getGameStart().getTime();
      final long difference = gameMillis - millis;
      if (difference > 43_200_000L) { // 12 Stunden
        clashes.add(new Clash(game.getGameStart(), this));
      }
      val clash = clashes.get(clashes.size() - 1);
      val teamperformance = game.getPerformanceOf(this);
      clash.addGame(teamperformance);
      millis = gameMillis;
    }
    return clashes;
  }

  public List<Teamperformance> getCompetitivePerformances() {
    return teamperformances.stream()
        .filter(teamperformance -> Util.inRange(teamperformance.getGame().getGameStart()))
        .collect(Collectors.toList());
  }

  public List<Teamperformance> getLeaguePerformances() {
    return teamperformances.stream()
        .filter(teamperformance -> teamperformance.getGame().getTurnamentmatch() != null &&
            teamperformance.getGame().getTurnamentmatch().getLeague().equals(PrimeData.getInstance().getCurrentGroup()))
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
    return getValueableTeams().contains(this) || scrims;
  }

  public String getLogoUrl() {
    return "https://cdn0.gamesports.net/league_team_logos/" + turneyId / 1000 + "000/" + turneyId + ".jpg";
  }

  public Set<TurnamentMatch> getTurnamentMatches() {
    final Set<TurnamentMatch> allmatches = new LinkedHashSet<>(matchesHome);
    allmatches.addAll(matchesGuest);
    return allmatches;
  }

  public String getStatus() {
    return "ACTIVE";
  }

  public InventoryStatus getStatusTag() {
    if (getStatus().equals("ACTIVE")) {
      return InventoryStatus.INSTOCK;
    } else if (getStatus().equals("INACTIVE")) {
      return InventoryStatus.LOWSTOCK;
    }
    return InventoryStatus.OUTOFSTOCK;
  }

  public TurnamentMatch getClosestTurnamentMatch(Date date) {
    long min = Long.MAX_VALUE;
    TurnamentMatch match = null;
    for (final TurnamentMatch turnamentMatch : getTurnamentMatches()) {
      if (turnamentMatch.isOpen() && turnamentMatch.getStart().before(date)) {
        final long diff = Math.abs(turnamentMatch.getStart().getTime() + turnamentMatch.getGames().size() * 2_700_000L - date.getTime());
        if (diff < min) {
          min = diff;
          match = turnamentMatch;
        }
      }
    }

    return match;
  }

  public String getBilance() {
    return getBilanceFor(Team.findTid(Const.TEAMID));
  }

  public String getBilanceFor(Team team) {
    final Query<Object[]> query = PrimeData.getInstance().getSession().getNamedQuery("Teamperformance.gamesOfTeam");
    query.setParameter("team", team);
    List<String> gameIds = query.list().stream().map(object -> String.valueOf(object[0])).collect(Collectors.toList());
    final Query<Object[]> query2 = PrimeData.getInstance().getSession().getNamedQuery("Teamperformance.bilanceOfTeamMatchup");
    query2.setParameter("gameids", gameIds);
    query2.setParameter("teamId", id);
    final List<Object[]> list2 = query2.list();
    return Util.getInt(list2.get(0)[0]) + ":" + Util.getInt(list2.get(0)[1]);
  }


  public LeagueTeam getLeagueTeam() {
    final Query<Object[]> query = PrimeData.getInstance().getSession().getNamedQuery("TurnamentMatch.findPerformancesOf");
    query.setParameter("team", this);
    query.setParameter("league", PrimeData.getInstance().getCurrentGroup());
    final Object[] list = query.list().get(0);

    final Query<Integer> query2 = PrimeData.getInstance().getSession().getNamedQuery("TurnamentMatch.findMatchesOf");
    query2.setParameter("team", this);
    query2.setParameter("league", PrimeData.getInstance().getCurrentGroup());
    final List<Integer> matchIds = query2.list();

    final Query<Object[]> query3 = PrimeData.getInstance().getSession().getNamedQuery("Teamperformance.teamOwn");
    query3.setParameter("team", this);
    query3.setParameter("matches", matchIds);
    final List<Object[]> list2 = query3.list();
    List<Double> doubles = Arrays.stream(list2.get(0)).map(o -> o != null ? Double.parseDouble(String.valueOf(o)) : 0).collect(Collectors.toList());

    LeagueTeam team = new LeagueTeam(turneyId, teamName, teamAbbr, getLogoUrl(),
        Util.longToInt((long) list[0]) + Util.longToInt((long) list[1]) + Util.longToInt((long) list[2]),
        Util.longToInt((long) list[0]), Util.longToInt((long) list[1]), Util.longToInt((long) list[2]));

    team.add(doubles);

    return team;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    final Team team = (Team) o;
    return Objects.equals(id, team.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}