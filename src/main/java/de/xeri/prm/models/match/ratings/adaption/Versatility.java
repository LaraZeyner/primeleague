package de.xeri.prm.models.match.ratings.adaption;

import java.util.Arrays;
import java.util.List;

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

  @Override
  public List<String> getData() {
    return Arrays.asList("Objectives:", Math.round(objectives * 100) + "", "Roaming:", Math.round(roaming * 100) + "",
        "Fighting", Math.round(fighting * 100) + "", "Income", Math.round(income * 100) + "", "Survival", Math.round(survival * 100) + "");
  }
}
