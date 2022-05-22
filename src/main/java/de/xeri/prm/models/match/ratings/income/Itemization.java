package de.xeri.prm.models.match.ratings.income;

import java.util.Map;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.ratings.Stat;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class Itemization extends RatingSubcategory {
  private final Map<String, Double> playerperformances;
  private final Lane lane;

  public Itemization(Map<String, Double> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getLegendaryItems(), getItemsBought(), getMejaisTime(), getGrievousWoundsAndPenetrationTime(), getStartitemSold());
  }

  public Stat getLegendaryItems() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane)
        .nullable();
  }

  public Stat getItemsBought() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "itemsAmount")
        .ignore();
  }

  public Stat getMejaisTime() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane, "mejaisCompleted")
        .reverse();
  }

  public Stat getGrievousWoundsAndPenetrationTime() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane, "antiHealing")
        .ignore()
        .sub("Grievous-Wounds", "antiHealing")
        .sub("Penetration", "penetration")
        .sub("Amplifier", "damageBuild");
  }

  public Stat getStartitemSold() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane, "startItemSold");
  }

}
