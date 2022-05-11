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

import de.xeri.league.game.Clash;
import de.xeri.league.loader.TeamLoader;
import de.xeri.league.manager.Data;
import de.xeri.league.models.enums.Lane;
import de.xeri.league.models.enums.QueueType;
import de.xeri.league.models.enums.Result;
import de.xeri.league.models.enums.StageType;
import de.xeri.league.models.match.Game;
import de.xeri.league.models.match.Teamperformance;
import de.xeri.league.models.match.playerperformance.Playerperformance;
import de.xeri.league.util.HibernateUtil;
import de.xeri.league.util.Util;
import de.xeri.league.util.logger.Logger;
import lombok.AllArgsConstructor;
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
@NamedQuery(name = "Team.findByScrim", query = "FROM Team t WHERE scrims = :scrims")
@NamedQuery(name = "Team.findByTId", query = "FROM Team t WHERE turneyId = :tid")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    return teamperformances.stream()
        .filter(teamperformance -> Util.inRange(teamperformance.getGame().getGameStart()))
        .collect(Collectors.toList());
  }

  public List<Teamperformance> getLeaguePerformances() {
    return teamperformances.stream()
        .filter(teamperformance -> teamperformance.getGame().getTurnamentmatch().getLeague().equals(Data.getInstance().getCurrentGroup()))
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

  public String getBilance() {
    int wins = 0;
    int ties = 0;
    int defeats = 0;
    for (TurnamentMatch turnamentMatch : getTurnamentMatches()) {
      if (turnamentMatch.getLeague().equals(Data.getInstance().getCurrentGroup())) {
        final Result result = turnamentMatch.getResult(this);
        if (result.equals(Result.VICTORY)) {
          wins++;
        } else if (result.equals(Result.TIE)) {
          ties++;
        } else if (result.equals(Result.DEFEAT)) {
          defeats++;
        }
      }
    }

    return wins + "   " + ties + "   " + defeats;
  }

  public String getWinsPerMatch() {
    int wins = 0;
    int ties = 0;
    int defeats = 0;
    int score1 = 0;
    for (TurnamentMatch turnamentMatch : getTurnamentMatches()) {
      if (turnamentMatch.getLeague().equals(Data.getInstance().getCurrentGroup())) {
        final Result result = turnamentMatch.getResult(this);
        if (result.equals(Result.VICTORY)) {
          wins++;
        } else if (result.equals(Result.TIE)) {
          ties++;
        } else if (result.equals(Result.DEFEAT)) {
          defeats++;
        }
        score1 += turnamentMatch.getPointsOfTeam(this);
      }
    }
    final int games = wins + ties + defeats;
    final double winsPerMatch = games == 0 ? 0 : score1 * 1d / games;
    return Math.round(winsPerMatch * 50) + "%";
  }

  public String getTeamScore() {
    int score1 = 0;
    int score2 = 0;
    for (TurnamentMatch turnamentMatch : getTurnamentMatches()) {
      if (turnamentMatch.getLeague().equals(Data.getInstance().getCurrentGroup())) {
        score1 += turnamentMatch.getPointsOfTeam(this);
        final Team otherTeam = turnamentMatch.getOtherTeam(this);
        if (otherTeam != null) {
          score2 += turnamentMatch.getPointsOfTeam(otherTeam);
        }
      }
    }
    return score1 + ":" + score2;
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


  public String getMatchtime() {
    int time = (int) getLeaguePerformances().stream()
        .mapToInt(leaguePerformance -> leaguePerformance.getGame().getDuration())
        .average().orElse(0);
    return time / 60 + (time % 60 < 10 ? ":0" : ":") + time % 60;
  }

  public String getWintime() {
    int time = (int) getLeaguePerformances().stream()
        .filter(Teamperformance::isWin)
        .mapToInt(leaguePerformance -> leaguePerformance.getGame().getDuration())
        .average().orElse(0);
    return time / 60 + (time % 60 < 10 ? ":0" : ":") + time % 60;
  }

  public String getLosetime() {
    int time = (int) getLeaguePerformances().stream()
        .filter(teamperformance -> !teamperformance.isWin())
        .mapToInt(leaguePerformance -> leaguePerformance.getGame().getDuration())
        .average().orElse(0);
    return time / 60 + (time % 60 < 10 ? ":0" : ":") + time % 60;
  }

  public int getGames() {
    return getWins() + getLosses();
  }

  public int getWins() {
    return (int) getLeaguePerformances().stream().filter(Teamperformance::isWin).count();
  }

  public int getLosses() {
    return (int) getLeaguePerformances().stream().filter(teamperformance -> !teamperformance.isWin()).count();
  }

  public String getKills() {
    return getLeaguePerformances().stream().mapToInt(Teamperformance::getTotalKills).sum() + ":" +
        getLeaguePerformances().stream().map(Teamperformance::getOtherTeamperformance)
            .filter(Objects::nonNull).mapToInt(Teamperformance::getTotalKills).sum();
  }

  public int getKillDiff() {
    return getLeaguePerformances().stream().mapToInt(Teamperformance::getTotalKills).sum() -
        getLeaguePerformances().stream().map(Teamperformance::getOtherTeamperformance)
            .filter(Objects::nonNull).mapToInt(Teamperformance::getTotalKills).sum();
  }

  public String getKillsPerMatch() {
    return getLeaguePerformances().stream().mapToInt(Teamperformance::getTotalKills).average().orElse(0) * 2 + ":" +
        getLeaguePerformances().stream().map(Teamperformance::getOtherTeamperformance)
            .mapToInt(Teamperformance::getTotalKills).average().orElse(0) * 2;
  }

  public String getGold() {
    return getLeaguePerformances().stream().mapToInt(Teamperformance::getTotalGold).sum() + ":" +
        getLeaguePerformances().stream().map(Teamperformance::getOtherTeamperformance)
            .filter(Objects::nonNull).mapToInt(Teamperformance::getTotalGold).sum();
  }

  public int getGoldDiff() {
    return getLeaguePerformances().stream().mapToInt(Teamperformance::getTotalGold).sum() -
        getLeaguePerformances().stream().map(Teamperformance::getOtherTeamperformance)
            .filter(Objects::nonNull).mapToInt(Teamperformance::getTotalGold).sum();
  }

  public String getGoldPerMatch() {
    return getLeaguePerformances().stream().mapToInt(Teamperformance::getTotalGold).average().orElse(0) * 2 + ":" +
        getLeaguePerformances().stream().map(Teamperformance::getOtherTeamperformance)
            .mapToInt(Teamperformance::getTotalGold).average().orElse(0) * 2;
  }


  public String getCreeps() {
    return getLeaguePerformances().stream().mapToInt(Teamperformance::getTotalCs).sum() + ":" +
        getLeaguePerformances().stream().map(Teamperformance::getOtherTeamperformance)
            .filter(Objects::nonNull).mapToInt(Teamperformance::getTotalCs).sum();
  }

  public int getCreepDiff() {
    return getLeaguePerformances().stream().mapToInt(Teamperformance::getTotalCs).sum() -
        getLeaguePerformances().stream().map(Teamperformance::getOtherTeamperformance)
            .filter(Objects::nonNull).mapToInt(Teamperformance::getTotalCs).sum();
  }

  public String getCreepsPerMatch() {
    return getLeaguePerformances().stream().mapToInt(Teamperformance::getTotalCs).average().orElse(0) * 2 + ":" +
        getLeaguePerformances().stream().map(Teamperformance::getOtherTeamperformance)
            .mapToInt(Teamperformance::getTotalCs).average().orElse(0) * 2;
  }

  public int getObjectives() {
    return getTowers() + getDrakes() + getHeralds() + getInhibs() + getBarons();
  }

  public int getObjectivesPerMatch() {
    if (getGames() == 0) {
      return 0;
    }
    return getObjectives() * 2 / getGames();
  }


  public int getTowers() {
    return getLeaguePerformances().stream().mapToInt(Teamperformance::getTowers).sum();
  }

  public int getTowersPerMatch() {
    return (int) Util.div(getTowers() * 2, getGames());
  }

  public int getDrakes() {
    return getLeaguePerformances().stream().mapToInt(Teamperformance::getDrakes).sum();
  }

  public int getDrakesPerMatch() {
    return (int) Util.div(getDrakes() * 2, getGames());
  }

  public int getInhibs() {
    return getLeaguePerformances().stream().mapToInt(Teamperformance::getInhibs).sum();
  }

  public int getInhibsPerMatch() {
    return (int) Util.div(getInhibs() * 2, getGames());
  }

  public int getHeralds() {
    return getLeaguePerformances().stream().mapToInt(Teamperformance::getHeralds).sum();
  }

  public int getHeraldsPerMatch() {
    return (int) Util.div(getHeralds() * 2, getGames());
  }

  public int getBarons() {
    return getLeaguePerformances().stream().mapToInt(Teamperformance::getBarons).sum();
  }

  public int getBaronsPerMatch() {
    return (int) Util.div(getBarons() * 2, getGames());
  }

  public long getScore() {
    final int idScore = id;                                      //                -10.000   10.000
    final int csScore = getCreepDiff() * 10_000;                             //             10.000.000    1.000
    final long goldScore = getGoldDiff() * 10_000_000L;                      //      1.000.000.000.000  100.000
    final long killScore = getKillDiff() * 1_000_000_000_000L;               //    100.000.000.000.000      100
    final long winsScore = (long) Integer.parseInt(getWinsPerMatch().replace("%", "")) * 1_000_000_000_000L;
    return idScore + csScore + goldScore + killScore + winsScore;
  }


}