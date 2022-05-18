package de.xeri.prm.models.match.ratings.roaming;

import java.util.List;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.StatSubcategory;
import de.xeri.prm.models.match.ratings.Ratings;
import de.xeri.prm.models.match.playerperformance.Playerperformance;
import de.xeri.prm.util.Const;
import lombok.Getter;

/**
 * Created by Lara on 12.05.2022 for web
 */
@Getter
public class Roaming {
  private final Lane lane;
  private final TurretPressure turretPressure;
  private final Macro macro;
  private final Roams roams;
  private final Ganks ganks;
  private final Dives dives;

  public Roaming(List<Playerperformance> playerperformances, Lane lane) {
    this.lane = lane;
    this.turretPressure = new TurretPressure(playerperformances, lane);
    this.macro = new Macro(playerperformances, lane);
    this.roams = new Roams(playerperformances, lane);
    this.ganks = new Ganks(playerperformances, lane);
    this.dives = new Dives(playerperformances, lane);
  }

  public double get() {
    double total = 0;
    total += turretPressure.get() * Ratings.getRating(StatSubcategory.TURRET_PRESSURE, lane.getSubtype()).getValue();
    total += macro.get() * Ratings.getRating(StatSubcategory.MACRO, lane.getSubtype()).getValue();
    total += roams.get() * Ratings.getRating(StatSubcategory.ROAMING, lane.getSubtype()).getValue();
    total += ganks.get() * Ratings.getRating(StatSubcategory.GANKING, lane.getSubtype()).getValue();
    total += dives.get() * Ratings.getRating(StatSubcategory.DIVING, lane.getSubtype()).getValue();

    return total / Const.RATING_FACTOR;
  }

  public String format() {
    double value = get() * Const.RATING_FACTOR;
    return String.valueOf(Math.round(value));
  }

  public double sum() {
    return turretPressure.get() + macro.get() + roams.get() + ganks.get() + dives.get();
  }

}
