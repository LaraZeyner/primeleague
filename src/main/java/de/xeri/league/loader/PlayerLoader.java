package de.xeri.league.loader;

import de.xeri.league.models.league.Account;
import de.xeri.league.util.Data;
import de.xeri.league.util.io.riot.RiotAccountRequester;
import de.xeri.league.util.logger.Logger;

/**
 * Created by Lara on 07.04.2022 for web
 */
public final class PlayerLoader {
  static {
    Logger logger = Logger.getLogger("Spieler laden");

    Account.get().stream().filter(Account::isValueable).filter(Account::isActive).forEach(RiotAccountRequester::loadElo);
    logger.info("Elos wurden aktualisiert.");
    Account.get().stream().filter(a -> !a.isActive()).filter(Account::isPlaying).forEach(a -> a.setActive(true));
    Account.get().stream().filter(Account::isValueable).filter(Account::isActive).forEach(GameIdLoader::loadGameIds);
    logger.info("Aktuelle Liga wurde aktualisiert.");
    Account.get().stream().filter(a -> !a.isValueable()).filter(Account::isActive).forEach(GameIdLoader::loadGameIds);
    logger.info("Alle Spieler wurden aktualisiert.");
  }

  public static void load() {
    Data.getInstance().commit();
  }

}
