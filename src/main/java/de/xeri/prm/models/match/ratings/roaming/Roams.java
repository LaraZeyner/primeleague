package de.xeri.prm.models.match.ratings.roaming;

import java.util.Map;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.ratings.Stat;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class Roams extends RatingSubcategory {
  private final Map<String, Double> playerperformances;
  private final Lane lane;

  public Roams(Map<String, Double> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getMinionAvantagePerRoam(), getRoamSuccess(), getGoldXpEfficiency(), getRoamExpense(), getObjectiveDamageWhileRoaming());
  }

  public Stat getMinionAvantagePerRoam() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "roamCreepScoreAdvantage")
        .nullable();
  }

  public Stat getRoamSuccess() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane, "roamSuccessScore")
        .nullable();
  }

  public Stat getGoldXpEfficiency() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane, "roamGoldXpAdvantage")
        .nullable()
        .sub("Gold durch Roams", "roamGoldAdvantage")
        .sub("XP durch Roams", "roamGoldXpAdvantage");
  }

  public Stat getRoamExpense() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane, "roamScore");
  }

  public Stat getObjectiveDamageWhileRoaming() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane, "roamObjectiveDamageAdvantage")
        .nullable();
  }

}
