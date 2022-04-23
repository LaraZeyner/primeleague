package de.xeri.league.loader;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.xeri.league.models.enums.QueueType;
import de.xeri.league.models.league.TurnamentMatch;
import de.xeri.league.models.match.Game;
import de.xeri.league.models.match.ScheduledGame;
import de.xeri.league.util.Data;
import de.xeri.league.util.io.riot.RiotGameRequester;

/**
 * Created by Lara on 08.04.2022 for web
 */
public final class GameLoader {

  static {
    final Stream<ScheduledGame> gameStream = ScheduledGame.get().stream();
    gameStream.filter(scheduledGame -> scheduledGame.getQueueType().equals(QueueType.TOURNEY)).forEach(RiotGameRequester::loadCompetitive);
    mergeTurnamentMatch();
    gameStream.filter(scheduledGame -> scheduledGame.getQueueType().equals(QueueType.CLASH)).forEach(RiotGameRequester::loadClashGame);
    Data.getInstance().commit();
    gameStream.filter(scheduledGame -> scheduledGame.getQueueType().equals(QueueType.OTHER)).forEach(RiotGameRequester::loadMatchmade);
  }

  private static void mergeTurnamentMatch() {
    for (TurnamentMatch match : TurnamentMatch.get()) {
      if (match.isOpen() && match.getHomeTeam() != null && match.getGuestTeam() != null) {
        final List<Game> gamesList = Game.get().stream().filter(game -> !match.getGames().contains(game))
            .filter(game -> game.getGametype().getId() == 0)
            .filter(game -> game.getTeams().contains(match.getHomeTeam()) && game.getTeams().contains(match.getGuestTeam()))
            .filter(game -> game.getDuration() > 300)
            .collect(Collectors.toList());
        gamesList.stream().limit(match.getGameAmount() - match.getGames().size()).forEach(match::addGame);
      }
    }
  }

  public static void load() {

  }
}
