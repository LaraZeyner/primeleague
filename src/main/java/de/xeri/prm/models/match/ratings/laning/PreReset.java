package de.xeri.prm.models.match.ratings.laning;

import java.util.Map;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.ratings.Stat;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class PreReset extends RatingSubcategory {
  private final Map<String, Double> playerperformances;
  private final Lane lane;

  public PreReset(Map<String, Double> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getResetTime(), getEnemyControlled(), getInitialBuffsAndScuttles(), getLead(), getGold());
  }

  public Stat getResetTime() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane, "firstBase")
        .reverse();
  }

  public Stat getGold() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane, "firstBaseResetGold")
        .ignore()
        .sub("verbleibend", "firstBaseGoldUnspent");
  }

  public Stat getEnemyControlled() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane, "firstBaseEnemyControlled")
        .nullable();
  }

  public Stat getInitialBuffsAndScuttles() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "initialScuttles")
        .nullable()
        .sub("Buffs", "initialBuffs");
  }

  public Stat getLead() {
    return new Stat(playerperformances, OutputType.NUMBER, 4, lane, "firstBaseLead");
  }

}
