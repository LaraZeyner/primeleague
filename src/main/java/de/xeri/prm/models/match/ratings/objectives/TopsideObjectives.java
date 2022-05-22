package de.xeri.prm.models.match.ratings.objectives;

import java.util.Map;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.ratings.Stat;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class TopsideObjectives extends RatingSubcategory {
  private final Map<String, Double> playerperformances;
  private final Lane lane;

  public TopsideObjectives(Map<String, Double> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getBaronTime(), getBaronsTakedownsAttempts(), getBaronPowerplay(), getHeraldTurrets(), getHeraldMulticharge());
  }

  public Stat getBaronTime() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane)
        .reverse();
  }

  public Stat getBaronsTakedownsAttempts() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "baronTakedownsAttempts")
        .nullable()
        .sub("Baron-Kills", "baronKills")
        .sub("Baron-Executes", "baronExecutes");
  }

  public Stat getBaronPowerplay() {
    return new Stat(playerperformances, OutputType.NUMBER, 4, lane);
  }

  public Stat getHeraldTurrets() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane);
  }

  public Stat getHeraldMulticharge() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane);
  }

}
