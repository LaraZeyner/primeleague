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
public class Plays extends RatingSubcategory {
  private final List<Playerperformance> playerperformances;
  private final Lane lane;

  public Plays(List<Playerperformance> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getAggressiveFlash(), getLevelupAllins(), getSoloKillDiff(), getOutplays(), getFirstBloodParticipation());
  }

  public Stat getAggressiveFlash() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane)
        .map(Playerperformance::getAggressiveFlash)
        .nullable();
  }

  public Stat getLevelupAllins() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "levelUpAllin")
        .map(Playerperformance::getLevelUpAllin)
        .nullable();
  }

  public Stat getSoloKillDiff() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "soloKillAdvantage")
        .map(p -> p.getStats().getSoloKillAdvantage())
        .nullable()
        .sub("Solo Kills", Playerperformance::getSoloKills)
        .sub("Solo Deaths", p -> p.getSoloKills() - p.getStats().getSoloKillAdvantage());
  }

  public Stat getOutplays() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "outplayed")
        .map(Playerperformance::getOutplayed)
        .nullable();
  }

  public Stat getFirstBloodParticipation() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, null)
        .map(p -> p.isFirstBlood() ? 1 : 0)
        .nullable();
  }

}
