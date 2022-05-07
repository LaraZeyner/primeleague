package de.xeri.league.game;

import de.xeri.league.models.enums.QueueType;
import de.xeri.league.models.match.ScheduledGame;
import de.xeri.league.util.Data;
import de.xeri.league.util.io.riot.RiotURLGenerator;
import lombok.val;

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
    val game = matchGenerator.getMatch(scheduledGame.getId());
    val timeline = matchGenerator.getTimeline(scheduledGame.getId());
    if (new GameAnalyser().validate(game, timeline, queueType))
      ScheduledGame.get().remove(scheduledGame);
    Data.getInstance().getSession().remove(scheduledGame);
    //Data.getInstance().commit();
  }
}
