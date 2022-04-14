package de.xeri.league.models.ids;

import java.io.Serializable;
import java.util.Objects;

import org.hibernate.Hibernate;

public class PlayerperformanceLevelId implements Serializable {
  private static final transient long serialVersionUID = -2404731249852550567L;

  private int playerperformance;
  private int level;

  // default constructor
  public PlayerperformanceLevelId() {
  }

  public PlayerperformanceLevelId(int playerperformance, int level) {
    this.playerperformance = playerperformance;
    this.level = level;
  }

  //<editor-fold desc="getter and setter">
  public int getLevel() {
    return level;
  }

  public void setLevel(Integer level) {
    this.level = level;
  }

  public Integer getPlayerperformance() {
    return playerperformance;
  }

  public void setPlayerperformance(Integer playerperformance) {
    this.playerperformance = playerperformance;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    final PlayerperformanceLevelId levelId = (PlayerperformanceLevelId) o;
    return Objects.equals(this.level, levelId.level) &&
        Objects.equals(this.playerperformance, levelId.playerperformance);
  }

  @Override
  public int hashCode() {
    return Objects.hash(level, playerperformance);
  }

  @Override
  public String toString() {
    return "PlayerperformanceLevelId{" +
        "playerperformance=" + playerperformance +
        ", level=" + level +
        '}';
  }
  //</editor-fold>
}