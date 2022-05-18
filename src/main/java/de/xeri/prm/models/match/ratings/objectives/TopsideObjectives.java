package de.xeri.prm.models.match.ratings.objectives;

import java.util.List;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.Stat;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.playerperformance.Playerperformance;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class TopsideObjectives extends RatingSubcategory {
  private final List<Playerperformance> playerperformances;
  private final Lane lane;

  public TopsideObjectives(List<Playerperformance> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getBaronTime(), getBaronsTakedownsAttempts(), getBaronPowerplay(), getHeraldTurrets(), getHeraldMulticharge());
  }

  public Stat getBaronTime() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane)
        .map(p -> p.getTeamperformance().getBaronTime())
        .reverse();
  }

  public Stat getBaronsTakedownsAttempts() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "baronTakedownsAttempts")
        .map(p -> p.getStats().getBaronTakedownsAttempts())
        .nullable()
        .sub("Baron-Kills", Playerperformance::getBaronKills)
        .sub("Baron-Executes", Playerperformance::getBaronExecutes);
  }

  public Stat getBaronPowerplay() {
    return new Stat(playerperformances, OutputType.NUMBER, 4, lane)
        .map(p -> p.getTeamperformance().getBaronPowerplay());
  }

  public Stat getHeraldTurrets() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane)
        .map(p -> p.getTeamperformance().getRiftTurrets());
  }

  public Stat getHeraldMulticharge() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane)
        .map(p -> p.getTeamperformance().getRiftOnMultipleTurrets());
  }

}
