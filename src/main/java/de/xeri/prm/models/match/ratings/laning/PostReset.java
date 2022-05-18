package de.xeri.prm.models.match.ratings.laning;

import java.util.List;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.Stat;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.playerperformance.Playerperformance;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class PostReset extends RatingSubcategory {
  private final List<Playerperformance> playerperformances;
  private final Lane lane;

  public PostReset(List<Playerperformance> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getResetTime(), getEnemyControlled(), getResourceConservation(), getConsumablesUsed(), getEarlyDamage());
  }

  public Stat getResetTime() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane, "secondBase")
        .map(p -> p.getStats().getSecondBase())
        .nullable();
  }

  public Stat getEnemyControlled() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane, "secondBaseEnemyControlled")
        .map(p -> p.getStats().getSecondBaseEnemyControlled())
        .nullable();
  }

  public Stat getResourceConservation() {
    return new Stat(playerperformances, OutputType.NUMBER, 4, lane)
        .map(p -> p.getStats().getResourceConservation())
        .nullable();
  }

  public Stat getConsumablesUsed() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, null)
        .map(p -> p.getStats().isConsumablesPurchased() ? 1 : 0)
        .nullable();
  }

  public Stat getEarlyDamage() {
    return new Stat(playerperformances, OutputType.PERCENT, 2, lane)
        .map(p -> p.getStats().getEarlyDamage())
        .nullable();
  }

}
