package de.xeri.prm.loader;

import java.util.Arrays;
import java.util.List;

import de.xeri.prm.manager.PrimeData;
import de.xeri.prm.models.match.ratings.DisplaystatSubtype;
import de.xeri.prm.models.match.ratings.Rating;
import de.xeri.prm.models.match.ratings.StatCategory;
import de.xeri.prm.models.match.ratings.StatSubcategory;

/**
 * Created by Lara on 26.04.2022 for web
 */
public final class StatCatLoader {

  static {
    for (StatSubcategory subcategory : StatSubcategory.values()) {
      final List<Rating> ratings = Arrays.asList(
      Rating.get(new Rating(subcategory, DisplaystatSubtype.ALLGEMEIN)),
      Rating.get(new Rating(subcategory, DisplaystatSubtype.TOP)),
      Rating.get(new Rating(subcategory, DisplaystatSubtype.JUNGLE)),
      Rating.get(new Rating(subcategory, DisplaystatSubtype.MIDDLE)),
      Rating.get(new Rating(subcategory, DisplaystatSubtype.BOTTOM)),
      Rating.get(new Rating(subcategory, DisplaystatSubtype.SUPPORT)));

      if (subcategory.getCategory().equals(StatCategory.MENTALITY_AND_ADAPTION)) {
        ratings.forEach(rating -> rating.setValue((short) 200));
      }
    }
  }


  public static void load() {
    PrimeData.getInstance().commit();
  }
}

