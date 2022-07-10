package de.xeri.prm.models.match.ratings.fighting;

import java.util.List;
import java.util.Map;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.ratings.Stat;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class StrongPhase extends RatingSubcategory {
  private final Map<String, Double> playerperformances;
  private final Lane lane;

  public StrongPhase(Map<String, Double> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getAllowComebacks(), getXpLead(), getLevelLead());
  }

  @Override
  public List<String> getData() {
    return handleData(getAllowComebacks(), getXpLead(), getLevelLead());
  }

  public Stat getAllowComebacks() {
    return new Stat(playerperformances, OutputType.PERCENT, 2, lane, "behindComeback")
        .nullable()
        .sub("Vorsprung abgebaut", "behindComeback")
        .sub("Comebacks", "comeback");
  }

  public Stat getXpLead() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane)
        .nullable();
  }

  public Stat getLevelLead() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane)
        .nullable();
  }

  public Stat getDuration() {
    return new Stat(playerperformances, OutputType.TIME, 3, lane)
        .nullable()
        .ignore();
  }


}
