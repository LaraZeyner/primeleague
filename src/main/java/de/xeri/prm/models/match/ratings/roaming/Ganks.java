package de.xeri.prm.models.match.ratings.roaming;

import java.util.Map;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.ratings.Stat;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class Ganks extends RatingSubcategory {
  private final Map<String, Double> playerperformances;
  private final Lane lane;

  public Ganks(Map<String, Double> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getTeamInvadesAndBuffsTaken(), getEarlyGanksSpottedAndTimeWasted(), getProximity(), getGankPriority(), getGankSetups());
  }

  public Stat getTeamInvadesAndBuffsTaken() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "invadingAndBuffs")
        .nullable()
        .sub("Earlgame Ganks", "ganksEarly");

  }

  public Stat getEarlyGanksSpottedAndTimeWasted() {
    return new Stat(playerperformances, OutputType.TIME, 3, lane, "jungleTimeWasted")
        .nullable()
        .ignore()
        .sub("Jungle Proximity", "laneProximityDifference");
  }

  public Stat getProximity() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane)
        .nullable();
  }

  public Stat getGankPriority() {
    return new Stat(playerperformances, OutputType.TEXT, 12, lane, "gankPriority") {
      @Override
      public String display() {
        return calculate() < 0 ? "TOPSIDE - " + Math.abs(calculate()) : "BOTSIDE - " + Math.abs(calculate());
      }
    };
  }

  public Stat getGankSetups() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane)
        .nullable();
  }

}
