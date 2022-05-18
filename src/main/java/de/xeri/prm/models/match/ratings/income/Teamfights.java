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
public class Teamfights extends RatingSubcategory {
  private final List<Playerperformance> playerperformances;
  private final Lane lane;

  public Teamfights(List<Playerperformance> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getParticipation(), getMultikills(), getDeathOrder(), getSuccessRate(), getAcesEarlyAndCleanFights(), getDamage());
  }

  public Stat getParticipation() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "teamfightParticipation")
        .map(p -> p.getStats().getTeamfightParticipation())
        .nullable()
        .sub("Anzahl Teamfights", p -> p.getStats().getTeamfightAmount());
  }

  public Stat getMultikills() {
    return new Stat(playerperformances, OutputType.TEXT, 15, lane, "multiKills") {
      @Override
      public String display() {
        return calculate() + " -> " + playerperformances.stream().mapToDouble(Playerperformance::getDoubleKills).average().orElse(0) + "-" +
            playerperformances.stream().mapToDouble(Playerperformance::getTripleKills).average().orElse(0) + "-" +
            playerperformances.stream().mapToDouble(Playerperformance::getQuadraKills).average().orElse(0) + "-" +
            playerperformances.stream().mapToDouble(Playerperformance::getPentaKills).average().orElse(0);
      }
    }.map(p -> p.getDoubleKills() + p.getTripleKills() * 2 + p.getQuadraKills() * 6 + p.getPentaKills() * 24)
        .nullable();
  }

  public Stat getDeathOrder() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane, "averageDeathOrder")
        .map(p -> p.getStats().getAverageDeathOrder());
  }

  public Stat getSuccessRate() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "teamfightWinrate")
        .map(p -> p.getStats().getTeamfightWinrate())
        .nullable()
        .sub("Teamfights gewonnen", p -> p.getStats().getTeamfightAmount() * p.getStats().getTeamfightWinrate());
  }

  public Stat getAcesEarlyAndCleanFights() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "acesAndClean")
        .map(p -> p.getStats().getAcesAndClean())
        .nullable()
        .sub("Earlygame Aces", p -> p.getTeamperformance().getEarlyAces())
        .sub("Flawless Aces", p -> p.getTeamperformance().getFlawlessAces());
  }

  public Stat getDamage() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "teamfightDamageRate")
        .map(p -> p.getStats().getTeamfightDamageRate())
        .nullable()
        .sub("Schaden insgesamt", Playerperformance::getDamageTotal)
        .sub("Schaden in Teamfights", p -> p.getDamageTotal() * p.getStats().getTeamfightDamageRate())
        .sub("Schaden pro Teamfight", p -> p.getDamageTotal() * p.getStats().getTeamfightDamageRate() / p.getStats().getTeamfightAmount());
  }

}
