package de.xeri.prm.models.match.ratings.roaming;

import java.util.List;
import java.util.Map;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.ratings.Stat;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class Dives extends RatingSubcategory {
  private final Map<String, Double> playerperformances;
  private final Lane lane;

  public Dives(Map<String, Double> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getDivingSuccessrate(), getDivingDisengagerate(), getDivesDied());
  }

  @Override
  public List<String> getData() {
    return handleData(getDivingSuccessrate(), getDivingDisengagerate(), getDivesDied());
  }

  public Stat getDivingSuccessrate() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "divesOwn")
        .nullable()
        .sub("Dives erfolgreich", "divesSuccessful")
        .sub("Dives gescheitert", "divesDone");
  }

  public Stat getDivingDisengagerate() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "divesEnemy")
        .nullable()
        .sub("Dives verhindert", "divesProtected")
        .sub("Dive-Tode", "divesGotten");
  }

  public Stat getDivesDied() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane)
        .nullable()
        .reverse();
  }

}
