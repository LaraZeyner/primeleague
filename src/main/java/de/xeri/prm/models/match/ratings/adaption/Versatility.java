package de.xeri.prm.models.match.ratings.adaption;

import de.xeri.prm.models.match.ratings.RatingSubcategory;
import lombok.Getter;

/**
 * Created by Lara on 12.05.2022 for web
 */
@Getter
public class Versatility extends RatingSubcategory {
  private final double objectives;
  private final double roaming;
  private final double fighting;
  private final double income;
  private final double survival;

  public Versatility(double objectives, double roaming, double fighting, double income, double survival) {
    this.objectives = objectives;
    this.roaming = roaming;
    this.fighting = fighting;
    this.income = income;
    this.survival = survival;
  }

  public double get() {
    return (objectives + roaming + fighting + income + survival) / 5;
  }
}
