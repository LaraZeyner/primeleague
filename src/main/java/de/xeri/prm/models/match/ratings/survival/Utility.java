package de.xeri.prm.models.match.ratings.survival;

import java.util.Map;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.ratings.Stat;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class Utility extends RatingSubcategory {
  private final Map<String, Double> playerperformances;
  private final Lane lane;

  public Utility(Map<String, Double> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getDamageShielded(), getCrowdControl(), getEnemiesControlled(), getTeammatesSaved(), getUtilityScore());
  }

  public Stat getDamageShielded() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane)
        .nullable();
  }

  public Stat getCrowdControl() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "immobilizations")
        .nullable();
  }

  public Stat getEnemiesControlled() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane, "enemyControlAdvantage")
        .nullable()
        .sub("Gegner kontrolliert", "enemyControlled");
  }

  public Stat getTeammatesSaved() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane, "savedAlly")
        .nullable();
  }

  public Stat getUtilityScore() {
    return new Stat(playerperformances, OutputType.PERCENT, 2, lane)
        .nullable()
        .sub("Vision", "visionScore")
        .sub("Crowd Control", "immobilizations")
        .sub("Schaden mitigiert", "damageMitigated");
  }

}
