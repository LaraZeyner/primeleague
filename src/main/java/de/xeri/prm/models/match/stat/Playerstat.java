package de.xeri.prm.models.match.stat;

import de.xeri.prm.models.match.playerperformance.Playerperformance;

/**
 * Created by Lara on 22.04.2022 for web
 */
public class Playerstat extends Stat {
  public static final Playerstat ASSISTS = new Playerstat("Assists", Playerperformance.class, "assists", 0, true, false);
  public static final Playerstat DEATHS = new Playerstat("Deaths", Playerperformance.class, "deaths", 0, true, false);
  public static final Playerstat KILLS = new Playerstat("Kills", Playerperformance.class, "kills", 0, true, false);

  private final boolean timeQuery;

  public Playerstat(String displayName, Class sourceClass, String attribute, int digits, boolean timeQuery, boolean percent) {
    super(displayName, sourceClass, attribute, digits, percent);
    this.timeQuery = timeQuery;
  }

  public TimeStat at(int seconds) {
    if (timeQuery) {
      return new TimeStat(getDisplayName(), getSourceClass(), getAttribute(), getDigits(), timeQuery, seconds);
    }
    return null;
  }

  public boolean isTimeQuery() {
    return timeQuery;
  }
}
