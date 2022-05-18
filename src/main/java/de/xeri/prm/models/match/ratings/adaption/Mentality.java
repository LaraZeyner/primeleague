package de.xeri.prm.models.match.ratings.adaption;

import java.util.List;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.Stat;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.playerperformance.Playerperformance;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class Mentality extends RatingSubcategory {
  private final List<Playerperformance> playerperformances;
  private final Lane lane;

  public Mentality(List<Playerperformance> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getSurrender(), getLaneLeadAfterDiedEarly(), getFarmingFromBehind(), getWardingFromBehind(), getDeathsFromBehind());
  }

  public Stat getSurrender() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, null)
        .map(p -> p.getTeamperformance().isSurrendered() ? 1 : 0)
        .nullable()
        .reverse();
  }

  public Stat getLaneLeadAfterDiedEarly() {
    return new Stat(playerperformances, OutputType.NUMBER, 4, lane, "leadDifferenceAfterDiedEarly")
        .map(p -> p.getStats().getLeadDifferenceAfterDiedEarly());
  }

  public Stat getFarmingFromBehind() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane, "farmingFromBehind")
        .map(p -> p.getStats().getFarmingFromBehind())
        .nullable();
  }

  public Stat getWardingFromBehind() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "wardingFromBehind")
        .map(p -> p.getStats().getWardingFromBehind())
        .nullable();
  }

  public Stat getDeathsFromBehind() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane)
        .map(p -> p.getStats().getDeathsFromBehind())
        .nullable()
        .reverse();
  }

}
