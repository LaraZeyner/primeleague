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
public class Skirmishes extends RatingSubcategory {
  private final List<Playerperformance> playerperformances;
  private final Lane lane;

  public Skirmishes(List<Playerperformance> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getParticipation(), getKillBilance(), getSuccessRate(), getDamage());
  }

  public Stat getParticipation() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "skirmishParticipation")
        .map(p -> p.getStats().getSkirmishParticipation())
        .nullable()
        .sub("Anzahl Skirmishes", p -> p.getStats().getSkirmishAmount());
  }

  public Stat getKillBilance() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane, "skirmishKillsPerSkirmish")
        .map(p -> p.getStats().getSkirmishKillsPerSkirmish())
        .nullable();
  }

  public Stat getSuccessRate() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "skirmishWinrate")
        .map(p -> p.getStats().getSkirmishWinrate())
        .nullable()
        .sub("Skirmishes gewonnen", p -> p.getStats().getSkirmishAmount() * p.getStats().getSkirmishWinrate());
  }

  public Stat getDamage() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "skirmishDamageRate")
        .map(p -> p.getStats().getSkirmishDamageRate())
        .nullable()
        .sub("Schaden insgesamt", Playerperformance::getDamageTotal)
        .sub("Schaden in Skirmishes", p -> p.getDamageTotal() * p.getStats().getSkirmishDamageRate())
        .sub("Schaden pro Skirmish", p -> p.getDamageTotal() * p.getStats().getSkirmishDamageRate() / p.getStats().getSkirmishAmount());
  }

}
