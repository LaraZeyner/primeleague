package de.xeri.prm.models.match.ratings.adaption;

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
public class Adaption {
  private final Lane lane;
  private final Mentality mentality;
  private final Consistency consistency;
  private final Adapt adaption;
  private final GameStats stats;

  public Adaption(Map<String, Double> playerperformances, Lane lane) {
    this.lane = lane;
    this.mentality = new Mentality(playerperformances, lane);
    this.consistency = new Consistency(playerperformances, lane);
    this.adaption = new Adapt(playerperformances, lane);
    this.stats = new GameStats(playerperformances, lane);
  }

  public double get() {
    double total = 0;
    total += mentality.get() * Ratings.getRating(StatSubcategory.GIVING_UP, lane.getSubtype()).getValue();
    total += consistency.get() * Ratings.getRating(StatSubcategory.CONSISTENCY, lane.getSubtype()).getValue();
    total += adaption.get() * Ratings.getRating(StatSubcategory.ADAPTION, lane.getSubtype()).getValue();
    total += stats.get() * Ratings.getRating(StatSubcategory.STATS, lane.getSubtype()).getValue();

    return total * 1.5 / Const.RATING_FACTOR;
  }

  public String format() {
    double value = get() * Const.RATING_CAT_FACTOR;
    return String.valueOf(Math.round(value));
  }

  public double sum() {
    return mentality.get() + consistency.get() + adaption.get() + stats.get();
  }

  public List<String> subKeys() {
    return Util.subkeys(StatSubcategory.GIVING_UP, StatSubcategory.CONSISTENCY, StatSubcategory.ADAPTION, StatSubcategory.STATS);
  }

  public List<String> subValues() {
    return Util.subvalues(mentality, consistency, adaption, stats);
  }

  public List<String> getSubcategoryStats(int id) {
    RatingSubcategory subcategory = Arrays.asList(mentality, consistency, adaption, stats).get(id);
    return subcategory.getData();
  }

}
