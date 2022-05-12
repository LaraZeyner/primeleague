package de.xeri.prm.models.ids;

import java.io.Serializable;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PlayerperformanceSummonerspellId implements Serializable {
  private static final long serialVersionUID = -4987082153851237430L;

  private int playerperformance;
  private byte summonerspell;

  //<editor-fold desc="getter and setter">
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PlayerperformanceSummonerspellId)) return false;
    final PlayerperformanceSummonerspellId that = (PlayerperformanceSummonerspellId) o;
    return getPlayerperformance() == that.getPlayerperformance() && getSummonerspell() == that.getSummonerspell();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getPlayerperformance(), getSummonerspell());
  }
  @Override
  public String toString() {
    return "PlayerperformanceSummonerspellId{" +
        "playerperformance=" + playerperformance +
        ", summonerspell=" + summonerspell +
        '}';
  }
  //</editor-fold>
}