package de.xeri.prm.servlet.datatables.league;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import de.xeri.prm.servlet.datatables.match.GameView;
import de.xeri.prm.util.Util;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Lara on 30.05.2022 for web
 */
@Data
public class LeagueTeam implements Serializable, Comparable<LeagueTeam> {
  private static final transient long serialVersionUID = -68738619502412789L;

  private String place;

  private final int tourneyId;
  private final String name;
  private final String abbreviation;
  private final String logoUrl;

  private final Integer matches;
  private final Integer wins;
  private final Integer ties;
  private final Integer losses;

  private Integer games;
  private Integer winsGames;
  private Integer lossGames;

  public String getTeamScore() {
    return winsGames + ":" + lossGames;
  }

  private String killsPerMatch;
  private String killsDiff;

  private String goldPerMatch;
  private String goldDiff;

  private String creepsPerMatch;
  private String creepsDiff;

  private int objectives;
  private String objectivesPerMatch;

  private int towers;
  private String towersPerMatch;

  private int drakes;
  private String drakesPerMatch;

  private int inhibs;
  private String inhibsPerMatch;

  private int heralds;
  private String heraldsPerMatch;

  private int barons;
  private String baronsPerMatch;

  private String matchTime;
  private String matchTimeWins;
  private String matchTimeLosses;

  private List<GameView> gameViews;

  public void add(List<Double> objects) {
    add(objects.get(0).longValue(), objects.get(1).longValue(), objects.get(2), objects.get(3), objects.get(4), objects.get(5),
        objects.get(6), objects.get(7), objects.get(8).longValue(), objects.get(9), objects.get(10).longValue(), objects.get(11),
        objects.get(12).longValue(), objects.get(13), objects.get(14).longValue(), objects.get(15), objects.get(16).longValue(), objects.get(17),
        objects.get(18), objects.get(19), objects.get(20));
  }

  public void add(Long games, Long wins, Double kills, Double killDiff, Double gold, Double goldDiff, Double creeps, Double creepsDiff,
                  Long towers, Double towersPerGame, Long drakes, Double drakesPerGame, Long inhibs, Double inhibsPerGame, Long heralds,
                  Double heraldsPerGame, Long barons, Double baronsPerGame, Double gameDuration, Double winDuration, Double loseDuration) {
    int winsPoints = this.wins * 2 + this.ties;
    int lossesPoints = this.losses * 2 + this.ties;
    this.winsGames = Math.max(Util.longToInt(wins), winsPoints);
    this.lossGames = Math.max(Util.longToInt(games - wins), lossesPoints);
    this.games = winsGames + lossGames;
    this.killsPerMatch = display(2 * kills, 2);
    this.killsDiff = display(killDiff, 3);
    this.goldPerMatch = display(2 * gold, 4);
    this.goldDiff = display(goldDiff, 4);
    this.creepsPerMatch = display(2 * creeps, 4); // x.x
    this.creepsDiff = display(creepsDiff, 4);
    this.towers = towers != null ? Util.longToInt(towers) : 0;
    this.towersPerMatch = display(2 * towersPerGame, 2); // x.x
    this.drakes = drakes != null ? Util.longToInt(drakes) : 0;
    this.drakesPerMatch = display(2 * drakesPerGame, 2);
    this.inhibs = inhibs != null ? Util.longToInt(inhibs) : 0;
    this.inhibsPerMatch = display(2 * inhibsPerGame, 2);
    this.heralds = heralds != null ? Util.longToInt(heralds) : 0;
    this.heraldsPerMatch = display(2 * heraldsPerGame, 2);
    this.barons = barons != null ? Util.longToInt(barons) : 0;
    this.baronsPerMatch = display(2 * baronsPerGame, 2);
    this.matchTime = timeFromSeconds(gameDuration);
    this.matchTimeWins = timeFromSeconds(winDuration);
    this.matchTimeLosses = timeFromSeconds(loseDuration);
    this.objectives = (towers != null ? Util.longToInt(towers) : 0) + (drakes != null ? Util.longToInt(drakes) : 0) +
        (inhibs != null ? Util.longToInt(inhibs) : 0) + (heralds != null ? Util.longToInt(heralds) : 0) + (barons != null ? Util.longToInt(barons) : 0);
    this.objectivesPerMatch = display(objectives * 1d / games, 2);
  }

  private String timeFromSeconds(Double value) {
    if (value != null) {
      final int minutes = (int) (value / 60);
      final int seconds = (int) Math.round(value % 60);
      return minutes + ":" + (seconds < 10 ? "0" + seconds : seconds);
    }
    return "--:--";
  }

  private Double stringToDouble(String str) {
    if (str.endsWith("k")) {
      return Double.parseDouble(str.replace("k", "")) * 1000;
    } else if (str.endsWith("M")) {
      return Double.parseDouble(str.replace("M", "")) * 1_000_000;
    } else if (str.equals("-")) {
      return 0d;
    }
    return Double.parseDouble(str);
  }

  public String getBilance() {
    return wins + "   " + ties + "   " + losses;
  }

  public String getWinsPerMatch() {
    return Math.round(winsGames * 100d / games) + "%";
  }

  private String display(Double valueDouble, int digits) {
    if (valueDouble != null) {
      final String value = String.valueOf(valueDouble);
      if (value.contains(".")) {
        String digitsBefore = value.split("\\.")[0];
        if (digitsBefore.equals("0")) digitsBefore = "";
        final String digitsAfter = value.split("\\.")[1];
        final int lengthBefore = digitsBefore.length();

        if (lengthBefore >= digits) {
          return digitsBefore.length() > digits ? digitsBefore.length() - digits <= 3 ?
              display(Integer.parseInt(digitsBefore) / 1_000d, digits - 1) + "k" :
              display(Integer.parseInt(digitsBefore) / 1_000_000d, digits - 1) + "M" : digitsBefore;
        } else {
          final StringBuilder out = new StringBuilder(digitsBefore);
          final int beforeSize = digitsBefore.length() - (digitsBefore.contains("-") ? 1 : 0);
          out.append(".").append(digitsAfter.length() >= digits - beforeSize ? digitsAfter.substring(0, digits - beforeSize) : digitsAfter);
          return out.toString();
        }
      }
      return value;
    }
    return "-";
  }

  double getWinrate() {
    return Util.div(wins + .5 * ties, matches);
  }

  @Override
  public int compareTo(@NotNull LeagueTeam team) {
    return Optional.ofNullable(compare(team.getWinrate(), getWinrate(),
        compare(team.getKillsDiff(), killsDiff,
            compare(team.getGoldDiff(), goldDiff,
                compare(team.getCreepsDiff(), creepsDiff,
                    compare(new Double(team.getTourneyId()), new Double(tourneyId), 0)))))).orElse(0);
  }

  private Integer compare(String value, String value2, Integer compareNext) {
    return compare(stringToDouble(value), stringToDouble(value2), compareNext);
  }

  private Integer compare(Double value, Double value2, Integer compareNext) {
    if (compareNext != null) {
      int rate = Double.compare(value, value2);
      return rate == 0 ? compareNext : Integer.valueOf(rate);
    }
    return 0;
  }
}
