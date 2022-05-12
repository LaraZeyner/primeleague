package de.xeri.prm.models.league;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
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

import de.xeri.prm.game.RiotGameRequester;
import de.xeri.prm.loader.GameIdLoader;
import de.xeri.prm.loader.MatchLoader;
import de.xeri.prm.manager.Data;
import de.xeri.prm.models.enums.LogAction;
import de.xeri.prm.models.enums.Matchstate;
import de.xeri.prm.models.enums.QueueType;
import de.xeri.prm.models.enums.Result;
import de.xeri.prm.models.enums.ScheduleType;
import de.xeri.prm.models.enums.StageType;
import de.xeri.prm.models.match.Game;
import de.xeri.prm.models.match.ScheduledGame;
import de.xeri.prm.util.Const;
import de.xeri.prm.util.HibernateUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NamedQuery;
import org.jetbrains.annotations.Nullable;

@Entity(name = "TurnamentMatch")
@Table(name = "turnament_match", indexes = {
    @Index(name = "matchday", columnList = "matchday"),
    @Index(name = "league", columnList = "league"),
    @Index(name = "home_team", columnList = "home_team"),
    @Index(name = "guest_team", columnList = "guest_team")
})
@NamedQuery(name = "TurnamentMatch.findAll", query = "FROM TurnamentMatch t")
@NamedQuery(name = "TurnamentMatch.findById", query = "FROM TurnamentMatch t WHERE id = :pk")
@NamedQuery(name = "TurnamentMatch.findByTeams", query = "FROM TurnamentMatch t WHERE homeTeam = :home AND guestTeam = :guest")
@ManagedBean
@Getter
@Setter
@NoArgsConstructor
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

  public static TurnamentMatch findTeams(Team home, Team guest) {
    return HibernateUtil.find(TurnamentMatch.class, new String[]{"home", "guest"}, new Object[]{home, guest});
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

  /**
   * @param id matchID
   * @param score matchEndscore
   */
  public TurnamentMatch(int id, String score, Date start) {
    this.id = id;
    this.score = score;
    this.start = start;
    this.state = Matchstate.CREATED;
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

  /**
   * Aktualisiere Matches
   * <ul>
   *   <li><b>Alle Matches:</b> 1 Mal beim Nachtupdate</li>
   *   <li><b>Laufende und recent Matches:</b> Alle 15 Minuten</li>
   *   <li><b>Eigene Liga:</b> Alle 5 Minuten</li>
   *   <li><b>Kommende Matches eigene Liga:</b> Alle 1 Minute</li>
   *   <li><b>Laufende Matches eigene Liga:</b> Alle 30 Sekunden</li>
   * </ul>
   */
  public boolean update() {
    getPlayers().stream().map(Player::getActiveAccount).forEach(GameIdLoader::loadGameIds);
    ScheduledGame.findMode(QueueType.TOURNEY).forEach(RiotGameRequester::loadCompetitive);
    return MatchLoader.analyseMatchPage(this);
  }


  public List<Player> getPlayers() {
    return logEntries.stream()
        .filter(logEntry -> logEntry.getLogAction().equals(LogAction.READY))
        .map(Matchlog::getPlayer)
        .collect(Collectors.toList());
  }

  public boolean isRunning() {
    return !state.equals(Matchstate.CLOSED) && new Date().after(start);
  }

  public boolean isOpen() {
    return getGameAmount() < games.size() || matchday.getStage().getStageType().equals(StageType.PLAYOFFS) && !score.contains("2");
  }

  public boolean isRecently() {
    final Date openingDate = new Date(matchday.getStart().getTime() - 7 * Const.MILLIS_PER_DAY);
    final Date closingDate = new Date(matchday.getEnd().getTime() + 14 * Const.MILLIS_PER_DAY);
    return new Date().before(closingDate) && new Date().after(openingDate);
  }

  public boolean isNotClosed() {
    final long limit = matchday.getEnd().getTime() + Const.DAYS_UNTIL_MATCH_CLOSED * Const.MILLIS_PER_DAY;
    return !state.equals(Matchstate.CLOSED) || (isOpen() && start.before(new Date(limit)));
  }

  @Nullable
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

  public byte getPointsOfTeam(Team team) {
    if (homeTeam.equals(team) || guestTeam.equals(team)) {
      if (score.equals("-:-")) {
        if (state.ordinal() < 10) {
          return 0;
        } else {
          return (byte) logEntries.stream()
              .filter(logEntry -> logEntry.getLogAction().equals(LogAction.REPORT))
              .map(Matchlog::getTeam)
              .filter(winningTeam -> winningTeam != null && winningTeam.equals(team))
              .count();
        }

      } else if (homeTeam.equals(team)) {
        final String team1String = score.split(":")[0];
        return (byte) Integer.parseInt(team1String);

      } else if (guestTeam.equals(team)) {
        final String team2String = score.split(":")[1];
        return (byte) Integer.parseInt(team2String);
      }
    }
    return 0;
  }

  public Result getResult(Team team) {
    if (homeTeam.equals(team) || guestTeam.equals(team)) {
      if (score.equals("-:-")) {
        return Result.UPCOMING;
      } else if (score.equals("0:0")) {
        return Result.CLOSED;
      } else if (score.contains(":")) {
        final String team1String = score.split(":")[0];
        final int team1Score = Integer.parseInt(team1String);

        final String team2String = score.split(":")[1];
        final int team2Score = Integer.parseInt(team2String);

        if (team1Score > team2Score) {
          return team.equals(homeTeam) ? Result.VICTORY : Result.DEFEAT;
        } else if (team1Score < team2Score) {
          return team.equals(guestTeam) ? Result.VICTORY : Result.DEFEAT;
        } else {
          return Result.TIE;
        }
      }
    }

    return null;
  }

  public boolean hasTeam(Team team) {
    return homeTeam != null && homeTeam.equals(team) || guestTeam != null && guestTeam.equals(team);
  }

  public boolean hasChanged(Date start) {
    return start.equals(this.start);
  }

  //<editor-fold desc="getter and setter">
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof TurnamentMatch)) return false;
    final TurnamentMatch turnamentMatch = (TurnamentMatch) o;
    return getId() == turnamentMatch.getId() && getLeague().equals(turnamentMatch.getLeague()) && getMatchday().equals(turnamentMatch.getMatchday()) && getStart().equals(turnamentMatch.getStart()) && Objects.equals(getHomeTeam(), turnamentMatch.getHomeTeam()) && Objects.equals(getGuestTeam(), turnamentMatch.getGuestTeam());
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
        '}';
  }
  //</editor-fold>
}