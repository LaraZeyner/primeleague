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
public class Wards extends RatingSubcategory {
  private final List<Playerperformance> playerperformances;
  private final Lane lane;

  public Wards(List<Playerperformance> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getVisionscoreAdvantage(), getTrinketEfficiency(), getFirstWardTime(), getWardsCleared(), getTrinketSwapTime());
  }

  public Stat getVisionscoreAdvantage() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane)
        .map(Playerperformance::getVisionscoreAdvantage)
        .nullable()
        .sub("Visionscore", Playerperformance::getVisionScore)
        .sub("Wards placed", Playerperformance::getWardsPlaced);
  }

  public Stat getTrinketEfficiency() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane)
        .map(p -> p.getStats().getTrinketEfficiency())
        .nullable();
  }

  public Stat getFirstWardTime() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane)
        .map(p -> p.getStats().getFirstWardTime())
        .reverse();
  }

  public Stat getWardsCleared() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane)
        .map(Playerperformance::getWardsCleared)
        .nullable();
  }

  public Stat getTrinketSwapTime() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane, "firstTrinketSwap")
        .map(p -> p.getStats().getFirstTrinketSwap())
        .reverse();
  }

}
