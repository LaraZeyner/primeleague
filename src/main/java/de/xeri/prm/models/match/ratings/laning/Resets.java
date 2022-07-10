package de.xeri.prm.models.match.ratings.laning;

import java.util.List;
import java.util.Map;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.ratings.Stat;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class Resets extends RatingSubcategory {
  private final Map<String, Double> playerperformances;
  private final Lane lane;

  public Resets(Map<String, Double> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getResetsThroughRecalls(), getDuration(), getGold(), getGoldLost(), getResetsWithTeam());
  }

  @Override
  public List<String> getData() {
    return handleData(getResetsThroughRecalls(), getDuration(), getGold(), getGoldLost(), getResetsWithTeam());
  }

  public Stat getResetsThroughRecalls() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "resetsThroughRecall")
        .nullable()
        .sub("Recalls", "resets", "resetsThroughRecall");
  }

  public Stat getDuration() {
    return new Stat(playerperformances, OutputType.TIME_FROM_MILLIS, 2, lane, "resetDuration")
        .reverse()
        .sub("Resets gesamt", "resets");
  }

  public Stat getGold() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane, "resetGold")
        .sub("verbleibend", "resetGoldUnspent");
  }

  public Stat getGoldLost() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane, "resetGoldGain")
        .nullable()
        .sub("Resets gesamt", "resets");
  }

  public Stat getResetsWithTeam() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "resetsTogether")
        .nullable()
        .sub("Resets mit Team", "resets", "resetsTogether");
  }

}
