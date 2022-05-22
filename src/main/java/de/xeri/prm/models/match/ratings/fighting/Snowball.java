package de.xeri.prm.models.match.ratings.fighting;

import java.util.Map;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.ratings.Stat;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class Snowball extends RatingSubcategory {
  private final Map<String, Double> playerperformances;
  private final Lane lane;

  public Snowball(Map<String, Double> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getKillsDeathsEarlygame(), getWinsIfAhead(), getLeadExtending());
  }

  public Stat getKillsDeathsEarlygame() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "kdEarly")
        .nullable()
        .sub("Kills Earlygame", "earlyKills");
  }

  public Stat getWinsIfAhead() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "winsAhead")
        .nullable()
        .sub("Ahead", "ahead");
  }

  public Stat getLeadExtending() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "aheadExtending")
        .nullable()
        .sub("Lane gesnowballt", "extendingLead");
  }

}
