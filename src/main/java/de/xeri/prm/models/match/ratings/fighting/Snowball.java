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
public class Snowball extends RatingSubcategory {
  private final List<Playerperformance> playerperformances;
  private final Lane lane;

  public Snowball(List<Playerperformance> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getKillsDeathsEarlygame(), getWinsIfAhead(), getLeadExtending());
  }

  public Stat getKillsDeathsEarlygame() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "kdEarly")
        .map(p -> p.getStats().getKdEarly())
        .nullable()
        .sub("Kills Earlygame", Playerperformance::getEarlyKills)
        .sub("Deaths Earlygame", p -> p.getEarlyKills() - p.getStats().getKdEarly());
  }

  public Stat getWinsIfAhead() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, null) {
      @Override
      public double calculate() {
        return playerperformances.stream()
            .filter(p -> p.getStats().isAhead())
            .mapToDouble(p -> p.getTeamperformance().isWin() ? 1 : 0)
            .average().orElse(0);
      }
    }.nullable()
        .sub("Ahead", p -> p.getStats().isAhead() ? 1 : 0);
  }

  public Stat getLeadExtending() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, null) {
      @Override
      public double calculate() {
        return playerperformances.stream()
            .filter(p -> p.getStats().isAhead())
            .mapToDouble(p -> p.getStats().isExtendingLead() ? 1 : 0)
            .average().orElse(0);
      }
    }.nullable()
        .sub("Lane gesnowballt", p -> p.getStats().isExtendingLead() ? 1 : 0);
  }

}
