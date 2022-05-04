package de.xeri.league.models.match;

import java.io.Serializable;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import de.xeri.league.models.dynamic.Champion;
import de.xeri.league.models.enums.QueueType;
import de.xeri.league.models.league.Account;
import de.xeri.league.models.league.Team;
import de.xeri.league.models.league.TurnamentMatch;
import de.xeri.league.models.match.playerperformance.Playerperformance;
import de.xeri.league.util.Data;
import de.xeri.league.util.HibernateUtil;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.NamedQuery;

@Entity(name = "Game")
@Table(name = "game", indexes = @Index(name = "turnamentmatch", columnList = "turnamentmatch"))
@NamedQuery(name = "Game.findAll", query = "FROM Game g")
@NamedQuery(name = "Game.findById", query = "FROM Game g WHERE id = :pk")
public class Game implements Serializable {

  @Transient
  private static final long serialVersionUID = 4639052028429524051L;

  public static Set<Game> get() {
    return new LinkedHashSet<>(HibernateUtil.findList(Game.class));
  }

  public static Game get(Game neu, Gametype gametype) {
    if (has(neu.getId())) {
      final Game game = find(neu.getId());
      game.setGameStart(neu.getGameStart());
      game.setDuration(neu.getDuration());
      return game;
    }
    gametype.getGames().add(neu);
    neu.setGametype(gametype);
    Data.getInstance().save(neu);
    return neu;
  }

  public static boolean has(String id) {
    return HibernateUtil.has(Game.class, id);
  }

  public static Game find(String id) {
    return HibernateUtil.find(Game.class, id);
  }


  public List<GamePause> getNotClosed() {
    return HibernateUtil.findList(GamePause.class, new String[]{"game", "end"}, new Object[]{this, 0}, "findByEnd");
  }

  public List<GamePause> getNotOpened() {
    return HibernateUtil.findList(GamePause.class, new String[]{"game", "start"}, new Object[]{this, 0});
  }

  @Id
  @Check(constraints = "game_id REGEXP ('^EUW')")
  @Column(name = "game_id", nullable = false, length = 16)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "turnamentmatch")
  private TurnamentMatch turnamentmatch;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "game_start", nullable = false)
  private Date gameStart;

  @Column(name = "duration", nullable = false)
  private short duration;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "gametype")
  private Gametype gametype;

  @OneToMany(mappedBy = "game")
  private final Set<Teamperformance> teamperformances = new LinkedHashSet<>();

  @OneToMany(mappedBy = "game")
  private final Set<ChampionSelection> championSelections = new LinkedHashSet<>();

  @OneToMany(mappedBy = "game")
  private final Set<GamePause> pauses = new LinkedHashSet<>();


  // default constructor
  public Game() {
  }

  public Game(String id, Date gameStart, short duration) {
    this.id = id;
    this.gameStart = gameStart;
    this.duration = duration;
  }

  public Teamperformance addTeamperformance(Teamperformance teamperformance, Team team) {
    return Teamperformance.get(teamperformance, this, team);
  }

  public ChampionSelection addChampionSelection(ChampionSelection selection, Champion champion) {
    return ChampionSelection.get(selection, this, champion);
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

  public Teamperformance getPerformanceOf(Team team) {
    return teamperformances.stream().filter(teamperformance -> teamperformance.getTeam().equals(team)).findFirst().orElse(null);
  }

  public Playerperformance getPerformanceOf(Account account) {
    return teamperformances.stream().findFirst().map(teamperformance -> teamperformance.getPerformanceOf(account)).orElse(null);
  }

  public List<Team> getTeams() {
    return teamperformances.stream().map(Teamperformance::getTeam).collect(Collectors.toList());
  }

  //<editor-fold desc="getter and setter">
  public Set<ChampionSelection> getChampionSelections() {
    return championSelections;
  }

  public Set<Teamperformance> getTeamperformances() {
    return teamperformances;
  }

  public Gametype getGametype() {
    return gametype;
  }

  public void setGametype(Gametype gametype) {
    this.gametype = gametype;
  }

  public short getDuration() {
    return duration;
  }

  public void setDuration(short duration) {
    this.duration = duration;
  }

  public Date getGameStart() {
    return gameStart;
  }

  public void setGameStart(Date gameStart) {
    this.gameStart = gameStart;
  }

  public TurnamentMatch getTurnamentmatch() {
    return turnamentmatch;
  }

  public void setTurnamentmatch(TurnamentMatch turnamentmatch) {
    this.turnamentmatch = turnamentmatch;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Set<GamePause> getPauses() {
    return pauses;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Game)) return false;
    final Game game = (Game) o;
    return getDuration() == game.getDuration() && getId().equals(game.getId()) && Objects.equals(getTurnamentmatch(), game.getTurnamentmatch()) && getGameStart().equals(game.getGameStart()) && getGametype() == game.getGametype() && getTeamperformances().equals(game.getTeamperformances()) && getChampionSelections().equals(game.getChampionSelections());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getTurnamentmatch(), getGameStart(), getDuration(), getGametype());
  }

  @Override
  public String toString() {
    return "Game{" +
        "id='" + id + '\'' +
        ", turnamentmatch=" + turnamentmatch +
        ", gameStart=" + gameStart +
        ", duration=" + duration +
        ", gametype=" + gametype +
        '}';
  }
  //</editor-fold>
}