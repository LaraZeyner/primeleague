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
public class Damage extends RatingSubcategory {
  private final List<Playerperformance> playerperformances;
  private final Lane lane;

  public Damage(List<Playerperformance> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getTeamDamage(), getTeamTankyness(), getTeamDurability(), getHealing(), getTimeInCombat());
  }

  public Stat getTeamDamage() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane)
        .map(p -> p.getStats().getTeamDamage())
        .nullable()
        .sub("Totaler Schaden: ", Playerperformance::getDamageTotal)
        .sub("Teamschaden", p -> p.getDamageTotal() * 1d / p.getTeamperformance().getTotalDamage());
  }

  public Stat getTeamTankyness() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "teamDamageTaken")
        .map(p -> p.getStats().getTeamDamageTaken())
        .nullable()
        .sub("Tankyness: ", Playerperformance::getDamageTaken)
        .sub("Team Tankyness", p -> p.getDamageTaken() * 1d / p.getTeamperformance().getTotalDamageTaken());
  }

  public Stat getTeamDurability() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "teamDamageMitigated")
        .map(p -> p.getStats().getTeamDamageMitigated())
        .nullable()
        .sub("Durability: ", Playerperformance::getDamageMitigated)
        .sub("Team Durability", p -> p.getDamageMitigated() * 1d * p.getStats().getTeamDamageMitigated());
  }

  public Stat getHealing() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane, "damageHealed")
        .map(Playerperformance::getDamageHealed)
        .nullable();
  }

  public Stat getTimeInCombat() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane, "secondsInCombat")
        .map(p -> p.getStats().getSecondsInCombat());
  }

}
