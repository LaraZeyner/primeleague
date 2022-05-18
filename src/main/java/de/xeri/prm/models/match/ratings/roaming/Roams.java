package de.xeri.prm.models.match.ratings.roaming;

import java.util.List;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.Stat;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.playerperformance.Playerperformance;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class Roams extends RatingSubcategory {
  private final List<Playerperformance> playerperformances;
  private final Lane lane;

  public Roams(List<Playerperformance> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getMinionAvantagePerRoam(), getRoamSuccess(), getGoldXpEfficiency(), getRoamExpense(), getObjectiveDamageWhileRoaming());
  }

  public Stat getMinionAvantagePerRoam() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "roamCreepScoreAdvantage")
        .map(p -> p.getStats().getRoamCreepScoreAdvantage())
        .nullable();
  }

  public Stat getRoamSuccess() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane, "roamSuccessScore")
        .map(p -> p.getStats().getRoamSuccessScore())
        .nullable();
  }

  public Stat getGoldXpEfficiency() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane, "roamGoldXpAdvantage")
        .map(p -> p.getStats().getRoamGoldXpAdvantage())
        .nullable()
        .sub("Gold durch Roams", p -> p.getStats().getRoamGoldAdvantage())
        .sub("XP durch Roams", p -> p.getStats().getRoamGoldXpAdvantage() - p.getStats().getRoamGoldAdvantage());
  }

  public Stat getRoamExpense() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane, "roamScore")
        .map(p -> p.getStats().getRoamScore());
  }

  public Stat getObjectiveDamageWhileRoaming() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane, "roamObjectiveDamageAdvantage")
        .map(p -> p.getStats().getRoamObjectiveDamageAdvantage())
        .nullable();
  }

}
