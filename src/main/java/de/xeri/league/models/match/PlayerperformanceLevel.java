package de.xeri.league.models.match;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.league.models.ids.PlayerperformanceLevelId;
import de.xeri.league.util.Data;
import de.xeri.league.util.HibernateUtil;
import org.hibernate.annotations.NamedQuery;

@Entity(name = "PlayerperformanceLevel")
@Table(name = "playerperformance_level", indexes = @Index(name = "level_playerperformance", columnList = "playerperformance"))
@IdClass(PlayerperformanceLevelId.class)
@NamedQuery(name = "PlayerperformanceLevel.findAll", query = "FROM PlayerperformanceLevel p")
@NamedQuery(name = "PlayerperformanceLevel.findBy",
    query = "FROM PlayerperformanceLevel p WHERE playerperformance = :playerperformance AND time = :time")
public class PlayerperformanceLevel implements Serializable {
  @Transient
  private static final long serialVersionUID = 6147476273247281841L;

  //<editor-fold desc="Queries">
  public static Set<PlayerperformanceLevel> get() {
    return new LinkedHashSet<>(HibernateUtil.findList(PlayerperformanceLevel.class));
  }

  public static PlayerperformanceLevel get(PlayerperformanceLevel neu, Playerperformance playerperformance) {
    if (has(playerperformance, neu.getLevel())) {
      return find(playerperformance, neu.getLevel());
    }
    playerperformance.getLevelups().add(neu);
    neu.setPlayerperformance(playerperformance);
    Data.getInstance().save(neu);
    return neu;
  }

  public static boolean has(Playerperformance playerperformance, int level) {
    return HibernateUtil.has(PlayerperformanceLevel.class, new String[]{"playerperformance", "level"},
        new Object[]{playerperformance, level});
  }

  public static PlayerperformanceLevel find(Playerperformance playerperformance, int level) {
    return HibernateUtil.find(PlayerperformanceLevel.class, new String[]{"playerperformance", "level"},
        new Object[]{playerperformance, level});
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