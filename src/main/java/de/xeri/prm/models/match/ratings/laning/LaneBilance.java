package de.xeri.prm.models.match.ratings.laning;

import java.util.List;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.Stat;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.playerperformance.Playerperformance;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class LaneBilance extends RatingSubcategory {
  private final List<Playerperformance> playerperformances;
  private final Lane lane;

  public LaneBilance(List<Playerperformance> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getLead(), getObjectiveAdvantage(), getTurretplateAdvantage(), getEnemyControlled());
  }

  public Stat getLead() {
    return new Stat(playerperformances, OutputType.NUMBER, 4, lane, "laneLead")
        .map(Playerperformance::getLaneLead)
        .nullable();
  }

  public Stat getObjectiveAdvantage() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "earlyObjectiveAdvantage")
        .map(p -> p.getStats().getEarlyObjectiveAdvantage())
        .nullable()
        .sub("Objectives erhalten", p -> p.getStats().getEarlyObjectives())
        .sub("Objectives abgegeben", p -> p.getStats().getEarlyObjectives() - p.getStats().getEarlyObjectiveAdvantage());
  }

  public Stat getTurretplateAdvantage() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane)
        .map(p -> p.getStats().getTurretplateAdvantage())
        .nullable()
        .sub("Turretplates zerstört", Playerperformance::getTurretplates)
        .sub("Turretplates verloren", p -> p.getTurretplates() - p.getStats().getTurretplateAdvantage());
  }

  public Stat getEnemyControlled() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane, "enemyControlAdvantageEarly")
        .map(p -> p.getStats().getEnemyControlAdvantageEarly())
        .nullable()
        .sub("Gegner kontrolliert", p -> p.getStats().getEnemyControlledEarly())
        .sub("Spieler kontrolliert", p -> p.getStats().getEnemyControlledEarly() - p.getStats().getEnemyControlAdvantageEarly());
  }

}
