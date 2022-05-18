package de.xeri.prm.game.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.xeri.prm.game.GameAnalyser;
import de.xeri.prm.game.events.items.ExperienceCalculator;
import de.xeri.prm.game.events.items.Inventory;
import de.xeri.prm.game.events.location.Position;
import de.xeri.prm.models.enums.EventTypes;
import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.enums.StoredStat;
import de.xeri.prm.models.league.Account;
import de.xeri.prm.models.league.Team;
import de.xeri.prm.util.io.exception.NoChallengeException;
import de.xeri.prm.util.logger.Logger;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import org.json.JSONObject;

/**
 * Created by Lara on 09.04.2022 for web
 * <p>
 * TINYINT(3) : -0.000.000.128 → 0.000.000.127 (0.000.000.255)
 * SMALLINT(5) : -0.000.032.768 → 0.000.032.767 (0.000.065.535)
 * MEDIUMINT(7): -0.008.388.608 → 0.008.388.607 (0.016.777.215)
 * INTEGER(10) : -2.147.483.648 → 2.147.483.647 (4.294.967.295)
 */

@Getter
public class JSONPlayer {
  private final int highestMinute;
  private final int id;
  private final Account account;
  private final String puuid;
  private final JSONObject json;
  private final Inventory inventory = new Inventory();
  private final List<JSONObject> events = new ArrayList<>();
  private final List<JSONObject> infos;
  private final boolean firstPick;

  public JSONPlayer(int id, JSONObject json, String puuid, boolean firstPick, int highestMinute) {
    this.id = id;
    this.json = json;
    this.puuid = puuid;
    this.infos = new ArrayList<>();
    IntStream.range(0, highestMinute + 1).forEach(i -> this.infos.add(null));
    this.account = Account.hasPuuid(puuid) ? Account.findPuuid(puuid) : null;
    this.firstPick = firstPick;
    this.highestMinute = highestMinute;
  }

  public int getPId() {
    return id + 1;
  }

  public void addEvent(@NonNull JSONObject event) {
    events.add(event);
  }

  public void addInfo(@NonNull JSONObject info, int minute) {
    infos.set(minute, info);
  }

  public JSONObject getLastInfo() {
    return infos.isEmpty() ? null : infos.get(getLastMinute());
  }

  public int getLastMinute() {
    return infos.isEmpty() ? -1 : infos.size() - 1;
  }

  public Lane getLane() {
    final String laneName = get(StoredStat.LANE);
    return laneName.equals("") ? Lane.UNKNOWN : Lane.valueOf(laneName);
  }

  public String get(@NonNull StoredStat storedStat) {
    if (storedStat.isChallenge() && json.has("challenges")) {
      return json.getJSONObject("challenges").getString(storedStat.getKey());
    } else if (json.has(storedStat.getKey())) {
      return json.getString(storedStat.getKey());
    }
    return null;
  }

  public boolean has(@NonNull StoredStat storedStat) {
    return storedStat.isChallenge() && json.has("challenges") && json.getJSONObject("challenges").has(storedStat.getKey()) || json.has(storedStat.getKey());
  }

  public Boolean getBool(@NonNull StoredStat storedStat) {
    if (storedStat.isChallenge() && json.has("challenges")) {
      final JSONObject challenges = json.getJSONObject("challenges");
      if (challenges.has(storedStat.getKey())) {
        return challenges.getBoolean(storedStat.getKey());
      }
    }

    return json.has(storedStat.getKey()) ? json.getBoolean(storedStat.getKey()) : null;
  }

  /**
   * SMALLINT(5) : (0.000.065.535)
   * MEDIUMINT(7): -0.008.388.608 → 0.008.388.607 (0.016.777.215)
   * INTEGER(10) : -2.147.483.648 → 2.147.483.647
   *
   * @param storedStat stat from JSON
   * @return Integer for smallint unsigned, mediumint and integer signed
   */
  public Integer getMedium(@NonNull StoredStat storedStat) {
    try {
      return handleStat(storedStat);

    } catch (NoChallengeException exception) {
      if (!GameAnalyser.noChallengeWarned) {
        Logger.getLogger("Spielanalyse").fine(exception.getMessage(), exception);
        GameAnalyser.noChallengeWarned = true;
      }

      return null;
    }
  }

  private Integer handleStat(@NonNull StoredStat storedStat) throws NoChallengeException {
    if (storedStat.isChallenge()) {
      if (json.has("challenges")) {
        final JSONObject challenges = json.getJSONObject("challenges");
        if (challenges.has(storedStat.getKey())) {
          return challenges.getInt(storedStat.getKey());
        }
      } else {
        throw new NoChallengeException("Challenges nicht erstellt");
      }
    }

    return json.has(storedStat.getKey()) ? json.getInt(storedStat.getKey()) : null;
  }

  /**
   * TINYINT(3) : (00.255)
   * SMALLINT(5) : -32.768 → 32.767
   *
   * @param storedStat stat from JSON
   * @return Short for tinyint unsigned and smallint signed
   */
  public Short getSmall(@NonNull StoredStat storedStat) {
    final Integer statInteger = getMedium(storedStat);
    if (statInteger != null) {
      return Short.parseShort(String.valueOf(statInteger));
    }
    return null;
  }

  /**
   * TINYINT(3) : -128 → 127
   *
   * @param storedStat stat from JSON
   * @return Byte for tinyint signed
   */
  public Byte getTiny(@NonNull StoredStat storedStat) {
    final Integer statInteger = getMedium(storedStat);
    if (statInteger != null) {
      return Byte.parseByte(String.valueOf(statInteger));
    }
    return null;
  }

  public Byte getTiny(@NonNull StoredStat challenge, @NonNull StoredStat alternative) {
    final Byte stat1 = getTiny(challenge);
    return stat1 != null ? stat1 : getTiny(alternative);
  }

  public JSONObject object(@NonNull StoredStat storedStat) {
    if (storedStat.isChallenge() && json.has("challenges")) {
      final JSONObject challenges = json.getJSONObject("challenges");
      if (challenges.has(storedStat.getKey())) {
        return challenges.getJSONObject(storedStat.getKey());
      }
    }

    return json.has(storedStat.getKey()) ? json.getJSONObject(storedStat.getKey()) : null;
  }

  public boolean isListed() {
    return account != null;
  }

  public List<Team> getTeams() {
    return account != null ? account.getTeams() : null;
  }

  public Team getOfficialTeam() {
    return account.getOfficialTeam();
  }

  public boolean hasTeam() {
    return getTeams() != null;
  }

  private List<JSONPlayer> getJSONPlayers() {
    return GameAnalyser.jsonTeams.stream()
        .flatMap(jsonTeam -> jsonTeam.getAllPlayers().stream())
        .collect(Collectors.toList());
  }

  public JSONPlayer getEnemy() {
    val jsonPlayers = getJSONPlayers().stream()
        .filter(player -> player.get(StoredStat.LANE).equals(get(StoredStat.LANE)))
        .collect(Collectors.toList()).stream()
        .filter(player -> player.getId() != this.id)
        .collect(Collectors.toList());
    return jsonPlayers.isEmpty() ? null : jsonPlayers.get(0);
  }

  public boolean hasEnemy() {
    return getEnemy() != null;
  }

  public int getStatAt(int minute, @NonNull TimelineStat stat) {
    return infos.get(minute) != null ? getValue(minute, stat) : getValue(getLastMinute(), stat);
  }

  public Position getPositionAt(int minute) {
    return new Position(getStatAt(minute, TimelineStat.POSITION_X), getStatAt(minute, TimelineStat.POSITION_Y));
  }

  public double getStatPerMinute(int minute, @NonNull TimelineStat stat) {
    return getStatAt(minute, stat) * 1d / minute;
  }

  private int getValue(int minute, @NonNull TimelineStat stat) {
    if (stat.getQueries()[0].startsWith("/")) {
      val jsonObject = infos.get(minute).getJSONObject(stat.getQueries()[0].substring(1));
      return jsonObject.getInt(stat.getQueries()[1]);

    } else {
      int value = infos.get(minute).getInt(stat.getQueries()[0]);
      if (stat.getQueries().length == 2) {
        value += infos.get(minute).getInt(stat.getQueries()[1]);
      }
      return value;
    }
  }

  public int getStatDifference(int start, int end, @NonNull TimelineStat stat) {
    if (start < highestMinute) {
      if (end <= highestMinute) {
        return getStatAt(end, stat) - getStatAt(start, stat);
      } else {
        return getStatAt(highestMinute, stat) - getStatAt(start, stat);
      }
    }
    return 0;
  }

  public int getLeadAt(int minute, @NonNull TimelineStat stat) {
    if (hasEnemy() && minute <= highestMinute) {
      return getStatAt(minute, stat) - getEnemy().getStatAt(minute, stat);
    }
    return 0;
  }

  public int getLeadDifferenceAt(int start, int end, @NonNull TimelineStat stat) {
    if (start < highestMinute) {
      if (end <= highestMinute) {
        return getLeadAt(end, stat) - getLeadAt(start, stat);
      } else {
        return getLeadAt(highestMinute, stat) - getLeadAt(start, stat);
      }
    }
    return 0;
  }

  public List<JSONObject> getEvents(@NonNull EventTypes... types) {
    return Arrays.stream(types)
        .flatMap(type -> getEvents(type, 0).stream())
        .collect(Collectors.toCollection(ArrayList::new));
  }

  public List<JSONObject> getEvents(@NonNull EventTypes type) {
    return getEvents(type, 0);
  }

  public List<JSONObject> getEvents(@NonNull EventTypes type, int startMillis) {
    return getEvents(type, startMillis, Integer.MAX_VALUE);
  }

  public List<JSONObject> getEvents(int startMillis, int endMillis) {
    return events.stream()
        .filter(event -> event.getInt("timestamp") >= startMillis)
        .filter(event -> event.getInt("timestamp") <= endMillis)
        .collect(Collectors.toList());
  }

  public List<JSONObject> getEvents(@NonNull EventTypes type, int startMillis, int endMillis) {
    val allowedTypes = type == EventTypes.ALL_ITEMEVENTS ? Arrays.asList(EventTypes.ITEM_DESTROYED, EventTypes.ITEM_PURCHASED,
        EventTypes.ITEM_SOLD, EventTypes.ITEM_UNDO) : Collections.singletonList(type);

    return events.stream()
        .filter(event -> allowedTypes.contains(EventTypes.valueOf(event.getString("type"))))
        .filter(event -> event.getInt("timestamp") >= startMillis)
        .filter(event -> event.getInt("timestamp") <= endMillis)
        .collect(Collectors.toList());
  }


  public void buildInventory() {
    inventory.build(getEvents(EventTypes.ALL_ITEMEVENTS), this);
  }

  public int getLevelAt(int minute) {
    final int statAt = getStatAt(minute, TimelineStat.EXPERIENCE);
    return ExperienceCalculator.getLevelOf(statAt);
  }

  public JSONTeam getTeam() {
    return isFirstPick() ? GameAnalyser.jsonTeams.get(0) : GameAnalyser.jsonTeams.get(1);
  }

  /**
   * Ermittelt den prozentualen Wert im Verhältnis zum maximalen Wert sofern vorhanden
   *
   * @param stat zu analysierende Stat
   * @return Prozentualer Anteil zum Maximalwert
   * @throws IllegalArgumentException sofern dieser Stat nicht ueber einen <b>CURRENT</b> und einen <b>TOTAL</b> Wert verfuegt
   */
  public int getStatPercentage(int minute, TimelineStat stat) {
    if (stat.name().contains("TOTAL") || stat.name().contains("CURRENT")) {
      val totalStat = (stat.name().contains("TOTAL")) ? stat : TimelineStat.valueOf(stat.name().replace("CURRENT", "TOTAL"));
      val currentStat = (stat.name().contains("CURRENT")) ? stat : TimelineStat.valueOf(stat.name().replace("TOTAL", "CURRENT"));
      return getStatAt(minute, totalStat) == 0 ? -1 : getStatAt(minute, currentStat) / getStatAt(minute, totalStat);
    }
    throw new IllegalArgumentException("Wert nicht zulässig");
  }

  public double getPool(int minute) {
    final double healthPool = getStatPercentage(minute, TimelineStat.CURRENT_HEALTH);
    final double resourcePool = getStatPercentage(minute, TimelineStat.CURRENT_HEALTH);
    return resourcePool == -1 ? healthPool : Math.min(healthPool, resourcePool);
  }
}
