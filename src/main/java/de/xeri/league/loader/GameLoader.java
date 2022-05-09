package de.xeri.league.loader;

import java.util.Set;
import java.util.stream.Collectors;

import de.xeri.league.game.RiotGameRequester;
import de.xeri.league.models.enums.QueueType;
import de.xeri.league.models.league.Team;
import de.xeri.league.models.league.TurnamentMatch;
import de.xeri.league.models.match.Game;
import de.xeri.league.models.match.Gametype;
import de.xeri.league.models.match.ScheduledGame;
import de.xeri.league.models.match.Teamperformance;
import de.xeri.league.util.Data;
import lombok.val;

/**
 * Created by Lara on 08.04.2022 for web
 */
public final class GameLoader {

  static {
    ScheduledGame.findMode(QueueType.TOURNEY).forEach(RiotGameRequester::loadCompetitive);
    //mergeTurnamentMatch();

    ScheduledGame.findMode(QueueType.CLASH).forEach(RiotGameRequester::loadClashGame);
    ScheduledGame.findMode(QueueType.OTHER).forEach(RiotGameRequester::loadMatchmade);
    //mergeTurnamentMatch();
  }

  private static void mergeTurnamentMatch() {
    final Gametype customs = Gametype.find((short) 0);
    final Gametype tourneys = Gametype.find((short) -1);

    final Set<Game> games = customs.getGames();
    games.addAll(tourneys.getGames());
    for (Game game : games) {
      if (game.getTeams().size() == 2) {
      }
    }


    for (TurnamentMatch match : TurnamentMatch.get()) {
      final Team homeTeam = match.getHomeTeam();
      final Team guestTeam = match.getGuestTeam();
      if (match.isOpen() && (homeTeam != null && guestTeam != null)) {
        val gamesHome = homeTeam.getTeamperformances().stream().map(Teamperformance::getGame).collect(Collectors.toList());
        val gamesGuest = guestTeam.getTeamperformances().stream().map(Teamperformance::getGame).collect(Collectors.toList());
        for (Game game : gamesHome) {
          if (gamesGuest.contains(game)) {
            match.addGame(game);
          }
        }
      }
    }
    Data.getInstance().commit();
  }

  public static void load() {

  }
}
