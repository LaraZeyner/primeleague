package de.xeri.league.loader;

import java.util.Arrays;
import java.util.List;

import de.xeri.league.models.match.neu.DisplaystatSubtype;
import de.xeri.league.models.match.neu.DisplaystatType;
import de.xeri.league.models.match.neu.Rating;
import de.xeri.league.models.match.neu.StatCategory;
import de.xeri.league.models.match.neu.StatSubcategory;
import de.xeri.league.manager.Data;

/**
 * Created by Lara on 26.04.2022 for web
 */
public final class StatCatLoader {

  static {
    for (StatSubcategory subcategory : StatSubcategory.values()) {
      final List<Rating> ratings = Arrays.asList(
      Rating.get(new Rating(subcategory, DisplaystatType.SPIELER, DisplaystatSubtype.ALLGEMEIN)), 
      Rating.get(new Rating(subcategory, DisplaystatType.SPIELER, DisplaystatSubtype.TOP)), 
      Rating.get(new Rating(subcategory, DisplaystatType.SPIELER, DisplaystatSubtype.JUNGLE)), 
      Rating.get(new Rating(subcategory, DisplaystatType.SPIELER, DisplaystatSubtype.MIDDLE)), 
      Rating.get(new Rating(subcategory, DisplaystatType.SPIELER, DisplaystatSubtype.BOTTOM)), 
      Rating.get(new Rating(subcategory, DisplaystatType.SPIELER, DisplaystatSubtype.SUPPORT)), 
      Rating.get(new Rating(subcategory, DisplaystatType.MATCHUP, DisplaystatSubtype.TOP)), 
      Rating.get(new Rating(subcategory, DisplaystatType.MATCHUP, DisplaystatSubtype.JUNGLE)), 
      Rating.get(new Rating(subcategory, DisplaystatType.MATCHUP, DisplaystatSubtype.MIDDLE)), 
      Rating.get(new Rating(subcategory, DisplaystatType.MATCHUP, DisplaystatSubtype.BOTTOM)), 
      Rating.get(new Rating(subcategory, DisplaystatType.MATCHUP, DisplaystatSubtype.SUPPORT)), 
      Rating.get(new Rating(subcategory, DisplaystatType.CHAMPION, DisplaystatSubtype.ALLGEMEIN)), 
      Rating.get(new Rating(subcategory, DisplaystatType.CHAMPION, DisplaystatSubtype.TOP)), 
      Rating.get(new Rating(subcategory, DisplaystatType.CHAMPION, DisplaystatSubtype.JUNGLE)), 
      Rating.get(new Rating(subcategory, DisplaystatType.CHAMPION, DisplaystatSubtype.MIDDLE)), 
      Rating.get(new Rating(subcategory, DisplaystatType.CHAMPION, DisplaystatSubtype.BOTTOM)), 
      Rating.get(new Rating(subcategory, DisplaystatType.CHAMPION, DisplaystatSubtype.SUPPORT)), 
      Rating.get(new Rating(subcategory, DisplaystatType.TEAM, DisplaystatSubtype.ALLGEMEIN)), 
      Rating.get(new Rating(subcategory, DisplaystatType.TEAM, DisplaystatSubtype.POSTGAME)), 
      Rating.get(new Rating(subcategory, DisplaystatType.SPIEL, DisplaystatSubtype.POSTGAME)));

      if (subcategory.getCategory().equals(StatCategory.MENTALITY_AND_ADAPTION)) {
        ratings.forEach(rating -> rating.setValue((short) 200));
      }
    }
  }


  public static void load() {
    Data.getInstance().commit();
  }
}

