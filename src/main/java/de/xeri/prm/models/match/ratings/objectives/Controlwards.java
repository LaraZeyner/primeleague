package de.xeri.prm.models.match.ratings.objectives;

import java.util.Map;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.ratings.Stat;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class Controlwards extends RatingSubcategory {
  private final Map<String, Double> playerperformances;
  private final Lane lane;

  public Controlwards(Map<String, Double> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getControlwardsPlaced(), getControlwardsProtected(), getControlwardsEnemyJungle(), getFirstControlwardTime(),
        getAverageControlwardInventoryTime());
  }

  public Stat getControlwardsPlaced() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "controlWards")
        .nullable();
  }

  public Stat getControlwardsProtected() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "guardedWards")
        .nullable();
  }

  public Stat getControlwardsEnemyJungle() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane, "controlWardUptime");
  }

  public Stat getFirstControlwardTime() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane, "firstControlwardTime")
        .reverse();
  }

  public Stat getAverageControlwardInventoryTime() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane, "controlWardInventoryTime")
        .reverse();
  }

}
