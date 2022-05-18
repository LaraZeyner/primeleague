package de.xeri.prm.models.match.ratings.income;

import java.util.List;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.Stat;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.playerperformance.Playerperformance;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class Itemization extends RatingSubcategory {
  private final List<Playerperformance> playerperformances;
  private final Lane lane;

  public Itemization(List<Playerperformance> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getLegendaryItems(), getItemsBought(), getMejaisTime(), getGrievousWoundsAndPenetrationTime(), getStartitemSold());
  }

  public Stat getLegendaryItems() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane)
        .map(p -> p.getStats().getLegendaryItems())
        .nullable();
  }

  public Stat getItemsBought() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "itemsAmount")
        .map(Playerperformance::getItemsAmount)
        .ignore();
  }

  public Stat getMejaisTime() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane, "mejaisCompleted")
        .map(Playerperformance::getMejaisCompleted)
        .reverse();
  }

  public Stat getGrievousWoundsAndPenetrationTime() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane, null)
        .map(p -> p.getStats().getSituationalTime())
        .ignore()
        .sub("Grievous-Wounds", p -> p.getStats().getAntiHealTime())
        .sub("Penetration", p -> p.getStats().getPenetrationTime())
        .sub("Amplifier", p -> p.getStats().getAmplifierTime());
  }

  public Stat getStartitemSold() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane, "startItemSold")
        .map(p -> p.getStats().getStartItemSold());
  }

}
