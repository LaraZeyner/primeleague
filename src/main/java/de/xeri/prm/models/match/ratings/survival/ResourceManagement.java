package de.xeri.prm.models.match.ratings.survival;

import java.util.List;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.Stat;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.playerperformance.Playerperformance;
import lombok.var;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class ResourceManagement extends RatingSubcategory {
  private final List<Playerperformance> playerperformances;
  private final Lane lane;

  public ResourceManagement(List<Playerperformance> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getHealthState(), getResourceState(), getWaveState(), getPlanedResets());
  }

  public Stat getHealthState() {
    return new Stat(playerperformances, OutputType.PERCENT, 2, lane, "averageLaneHealth")
        .map(p -> p.getStats().getAverageLaneHealth())
        .nullable();
  }

  public Stat getResourceState() {
    return new Stat(playerperformances, OutputType.PERCENT, 2, lane, "averageLaneResource")
        .map(p -> p.getStats().getAverageLaneResource())
        .nullable();
  }

  public Stat getWaveState() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane, "waveState") {
      @Override
      public double calculate() {
        var holds = playerperformances.stream().mapToDouble(p -> p.getStats().getHolds()).average().orElse(0);
        var freezes = playerperformances.stream().mapToDouble(p -> p.getStats().getFreezes()).average().orElse(0);
        var pushes = playerperformances.stream().mapToDouble(p -> p.getStats().getPushes()).average().orElse(0);
        return Math.min(holds, Math.min(freezes, pushes));
      }
    }.nullable();
  }

  public Stat getPlanedResets() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "plannedResets")
        .map(p -> p.getStats().getPlannedResets())
        .nullable()
        .sub("insgesamt", p -> p.getStats().getResets())
        .sub("geplante Resets", p -> p.getStats().getResets() * p.getStats().getPlannedResets());
  }

}
