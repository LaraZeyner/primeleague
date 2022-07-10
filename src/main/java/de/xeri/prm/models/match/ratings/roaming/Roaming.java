package de.xeri.prm.models.match.ratings.roaming;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.ratings.Ratings;
import de.xeri.prm.models.match.ratings.StatSubcategory;
import de.xeri.prm.util.Const;
import de.xeri.prm.util.Util;
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

  public Roaming(Map<String, Double> playerperformances, Lane lane) {
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
    double value = get() * Const.RATING_CAT_FACTOR;
    return String.valueOf(Math.round(value));
  }

  public double sum() {
    return turretPressure.get() + macro.get() + roams.get() + ganks.get() + dives.get();
  }

  public List<String> subKeys() {
    return Util.subkeys(StatSubcategory.TURRET_PRESSURE, StatSubcategory.MACRO, StatSubcategory.ROAMING, StatSubcategory.GANKING,
        StatSubcategory.DIVING);
  }

  public List<String> subValues() {
    return Util.subvalues(turretPressure, macro, roams, ganks, dives);
  }

  public List<String> getSubcategoryStats(int id) {
    RatingSubcategory subcategory = Arrays.asList(turretPressure, macro, roams, ganks, dives).get(id);
    return subcategory.getData();
  }

}
