package de.xeri.prm.models.match.ratings.objectives;

import java.util.Map;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.ratings.Stat;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class ObjectivePressure extends RatingSubcategory {
  private final Map<String, Double> playerperformances;
  private final Lane lane;

  public ObjectivePressure(Map<String, Double> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getObjectiveAfterSpawn(), getStolenObjectivesAndContestRate(), getDamageAgainstObjectives(),
        getScuttleControlOverall(), getJunglerTakedownsBeforeObjective());
  }

  public Stat getObjectiveAfterSpawn() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane)
        .nullable();
  }

  public Stat getStolenObjectivesAndContestRate() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "objectivesStolenAndContested")
        .nullable()
        .sub("Objectives gestohlen", "objectivesStolen")
        .sub("Objectives contestet", "objectiveContests");
  }

  public Stat getDamageAgainstObjectives() {
    return new Stat(playerperformances, OutputType.NUMBER, 4, lane, "objectivesDamage")
        .nullable();
  }

  public Stat getScuttleControlOverall() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "totalScuttles")
        .nullable();
  }

  public Stat getJunglerTakedownsBeforeObjective() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "objectivesKilledJunglerBefore")
        .nullable()
        .sub("Jungler-Takedowns", "junglerKillsAtObjective")
        .sub("Objectives Gesamt", "objectivesKilledJunglerBefore", "junglerKillsAtObjective");
  }

}
