package de.xeri.prm.models.match.ratings.roaming;

import java.util.List;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.Stat;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.playerperformance.Playerperformance;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class Dives extends RatingSubcategory {
  private final List<Playerperformance> playerperformances;
  private final Lane lane;

  public Dives(List<Playerperformance> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getDivingSuccessrate(), getDivingDisengagerate(), getDivesDied());
  }

  public Stat getDivingSuccessrate() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "divesOwn")
        .map(p -> p.getStats().getDivesOwn())
        .nullable()
        .sub("Dives erfolgreich", Playerperformance::getDivesSuccessful)
        .sub("Dives gescheitert", p -> p.getDivesDone() - p.getDivesSuccessful());
  }

  public Stat getDivingDisengagerate() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "divesEnemy")
        .map(p -> p.getStats().getDivesEnemy())
        .nullable()
        .sub("Dives verhindert", Playerperformance::getDivesProtected)
        .sub("Dive-Tode", p -> p.getStats().getDivesDied());
  }

  public Stat getDivesDied() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane)
        .map(p -> p.getStats().getDivesDied())
        .nullable()
        .reverse();
  }

}
