package de.xeri.league.game;

import de.xeri.league.manager.Data;
import de.xeri.league.models.enums.QueueType;
import de.xeri.league.models.match.ScheduledGame;
import de.xeri.league.util.io.riot.RiotURLGenerator;
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
    for (int i = 0; i < 5; i++) {
      if (game != null) {
        break;
      }
      try {
        Thread.sleep(5_000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      game = matchGenerator.getMatch(scheduledGame.getId());
    }

    if (game != null) {
      val timeline = matchGenerator.getTimeline(scheduledGame.getId());
      if (new GameAnalyser().validate(game, timeline, queueType)) {
        Data.getInstance().remove(scheduledGame);
      }
      System.out.println("Spiel " + scheduledGame.getId() + " geladen");
    } else {
      System.err.println("Spiel " + scheduledGame.getId() + " konnte nicht geladen werden");
    }
    Data.getInstance().getSession().remove(scheduledGame);
    Data.getInstance().commit();
  }
}
