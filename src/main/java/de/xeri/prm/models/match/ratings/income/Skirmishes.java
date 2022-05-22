package de.xeri.prm.models.match.ratings.income;

import java.util.Map;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.ratings.Stat;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class Skirmishes extends RatingSubcategory {
  private final Map<String, Double> playerperformances;
  private final Lane lane;

  public Skirmishes(Map<String, Double> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getParticipation(), getKillBilance(), getSuccessRate(), getDamage());
  }

  public Stat getParticipation() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "skirmishParticipation")
        .nullable()
        .sub("Anzahl Skirmishes", "skirmishAmount");
  }

  public Stat getKillBilance() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane, "skirmishKillsPerSkirmish")
        .nullable();
  }

  public Stat getSuccessRate() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "skirmishWinrate")
        .nullable()
        .sub("Skirmishes gewonnen", "skirmishAmount", "skirmishWinrate");
  }

  public Stat getDamage() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "skirmishDamageRate")
        .nullable()
        .sub("Schaden in Skirmishes", "damageTotal", "skirmishDamageRate");
  }

}
