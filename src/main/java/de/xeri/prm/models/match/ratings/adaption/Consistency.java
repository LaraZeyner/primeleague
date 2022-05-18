package de.xeri.prm.models.match.ratings.adaption;

import java.util.List;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.Stat;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.playerperformance.Playerperformance;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class Consistency extends RatingSubcategory {
  private final List<Playerperformance> playerperformances;
  private final Lane lane;

  public Consistency(List<Playerperformance> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getLevelupEarlier(), getGoldAdvantageFromAhead(), getGoldAdvantageFromBehind(), getXpAdvantageFromAhead(),
        getXpAdvantageFromBehind());
  }

  public Stat getLevelupEarlier() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane)
        .map(p -> p.getStats().getLevelupEarlier())
        .nullable();
  }

  public Stat getGoldAdvantageFromAhead() {
    return new Stat(playerperformances, OutputType.NUMBER, 4, lane, "goldFromAhead") {
      @Override
      public double calculate() {
        return playerperformances.stream()
            .filter(p -> p.getStats().isAhead())
            .mapToDouble(p -> p.getStats().getGoldFromBehind())
            .average().orElse(0);
      }
    }.nullable();
  }

  public Stat getGoldAdvantageFromBehind() {
    return new Stat(playerperformances, OutputType.NUMBER, 4, lane, "goldFromBehind") {
      @Override
      public double calculate() {
        return playerperformances.stream()
            .filter(p -> p.getStats().isBehind())
            .mapToDouble(p -> p.getStats().getGoldFromBehind())
            .average().orElse(0);
      }
    }.nullable();
  }

  public Stat getXpAdvantageFromAhead() {
    return new Stat(playerperformances, OutputType.NUMBER, 4, lane, "xpFromAhead") {
      @Override
      public double calculate() {
        return playerperformances.stream()
            .filter(p -> p.getStats().isAhead())
            .mapToDouble(p -> p.getStats().getXpFromBehind())
            .average().orElse(0);
      }
    }.nullable();
  }

  public Stat getXpAdvantageFromBehind() {
    return new Stat(playerperformances, OutputType.NUMBER, 4, lane, "xpFromBehind") {
      @Override
      public double calculate() {
        return playerperformances.stream()
            .filter(p -> p.getStats().isBehind())
            .mapToDouble(p -> p.getStats().getXpFromBehind())
            .average().orElse(0);
      }
    }.nullable();
  }

}
