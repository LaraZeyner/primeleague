package de.xeri.league;

import de.xeri.league.util.logger.Logger;

/**
 * Created by Lara on 06.04.2022 for web
 */
public class Test {

  public static void main(String[] args) {
    Logger logger = Logger.getLogger("Test");
    logger.severe("schwerer Fehler");
    logger.warning("Warnung");
    logger.attention("Warnung");
    logger.config("Konfiguration");
    logger.info("Information");
    logger.fine("information that will be broadly interesting");
    logger.finer("logging calls for entering, returning, or throwing an exception");
    logger.finest("highly detailed tracing message.");
    logger.throwing(new Exception());
  }
}
