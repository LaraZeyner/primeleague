package de.xeri.prm.models.match.ratings.laning;

import java.util.List;
import java.util.Map;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.ratings.Stat;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class PostReset extends RatingSubcategory {
  private final Map<String, Double> playerperformances;
  private final Lane lane;

  public PostReset(Map<String, Double> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getResetTime(), getEnemyControlled(), getResourceConservation(), getConsumablesUsed(), getEarlyDamage());
  }

  @Override
  public List<String> getData() {
    return handleData(getResetTime(), getEnemyControlled(), getResourceConservation(), getConsumablesUsed(), getEarlyDamage());
  }

  public Stat getResetTime() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane, "secondBase")
        .nullable();
  }

  public Stat getEnemyControlled() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane, "secondBaseEnemyControlled")
        .nullable();
  }

  public Stat getResourceConservation() {
    return new Stat(playerperformances, OutputType.NUMBER, 4, lane)
        .nullable();
  }

  public Stat getConsumablesUsed() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "consumablesPurchased")
        .nullable();
  }

  public Stat getEarlyDamage() {
    return new Stat(playerperformances, OutputType.PERCENT, 2, lane)
        .nullable();
  }

}
