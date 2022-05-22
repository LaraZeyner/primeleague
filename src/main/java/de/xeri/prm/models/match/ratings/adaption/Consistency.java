package de.xeri.prm.models.match.ratings.adaption;

import java.util.Map;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.ratings.Stat;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class Consistency extends RatingSubcategory {
  private final Map<String, Double> playerperformances;
  private final Lane lane;

  public Consistency(Map<String, Double> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getLevelupEarlier(), getGoldAdvantageFromAhead(), getGoldAdvantageFromBehind(), getXpAdvantageFromAhead(),
        getXpAdvantageFromBehind());
  }

  public Stat getLevelupEarlier() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane)
        .nullable();
  }

  public Stat getGoldAdvantageFromAhead() {
    return new Stat(playerperformances, OutputType.NUMBER, 4, lane, "goldFromAhead")
        .nullable();
  }

  public Stat getGoldAdvantageFromBehind() {
    return new Stat(playerperformances, OutputType.NUMBER, 4, lane, "goldFromBehind")
        .nullable();
  }

  public Stat getXpAdvantageFromAhead() {
    return new Stat(playerperformances, OutputType.NUMBER, 4, lane, "xpFromAhead")
        .nullable();
  }

  public Stat getXpAdvantageFromBehind() {
    return new Stat(playerperformances, OutputType.NUMBER, 4, lane, "xpFromBehind")
        .nullable();
  }

}
