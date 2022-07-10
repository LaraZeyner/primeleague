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
public class Teamfights extends RatingSubcategory {
  private final Map<String, Double> playerperformances;
  private final Lane lane;

  public Teamfights(Map<String, Double> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getParticipation(), getMultikills(), getDeathOrder(), getSuccessRate(), getAcesEarlyAndCleanFights(), getDamage());
  }

  @Override
  public List<String> getData() {
    return handleData(getParticipation(), getMultikills(), getDeathOrder(), getSuccessRate(), getAcesEarlyAndCleanFights(), getDamage());
  }

  public Stat getParticipation() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "teamfightParticipation")
        .nullable()
        .sub("Anzahl Teamfights", "teamfightAmount");
  }

  public Stat getMultikills() {
    return new Stat(playerperformances, OutputType.TEXT, 15, lane, "multiKills")
        .nullable();
  }

  public Stat getDeathOrder() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane, "averageDeathOrder");
  }

  public Stat getSuccessRate() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "teamfightWinrate")
        .nullable()
        .sub("Teamfights gewonnen", "teamfightAmount", "teamfightWinrate");
  }

  public Stat getAcesEarlyAndCleanFights() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "acesAndClean")
        .nullable();
  }

  public Stat getDamage() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "teamfightDamageRate")
        .nullable()
        .sub("Schaden in Teamfights", "damageTotal", "teamfightDamageRate");
  }

}
