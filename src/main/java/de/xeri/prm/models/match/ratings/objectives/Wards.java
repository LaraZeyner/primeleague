package de.xeri.prm.models.match.ratings.objectives;

import java.util.Map;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.ratings.Stat;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class Wards extends RatingSubcategory {
  private final Map<String, Double> playerperformances;
  private final Lane lane;

  public Wards(Map<String, Double> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getVisionscoreAdvantage(), getTrinketEfficiency(), getFirstWardTime(), getWardsCleared(), getTrinketSwapTime());
  }

  public Stat getVisionscoreAdvantage() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane)
        .nullable()
        .sub("Visionscore", "visionScore")
        .sub("Wards placed","wardsPlaced");
  }

  public Stat getTrinketEfficiency() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane)
        .nullable();
  }

  public Stat getFirstWardTime() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane)
        .reverse();
  }

  public Stat getWardsCleared() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane)
        .nullable();
  }

  public Stat getTrinketSwapTime() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane, "firstTrinketSwap")
        .reverse();
  }

}
