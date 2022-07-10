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
public class Playstyle extends RatingSubcategory {
  private final Map<String, Double> playerperformances;
  private final Lane lane;

  public Playstyle(Map<String, Double> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getPositioning(), getKillDeathPosition(), getKeyspellsUsed(), getSpellBilance(), getReactions());
  }

  @Override
  public List<String> getData() {
    return handleData(getPositioning(), getKillDeathPosition(), getKeyspellsUsed(), getSpellBilance(), getReactions());
  }

  public Stat getPositioning() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "lanePositioning")
        .ignore();
  }

  public Stat getKillDeathPosition() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "laneKillDeathPositioning")
        .sub("Kill Positioning", "laneKillPositioning")
        .ignore();
  }

  public Stat getKeyspellsUsed() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane)
        .nullable()
        .sub("Q genutzt", "qUsages")
        .sub("W genutzt", "wUsages")
        .sub("E genutzt", "eUsages")
        .sub("R genutzt", "rUsages");
  }

  public Stat getSpellBilance() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "totalSpellBilance")
        .nullable()
        .sub("Spell Bilanz", "hitBilance")
        .sub("Dodge Bilanz", "dodgeBilance")
        .sub("Spells getroffen", "spellsHit")
        .sub("Spells gedodged", "spellsDodged");
  }

  public Stat getReactions() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane, "reactionBilance")
        .nullable()
        .sub("schnelle Reaktionen", "quickDodged");
  }

}
