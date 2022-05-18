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
public class ObjectivePressure extends RatingSubcategory {
  private final List<Playerperformance> playerperformances;
  private final Lane lane;

  public ObjectivePressure(List<Playerperformance> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getObjectiveAfterSpawn(), getStolenObjectivesAndContestRate(), getDamageAgainstObjectives(),
        getScuttleControlOverall(), getJunglerTakedownsBeforeObjective());
  }

  public Stat getObjectiveAfterSpawn() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane)
        .map(p -> p.getTeamperformance().getObjectiveAtSpawn())
        .nullable();
  }

  public Stat getStolenObjectivesAndContestRate() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "objectivesStolenAndContested")
        .map(p -> p.getStats().getObjectivesStolenAndContested())
        .nullable()
        .sub("Objectives gestohlen", Playerperformance::getObjectivesStolen)
        .sub("Objectives contestet", p -> p.getTeamperformance().getObjectiveContests());
  }

  public Stat getDamageAgainstObjectives() {
    return new Stat(playerperformances, OutputType.NUMBER, 4, lane, "objectivesDamage")
        .map(Playerperformance::getObjectivesDamage)
        .nullable();
  }

  public Stat getScuttleControlOverall() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "totalScuttles")
        .map(Playerperformance::getTotalScuttles)
        .nullable();
  }

  public Stat getJunglerTakedownsBeforeObjective() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "objectivesKilledJunglerBefore")
        .map(p -> p.getStats().getObjectivesKilledJunglerBefore())
        .nullable()
        .sub("Jungler-Takedowns", Playerperformance::getJunglerKillsAtObjective)
        .sub("Objectives Gesamt", p -> p.getJunglerKillsAtObjective() * p.getStats().getObjectivesKilledJunglerBefore());
  }

}
