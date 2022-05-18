package de.xeri.prm.models.match.ratings.survival;

import java.util.List;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.Stat;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.playerperformance.Playerperformance;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class Utility extends RatingSubcategory {
  private final List<Playerperformance> playerperformances;
  private final Lane lane;

  public Utility(List<Playerperformance> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getDamageShielded(), getCrowdControl(), getEnemiesControlled(), getTeammatesSaved(), getUtilityScore());
  }

  public Stat getDamageShielded() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane)
        .map(Playerperformance::getDamageShielded)
        .nullable();
  }

  public Stat getCrowdControl() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "immobilizations")
        .map(Playerperformance::getImmobilizations)
        .nullable();
  }

  public Stat getEnemiesControlled() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane, "enemyControlAdvantage")
        .map(p -> p.getStats().getEnemyControlAdvantage())
        .nullable()
        .sub("Gegner kontrolliert", p -> p.getStats().getEnemyControlled())
        .sub("Spieler kontrolliert", p -> p.getStats().getEnemyControlled() - p.getStats().getEnemyControlAdvantage());
  }

  public Stat getTeammatesSaved() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane, "savedAlly")
        .map(Playerperformance::getSavedAlly)
        .nullable();
  }

  public Stat getUtilityScore() {
    return new Stat(playerperformances, OutputType.PERCENT, 2, lane)
        .map(p -> p.getStats().getUtilityScore())
        .nullable()
        .sub("Vision", Playerperformance::getVisionScore)
        .sub("Crowd Control", Playerperformance::getImmobilizations)
        .sub("Schaden mitigiert", Playerperformance::getDamageMitigated);
  }

}
