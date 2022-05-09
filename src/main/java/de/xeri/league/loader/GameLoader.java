package de.xeri.league.loader;

import de.xeri.league.game.RiotGameRequester;
import de.xeri.league.models.enums.QueueType;
import de.xeri.league.models.match.ScheduledGame;

/**
 * Created by Lara on 08.04.2022 for web
 */
public final class GameLoader {

  static {
    ScheduledGame.findMode(QueueType.TOURNEY).forEach(RiotGameRequester::loadCompetitive);

    ScheduledGame.findMode(QueueType.CLASH).forEach(RiotGameRequester::loadClashGame);
    ScheduledGame.findMode(QueueType.OTHER).forEach(RiotGameRequester::loadMatchmade);
  }

  public static void load() {

  }
}
