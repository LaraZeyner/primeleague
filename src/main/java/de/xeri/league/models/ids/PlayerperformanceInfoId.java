package de.xeri.league.models.ids;

import java.io.Serializable;
import java.util.Objects;

public class PlayerperformanceInfoId implements Serializable {
  private static final transient long serialVersionUID = -1938784659403319856L;

  private int playerperformance;
  private byte minute;

  // default constructor
  public PlayerperformanceInfoId() {
  }

  public PlayerperformanceInfoId(int playerperformance, byte minute) {
    this.playerperformance = playerperformance;
    this.minute = minute;
  }

  //<editor-fold desc="getter and setter">
  public Integer getPlayerperformance() {
    return playerperformance;
  }

  public void setPlayerperformance(Integer playerperformance) {
    this.playerperformance = playerperformance;
  }

  public byte getMinute() {
    return minute;
  }

  public void setMinute(byte minute) {
    this.minute = minute;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PlayerperformanceInfoId)) return false;
    final PlayerperformanceInfoId infoId = (PlayerperformanceInfoId) o;
    return Objects.equals(getPlayerperformance(), infoId.getPlayerperformance()) && getMinute() == infoId.getMinute();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getPlayerperformance(), getMinute());
  }

  @Override
  public String toString() {
    return "PlayerperformanceInfoId{" +
        "playerperformance=" + playerperformance +
        ", minute=" + minute +
        '}';
  }
  //</editor-fold>
}