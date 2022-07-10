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
public class ResourceManagement extends RatingSubcategory {
  private final Map<String, Double> playerperformances;
  private final Lane lane;

  public ResourceManagement(Map<String, Double> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getHealthState(), getResourceState(), getWaveState(), getPlanedResets());
  }

  @Override
  public List<String> getData() {
    return handleData(getHealthState(), getResourceState(), getWaveState(), getPlanedResets());
  }

  public Stat getHealthState() {
    return new Stat(playerperformances, OutputType.PERCENT, 2, lane, "averageLaneHealth")
        .nullable();
  }

  public Stat getResourceState() {
    return new Stat(playerperformances, OutputType.PERCENT, 2, lane, "averageLaneResource")
        .nullable();
  }

  public Stat getWaveState() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane, "waveState")
        .nullable();
  }

  public Stat getPlanedResets() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "plannedResets")
        .nullable()
        .sub("insgesamt", "resets")
        .sub("geplante Resets", "resets", "plannedResets");
  }

}
