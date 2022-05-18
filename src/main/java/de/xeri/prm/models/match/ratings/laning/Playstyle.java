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
public class Playstyle extends RatingSubcategory {
  private final List<Playerperformance> playerperformances;
  private final Lane lane;

  public Playstyle(List<Playerperformance> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getPositioning(), getKillDeathPosition(), getKeyspellsUsed(), getSpellBilance(), getReactions());
  }

  public Stat getPositioning() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "lanePositioning")
        .map(p -> p.getStats().getLanePositioning())
        .sub("Earlygame", p -> p.getStats().getLanePositioning())
        .sub(" - wenn ahead", p -> p.getStats().isAhead() ? p.getStats().getLanePositioning() : 0)
        .sub(" - wenn behind", p -> p.getStats().isBehind() ? p.getStats().getLanePositioning() : 0)
        .sub("Midgame", p -> p.getStats().getMidgamePositioning())
        .sub(" - wenn ahead", p -> p.getStats().isAhead() ? p.getStats().getMidgamePositioning() : 0)
        .sub(" - wenn behind", p -> p.getStats().isBehind() ? p.getStats().getMidgamePositioning() : 0)
        .sub("Lategame", p -> p.getStats().getLategamePositioning())
        .sub(" - wenn ahead", p -> p.getStats().isAhead() ? p.getStats().getLategamePositioning() : 0)
        .sub(" - wenn behind", p -> p.getStats().isBehind() ? p.getStats().getLategamePositioning() : 0)
        .ignore();
  }

  public Stat getKillDeathPosition() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "laneKillDeathPositioning")
        .map(p -> p.getStats().getLaneKillDeathPositioning())
        .sub("Kill Positioning", p -> p.getStats().getLaneKillPositioning())
        .sub("Death Positioning", p -> p.getStats().getLaneKillDeathPositioning() * 2 - p.getStats().getLaneKillPositioning())
        .ignore();
  }

  public Stat getKeyspellsUsed() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane)
        .map(p -> p.getStats().getKeyspellsUsed())
        .nullable()
        .sub("Q genutzt", Playerperformance::getQUsages)
        .sub("W genutzt", Playerperformance::getWUsages)
        .sub("E genutzt", Playerperformance::getEUsages)
        .sub("R genutzt", Playerperformance::getRUsages);
  }

  public Stat getSpellBilance() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "totalSpellBilance")
        .map(p -> p.getStats().getTotalSpellBilance())
        .nullable()
        .sub("Spell Bilanz", p -> p.getStats().getHitBilance())
        .sub("Dodge Bilanz", p -> p.getStats().getDodgeBilance())
        .sub("Spells getroffen",
            p -> (p.getSpellsHit() * -1 * p.getStats().getHitBilance() + p.getSpellsHit()) / p.getStats().getHitBilance())
        .sub("Spells gedodged",
            p -> (p.getSpellsDodged() * -1 * p.getStats().getDodgeBilance() + p.getSpellsDodged()) / p.getStats().getDodgeBilance());
  }

  public Stat getReactions() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane, "reactionBilance")
        .map(p -> p.getStats().getReactionBilance())
        .nullable()
        .sub("schnelle Reaktionen", Playerperformance::getQuickDodged)
        .sub("gegnerische Reaktionen", p -> p.getStats().getReactionBilance() - p.getQuickDodged());
  }

}
