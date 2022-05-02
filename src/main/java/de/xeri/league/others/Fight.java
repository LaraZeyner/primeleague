package de.xeri.league.others;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.xeri.league.models.enums.Lane;
import de.xeri.league.models.match.Playerperformance;
import de.xeri.league.models.match.location.Position;
import de.xeri.league.models.others.Kill;
import de.xeri.league.others.enums.Fighttype;
import de.xeri.league.util.Const;
import de.xeri.league.util.Util;
import de.xeri.league.util.io.riot.JSONPlayer;
import lombok.val;

public class Fight {
  protected final Set<Kill> kills;

  public Fight(Kill kill) {
    this.kills = new LinkedHashSet<>();
    this.kills.add(kill);
  }

  public Fight(Set<Kill> kills) {
    this.kills = kills;
  }

  public void addKill(Kill kill) {
    this.kills.add(kill);
  }

  public Position getLastPosition() {
    if (!kills.isEmpty()) {
      return new ArrayList<>(kills).get(kills.size() - 1).getPosition();
    }
    return null;
  }

  public int getLastTimestamp() {
    if (!kills.isEmpty()) {
      return new ArrayList<>(kills).get(kills.size() - 1).getTimestamp();
    }
    return -1;
  }

  public List<Integer> getInvolvedPlayers() {
    return kills.stream()
        .flatMap(kill -> kill.getParticipants().keySet().stream())
        .collect(Collectors.toList());
  }

  public Fighttype getFighttype() {
    val involved = getInvolvedPlayers();
    final long bluePlayerAmount = involved.stream().filter(id -> id >= 1 && id <= 5).count();
    final long redPlayerAmount = involved.stream().filter(id -> id >= 6 && id <= 10).count();

    if (Math.min(bluePlayerAmount, redPlayerAmount) >= Const.TEAMFIGHT_PLAYERS_REQUIRED) {
      return Fighttype.TEAMFIGHT;

    } else if (Math.min(bluePlayerAmount, redPlayerAmount) == 1) {
      return Math.max(bluePlayerAmount, redPlayerAmount) > 1 || kills.size() > 1 ? Fighttype.PICK : Fighttype.DUEL;

    } else if (Math.min(bluePlayerAmount, redPlayerAmount) == Const.SKIRMISH_PLAYERS_REQUIRED) {
      return Fighttype.SKIRMISH;

    }
    throw new IllegalArgumentException("Geht gar nicht");
  }

  /**
   * Takedown in anderer Lane (Minute 1-15)
   * <ul>
   *   <li>gegen anderen Laner oder </li>
   *   <li>mit Beteiligung anderer</li>
   * </ul>
   */
  public boolean isGankOf(Lane lane, JSONPlayer player, JSONPlayer enemy) {
    if (new ArrayList<>(kills).get(0).getTimestamp() < Const.EARLYGAME_UNTIL_MINUTE * 60_000) {
      for (Kill kill : kills) {
        if (enemy != null && kill.isInvolved(player.getId() + 1) && !lane.getArea().isInArea(kill.getPosition())) {
          return !kill.isInvolved(enemy.getId() + 1) || (!getFighttype().equals(Fighttype.DUEL));
        }
      }
    }
    return false;
  }

  public boolean isWinner(int pId) {
    final int playersTeam1 = (int) getInvolvedPlayers().stream().filter(player -> player < 6 && player > 0).count();
    final int playersTeam2 = (int) getInvolvedPlayers().stream().filter(player -> player > 5).count();
    final int killsTeam1 = (int) kills.stream().filter(kill -> kill.getKiller() < 6 && kill.getKiller() > 0).count();
    final int killsTeam2 = (int) kills.stream().filter(kill -> kill.getKiller() > 5).count();
    final int remainingTeam1 = playersTeam1 - killsTeam2;
    final int remainingTeam2 = playersTeam2 - killsTeam1;
    return pId > 5 ? remainingTeam2 > remainingTeam1 : remainingTeam1 > remainingTeam2;
  }

  public int getFightDamage(Playerperformance playerperformance, JSONPlayer player) {
    final int start = start(playerperformance, player);
    int end = end(playerperformance, player);
    if (end == start) {
      end++;
    }
    return damageBetween(playerperformance, start, end);
  }

  private static int damageBetween(Playerperformance playerperformance, int start, int end) {
    val startInfo = playerperformance.getInfos().stream()
        .filter(info -> info.getMinute() == start / 60_000)
        .findFirst().orElse(null);
    if (startInfo != null) {
      val endInfo = playerperformance.getInfos().stream()
          .filter(info -> info.getMinute() == start / 60_000)
          .findFirst()
          .orElse(new ArrayList<>(playerperformance.getInfos()).get(playerperformance.getInfos().size() - 1));
      if (endInfo != null) {
        return endInfo.getTotalDamage() - startInfo.getTotalDamage();
      }
    }
    return 0;
  }



  public List<Kill> getKills() {
    return new ArrayList<>(kills);
  }

  public Teamfight getTeamfight() {
    if (getFighttype().equals(Fighttype.TEAMFIGHT)) {
      return new Teamfight(kills);
    }
    return null;
  }

  public Duel getDuel() {
    if (getFighttype().equals(Fighttype.DUEL)) {
      return new Duel(kills);
    }
    return null;
  }

  public Pick getPick() {
    if (getFighttype().equals(Fighttype.PICK)) {
      return new Pick(kills);
    }
    return null;
  }

  public Skirmish getSkirmish() {
    if (getFighttype().equals(Fighttype.SKIRMISH)) {
      return new Skirmish(kills);
    }
    return null;
  }


  public int start(Playerperformance playerperformance, JSONPlayer player) {
    val firstKill = kills.stream().filter(kill -> kill.isInvolved(player.getId() + 1))
        .findFirst().orElse(null);
    return handleKill(playerperformance, firstKill);
  }

  public int end(Playerperformance playerperformance, JSONPlayer player) {

    val firstKill = kills.stream().filter(kill -> kill.isInvolved(player.getId() + 1))
        .reduce((first, second) -> second).orElse(null);
    return handleKill(playerperformance, firstKill);
  }

  public int duration(Playerperformance playerperformance, JSONPlayer player) {
    return end(playerperformance, player) - start(playerperformance, player);
  }

  private int handleKill(Playerperformance playerperformance, Kill firstKill) {
    if (firstKill != null) {
      val lane = playerperformance.getLane();
      val lanePosition = lane.getArea().getCenter();
      final double distance = lanePosition == null ? 5000 : Util.distance(firstKill.getPosition(), lanePosition);
      final int walkTime = (int) (distance * 2.5);
      return firstKill.getTimestamp() + walkTime;
    }
    return -1;
  }

}
