package de.xeri.prm.models.match.ratings.fighting;

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
public class Fighting {
  private final Lane lane;
  private final Damage damage;
  private final Plays plays;
  private final Catches catches;
  private final Snowball snowball;
  private final StrongPhase strongPhase;

  public Fighting(Map<String, Double> playerperformances, Lane lane) {
    this.lane = lane;
    this.damage = new Damage(playerperformances, lane);
    this.plays = new Plays(playerperformances, lane);
    this.catches = new Catches(playerperformances, lane);
    this.snowball = new Snowball(playerperformances, lane);
    this.strongPhase = new StrongPhase(playerperformances, lane);
  }

  public double get() {
    double total = 0;
    total += damage.get() * Ratings.getRating(StatSubcategory.DAMAGE, lane.getSubtype()).getValue();
    total += plays.get() * Ratings.getRating(StatSubcategory.PLAYMAKING, lane.getSubtype()).getValue();
    total += catches.get() * Ratings.getRating(StatSubcategory.CATCHING, lane.getSubtype()).getValue();
    total += snowball.get() * Ratings.getRating(StatSubcategory.SNOWBALLING, lane.getSubtype()).getValue();
    total += strongPhase.get() * Ratings.getRating(StatSubcategory.STRONG_PHASE, lane.getSubtype()).getValue();

    return total / Const.RATING_FACTOR;
  }

  public String format() {
    double value = get() * Const.RATING_CAT_FACTOR;
    return String.valueOf(Math.round(value));
  }

  public double sum() {
    return damage.get() + plays.get() + catches.get() + snowball.get() + strongPhase.get();
  }

  public List<String> subKeys() {
    return Util.subkeys(StatSubcategory.DAMAGE, StatSubcategory.PLAYMAKING, StatSubcategory.CATCHING, StatSubcategory.SNOWBALLING,
        StatSubcategory.STRONG_PHASE);
  }

  public List<String> subValues() {
    return Util.subvalues(damage, plays, catches, snowball, strongPhase);
  }

  public List<String> getSubcategoryStats(int id) {
    RatingSubcategory subcategory = Arrays.asList(damage, plays, catches, snowball, strongPhase).get(id);
    return subcategory.getData();
  }

}
