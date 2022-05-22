package de.xeri.prm.models.match.ratings.adaption;

import java.util.Map;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.ratings.Stat;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class Adapt extends RatingSubcategory {
  private final Map<String, Double> playerperformances;
  private final Lane lane;

  public Adapt(Map<String, Double> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getAntiHealing(), getPenetration(), getDamageBuild(), getResistanceBuild(), getFarmstop());
  }

  public Stat getAntiHealing() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane)
        .nullable();
  }

  public Stat getPenetration() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane)
        .nullable();
  }

  public Stat getDamageBuild() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane)
        .nullable();
  }

  public Stat getResistanceBuild() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane)
        .nullable();
  }

  public Stat getFarmstop() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane)
        .nullable();
  }

}
