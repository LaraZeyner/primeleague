package de.xeri.prm.models.match.ratings.fighting;

import java.util.List;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.Stat;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.playerperformance.Playerperformance;
import de.xeri.prm.models.match.playerperformance.PlayerperformanceInfo;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class StrongPhase extends RatingSubcategory {
  private final List<Playerperformance> playerperformances;
  private final Lane lane;

  public StrongPhase(List<Playerperformance> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getHighestLeadMinute(), getLowestLeadMinute(), getAllowComebacks(), getXpLead());
  }

  public Stat getHighestLeadMinute() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane) {
      @Override
      public double calculate() {
        double leadAtMax = Double.MIN_VALUE;
        int leadAtMaxMinute = 0;
        for (int i = 1; i < 101; i++) {
          final int finalI = i;
          double leadAt = playerperformances.stream()
              .flatMap(playerperformance -> playerperformance.getInfos().stream())
              .filter(info -> info.getMinute() == finalI)
              .mapToInt(PlayerperformanceInfo::getLead)
              .average().orElse(0);
          if (leadAtMax < leadAt) {
            leadAtMax = leadAt;
            leadAtMaxMinute = i;
          }
        }
        return leadAtMaxMinute;
      }
    }.ignore();
  }

  public Stat getLowestLeadMinute() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane) {
      @Override
      public double calculate() {
        double leadAtMin = Double.MAX_VALUE;
        int leadAtMinMinute = 0;
        for (int i = 1; i < 101; i++) {
          final int finalI = i;
          double leadAt = playerperformances.stream()
              .flatMap(playerperformance -> playerperformance.getInfos().stream())
              .filter(info -> info.getMinute() == finalI)
              .mapToInt(PlayerperformanceInfo::getLead)
              .average().orElse(0);
          if (leadAtMin > leadAt) {
            leadAtMin = leadAt;
            leadAtMinMinute = i;
          }
        }
        return leadAtMinMinute;
      }
    }.ignore();
  }

  public Stat getAllowComebacks() {
    return new Stat(playerperformances, OutputType.PERCENT, 2, lane, null) {
      @Override
      public double calculate() {
        return playerperformances.stream()
            .filter(p -> p.getStats().isBehind())
            .mapToDouble(p -> p.getStats().isComeback() ? 1 : 0)
            .average().orElse(0) +
            playerperformances.stream()
                .filter(p -> p.getStats().isAhead())
                .mapToDouble(p -> p.getStats().isComeback() ? -1 : 0)
                .average().orElse(0);
      }
    }.nullable()
        .sub("Vorsprung abgebaut", p -> p.getStats().isAhead() && p.getStats().isComeback() ? 1 : 0)
        .sub("Rückstand aufgeholt", p -> p.getStats().isBehind() && p.getStats().isComeback() ? 1 : 0);
  }

  public Stat getXpLead() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane)
        .map(p -> p.getStats().getXpLead())
        .nullable();
  }

}
