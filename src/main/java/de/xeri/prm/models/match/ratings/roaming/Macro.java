package de.xeri.prm.models.match.ratings.roaming;

import java.util.List;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.Stat;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.playerperformance.Playerperformance;
import de.xeri.prm.util.Const;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class Macro extends RatingSubcategory {
  private final List<Playerperformance> playerperformances;
  private final Lane lane;

  public Macro(List<Playerperformance> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getTeleportKills(), getJungleCampsStolen(), getMidgameGoldXPEfficiency(), getGrouping(), getLateXPGoldLead());
  }

  public Stat getTeleportKills() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane)
        .map(Playerperformance::getTeleportKills)
        .nullable();
  }

  public Stat getJungleCampsStolen() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "invadedCreeps")
        .map(Playerperformance::getInvadedCreeps)
        .nullable();
  }

  public Stat getMidgameGoldXPEfficiency() {
    return new Stat(playerperformances, OutputType.PERCENT, 4, lane)
        .map(p -> p.getStats().getMidgameGoldXPEfficiency())
        .sub("Midgame-Gold", p -> p.getStats().getMidgameGoldEfficiency() * Const.MIDGAME_GOLD)
        .sub("Lane-Midgame-Gold", p -> Const.MIDGAME_GOLD)
        .sub("Midgame-XP", p -> (p.getStats().getMidgameGoldXPEfficiency() * 2 - p.getStats().getMidgameGoldEfficiency()) * Const.MIDGAME_XP)
        .sub("Lane-Midgame-XP", p -> Const.MIDGAME_XP);
  }

  public Stat getGrouping() {
    return new Stat(playerperformances, OutputType.NUMBER, 5, lane, "companionScore")
        .map(p -> p.getStats().getCompanionScore());
  }

  public Stat getLateXPGoldLead() {
    return new Stat(playerperformances, OutputType.NUMBER, 4, lane, "lategameLead")
        .map(p -> p.getStats().getLategameLead());
  }

}
