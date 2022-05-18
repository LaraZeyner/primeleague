package de.xeri.prm.models.match.ratings;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Lara on 12.05.2022 for web
 */
public abstract class RatingSubcategory {

  public abstract double get();

  protected double handleValues(Stat... stats) {
    final List<Stat> stats1 = Arrays.asList(stats);
    return stats1.stream().filter(Stat::isRelevant).mapToDouble(Stat::value).average().orElse(0) * 5;
  }
}
