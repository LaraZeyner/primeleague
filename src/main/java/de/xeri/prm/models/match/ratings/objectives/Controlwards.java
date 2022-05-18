package de.xeri.prm.models.match.ratings.objectives;

import java.util.List;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.Stat;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.playerperformance.Playerperformance;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class Controlwards extends RatingSubcategory {
  private final List<Playerperformance> playerperformances;
  private final Lane lane;

  public Controlwards(List<Playerperformance> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getControlwardsPlaced(), getControlwardsProtected(), getControlwardsEnemyJungle(), getFirstControlwardTime(),
        getAverageControlwardInventoryTime());
  }

  public Stat getControlwardsPlaced() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "controlWards")
        .map(Playerperformance::getControlWards)
        .nullable();
  }

  public Stat getControlwardsProtected() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "guardedWards")
        .map(Playerperformance::getGuardedWards)
        .nullable();
  }

  public Stat getControlwardsEnemyJungle() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane, "controlWardUptime")
        .map(Playerperformance::getControlWardUptime);
  }

  public Stat getFirstControlwardTime() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane, "firstControlwardTime")
        .map(p -> p.getStats().getFirstControlwardTime())
        .reverse();
  }

  public Stat getAverageControlwardInventoryTime() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane, "controlWardInventoryTime")
        .map(p -> p.getStats().getControlWardInventoryTime())
        .reverse();
  }

}
