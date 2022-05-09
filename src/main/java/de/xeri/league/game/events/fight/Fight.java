package de.xeri.league.game.events.fight;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.xeri.league.game.events.fight.enums.Fighttype;
import de.xeri.league.game.events.fight.enums.Multikill;
import de.xeri.league.game.events.location.Position;
import de.xeri.league.game.models.JSONPlayer;
import de.xeri.league.game.models.TimelineStat;
import de.xeri.league.models.enums.Lane;
import de.xeri.league.models.enums.StoredStat;
import de.xeri.league.util.Const;
import de.xeri.league.util.Util;
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

  /**
   * Ermittelt, inwiefern der Spieler am Fight beteiligt war
   * @param pId angepasste PID
   * @return ob am Fight beteiligt
   */
  public boolean isInvolved(int pId) {
    return getInvolvedPlayers().contains(pId);
  }

  /**
   * Ermittelt, inwiefern der Spieler im Fight gestorben ist
   * @param player Spieler
   * @return ob im Fight gestorben
   */
  public boolean isDying(JSONPlayer player) {
    return getKills().stream().anyMatch(kill -> kill.getVictim() == player.getPId());
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
        if (enemy != null && kill.isInvolved(player.getId() + 1) && !lane.isInArea(kill.getPosition(), player.isFirstPick())) {
          return !kill.isInvolved(enemy.getId() + 1) || (!getFighttype().equals(Fighttype.DUEL));
        }
      }
    }
    return false;
  }

  public int getMultiKillCount(int pId) {
    return (int) kills.stream()
        .filter(kill -> kill.getKiller() == pId)
        .count();
  }

  public Multikill getMultiKill(int pId) {
    return Multikill.values()[getMultiKillCount(pId)];
  }

  public int getMultiTakedownCount(int pId) {
    return (int) kills.stream()
        .filter(kill -> kill.isInvolved(pId))
        .filter(kill -> kill.getVictim() != pId)
        .count();
  }

  public Multikill getMultiTakedown(int pId) {
    return Multikill.values()[getMultiTakedownCount(pId)];
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

  /**
   * Ermittelt den Schaden eines Spielers an Champions waehrend eines Fights
   * @param player Spieler
   * @return Gesamtschaden durch den Spieler verursacht
   */
  public int getFightDamage(JSONPlayer player) {
    final int start = getStart(player);
    int end = getEnd(player);
    if (end == start) {
      end+= 60_000;
    }
    if (end / 60_000 <= player.getLastMinute()) {
      return damageBetween(player, start, end);
    }
    return 0;
  }

  private static int damageBetween(JSONPlayer player, int start, int end) {
    final int startDamage = player.getStatAt(start / 60_000, TimelineStat.DAMAGE);
    final int endDamage;
    if (end / 60_000 <= player.getLastMinute()) {
      endDamage = player.getStatAt(end / 60_000, TimelineStat.DAMAGE);
    } else {
      endDamage = player.getMedium(StoredStat.DAMAGE_TOTAL);
    }

    return endDamage - startDamage;
  }

  public List<Kill> getKills() {
    return new ArrayList<>(kills);
  }

  public Teamfight getTeamfight() {
    return getFighttype().equals(Fighttype.TEAMFIGHT) ? new Teamfight(kills) : null;
  }

  public Duel getDuel() {
    return getFighttype().equals(Fighttype.DUEL) ? new Duel(kills) : null;
  }

  public Pick getPick() {
    return getFighttype().equals(Fighttype.PICK) ? new Pick(kills) : null;
  }

  public Skirmish getSkirmish() {
    return getFighttype().equals(Fighttype.SKIRMISH) ? new Skirmish(kills) : null;
  }

  /**
   * Ermittelt den Startzeitpunkt des Fights fuer den Spieler
   * @param player Spieler
   * @return Start-Millis
   */
  public int getStart(JSONPlayer player) {
    val firstKill = kills.stream()
        .filter(kill -> kill.isInvolved(player.getId() + 1))
        .findFirst().orElse(null);
    return handleKill(player, firstKill);
  }

  /**
   * Ermittelt den Endzeitpunkt des Fights fuer den Spieler
   * @param player Spieler
   * @return End-Millis
   */
  public int getEnd(JSONPlayer player) {
    val firstKill = kills.stream()
        .filter(kill -> kill.isInvolved(player.getId() + 1))
        .reduce((first, second) -> second).orElse(null);
    return handleKill(player, firstKill);
  }

  /**
   * Ermittelt, inwiefern der Fight innerhalb des Zeitraumes stattfand
   * @param player Spieler
   * @param startMinute Startminute
   * @param endMinute Endminute
   * @return ob Fight im Zeitraum stattfand
   */
  public boolean isInsideMinutes(JSONPlayer player, int startMinute, int endMinute) {
    return getStart(player) > startMinute * 60_000 &&  getEnd(player) < endMinute * 60_000;
  }

  /**
   * Ermittelt die Dauer des Fights fuer einen Spieler
   * @param player Spieler
   * @return Dauer in Millis
   */
  public int duration(JSONPlayer player) {
    return getEnd(player) - getStart(player);
  }

  private int handleKill(JSONPlayer player, Kill kill) {
    if (kill != null) {
      val lane = player.getLane();
      final Position killPosition = kill.getPosition();
      val lanePosition = lane.getCenter(killPosition, player.isFirstPick());
      final double distance = Util.distance(killPosition, lanePosition);
      final int walkTime = (int) (distance * 2.5);
      return kill.getTimestamp() + walkTime;
    }
    return -1;
  }

}
