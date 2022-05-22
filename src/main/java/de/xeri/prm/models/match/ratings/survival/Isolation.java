package de.xeri.prm.models.match.ratings.survival;

import java.util.Map;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.ratings.Stat;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class Isolation extends RatingSubcategory {
  private final Map<String, Double> playerperformances;
  private final Lane lane;

  public Isolation(Map<String, Double> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getFarmEfficiency(), getXPEfficiency(), getUsedWards(), getDamageTraded(), getResets());
  }

  public Stat getFarmEfficiency() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "earlyFarmEfficiency")
        .nullable();
  }

  public Stat getXPEfficiency() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "earlyXpEfficiency")
        .nullable();
  }

  public Stat getUsedWards() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "wardsEarlygame")
        .nullable();
  }

  public Stat getDamageTraded() {
    return new Stat(playerperformances, OutputType.NUMBER, 4, lane, "earlyDamageTrading")
        .nullable();
  }

  public Stat getResets() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane)
        .nullable();
  }

}
