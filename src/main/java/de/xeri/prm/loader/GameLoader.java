package de.xeri.prm.loader;

import de.xeri.prm.game.RiotGameRequester;
import de.xeri.prm.models.enums.QueueType;
import de.xeri.prm.models.match.ScheduledGame;

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
