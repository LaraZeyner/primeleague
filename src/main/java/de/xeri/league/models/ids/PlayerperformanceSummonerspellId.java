package de.xeri.league.models.ids;

import java.io.Serializable;
import java.util.Objects;

import org.hibernate.Hibernate;

public class PlayerperformanceSummonerspellId implements Serializable {
  private static final long serialVersionUID = -4987082153851237430L;

  private int playerperformance;
  private String summonerspell;

  //<editor-fold desc="getter and setter">
  public String getSummonerspell() {
    return summonerspell;
  }

  public void setSummonerspell(String summonerspell) {
    this.summonerspell = summonerspell;
  }

  public int getPlayerperformance() {
    return playerperformance;
  }

  public void setPlayerperformance(int playerperformance) {
    this.playerperformance = playerperformance;
  }

  @Override
  public int hashCode() {
    return Objects.hash(summonerspell, playerperformance);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    final PlayerperformanceSummonerspellId entity = (PlayerperformanceSummonerspellId) o;
    return Objects.equals(this.summonerspell, entity.summonerspell) &&
        Objects.equals(this.playerperformance, entity.playerperformance);
  }

  @Override
  public String toString() {
    return "PlayerperformanceSummonerspellId{" +
        "playerperformance=" + playerperformance +
        ", summonerspell='" + summonerspell + '\'' +
        '}';
  }
  //</editor-fold>
}