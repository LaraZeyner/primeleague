package de.xeri.prm.models.match.ratings.survival;

import java.util.List;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.Stat;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.playerperformance.Playerperformance;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class Isolation extends RatingSubcategory {
  private final List<Playerperformance> playerperformances;
  private final Lane lane;

  public Isolation(List<Playerperformance> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getFarmEfficiency(), getXPEfficiency(), getUsedWards(), getDamageTraded(), getResets());
  }

  public Stat getFarmEfficiency() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "earlyFarmEfficiency")
        .map(p -> p.getStats().getEarlyFarmEfficiency())
        .nullable();
  }

  public Stat getXPEfficiency() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "earlyXpEfficiency")
        .map(p -> p.getStats().getEarlyXpEfficiency())
        .nullable();
  }

  public Stat getUsedWards() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "wardsEarlygame")
        .map(p -> p.getStats().getWardsEarlygame())
        .nullable();
  }

  public Stat getDamageTraded() {
    return new Stat(playerperformances, OutputType.NUMBER, 4, lane, "earlyDamageTrading")
        .map(p -> p.getStats().getEarlyDamageTrading())
        .nullable();
  }

  public Stat getResets() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane)
        .map(p -> p.getStats().getResets())
        .nullable();
  }

}
