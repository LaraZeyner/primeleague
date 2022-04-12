package de.xeri.league.util.io.riot;

/**
 * Created by Lara on 08.04.2022 for web
 */
public class RiotURLGenerator {
  public static RiotMatchURLGenerator getMatch() {
    return new RiotMatchURLGenerator();
  }

  public static RiotAccountURLGenerator getAccount() {
    return new RiotAccountURLGenerator();
  }
}
