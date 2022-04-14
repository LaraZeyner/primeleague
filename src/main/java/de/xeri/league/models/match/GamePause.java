package de.xeri.league.models.match;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

import de.xeri.league.util.Data;
import de.xeri.league.util.Util;

@Entity
@Table(name = "game_pause", indexes = {
    @Index(name = "game", columnList = "game")
})
public class GamePause implements Serializable {
  @Transient
  private static final long serialVersionUID = -2398099776734029928L;

  //<editor-fold desc="Queries">
  private static Set<GamePause> data;

  public static void save() {
    if (data != null) data.forEach(Data.getInstance().getSession()::saveOrUpdate);
  }

  public static Set<GamePause> get() {
    if (data == null)
      data = new LinkedHashSet<>((List<GamePause>) Util.query("Playerperformance_Level"));
    return data;
  }

  static GamePause get(GamePause neu, Game game) {
    get();
    if (find(game, neu.getStart()) == null) {
      game.getPauses().add(neu);
      neu.setGame(game);
      data.add(neu);
    }
    return find(game, neu.getStart());
  }

  public static GamePause find(Game game, long start) {
    return data.stream().filter(entry -> entry.getGame().equals(game) && entry.getStart() == start).findFirst().orElse(null);
  }

  public static List<GamePause> getNotClosed() {
    return data.stream().filter(entry -> entry.getEnd() == 0).collect(Collectors.toList());
  }

  public static List<GamePause> getNotOpened() {
    return data.stream().filter(entry -> entry.getStart() == 0).collect(Collectors.toList());
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
  public static void setData(Set<GamePause> data) {
    GamePause.data = data;
  }

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