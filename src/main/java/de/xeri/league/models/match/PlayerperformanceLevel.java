package de.xeri.league.models.match;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.league.models.ids.PlayerperformanceLevelId;
import de.xeri.league.util.Data;
import de.xeri.league.util.Util;

@Entity(name = "Playerperformance_Level")
@Table(name = "playerperformance_level")
@IdClass(PlayerperformanceLevelId.class)
public class PlayerperformanceLevel implements Serializable {
  @Transient
  private static final long serialVersionUID = 6147476273247281841L;

  //<editor-fold desc="Queries">
  private static Set<PlayerperformanceLevel> data;

  public static void save() {
    if (data != null) data.forEach(Data.getInstance().getSession()::saveOrUpdate);
  }

  public static Set<PlayerperformanceLevel> get() {
    if (data == null)
      data = new LinkedHashSet<>((List<PlayerperformanceLevel>) Util.query("Playerperformance_Level"));
    return data;
  }

  public static PlayerperformanceLevel get(PlayerperformanceLevel neu, Playerperformance performance) {
    get();
    if (find(performance, neu.getLevel()) == null) {
      performance.getLevelups().add(neu);
      neu.setPlayerperformance(performance);
      data.add(neu);
    }
    return find(performance, neu.getLevel());
  }

  public static PlayerperformanceLevel find(Playerperformance playerperformance, int level) {
    return data.stream().filter(entry -> entry.getPlayerperformance().equals(playerperformance) && entry.getLevel() == level)
        .findFirst().orElse(null);
  }
  //</editor-fold>
  
  @Id
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "playerperformance")
  private Playerperformance playerperformance;

  @Id
  @Column(name = "level_number", nullable = false)
  private byte level;

  @Column(name = "levelup_time", nullable = false)
  private int time;

  // default constructor
  public PlayerperformanceLevel() {
  }

  public PlayerperformanceLevel(byte level, int time) {
    this.level = level;
    this.time = time;
  }
  
  //<editor-fold desc="getter and setter">
  public Playerperformance getPlayerperformance() {
    return playerperformance;
  }

  public void setPlayerperformance(Playerperformance playerperformance) {
    this.playerperformance = playerperformance;
  }

  public byte getLevel() {
    return level;
  }

  public int getTime() {
    return time;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PlayerperformanceLevel)) return false;
    final PlayerperformanceLevel level = (PlayerperformanceLevel) o;
    return getLevel() == level.getLevel() && getTime() == level.getTime() && getPlayerperformance().equals(level.getPlayerperformance());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getPlayerperformance(), getLevel(), getTime());
  }

  @Override
  public String toString() {
    return "PlayerperformanceLevel{" +
        "playerperformance=" + playerperformance +
        ", level=" + level +
        ", time=" + time +
        '}';
  }
  //</editor-fold>
}