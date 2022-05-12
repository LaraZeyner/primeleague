package de.xeri.prm.util;

import java.util.Date;
import java.util.Set;

import de.xeri.prm.loader.MatchLoader;
import de.xeri.prm.models.league.TurnamentMatch;

/**
 * Created by Lara on 15.04.2022 for web
 */
public class BreakManager {
  public static long loop(long l) {
    l = l * 3;
    final Set<TurnamentMatch> turnamentMatches = TurnamentMatch.get();
    for (TurnamentMatch turnamentMatch : turnamentMatches) {
      if (l == 0) break;
      if (turnamentMatch.isNotClosed() && turnamentMatch.isRecently()) {
        MatchLoader.analyseMatchPage(turnamentMatch);
        l--;
      }
    }
    return turnamentMatches.stream().filter(TurnamentMatch::isNotClosed)
        .filter(turnamentMatch -> turnamentMatch.getStart().before(new Date())).count();
  }
}
