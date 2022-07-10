package de.xeri.prm.models.match.ratings.income;

import java.util.List;
import java.util.Map;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.ratings.Stat;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class GeneralIncome extends RatingSubcategory {
  private final Map<String, Double> playerperformances;
  private final Lane lane;

  public GeneralIncome(Map<String, Double> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getCreepsPerMinute(), getXpPerMinute(), getGoldPerMinute(), getCreepAdvantage(), getTrueKDA());
  }

  @Override
  public List<String> getData() {
    return handleData(getCreepsPerMinute(), getXpPerMinute(), getGoldPerMinute(), getCreepAdvantage(), getTrueKDA());
  }

  public Stat getCreepsPerMinute() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "csPerMinute")
        .nullable()
        .sub("Total CS", "totalCreeps")
        .sub("Spielzeit", "duration");
  }

  public Stat getXpPerMinute() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane)
        .nullable()
        .sub("Total XP", "experience")
        .sub("Spielzeit", "duration");
  }

  public Stat getGoldPerMinute() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane)
        .nullable()
        .sub("Total Gold", "goldTotal")
        .sub("Spielzeit", "duration");
  }

  public Stat getCreepAdvantage() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "creepScoreAdvantage")
        .nullable()
        .sub("Total CS", "totalCreeps");
  }

  public Stat getTrueKDA() {
    return new Stat(playerperformances, OutputType.TEXT, 12, lane, "trueKdaValue") {
      @Override
      public String display() {
        return Math.round(playerperformances.get("trueKdaKills") * 10) / 10.0 + " / " +
            Math.round(playerperformances.get("trueKdaDeaths") * 10) / 10.0 + " / " +
            Math.round(playerperformances.get("trueKdaAssists") * 10) / 10.0;
      }
    }.nullable();
  }

}
