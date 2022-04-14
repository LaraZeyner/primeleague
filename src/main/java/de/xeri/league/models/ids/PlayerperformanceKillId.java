package de.xeri.league.models.ids;

import java.io.Serializable;
import java.util.Objects;

public class PlayerperformanceKillId implements Serializable {
  private static final transient long serialVersionUID = -1634613140851223357L;

  private int id;
  private int playerperformance;

  // default constructor
  public PlayerperformanceKillId() {
  }

  public PlayerperformanceKillId(int id, int playerperformance) {
    this.id = id;
    this.playerperformance = playerperformance;
  }

  //<editor-fold desc="getter and setter">
  public Integer getPlayerperformance() {
    return playerperformance;
  }

  public void setPlayerperformance(Integer playerperformance) {
    this.playerperformance = playerperformance;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PlayerperformanceKillId)) return false;
    final PlayerperformanceKillId infoId = (PlayerperformanceKillId) o;
    return Objects.equals(getPlayerperformance(), infoId.getPlayerperformance()) && getId() == infoId.getId();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getPlayerperformance(), getId());
  }

  @Override
  public String toString() {
    return "PlayerperformanceInfoId{" +
        "id=" + id +
        ", playerperformance=" + playerperformance +
        '}';
  }
  //</editor-fold>
}