package de.xeri.prm.models.match.ratings.objectives;

import java.util.Map;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.Ratings;
import de.xeri.prm.models.match.ratings.StatSubcategory;
import de.xeri.prm.util.Const;
import lombok.Getter;

/**
 * Created by Lara on 12.05.2022 for web
 */
@Getter
public class Objectives {
  private final Lane lane;
  private final ObjectivePressure objectivePressure;
  private final TopsideObjectives topsideObjectives;
  private final BotsideObjectives botsideObjectives;
  private final Wards wards;
  private final Controlwards controlwards;

  public Objectives(Map<String, Double> playerperformances, Lane lane) {
    this.lane = lane;
    this.objectivePressure = new ObjectivePressure(playerperformances, lane);
    this.topsideObjectives = new TopsideObjectives(playerperformances, lane);
    this.botsideObjectives = new BotsideObjectives(playerperformances, lane);
    this.wards = new Wards(playerperformances, lane);
    this.controlwards = new Controlwards(playerperformances, lane);
  }

  public double get() {
    double total = 0;
    total += objectivePressure.get() * Ratings.getRating(StatSubcategory.OBJECTIVE_PRESSURE, lane.getSubtype()).getValue();
    total += topsideObjectives.get() * Ratings.getRating(StatSubcategory.TOPSIDE_OBJECTIVES, lane.getSubtype()).getValue();
    total += botsideObjectives.get() * Ratings.getRating(StatSubcategory.BOTSIDE_OBJECTIVES, lane.getSubtype()).getValue();
    total += wards.get() * Ratings.getRating(StatSubcategory.WARDING, lane.getSubtype()).getValue();
    total += controlwards.get() * Ratings.getRating(StatSubcategory.CONTROLWARDS, lane.getSubtype()).getValue();

    return total / Const.RATING_FACTOR;
  }

  public String format() {
    double value = get() * Const.RATING_CAT_FACTOR;
    return String.valueOf(Math.round(value));
  }

  public double sum() {
    return objectivePressure.get() + topsideObjectives.get() + botsideObjectives.get() + wards.get() + controlwards.get();
  }

}
