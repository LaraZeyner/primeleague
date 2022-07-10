package de.xeri.prm.models.match.ratings.laning;

import java.util.List;
import java.util.Map;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.ratings.Stat;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class LaneBilance extends RatingSubcategory {
  private final Map<String, Double> playerperformances;
  private final Lane lane;

  public LaneBilance(Map<String, Double> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getLead(), getObjectiveAdvantage(), getTurretplateAdvantage(), getEnemyControlled());
  }

  @Override
  public List<String> getData() {
    return handleData(getLead(), getObjectiveAdvantage(), getTurretplateAdvantage(), getEnemyControlled());
  }

  public Stat getLead() {
    return new Stat(playerperformances, OutputType.NUMBER, 4, lane, "laneLead")
        .nullable();
  }

  public Stat getObjectiveAdvantage() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "earlyObjectiveAdvantage")
        .nullable()
        .sub("Objectives erhalten", "earlyObjectives");
  }

  public Stat getTurretplateAdvantage() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane)
        .nullable()
        .sub("Turretplates zerstört", "turretplates");
  }

  public Stat getEnemyControlled() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane, "enemyControlAdvantageEarly")
        .nullable()
        .sub("Gegner kontrolliert", "enemyControlledEarly");
  }

}
