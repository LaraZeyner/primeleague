package de.xeri.prm.models.match.ratings.income;

import java.util.List;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.Stat;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.playerperformance.Playerperformance;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class EarlyIncome extends RatingSubcategory {
  private final List<Playerperformance> playerperformances;
  private final Lane lane;

  public EarlyIncome(List<Playerperformance> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getEarlyLaneLead(), getLaneLead(), getFirstFullItem(), getEarlyCreepScore(), getEarlyFarmEfficiency());
  }

  public Stat getEarlyLaneLead() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane, "earlyGoldAdvantage")
        .map(p -> p.getStats().getEarlyGoldAdvantage())
        .nullable();
  }

  public Stat getLaneLead() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane)
        .map(Playerperformance::getLaneLead)
        .nullable();
  }

  public Stat getFirstFullItem() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane)
        .map(p -> p.getStats().getFirstFullItem());
  }

  public Stat getEarlyCreepScore() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "earlyCreeps")
        .map(Playerperformance::getEarlyCreeps);
  }

  public Stat getEarlyFarmEfficiency() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane)
        .map(p -> p.getStats().getEarlyFarmEfficiency())
        .nullable()
        .sub("CS@15", p -> p.getStats().getEarlyFarmEfficiency() * 158);
  }

}
