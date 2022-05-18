package de.xeri.prm.models.match.ratings.fighting;

import java.util.List;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.Stat;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.playerperformance.Playerperformance;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class Catches extends RatingSubcategory {
  private final List<Playerperformance> playerperformances;
  private final Lane lane;

  public Catches(List<Playerperformance> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getBountyGotten(), getAssassinations(), getPicksMade(), getAmbushes(), getDuelWinrate());
  }

  public Stat getBountyGotten() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane, "bountyDifference")
        .map(p -> p.getStats().getBountyDifference())
        .nullable()
        .sub("Bounty erhalten", Playerperformance::getBountyGold)
        .sub("Bounty gegeben", p -> p.getStats().getBountyDifference() - p.getBountyGold());
  }

  public Stat getAssassinations() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "assassinated")
        .map(Playerperformance::getAssassinated)
        .nullable();
  }

  public Stat getPicksMade() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "pickAdvantage")
        .map(p -> p.getStats().getPickAdvantage())
        .nullable()
        .sub("Picks gemacht", Playerperformance::getPicksMade)
        .sub("gepickt worden", p -> p.getPicksMade() - p.getStats().getPickAdvantage());
  }

  public Stat getAmbushes() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "ambush")
        .map(Playerperformance::getAmbush);
  }

  public Stat getDuelWinrate() {
    return new Stat(playerperformances, OutputType.PERCENT, 2, lane)
        .map(p -> p.getStats().getDuelWinrate())
        .nullable()
        .sub("Duelle gewonnen", p -> p.getStats().getDuelWins())
        .sub("Duelle verloren", p -> p.getStats().getDuelWins() * (1 - p.getStats().getDuelWinrate()));
  }

}
