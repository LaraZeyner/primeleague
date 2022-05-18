package de.xeri.prm.models.match.ratings.survival;

import java.util.List;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.Stat;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.playerperformance.Playerperformance;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class GeneralSurvival extends RatingSubcategory {
  private final List<Playerperformance> playerperformances;
  private final Lane lane;

  public GeneralSurvival(List<Playerperformance> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getLivePlaytime(), getTimeDead(), getSurvivedClose(), getDeathPositioning());
  }

  public Stat getLivePlaytime() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "timeAlivePercent")
        .map(p -> p.getStats().getTimeAlivePercent())
        .sub("Todeszeit", Playerperformance::getTimeDead)
        .sub("Minuten", p -> p.getTeamperformance().getGame().getDuration() * 1d / 60);
  }

  public Stat getTimeDead() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane)
        .map(Playerperformance::getTimeDead)
        .reverse()
        .sub("Längste Zeit am Leben", Playerperformance::getTimeAlive);
  }

  public Stat getSurvivedClose() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane)
        .map(Playerperformance::getSurvivedClose);
  }

  public Stat getDeathPositioning() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, "relativeDeathPositioning")
        .map(p -> p.getStats().getRelativeDeathPositioning())
        .nullable();
  }

}
