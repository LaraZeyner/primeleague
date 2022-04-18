package de.xeri.league.util;

import java.util.Date;
import java.util.Set;

import de.xeri.league.loader.MatchLoader;
import de.xeri.league.models.league.TurnamentMatch;

/**
 * Created by Lara on 15.04.2022 for web
 */
public class BreakManager {
  public static long loop() {
    final Set<TurnamentMatch> turnamentMatches = TurnamentMatch.get();
    for (TurnamentMatch turnamentMatch : turnamentMatches) {
      if (turnamentMatch.isNotClosed() && turnamentMatch.getStart().before(new Date())) {
        MatchLoader.analyseMatchPage(turnamentMatch);
      }
    }
    return turnamentMatches.stream().filter(TurnamentMatch::isNotClosed)
        .filter(turnamentMatch -> turnamentMatch.getStart().before(new Date())).count();
  }
}
