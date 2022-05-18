package de.xeri.prm.models.match.ratings.laning;

import java.util.List;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.Stat;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.playerperformance.Playerperformance;
import de.xeri.prm.util.Util;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class Resets extends RatingSubcategory {
  private final List<Playerperformance> playerperformances;
  private final Lane lane;

  public Resets(List<Playerperformance> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getResetsThroughRecalls(), getDuration(), getGold(), getGoldLost(), getResetsWithTeam());
  }

  public Stat getResetsThroughRecalls() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "resetsThroughRecall")
        .map(p -> p.getStats().getResetsThroughRecall())
        .nullable()
        .sub("Recalls", p -> p.getStats().getResets() * p.getStats().getResetsThroughRecall())
        .sub("Tode", p -> p.getStats().getResets() - p.getStats().getResets() * p.getStats().getResetsThroughRecall());
  }

  public Stat getDuration() {
    return new Stat(playerperformances, OutputType.TIME_FROM_MILLIS, 2, lane, "resetDuration")
        .map(p -> p.getStats().getResetDuration())
        .reverse()
        .sub("Resets gesamt", p -> p.getStats().getResets())
        .sub("mittlere Dauer", p -> Util.div(p.getStats().getResetDuration(), p.getStats().getResets()));
  }

  public Stat getGold() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane, "resetGold")
        .map(p -> p.getStats().getResetGold())
        .sub("verbleibend", p -> p.getStats().getResetGoldUnspent());
  }

  public Stat getGoldLost() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane, "resetGoldGain")
        .map(p -> p.getStats().getResetGoldGain())
        .nullable()
        .sub("Resets gesamt", p -> p.getStats().getResets())
        .sub("Gold pro Reset", p -> p.getStats().getResets() == 0 ? 0 : p.getStats().getResetGoldGain() * 1d / p.getStats().getResets());
  }

  public Stat getResetsWithTeam() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "resetsTogether")
        .map(p -> p.getStats().getResetsTogether())
        .nullable()
        .sub("Resets mit Team", p -> p.getStats().getResets() * p.getStats().getResetsTogether())
        .sub("Resets alleine", p -> p.getStats().getResets() - p.getStats().getResets() * p.getStats().getResetsTogether());
  }

}
