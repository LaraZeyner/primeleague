package de.xeri.prm.models.match.ratings.income;

import java.util.List;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.Stat;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.playerperformance.Playerperformance;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class GeneralIncome extends RatingSubcategory {
  private final List<Playerperformance> playerperformances;
  private final Lane lane;

  public GeneralIncome(List<Playerperformance> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getCreepsPerMinute(), getXpPerMinute(), getGoldPerMinute(), getCreepAdvantage(), getTrueKDA());
  }

  public Stat getCreepsPerMinute() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "csPerMinute")
        .map(p -> p.getStats().getCsPerMinute())
        .nullable()
        .sub("Total CS", Playerperformance::getTotalCreeps)
        .sub("Minuten", p -> p.getTeamperformance().getGame().getDuration() * 1d / 60);
  }

  public Stat getXpPerMinute() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane)
        .map(p -> p.getStats().getXpPerMinute())
        .nullable()
        .sub("Total XP", Playerperformance::getExperience)
        .sub("Minuten", p -> p.getTeamperformance().getGame().getDuration() * 1d / 60);
  }

  public Stat getGoldPerMinute() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane)
        .map(p -> p.getStats().getGoldPerMinute())
        .nullable()
        .sub("Total Gold", Playerperformance::getGoldTotal)
        .sub("Minuten", p -> p.getTeamperformance().getGame().getDuration() * 1d / 60);
  }

  public Stat getCreepAdvantage() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "creepScoreAdvantage")
        .map(Playerperformance::getCreepScoreAdvantage)
        .nullable()
        .sub("Total CS", Playerperformance::getTotalCreeps);
  }

  public Stat getTrueKDA() {
    return new Stat(playerperformances, OutputType.TEXT, 12, lane, "trueKdaValue") {
      @Override
      public String display() {
        return Math.round(playerperformances.stream().mapToDouble(p -> p.getStats().getTrueKdaKills()).average()
            .orElse(0) * 10) / 10.0 + " / " +
            Math.round(playerperformances.stream().mapToDouble(p -> p.getStats().getTrueKdaDeaths()).average()
                .orElse(1) * 10) / 10.0 + " / " +
            Math.round(playerperformances.stream().mapToDouble(p -> p.getStats().getTrueKdaAssists()).average()
                .orElse(0) * 10) / 10.0;
      }
    }.map(p -> p.getStats().getTrueKdaValue())
        .nullable();
  }

}
