package de.xeri.prm.models.match;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import de.xeri.prm.manager.PrimeData;
import de.xeri.prm.models.dynamic.Champion;
import de.xeri.prm.models.enums.QueueType;
import de.xeri.prm.models.league.Account;
import de.xeri.prm.models.league.Team;
import de.xeri.prm.models.league.TurnamentMatch;
import de.xeri.prm.models.match.playerperformance.Playerperformance;
import de.xeri.prm.util.Const;
import de.xeri.prm.util.HibernateUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.NamedQuery;

@Entity(name = "Game")
@Table(name = "game", indexes = @Index(name = "turnamentmatch", columnList = "turnamentmatch"))
@NamedQuery(name = "Game.findAll", query = "FROM Game g")
@NamedQuery(name = "Game.findById", query = "FROM Game g WHERE id = :pk")
@NamedQuery(name = "Game.findByTourney", query = "FROM Game g WHERE gametype < :type")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Game implements Serializable {

  @Transient
  private static final long serialVersionUID = 4639052028429524051L;

  public static Game get(Game neu, Gametype gametype) {
    final Game game = find(neu.getId());
    if (game != null) {
      game.setGameStart(neu.getGameStart());
      game.setDuration(neu.getDuration());
      return game;
    }
    gametype.getGames().add(neu);
    neu.setGametype(gametype);
    PrimeData.getInstance().save(neu);
    return neu;
  }

  public static boolean has(String id) {
    return HibernateUtil.has(Game.class, id);
  }

  public static Game find(String id) {
    return HibernateUtil.find(Game.class, id);
  }


  public List<GamePause> getNotClosed() {
    return HibernateUtil.findList(GamePause.class, new String[]{"game", "end"}, new Object[]{this, (long) 0}, "findByEnd");
  }

  public List<GamePause> getNotOpened() {
    return HibernateUtil.findList(GamePause.class, new String[]{"game", "start"}, new Object[]{this, (long) 0});
  }

  @Id
  @Check(constraints = "game_id REGEXP ('^EUW')")
  @Column(name = "game_id", nullable = false, length = 16)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "turnamentmatch")
  @ToString.Exclude
  private TurnamentMatch turnamentmatch;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "game_start", nullable = false)
  private Date gameStart;

  @Column(name = "duration", nullable = false)
  private short duration;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "gametype")
  @ToString.Exclude
  private Gametype gametype;

  @OneToMany(mappedBy = "game")
  @LazyCollection(LazyCollectionOption.EXTRA)
  @OrderColumn
  @ToString.Exclude
  private final Set<Teamperformance> teamperformances = new LinkedHashSet<>();

  @OneToMany(mappedBy = "game")
  @ToString.Exclude
  private final Set<ChampionSelection> championSelections = new LinkedHashSet<>();

  @OneToMany(mappedBy = "game")
  @ToString.Exclude
  private final Set<GamePause> pauses = new LinkedHashSet<>();

  public Game(String id, Date gameStart, short duration) {
    this.id = id;
    this.gameStart = gameStart;
    this.duration = duration;
  }

  public Teamperformance addTeamperformance(Teamperformance teamperformance, Team team) {
    return Teamperformance.get(teamperformance, this, team);
  }

  public ChampionSelection addChampionSelection(ChampionSelection selection, Champion champion, boolean check) {
    return ChampionSelection.get(selection, this, champion, check);
  }

  public GamePause addPause(GamePause pause) {
    return GamePause.get(pause, this);
  }

  public boolean isQueue(QueueType queueType) {
    return gametype.getId() == queueType.getQueueId() || gametype.getId() == -1 && queueType.equals(QueueType.TOURNEY);
  }

  public boolean isCompetitive() {
    return gametype.getId() == 0 || gametype.getId() == -1 || gametype.getId() == 700;
  }

  public boolean isRecently() {
    return System.currentTimeMillis() - gameStart.getTime() < Const.DAYS_UNTIL_INACTIVE * Const.MILLIS_PER_DAY;
  }

  public boolean isVeryRecently() {
    return System.currentTimeMillis() - gameStart.getTime() < 30 * Const.MILLIS_PER_DAY;
  }

  public Teamperformance getPerformanceOf(Team team) {
    return teamperformances.stream().filter(teamperformance -> teamperformance.getTeam() != null && teamperformance.getTeam().equals(team)).findFirst().orElse(null);
  }

  public Playerperformance getPerformanceOf(Account account) {
    return teamperformances.stream().findFirst().map(teamperformance -> teamperformance.getPerformanceOf(account)).orElse(null);
  }

  public List<Team> getTeams() {
    return teamperformances.stream().map(Teamperformance::getTeam).collect(Collectors.toList());
  }

  public String getStartString() {
    return new SimpleDateFormat("dd.MM. HH:mm").format(gameStart);
  }

  public String getDurationString() {
    return duration / 60 + ":" + (duration % 60 < 10 ? "0" + duration % 60 : duration % 60);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    final Game game = (Game) o;
    return id != null && Objects.equals(id, game.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}