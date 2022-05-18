package de.xeri.prm.models.match.ratings.laning;

import java.util.List;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.Stat;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.playerperformance.Playerperformance;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class PreReset extends RatingSubcategory {
  private final List<Playerperformance> playerperformances;
  private final Lane lane;

  public PreReset(List<Playerperformance> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getResetTime(), getEnemyControlled(), getInitialBuffsAndScuttles(), getLead(), getGold());
  }

  public Stat getResetTime() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane, "firstBase")
        .map(p -> p.getStats().getFirstBase())
        .reverse();
  }

  public Stat getGold() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane, "firstBaseResetGold")
        .map(p -> p.getStats().getFirstBaseResetGold())
        .ignore()
        .sub("verbleibend", p -> p.getStats().getFirstBaseGoldUnspent());
  }

  public Stat getEnemyControlled() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane, "firstBaseEnemyControlled")
        .map(p -> p.getStats().getFirstBaseEnemyControlled())
        .nullable();
  }

  public Stat getInitialBuffsAndScuttles() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "initialScuttles")
        .map(Playerperformance::getInitialScuttles)
        .nullable()
        .sub("Buffs", Playerperformance::getInitialBuffs);
  }

  public Stat getLead() {
    return new Stat(playerperformances, OutputType.NUMBER, 4, lane, "firstBaseLead")
        .map(p -> p.getStats().getFirstBaseLead());
  }

}
