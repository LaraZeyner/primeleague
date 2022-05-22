package de.xeri.prm.models.match.ratings.adaption;

import java.util.Map;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.ratings.Stat;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class Mentality extends RatingSubcategory {
  private final Map<String, Double> playerperformances;
  private final Lane lane;

  public Mentality(Map<String, Double> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getSurrender(), getLaneLeadAfterDiedEarly(), getFarmingFromBehind(), getWardingFromBehind(), getDeathsFromBehind());
  }

  public Stat getSurrender() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane)
        .nullable()
        .reverse();
  }

  public Stat getLaneLeadAfterDiedEarly() {
    return new Stat(playerperformances, OutputType.NUMBER, 4, lane, "leadDifferenceAfterDiedEarly");
  }

  public Stat getFarmingFromBehind() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane)
        .nullable();
  }

  public Stat getWardingFromBehind() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane)
        .nullable();
  }

  public Stat getDeathsFromBehind() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane)
        .nullable()
        .reverse();
  }

}
