package de.xeri.prm.util;

import java.util.Set;

import de.xeri.prm.loader.MatchLoader;
import de.xeri.prm.models.league.TurnamentMatch;
import de.xeri.prm.util.logger.Logger;

/**
 * Created by Lara on 15.04.2022 for web
 */
public class BreakManager {
  public static void loop(long l) {
    long start = System.currentTimeMillis();
    int count = 0;
    l = l / 4;
    final Set<TurnamentMatch> turnamentMatches = TurnamentMatch.get();
    for (TurnamentMatch turnamentMatch : turnamentMatches) {
      if (l == 0) break;
      if (turnamentMatch.isNotClosed() && turnamentMatch.isRecently()) {
        MatchLoader.analyseMatchPage(turnamentMatch);
        l--;
        count++;
      }
    }
    Logger.getLogger("Break").info(count + " Matches in " + (System.currentTimeMillis() - start) / 1000 + "s");
  }
}
