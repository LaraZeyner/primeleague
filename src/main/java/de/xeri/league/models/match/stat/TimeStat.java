package de.xeri.league.models.match.stat;

import de.xeri.league.models.match.PlayerperformanceKill;

/**
 * Created by Lara on 22.04.2022 for web
 */
public class TimeStat extends Stat {
  public static TimeStat ASSISTS = new TimeStat("Assists", PlayerperformanceKill.class, null, 0, false);
  public static TimeStat DEATHS = new TimeStat("Deaths", PlayerperformanceKill.class, null, 0, false);
  public static TimeStat KILLS = new TimeStat("Kill", PlayerperformanceKill.class, null, 0, false);

  private int seconds;

  public TimeStat(String displayName, Class sourceClass, String attribute, int digits, boolean percent) {
    super(displayName, sourceClass, attribute, digits, percent);
    this.seconds = 900;
  }

  public TimeStat(String displayName, Class sourceClass, String attribute, int digits, boolean percent, int seconds) {
    super(displayName, sourceClass, attribute, digits, percent);
    this.seconds = seconds;
  }

  public int getSeconds() {
    return seconds;
  }

  public TimeStat getAdvanced(TimeStat timeStat) {
    if (timeStat.getAttribute() == null) {

    }
    return timeStat;
  }
}
