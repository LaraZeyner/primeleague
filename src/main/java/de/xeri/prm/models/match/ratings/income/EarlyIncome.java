package de.xeri.prm.models.match.ratings.income;

import java.util.Map;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.ratings.Stat;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class EarlyIncome extends RatingSubcategory {
  private final Map<String, Double> playerperformances;
  private final Lane lane;

  public EarlyIncome(Map<String, Double> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getEarlyLaneLead(), getLaneLead(), getFirstFullItem(), getEarlyCreepScore(), getEarlyFarmEfficiency());
  }

  public Stat getEarlyLaneLead() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane, "earlyGoldAdvantage")
        .nullable();
  }

  public Stat getLaneLead() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane)
        .nullable();
  }

  public Stat getFirstFullItem() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane);
  }

  public Stat getEarlyCreepScore() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "earlyCreeps");
  }

  public Stat getEarlyFarmEfficiency() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane)
        .nullable()
        .sub("CS@15", "earlyFarmEfficiency", 158);
  }

}
