package de.xeri.league;

import java.util.Date;

import de.xeri.league.manager.LoadupManager;
import de.xeri.league.util.logger.Logger;

/**
 * Created by Lara on 22.03.2022 for TRUES
 */
public final class Main {
  private static final Logger logger = Logger.getLogger("Main");

  public static void main(String[] args) {
    final Date date = LoadupManager.init();

    if (LoadupManager.loadRiotData()) {
      logger.info("Riots Daten geladen");
    }

    if (LoadupManager.loadPrimeLeague()) {
      logger.info("Prime League geladen");
    }

    if (LoadupManager.loadGames()) {
      logger.info("Spiele geladen");
    }

    System.out.println((System.currentTimeMillis() - date.getTime()) / 1000.0 + " sec");
    System.out.println("TRUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUE");
  }
}
