package de.xeri.prm.models.match.ratings.fighting;

import java.util.Map;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.ratings.Stat;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class Plays extends RatingSubcategory {
  private final Map<String, Double> playerperformances;
  private final Lane lane;

  public Plays(Map<String, Double> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getAggressiveFlash(), getLevelupAllins(), getSoloKillDiff(), getOutplays(), getFirstBloodParticipation());
  }

  public Stat getAggressiveFlash() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane)
        .nullable();
  }

  public Stat getLevelupAllins() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "levelUpAllin")
        .nullable();
  }

  public Stat getSoloKillDiff() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "soloKillAdvantage")
        .nullable()
        .sub("Solo Kills", "soloKills");
  }

  public Stat getOutplays() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "outplayed")
        .nullable();
  }

  public Stat getFirstBloodParticipation() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "firstBlood")
        .nullable();
  }

}
