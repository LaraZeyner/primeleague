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
public class EarlySurvival extends RatingSubcategory {
  private final List<Playerperformance> playerperformances;
  private final Lane lane;

  public EarlySurvival(List<Playerperformance> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getFirstKillDeath(), getFirstBaseThroughRecall(), getLaneLeadWithoutDeaths());
  }

  public Stat getFirstKillDeath() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane, "firstKillDeathTime")
        .map(p -> p.getStats().getFirstKillDeathTime())
        .nullable()
        .sub("1. Kill", p -> p.getStats().getFirstKillTime())
        .sub("1. Death", p -> p.getStats().getFirstKillTime() - p.getStats().getFirstKillDeathTime());
  }

  public Stat getFirstBaseThroughRecall() {
    return new Stat(playerperformances, OutputType.PERCENT, 2, lane, null)
        .map(p -> p.getStats().isFirstBaseThroughRecall() ? 1 : 0)
        .nullable();
  }

  public Stat getLaneLeadWithoutDeaths() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane, "leadWithoutDying")
        .map(p -> p.getStats().getLeadWithoutDying())
        .nullable();
  }

}
