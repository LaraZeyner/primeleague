package de.xeri.prm.models.match.ratings.laning;

import java.util.List;

import java.util.Map;

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
public class Laning {
  private final Lane lane;
  private final Resets resets;
  private final PreReset preReset;
  private final PostReset postReset;
  private final LaneBilance laneBilance;
  private final Playstyle playstyle;

  public Laning(Map<String, Double> playerperformances, Lane lane) {
    this.lane = lane;
    this.resets = new Resets(playerperformances, lane);
    this.preReset = new PreReset(playerperformances, lane);
    this.postReset = new PostReset(playerperformances, lane);
    this.laneBilance = new LaneBilance(playerperformances, lane);
    this.playstyle = new Playstyle(playerperformances, lane);
  }

  public double get() {
    double total = 0;
    total += resets.get() * Ratings.getRating(StatSubcategory.RESETS, lane.getSubtype()).getValue();
    total += preReset.get() * Ratings.getRating(StatSubcategory.PRE_FIRST_BASE, lane.getSubtype()).getValue();
    total += postReset.get() * Ratings.getRating(StatSubcategory.POST_FIRST_BASE, lane.getSubtype()).getValue();
    total += laneBilance.get() * Ratings.getRating(StatSubcategory.LANE_BILANCE, lane.getSubtype()).getValue();
    total += playstyle.get() * Ratings.getRating(StatSubcategory.PLAYSTYLE, lane.getSubtype()).getValue();

    return total / Const.RATING_FACTOR;
  }

  public String format() {
    double value = get() * Const.RATING_CAT_FACTOR;
    return String.valueOf(Math.round(value));
  }

  public double sum() {
  return resets.get() + preReset.get() + postReset.get() + laneBilance.get() + playstyle.get();
  }

}
