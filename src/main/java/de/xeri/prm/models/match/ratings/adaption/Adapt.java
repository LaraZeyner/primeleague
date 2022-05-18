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
public class Adapt extends RatingSubcategory {
  private final List<Playerperformance> playerperformances;
  private final Lane lane;

  public Adapt(List<Playerperformance> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getAntiHealing(), getPenetration(), getDamageBuild(), getResistanceBuild(), getFarmstop());
  }

  public Stat getAntiHealing() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane)
        .map(p -> p.getStats().getAntiHealTime() != 0 ? 1 : 0)
        .nullable();
  }

  public Stat getPenetration() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane)
        .map(p -> p.getStats().getPenetrationTime() != 0 ? 1 : 0)
        .nullable();
  }

  public Stat getDamageBuild() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane)
        .map(p -> p.getStats().getAmplifierTime() != 0 ? 1 : 0)
        .nullable();
  }

  public Stat getResistanceBuild() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane)
        .map(p -> p.getStats().getDurabilityTime() != 0 ? 1 : 0)
        .nullable();
  }

  public Stat getFarmstop() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane)
        .map(p -> p.getStats().getCsDropAtMinute() != 0 ? 1 : 0)
        .nullable()
        .sub("Minute", p -> p.getStats().getCsDropAtMinute());
  }

}
