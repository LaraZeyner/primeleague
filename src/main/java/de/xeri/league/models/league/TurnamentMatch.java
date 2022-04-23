package de.xeri.league.models.league;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import de.xeri.league.models.enums.Matchstate;
import de.xeri.league.models.enums.ScheduleType;
import de.xeri.league.models.enums.StageType;
import de.xeri.league.models.match.Game;
import de.xeri.league.util.Const;
import de.xeri.league.util.Data;
import de.xeri.league.util.HibernateUtil;
import org.hibernate.annotations.NamedQuery;

@Entity(name = "TurnamentMatch")
@Table(name = "turnament_match", indexes = {
    @Index(name = "matchday", columnList = "matchday"),
    @Index(name = "league", columnList = "league"),
    @Index(name = "home_team", columnList = "home_team"),
    @Index(name = "guest_team", columnList = "guest_team")
})
@NamedQuery(name = "TurnamentMatch.findAll", query = "FROM TurnamentMatch t")
@NamedQuery(name = "TurnamentMatch.findById", query = "FROM TurnamentMatch t WHERE id = :pk")
public class TurnamentMatch implements Serializable {

  @Transient
  private static final long serialVersionUID = -5623549707585401516L;

  public static Set<TurnamentMatch> get() {
    return new LinkedHashSet<>(HibernateUtil.findList(TurnamentMatch.class));
  }

  public static TurnamentMatch get(TurnamentMatch neu, League league, Matchday matchday) {
    if (has(neu.getId())) {
      final TurnamentMatch turnamentMatch = find(neu.getId());
      turnamentMatch.setScore(turnamentMatch.getScore());
      return turnamentMatch;
    }
    league.addMatch(neu);
    matchday.addMatch(neu);
    Data.getInstance().save(neu);
    return neu;
  }

  public static boolean has(int id) {
    return HibernateUtil.has(TurnamentMatch.class, id);
  }

  public static TurnamentMatch find(int id) {
    return HibernateUtil.find(TurnamentMatch.class, id);
  }

  @Id
  @Column(name = "match_id", nullable = false)
  private int id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "league")
  private League league;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "matchday")
  private Matchday matchday;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "match_start", nullable = false)
  private Date start;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "home_team")
  private Team homeTeam;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "guest_team")
  private Team guestTeam;

  @Column(name = "score", nullable = false, length = 3)
  private String score;

  @Enumerated(EnumType.STRING)
  @Column(name = "matchstate", nullable = false, length = 23)
  private Matchstate state;

  @OneToMany(mappedBy = "turnamentmatch")
  private final Set<Game> games = new LinkedHashSet<>();

  @OneToMany(mappedBy = "match")
  private final Set<Matchlog> logEntries = new LinkedHashSet<>();

  // default constructor
  public TurnamentMatch() {
  }

  /**
   * Usage: Contructor — setStart() — setState() — Foreach: addGame(), addEntry()
   * @param id matchID
   * @param score matchEndscore
   */
  public TurnamentMatch(int id, String score) {
    this.id = id;
    this.score = score;
  }

  public void addGame(Game game) {
    games.add(game);
    game.setTurnamentmatch(this);
  }

  public Matchlog addEntry(Matchlog entry) {
    return Matchlog.get(entry, this);
  }

  public int getGameAmount() {
    final StageType stageType = matchday.getStage().getStageType();
    if (matchday.getType().equals("Spieltag 8") || stageType.equals(StageType.KALIBRIERUNGSPHASE)) {
      return 1;
    } else if (stageType.equals(StageType.GRUPPENPHASE)) {
      return 2;
    } else if (stageType.equals(StageType.PLAYOFFS)) {
      return 3;
    }
    return 10;
  }

  public boolean isOpen() {
    return getGameAmount() < games.size();
  }

  public boolean isNotClosed() {
    final long limit = matchday.getEnd().getTime() + Const.DAYS_UNTIL_MATCH_CLOSED * Const.MILLIS_PER_DAY;
    return !state.equals(Matchstate.CLOSED) || (isOpen() && start.before(new Date(limit)));
  }

  public Team getOtherTeam(Team team) {
    return team.equals(homeTeam) ? guestTeam : team.equals(guestTeam) ? homeTeam : null;
  }

  public ScheduleType getScheduleType() {
    final String s = matchday.getType().split(" ")[1];
    if (matchday.getStage().getStageType().equals(StageType.GRUPPENPHASE)) {
      return s.equals("8") && !league.getName().contains("Starter") ? ScheduleType.TIEBREAKER : ScheduleType.valueOf("SPIELTAG_" + s);
    } else if (matchday.getStage().getStageType().equals(StageType.PLAYOFFS)) {
      return ScheduleType.valueOf("PLAYOFF_" + s);
    } else if (matchday.getStage().getStageType().equals(StageType.KALIBRIERUNGSPHASE)) {
      return ScheduleType.valueOf("VORRUNDE_" + s);
    }
    return null;
  }

  public boolean hasTeam(Team team) {
    return homeTeam.equals(team) || guestTeam.equals(team);
  }

  //<editor-fold desc="getter and setter">
  public Set<Game> getGames() {
    return games;
  }

  public Matchstate getState() {
    return state;
  }

  public void setState(Matchstate matchstate) {
    this.state = matchstate;
  }

  public String getScore() {
    return score;
  }

  public void setScore(String score) {
    this.score = score;
  }

  public Team getGuestTeam() {
    return guestTeam;
  }

  void setGuestTeam(Team guestTeam) {
    this.guestTeam = guestTeam;
  }

  public Team getHomeTeam() {
    return homeTeam;
  }

  void setHomeTeam(Team homeTeam) {
    this.homeTeam = homeTeam;
  }

  public Date getStart() {
    return start;
  }

  public void setStart(Date matchStart) {
    this.start = matchStart;
  }

  public Matchday getMatchday() {
    return matchday;
  }

  public void setMatchday(Matchday matchday) {
    this.matchday = matchday;
  }

  public League getLeague() {
    return league;
  }

  public void setLeague(League league) {
    this.league = league;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Set<Matchlog> getLogEntries() {
    return logEntries;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof TurnamentMatch)) return false;
    final TurnamentMatch turnamentMatch = (TurnamentMatch) o;
    return getId() == turnamentMatch.getId() && getLeague().equals(turnamentMatch.getLeague()) && getMatchday().equals(turnamentMatch.getMatchday()) && getStart().equals(turnamentMatch.getStart()) && Objects.equals(getHomeTeam(), turnamentMatch.getHomeTeam()) && Objects.equals(getGuestTeam(), turnamentMatch.getGuestTeam()) && getScore().equals(turnamentMatch.getScore()) && getState() == turnamentMatch.getState() && Objects.equals(getGames(), turnamentMatch.getGames()) && logEntries.equals(turnamentMatch.logEntries);
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getLeague(), getMatchday(), getStart(), getHomeTeam(), getGuestTeam(), getScore(), getState());
  }

  @Override
  public String toString() {
    return "TurnamentMatch{" +
        "id=" + id +
        ", league=" + league +
        ", matchday=" + matchday +
        ", start=" + start +
        ", homeTeam=" + homeTeam +
        ", guestTeam=" + guestTeam +
        ", score='" + score + '\'' +
        ", state=" + state +
        ", games=" + games.size() +
        ", matchlog=" + logEntries.size() +
        '}';
  }
  //</editor-fold>
}