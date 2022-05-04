package de.xeri.league.game.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import de.xeri.league.game.RiotGameRequester;
import de.xeri.league.game.events.items.ExperienceCalculator;
import de.xeri.league.game.events.items.Inventory;
import de.xeri.league.game.events.location.Position;
import de.xeri.league.models.enums.EventTypes;
import de.xeri.league.models.enums.Lane;
import de.xeri.league.models.enums.StoredStat;
import de.xeri.league.models.league.Account;
import de.xeri.league.models.league.Team;
import lombok.Getter;
import lombok.val;
import org.json.JSONArray;
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
  public static JSONPlayer getPlayer(int id) {
    return RiotGameRequester.jsonTeams.stream()
        .flatMap(team -> team.getAllPlayers().stream())
        .filter(jsonPlayer -> jsonPlayer.getId() == id)
        .findFirst().orElse(null);
  }

  private final int id;
  private final Account account;
  private final String puuid;
  private final JSONObject json;
  private final Inventory inventory = new Inventory();
  private final List<JSONObject> events = new ArrayList<>();
  private final List<JSONObject> infos = new ArrayList<>();
  private final boolean firstPick;

  public JSONPlayer(int id, JSONObject json, String puuid, boolean firstPick) {
    this.id = id;
    this.json = json;
    this.puuid = puuid;
    this.account = Account.findPuuid(puuid);
    this.firstPick = firstPick;
  }

  public void addEvent(JSONObject event) {
    events.add(event);
  }

  public void addInfo(JSONObject info, int minute) {
    infos.set(minute, info);
  }

  public JSONObject getLastInfo() {
    return infos.isEmpty() ? null : infos.get(getLastMinute());
  }

  public int getLastMinute() {
    return infos.isEmpty() ? -1 : infos.size() - 1;
  }

  public Lane getLane() {
    return Lane.valueOf(get(StoredStat.LANE));
  }

  public String get(StoredStat storedStat) {
    if (storedStat.isChallenge() && json.has("challenges")) {
      return json.getJSONObject("challenges").getString(storedStat.getKey());
    } else if (json.has(storedStat.getKey())) {
      return json.getString(storedStat.getKey());
    }
    return null;
  }

  public Boolean getBool(StoredStat storedStat) {
    if (storedStat.isChallenge() && json.has("challenges")) {
      return json.getJSONObject("challenges").getBoolean(storedStat.getKey());
    } else if (json.has(storedStat.getKey())) {
      return json.getBoolean(storedStat.getKey());
    }
    return null;
  }

  /**
   * SMALLINT(5) : (0.000.065.535)
   * MEDIUMINT(7): -0.008.388.608 → 0.008.388.607 (0.016.777.215)
   * INTEGER(10) : -2.147.483.648 → 2.147.483.647
   *
   * @param storedStat stat from JSON
   * @return Integer for smallint unsigned, mediumint and integer signed
   */
  public Integer getMedium(StoredStat storedStat) {
    if (storedStat.isChallenge() && json.has("challenges")) {
      return json.getJSONObject("challenges").getInt(storedStat.getKey());
    } else if (json.has(storedStat.getKey())) {
      return json.getInt(storedStat.getKey());
    }
    return null;
  }

  public Integer getMedium(StoredStat challenge, StoredStat alternative) {
    return getMedium(challenge) != null ? getMedium(challenge) : getMedium(alternative);
  }

  /**
   * TINYINT(3) : (00.255)
   * SMALLINT(5) : -32.768 → 32.767
   *
   * @param storedStat stat from JSON
   * @return Short for tinyint unsigned and smallint signed
   */
  public Short getSmall(StoredStat storedStat) {
    if (getMedium(storedStat) != null) {
      return Short.parseShort(String.valueOf(getMedium(storedStat)));
    }
    return null;
  }

  public Short getSmall(StoredStat storedStat, int factor) {
    if (getMedium(storedStat) != null) {
      return Short.parseShort(String.valueOf(getMedium(storedStat)));
    }
    return null;
  }

  public Short getSmall(StoredStat challenge, StoredStat alternative) {
    return getSmall(challenge) != null ? getSmall(challenge) : getSmall(alternative);
  }

  /**
   * TINYINT(3) : -128 → 127
   *
   * @param storedStat stat from JSON
   * @return Byte for tinyint signed
   */
  public Byte getTiny(StoredStat storedStat) {
    if (getMedium(storedStat) != null) {
      return Byte.parseByte(String.valueOf(getMedium(storedStat)));
    }
    return null;
  }

  public Byte getTiny(StoredStat challenge, StoredStat alternative) {
    return getTiny(challenge) != null ? getTiny(challenge) : getTiny(alternative);
  }

  public JSONArray array(StoredStat storedStat) {
    if (storedStat.isChallenge() && json.has("challenges")) {
      return json.getJSONObject("challenges").getJSONArray(storedStat.getKey());
    } else if (json.has(storedStat.getKey())) {
      return json.getJSONArray(storedStat.getKey());
    }
    return null;
  }

  public JSONObject object(StoredStat storedStat) {
    if (storedStat.isChallenge() && json.has("challenges")) {
      return json.getJSONObject("challenges").getJSONObject(storedStat.getKey());
    } else if (json.has(storedStat.getKey())) {
      return json.getJSONObject(storedStat.getKey());
    }
    return null;
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
    return RiotGameRequester.jsonTeams.stream()
        .flatMap(jsonTeam -> jsonTeam.getAllPlayers().stream())
        .collect(Collectors.toList());
  }

  private List<JSONPlayer> findJSONPlayersWith(String value) {
    return getJSONPlayers().stream()
        .filter(player -> player.get(StoredStat.LANE).equals(value))
        .collect(Collectors.toList());
  }

  public JSONPlayer getEnemy() {
    val jsonPlayers = findJSONPlayersWith(get(StoredStat.LANE)).stream()
        .filter(player -> player.getId() != this.id)
        .collect(Collectors.toList());
    return jsonPlayers.isEmpty() ? null : jsonPlayers.get(0);
  }

  public boolean hasEnemy() {
    return getEnemy() != null;
  }

  public int getStatAt(int minute, TimelineStat stat) {
    int value = -1;
    if (infos.get(minute) != null) {
      value = getValue(minute, stat);
    } else if (!infos.isEmpty()) {
      value = getValue(getLastMinute(), stat);
    }

    return value;
  }

  public Position getPositionAt(int minute) {
    return new Position(getStatAt(minute, TimelineStat.POSITION_X), getStatAt(minute, TimelineStat.POSITION_Y));
  }

  public double getStatPerMinute(int minute, TimelineStat stat) {
    return getStatAt(minute, stat) * 1d / minute;
  }

  private int getValue(int minute, TimelineStat stat) {
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

  public int getLeadAt(int minute, TimelineStat stat) {
    if (hasEnemy()) {
      return getStatAt(minute, stat) - getEnemy().getStatAt(minute, stat);
    }
    return 0;
  }

  public int getLeadDifferenceAt(int start, int end, TimelineStat stat) {
    return getLeadAt(end, stat) - getLeadAt(start, stat);
  }

  public List<JSONObject> getEvents(EventTypes type) {
    return getEvents(type, 0);
  }

  public List<JSONObject> getEvents(EventTypes type, int startMillis) {
    return getEvents(type, startMillis, Integer.MAX_VALUE);
  }

  public List<JSONObject> getEvents(int startMillis, int endMillis) {
    return events.stream()
        .filter(event -> event.getInt("timestamp") >= startMillis)
        .filter(event -> event.getInt("timestamp") <= endMillis)
        .collect(Collectors.toList());
  }

  public List<JSONObject> getEvents(EventTypes type, int startMillis, int endMillis) {
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
    return JSONTeam.getTeam(isFirstPick() ? 100 : 200);
  }

  /**
   * Ermittelt den prozentualen Wert im Verhältnis zum maximalen Wert sofern vorhanden
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
