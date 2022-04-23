package de.xeri.league.models.match.stat;

import java.util.List;

/**
 * Created by Lara on 22.04.2022 for web
 */
public class TimeStat extends Stat {
  private int seconds;

  public TimeStat(String displayName, Class sourceClass, String attribute, int digits, boolean percent, int seconds) {
    super(displayName, sourceClass, attribute, digits, percent);
    this.seconds = seconds;
  }

  public int getSeconds() {
    return seconds;
  }
}
