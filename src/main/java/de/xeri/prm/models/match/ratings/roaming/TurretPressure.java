package de.xeri.prm.models.match.ratings.roaming;

import java.util.List;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.Stat;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.playerperformance.Playerperformance;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class TurretPressure extends RatingSubcategory {
  private final List<Playerperformance> playerperformances;
  private final Lane lane;

  public TurretPressure(List<Playerperformance> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getFirstTowerAdvantage(), getTurretPlatings(), getTurretTakedownsEarly(), getSplitpush(), getTurretParticipation());
  }

  public Stat getFirstTowerAdvantage() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane, "firstturretAdvantage")
        .map(Playerperformance::getFirstturretAdvantage)
        .nullable();
  }

  public Stat getTurretPlatings() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "turretplates")
        .map(Playerperformance::getTurretplates)
        .nullable();
  }

  public Stat getTurretTakedownsEarly() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "earlyTurrets")
        .map(Playerperformance::getEarlyTurrets)
        .nullable();
  }

  public Stat getSplitpush() {
    return new Stat(playerperformances, OutputType.NUMBER, 5, lane, "splitpushedTurrets")
        .map(p -> p.getStats().getSplitScore())
        .sub("Türme gesplitpusht", Playerperformance::getSplitpushedTurrets);
  }

  public Stat getTurretParticipation() {
    return new Stat(playerperformances, OutputType.PERCENT, 2, lane, "turretParticipation")
        .map(p -> p.getStats().getTurretParticipation())
        .nullable()
        .sub("beteiligt an", Playerperformance::getTurretTakedowns)
        .sub("gesamt", p -> p.getTeamperformance().getTowers());
  }

}
