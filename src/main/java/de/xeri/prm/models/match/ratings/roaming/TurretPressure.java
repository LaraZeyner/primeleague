package de.xeri.prm.models.match.ratings.roaming;

import java.util.Map;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.ratings.Stat;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class TurretPressure extends RatingSubcategory {
  private final Map<String, Double> playerperformances;
  private final Lane lane;

  public TurretPressure(Map<String, Double> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getFirstTowerAdvantage(), getTurretPlatings(), getTurretTakedownsEarly(), getSplitpush(), getTurretParticipation());
  }

  public Stat getFirstTowerAdvantage() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane, "firstturretAdvantage")
        .nullable();
  }

  public Stat getTurretPlatings() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "turretplates")
        .nullable();
  }

  public Stat getTurretTakedownsEarly() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "earlyTurrets")
        .nullable();
  }

  public Stat getSplitpush() {
    return new Stat(playerperformances, OutputType.NUMBER, 5, lane, "splitpushedTurrets")
        .sub("Türme gesplitpusht", "splitpushedTurrets");
  }

  public Stat getTurretParticipation() {
    return new Stat(playerperformances, OutputType.PERCENT, 2, lane, "turretParticipation")
        .nullable()
        .sub("beteiligt an", "turretTakedowns")
        .sub("gesamt", "towers");
  }

}
