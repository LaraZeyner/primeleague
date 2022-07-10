package de.xeri.prm.models.match.ratings.survival;

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
public class Survival {
  private final Lane lane;
  private final EarlySurvival earlySurvival;
  private final GeneralSurvival generalSurvival;
  private final Utility utility;
  private final ResourceManagement resourceManagement;
  private final Isolation isolation;

  public Survival(Map<String, Double> playerperformances, Lane lane) {
    this.lane = lane;
    this.earlySurvival = new EarlySurvival(playerperformances, lane);
    this.generalSurvival = new GeneralSurvival(playerperformances, lane);
    this.utility = new Utility(playerperformances, lane);
    this.resourceManagement = new ResourceManagement(playerperformances, lane);
    this.isolation = new Isolation(playerperformances, lane);
  }

  public double get() {
    double total = 0;
    total += earlySurvival.get() * Ratings.getRating(StatSubcategory.EARLY_SURVIVAL, lane.getSubtype()).getValue();
    total += generalSurvival.get() * Ratings.getRating(StatSubcategory.SURVIVAL, lane.getSubtype()).getValue();
    total += utility.get() * Ratings.getRating(StatSubcategory.TEAM_UTILITY, lane.getSubtype()).getValue();
    total += resourceManagement.get() * Ratings.getRating(StatSubcategory.WAVE_RESOURCE_MANAGEMENT, lane.getSubtype()).getValue();
    total += isolation.get() * Ratings.getRating(StatSubcategory.ISOLATION, lane.getSubtype()).getValue();

    return total / Const.RATING_FACTOR;
  }

  public String format() {
    double value = get() * Const.RATING_CAT_FACTOR;
    return String.valueOf(Math.round(value));
  }

  public double sum() {
    return earlySurvival.get() + generalSurvival.get() + utility.get() + resourceManagement.get() + isolation.get();
  }

  public List<String> subKeys() {
    return Util.subkeys(StatSubcategory.EARLY_SURVIVAL, StatSubcategory.SURVIVAL, StatSubcategory.TEAM_UTILITY,
        StatSubcategory.WAVE_RESOURCE_MANAGEMENT, StatSubcategory.ISOLATION);
  }

  public List<String> subValues() {
    return Util.subvalues(earlySurvival, generalSurvival, utility, resourceManagement, isolation);
  }

  public List<String> getSubcategoryStats(int id) {
    RatingSubcategory subcategory = Arrays.asList(earlySurvival, generalSurvival, utility, resourceManagement, isolation).get(id);
    return subcategory.getData();
  }

}
