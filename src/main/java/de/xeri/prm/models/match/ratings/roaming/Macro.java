package de.xeri.prm.models.match.ratings.roaming;

import java.util.List;
import java.util.Map;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.ratings.Stat;
import de.xeri.prm.util.Const;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class Macro extends RatingSubcategory {
  private final Map<String, Double> playerperformances;
  private final Lane lane;

  public Macro(Map<String, Double> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getTeleportKills(), getJungleCampsStolen(), getMidgameGoldXPEfficiency(), getGrouping(), getLateXPGoldLead());
  }

  @Override
  public List<String> getData() {
    return handleData(getTeleportKills(), getJungleCampsStolen(), getMidgameGoldXPEfficiency(), getGrouping(), getLateXPGoldLead());
  }

  public Stat getTeleportKills() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane)
        .nullable();
  }

  public Stat getJungleCampsStolen() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "invadedCreeps")
        .nullable();
  }

  public Stat getMidgameGoldXPEfficiency() {
    return new Stat(playerperformances, OutputType.PERCENT, 4, lane)
        .sub("Midgame-Gold", "midgameGoldEfficiency", Const.MIDGAME_GOLD)
        .sub("Lane-Midgame-Gold", Const.MIDGAME_GOLD)
        .sub("Midgame-XP", "midgameGoldXPEfficiency", Const.MIDGAME_XP)
        .sub("Lane-Midgame-XP", Const.MIDGAME_XP);
  }

  public Stat getGrouping() {
    return new Stat(playerperformances, OutputType.NUMBER, 5, lane, "companionScore");
  }

  public Stat getLateXPGoldLead() {
    return new Stat(playerperformances, OutputType.NUMBER, 4, lane, "lategameLead");
  }

}
