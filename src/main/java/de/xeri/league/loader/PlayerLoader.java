package de.xeri.league.loader;

import java.util.LinkedHashSet;
import java.util.Set;

import de.xeri.league.models.league.Account;
import de.xeri.league.models.league.Player;
import de.xeri.league.models.league.Team;
import de.xeri.league.util.Data;
import de.xeri.league.util.io.riot.RiotAccountRequester;
import de.xeri.league.util.logger.Logger;

/**
 * Created by Lara on 07.04.2022 for web
 */
public final class PlayerLoader {
  static {
    Logger logger = Logger.getLogger("Spieler laden");

    Set<Team> teamList = new LinkedHashSet<>(Data.getInstance().getCurrentGroup().getTeams());
    teamList.addAll(Team.findScrim());

    for (Team team : teamList) {
      for (Player player : team.getPlayers()) {
        final Account account = player.getActiveAccount();
        if (account != null) {
          if (account.isActive()) {
            if (account.isValueable()) {
              RiotAccountRequester.loadElo(account);
            }
            GameIdLoader.loadGameIds(account);
            logger.info("Spieler " + player.getName() + " geladen");

          } else if (!account.isActive()) {
            if (account.isPlaying()) {
              account.setActive(true);
            }
          }
        }
      }
    }

    logger.info("Alle Spieler wurden aktualisiert.");
  }

  public static void load() {
    Data.getInstance().commit();
  }

}
