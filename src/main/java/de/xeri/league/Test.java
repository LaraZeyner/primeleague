package de.xeri.league;

import java.util.Date;

import de.xeri.league.manager.LoadupManager;
import de.xeri.league.util.logger.Logger;

/**
 * Created by Lara on 06.04.2022 for web
 */
public class Test {
  private static final Logger logger = Logger.getLogger("Test");

  public static void main(String[] args) {
    final Date date = LoadupManager.init();

    if (LoadupManager.loadGames()) {
      logger.info("Spiele geladen");
    }

    System.out.println((System.currentTimeMillis() - date.getTime()) / 1000.0 + " sec");
    System.out.println("TRUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUE");
  }
}
