package de.xeri.prm.models.match.ratings.roaming;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.Teamperformance;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.Stat;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.playerperformance.JunglePath;
import de.xeri.prm.models.match.playerperformance.Playerperformance;
import de.xeri.prm.util.Util;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class Ganks extends RatingSubcategory {
  private final List<Playerperformance> playerperformances;
  private final Lane lane;
  private final HashMap<JunglePath, Integer> path;

  public Ganks(List<Playerperformance> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;

    Map<JunglePath, Integer> clears = new HashMap<>();
    for (JunglePath junglePath : JunglePath.get()) {
      final long amount = playerperformances.stream()
          .map(Playerperformance::getTeamperformance)
          .map(Teamperformance::getJunglePath)
          .filter(Objects::nonNull)
          .filter(path -> path.equals(junglePath)).count();
      clears.put(junglePath, (int) amount);
    }
    this.path = clears.entrySet().stream()
        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
  }

  public double get() {
    return handleValues(getTeamInvadesAndBuffsTaken(), getEarlyGanksSpottedAndTimeWasted(), getProximity(), getGankPriority(), getGankSetups());
  }

  public Stat getTeamInvadesAndBuffsTaken() {
    return new Stat(playerperformances, OutputType.NUMBER, 2, lane, "invadingAndBuffs")
        .map(p -> p.getStats().getInvadingAndBuffs())
        .nullable()
        .sub("Earlgame Ganks", Playerperformance::getGanksEarly);

  }

  public Stat getEarlyGanksSpottedAndTimeWasted() {
    return new Stat(playerperformances, OutputType.TIME, 3, lane, "jungleTimeWasted")
        .map(p -> p.getTeamperformance().getJungleTimeWasted())
        .nullable()
        .ignore()
        .sub("Jungle Time verloren", p -> p.getTeamperformance().getJungleTimeWasted())
        .sub("Jungle Proximity", p -> p.getStats().getLaneProximityDifference());
  }

  public Stat getProximity() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane)
        .map(p -> p.getStats().getProximity())
        .nullable()
        .subValue(pathBy(1), amountBy(1))
        .subValue(pathBy(2), amountBy(2))
        .subValue(pathBy(3), amountBy(3))
        .subValue(pathBy(4), amountBy(4))
        .subValue(pathBy(5), amountBy(5));
  }

  public Stat getGankPriority() {
    return new Stat(playerperformances, OutputType.TEXT, 12, lane, "gankPriority") {
      private final int ganksTop = playerperformances.stream().mapToInt(Playerperformance::getGanksTop).sum();
      private final int ganksMid = playerperformances.stream().mapToInt(Playerperformance::getGanksMid).sum();
      private final int ganksBot = playerperformances.stream().mapToInt(Playerperformance::getGanksBot).sum();

      @Override
      public double calculate() {
        return Util.div(ganksTop - ganksBot, ganksTop + ganksMid + ganksBot);
      }

      @Override
      public String display() {
        final String str;
        if (ganksTop > ganksMid && calculate() > 0) {
          str = "Topside ";
        } else if (ganksBot > ganksMid && calculate() < 0) {
          str = "Botside ";
        } else {
          str = "Midlane ";
        }
        final String suffix;
        if (Stream.of(ganksBot, ganksMid, ganksTop).mapToInt(Integer::new).sum() == 0) {
          suffix = "";
        } else {
          final int max = Stream.of(ganksBot, ganksMid, ganksTop).mapToInt(Integer::new).max().orElse(0) * 100 /
              Stream.of(ganksBot, ganksMid, ganksTop).mapToInt(Integer::new).sum();
          suffix = max + " %";
        }
        return str + suffix;
      }
    };
  }

  public Stat getGankSetups() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane)
        .map(Playerperformance::getGankSetups)
        .nullable();
  }

  private String pathBy(int order) {
    int number = 0;
    for (Map.Entry<JunglePath, Integer> entry : path.entrySet()) {
      number++;
      if (number == order) {
        return entry.getKey().getName();
      }
    }
    return "Null";
  }

  private int amountBy(int order) {
    int number = 0;
    for (Map.Entry<JunglePath, Integer> entry : path.entrySet()) {
      number++;
      if (number == order) {
        return entry.getValue();
      }
    }
    return 0;
  }

}
