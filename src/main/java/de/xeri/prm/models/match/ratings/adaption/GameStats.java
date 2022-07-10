package de.xeri.prm.models.match.ratings.adaption;

import java.util.List;
import java.util.Map;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.ratings.Stat;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class GameStats extends RatingSubcategory {
  private final Map<String, Double> playerperformances;
  private final Lane lane;

  public GameStats(Map<String, Double> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getWinrate(), getBlueWinrate(), getRedWinrate(), getKillParticipation(), getKDA());
  }

  @Override
  public List<String> getData() {
    return handleData(getWinrate(), getBlueWinrate(), getRedWinrate(), getKillParticipation(), getKDA());
  }

  public Stat getWinrate() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane)
        .nullable();
  }

  public Stat getBlueWinrate() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane)
        .nullable();
  }

  public Stat getRedWinrate() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane)
        .nullable();
  }

  public Stat getKillParticipation() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane)
        .nullable();
  }

  public Stat getKDA() {
    return new Stat(playerperformances, OutputType.TEXT, 12, lane, "kDA") {
      @Override
      public String display() {
        return Math.round(10 * playerperformances.get("kills")) / 10 + "/" +
            Math.round(10 * playerperformances.get("deaths")) / 10 + "/" +
            Math.round(10 * playerperformances.get("assists")) / 10;
      }
    }.sub("Kills", "kills")
        .sub("Deaths", "deaths")
        .sub("Assists", "assists");
  }

}
