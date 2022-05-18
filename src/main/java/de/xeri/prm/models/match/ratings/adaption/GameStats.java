package de.xeri.prm.models.match.ratings.adaption;

import java.util.List;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.Stat;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.playerperformance.Playerperformance;
import de.xeri.prm.util.Util;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class GameStats extends RatingSubcategory {
  private final List<Playerperformance> playerperformances;
  private final Lane lane;

  public GameStats(List<Playerperformance> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getWinrate(), getBlueWinrate(), getRedWinrate(), getKillParticipation(), getKDA());
  }

  public Stat getWinrate() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, null)
        .map(p -> p.getTeamperformance().isWin() ? 1 : 0)
        .nullable();
  }

  public Stat getBlueWinrate() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, null) {
      @Override
      public double calculate() {
        return playerperformances.stream()
            .filter(p -> p.getTeamperformance().isFirstPick())
            .mapToDouble(p -> p.getTeamperformance().isWin() ? 1 : 0).average().orElse(0);
      }
    }.nullable();
  }

  public Stat getRedWinrate() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, null) {
      @Override
      public double calculate() {
        return playerperformances.stream()
            .filter(p -> !p.getTeamperformance().isFirstPick())
            .mapToDouble(p -> p.getTeamperformance().isWin() ? 1 : 0).average().orElse(0);
      }
    }.nullable();
  }

  public Stat getKillParticipation() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane)
        .map(p -> p.getStats().getKillParticipation())
        .nullable();
  }

  public Stat getKDA() {
    return new Stat(playerperformances, OutputType.TEXT, 12, lane, null) {
      @Override
      public double calculate() {
        return Util.div(playerperformances.stream().mapToInt(Playerperformance::getKills).sum() +
            playerperformances.stream().mapToInt(Playerperformance::getAssists).sum(),
            playerperformances.stream().mapToInt(Playerperformance::getDeaths).sum(), true);
      }

      @Override
      public String display() {
        return playerperformances.stream().mapToInt(Playerperformance::getKills).average().orElse(0) + " / " +
            playerperformances.stream().mapToInt(Playerperformance::getDeaths).average().orElse(0) + " / " +
            playerperformances.stream().mapToInt(Playerperformance::getAssists).average().orElse(0);
      }

      @Override
      public double average() {
        return Util.div((Playerperformance.getValues().get(lane).get("kills").getAverage() +
                Playerperformance.getValues().get(lane).get("assists").getAverage()),
            Playerperformance.getValues().get(lane).get("deaths").getAverage(), true);
      }

      @Override
      public double maximum() {
        return Util.div((Playerperformance.getValues().get(lane).get("kills").getHighest() +
                Playerperformance.getValues().get(lane).get("assists").getHighest()),
            Playerperformance.getValues().get(lane).get("deaths").getHighest(), true);
      }
    }.sub("Kills", p -> playerperformances.stream().mapToInt(Playerperformance::getKills).average().orElse(0))
        .sub("Deaths", p -> playerperformances.stream().mapToInt(Playerperformance::getDeaths).average().orElse(0))
        .sub("Assists", p -> playerperformances.stream().mapToInt(Playerperformance::getAssists).average().orElse(0));
  }

}
