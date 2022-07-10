package de.xeri.prm.models.match.ratings.objectives;

import java.util.List;
import java.util.Map;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.ratings.Stat;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class BotsideObjectives extends RatingSubcategory {
  private final Map<String, Double> playerperformances;
  private final Lane lane;

  public BotsideObjectives(Map<String, Double> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getDragonTime(), getDragonTakedowns(), getElderTime(), getFirstDrake(), getSoulrateAndPerfect());
  }

  @Override
  public List<String> getData() {
    return handleData(getDragonTime(), getDragonTakedowns(), getElderTime(), getFirstDrake(), getSoulrateAndPerfect());
  }

  public Stat getDragonTime() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane)
        .reverse();
  }

  public Stat getDragonTakedowns() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane)
        .nullable()
        .sub("Keine", "noDrakes")
        .sub("1 Mal", "oneDrake")
        .sub("2 Mal", "twoDrakes")
        .sub("3 Mal", "threeDrakes")
        .sub("4 Mal", "fourDrakes")
        .sub("öfter", "moreDrakes");
  }

  public Stat getElderTime() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane)
        .reverse();
  }

  public Stat getFirstDrake() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane)
        .nullable();
  }

  public Stat getSoulrateAndPerfect() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "soulratePerfect") {
      @Override
      public double average() {
        return .75;
      }

      @Override
      public double maximum() {
        return 6;
      }

      @Override
      public double minimum() {
        return 0;
      }
    }.nullable();
  }

}
