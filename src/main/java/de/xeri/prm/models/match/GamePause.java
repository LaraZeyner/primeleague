package de.xeri.prm.models.match;

import java.io.Serializable;
import java.util.LinkedHashSet;
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
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.prm.manager.Data;
import de.xeri.prm.util.HibernateUtil;
import org.hibernate.annotations.NamedQuery;

@Entity(name = "GamePause")
@Table(name = "game_pause", indexes = @Index(name = "game", columnList = "game"))
@NamedQuery(name = "GamePause.findAll", query = "FROM GamePause g")
@NamedQuery(name = "GamePause.findBy", query = "FROM GamePause g WHERE game = :game AND g.start = :start")
@NamedQuery(name = "GamePause.findByEnd", query = "FROM GamePause g WHERE game = :game AND g.end = :end")
public class GamePause implements Serializable {
  @Transient
  private static final long serialVersionUID = -2398099776734029928L;

  //<editor-fold desc="Queries">
  public static Set<GamePause> get() {
    return new LinkedHashSet<>(HibernateUtil.findList(GamePause.class));
  }

  public static GamePause get(GamePause neu, Game game) {
    if (has(game, neu.getStart())) {
      final GamePause gamePause = find(game, neu.getStart());
      gamePause.setStart(neu.getStart());
      gamePause.setEnd(neu.getEnd());
      return gamePause;
    }
    game.getPauses().add(neu);
    neu.setGame(game);
    Data.getInstance().save(neu);
    return neu;
  }

  public static boolean has(Game game, long start) {
    return HibernateUtil.has(GamePause.class, new String[]{"game", "start"}, new Object[]{game, start});
  }

  public static boolean has(int id) {
    return HibernateUtil.has(GamePause.class, id);
  }

  public static GamePause find(int id) {
    return HibernateUtil.find(GamePause.class, id);
  }

  public static GamePause find(Game game, long start) {
    return HibernateUtil.find(GamePause.class, new String[]{"game", "start"}, new Object[]{game, start});
  }
  //</editor-fold>
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "pause_id", nullable = false)
  private int id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "game", nullable = false)
  private Game game;

  @Column(name = "pause_start", nullable = false)
  private long start;

  @Column(name = "pause_end", nullable = false)
  private long end;

  // default constructor
  public GamePause() {
  }

  public GamePause(long start, long end) {
    this.start = start;
    this.end = end;
  }

  //<editor-fold desc="getter and setter">
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Game getGame() {
    return game;
  }

  public void setGame(Game game) {
    this.game = game;
  }

  public long getStart() {
    return start;
  }

  public void setStart(long start) {
    this.start = start;
  }

  public long getEnd() {
    return end;
  }

  public void setEnd(long end) {
    this.end = end;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof GamePause)) return false;
    final GamePause gamePause = (GamePause) o;
    return getId() == gamePause.getId() && getStart() == gamePause.getStart() && getEnd() == gamePause.getEnd() && getGame().equals(gamePause.getGame());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getGame(), getStart(), getEnd());
  }

  @Override
  public String toString() {
    return "GamePause{" +
        "id=" + id +
        ", game=" + game +
        ", start=" + start +
        ", end=" + end +
        '}';
  }
  //</editor-fold>
}