package de.xeri.prm.models.match.ratings.fighting;

import java.util.Map;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.ratings.Stat;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class Catches extends RatingSubcategory {
  private final Map<String, Double> playerperformances;
  private final Lane lane;

  public Catches(Map<String, Double> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getBountyGotten(), getAssassinations(), getPicksMade(), getAmbushes(), getDuelWinrate());
  }

  public Stat getBountyGotten() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane, "bountyDifference")
        .nullable()
        .sub("Bounty erhalten", "bountyGold");
  }

  public Stat getAssassinations() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "assassinated")
        .nullable();
  }

  public Stat getPicksMade() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "pickAdvantage")
        .nullable()
        .sub("Picks gemacht", "picksMade");
  }

  public Stat getAmbushes() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "ambush");
  }

  public Stat getDuelWinrate() {
    return new Stat(playerperformances, OutputType.PERCENT, 2, lane)
        .nullable()
        .sub("Duelle gewonnen", "duelWins");
  }

}
