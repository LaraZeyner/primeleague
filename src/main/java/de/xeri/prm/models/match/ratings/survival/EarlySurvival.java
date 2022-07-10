package de.xeri.prm.models.match.ratings.survival;

import java.util.List;
import java.util.Map;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.ratings.Stat;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class EarlySurvival extends RatingSubcategory {
  private final Map<String, Double> playerperformances;
  private final Lane lane;

  public EarlySurvival(Map<String, Double> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getFirstKillDeath(), getFirstBaseThroughRecall(), getLaneLeadWithoutDeaths());
  }

  @Override
  public List<String> getData() {
    return handleData(getFirstKillDeath(), getFirstBaseThroughRecall(), getLaneLeadWithoutDeaths());
  }

  public Stat getFirstKillDeath() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane, "firstKillDeathTime")
        .nullable()
        .sub("1. Kill", "firstKillTime")
        .sub("1. Death", "firstKillDeathTime");
  }

  public Stat getFirstBaseThroughRecall() {
    return new Stat(playerperformances, OutputType.PERCENT, 2, lane, "firstBaseThroughRecall")
        .nullable();
  }

  public Stat getLaneLeadWithoutDeaths() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane, "leadWithoutDying")
        .nullable();
  }

}
