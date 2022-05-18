package de.xeri.prm.models.match.ratings.survival;

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
public class Survival {
  private final Lane lane;
  private final EarlySurvival earlySurvival;
  private final GeneralSurvival generalSurvival;
  private final Utility utility;
  private final ResourceManagement resourceManagement;
  private final Isolation isolation;

  public Survival(List<Playerperformance> playerperformances, Lane lane) {
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
    double value = get() * Const.RATING_FACTOR;
    return String.valueOf(Math.round(value));
  }

  public double sum() {
    return earlySurvival.get() + generalSurvival.get() + utility.get() + resourceManagement.get() + isolation.get();
  }

}
