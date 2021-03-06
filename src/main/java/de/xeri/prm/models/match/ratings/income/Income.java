package de.xeri.prm.models.match.ratings.income;

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
public class Income {
  private final Lane lane;
  private final Teamfights teamfights;
  private final Skirmishes skirmishes;
  private final EarlyIncome earlyIncome;
  private final GeneralIncome generalIncome;
  private final Itemization itemization;

  public Income(Map<String, Double> playerperformances, Lane lane) {
    this.lane = lane;
    this.teamfights = new Teamfights(playerperformances, lane);
    this.skirmishes = new Skirmishes(playerperformances, lane);
    this.earlyIncome = new EarlyIncome(playerperformances, lane);
    this.generalIncome = new GeneralIncome(playerperformances, lane);
    this.itemization = new Itemization(playerperformances, lane);
  }

  public double get() {
    double total = 0;
    total += teamfights.get() * Ratings.getRating(StatSubcategory.TEAMFIGHTING, lane.getSubtype()).getValue();
    total += skirmishes.get() * Ratings.getRating(StatSubcategory.SKIRMISHING, lane.getSubtype()).getValue();
    total += earlyIncome.get() * Ratings.getRating(StatSubcategory.EARLY_INCOME, lane.getSubtype()).getValue();
    total += generalIncome.get() * Ratings.getRating(StatSubcategory.INCOME, lane.getSubtype()).getValue();
    total += itemization.get() * Ratings.getRating(StatSubcategory.ITEMIZATION, lane.getSubtype()).getValue();

    return total / Const.RATING_FACTOR;
  }

  public String format() {
    double value = get() * Const.RATING_CAT_FACTOR;
    return String.valueOf(Math.round(value));
  }

  public double sum() {
    return teamfights.get() + skirmishes.get() + earlyIncome.get() + generalIncome.get() + itemization.get();
  }

  public List<String> subKeys() {
    return Util.subkeys(StatSubcategory.TEAMFIGHTING, StatSubcategory.SKIRMISHING, StatSubcategory.EARLY_INCOME, StatSubcategory.INCOME,
        StatSubcategory.ITEMIZATION);
  }

  public List<String> subValues() {
    return Util.subvalues(teamfights, skirmishes, earlyIncome, generalIncome, itemization);
  }

  public List<String> getSubcategoryStats(int id) {
    RatingSubcategory subcategory = Arrays.asList(teamfights, skirmishes, earlyIncome, generalIncome, itemization).get(id);
    return subcategory.getData();
  }

}
