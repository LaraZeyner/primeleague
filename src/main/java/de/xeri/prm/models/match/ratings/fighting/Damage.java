package de.xeri.prm.models.match.ratings.fighting;

import java.util.Map;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.ratings.Stat;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class Damage extends RatingSubcategory {
  private final Map<String, Double> playerperformances;
  private final Lane lane;

  public Damage(Map<String, Double> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getTeamDamage(), getTeamTankyness(), getTeamDurability(), getHealing(), getTimeInCombat());
  }

  public Stat getTeamDamage() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane)
        .sub("Totaler Schaden: ", "damageTotal");
  }

  public Stat getTeamTankyness() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "teamDamageTaken")
        .nullable()
        .sub("Tankyness: ", "damageTaken");
  }

  public Stat getTeamDurability() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "teamDamageMitigated")
        .nullable()
        .sub("Durability: ", "damageMitigated");
  }

  public Stat getHealing() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane, "damageHealed")
        .nullable();
  }

  public Stat getTimeInCombat() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane, "secondsInCombat");
  }

}
