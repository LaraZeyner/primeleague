package de.xeri.prm.game;

import de.xeri.prm.manager.Data;
import de.xeri.prm.models.enums.QueueType;
import de.xeri.prm.models.match.ScheduledGame;
import de.xeri.prm.util.io.riot.RiotURLGenerator;
import lombok.val;
import lombok.var;

/**
 * Created by Lara on 08.04.2022 for web
 */
public final class RiotGameRequester {

  public static void loadCompetitive(ScheduledGame scheduledGame) {
    loadGame(scheduledGame, QueueType.TOURNEY);
  }

  public static void loadClashGame(ScheduledGame scheduledGame) {
    loadGame(scheduledGame, QueueType.CLASH);
  }

  public static void loadMatchmade(ScheduledGame scheduledGame) {
    loadGame(scheduledGame, QueueType.OTHER);
  }

  private static void loadGame(ScheduledGame scheduledGame, QueueType queueType) {
    val matchGenerator = RiotURLGenerator.getMatch();
    var game = matchGenerator.getMatch(scheduledGame.getId());

    if (game != null) {
      val timeline = matchGenerator.getTimeline(scheduledGame.getId());
      if (new GameAnalyser().validate(game, timeline, queueType)) {
        Data.getInstance().remove(scheduledGame);
      }
    } else {
      System.err.println("Spiel " + scheduledGame.getId() + " konnte nicht geladen werden");
    }
    Data.getInstance().getSession().remove(scheduledGame);
    Data.getInstance().commit();
  }
}
