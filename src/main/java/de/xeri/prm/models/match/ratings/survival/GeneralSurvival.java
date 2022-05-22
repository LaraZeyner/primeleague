package de.xeri.prm.models.match.ratings.survival;

import java.util.Map;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.ratings.Stat;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class GeneralSurvival extends RatingSubcategory {
  private final Map<String, Double> playerperformances;
  private final Lane lane;

  public GeneralSurvival(Map<String, Double> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getLivePlaytime(), getTimeDead(), getSurvivedClose(), getDeathPositioning());
  }

  public Stat getLivePlaytime() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "timeAlivePercent")
        .sub("Todeszeit", "timeDead")
        .sub("Minuten", "duration");
  }

  public Stat getTimeDead() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane)
        .reverse()
        .sub("Längste Zeit am Leben", "timeAlive");
  }

  public Stat getSurvivedClose() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane);
  }

  public Stat getDeathPositioning() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "relativeDeathPositioning")
        .nullable();
  }

}
