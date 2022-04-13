package de.xeri.league.util.io.riot;

import java.util.List;

import de.xeri.league.models.enums.StoredStat;
import de.xeri.league.models.league.Account;
import de.xeri.league.models.league.Team;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Lara on 09.04.2022 for web#
 * <p>
 *  TINYINT(3) : -0.000.000.128 → 0.000.000.127 (0.000.000.255)
 * SMALLINT(5) : -0.000.032.768 → 0.000.032.767 (0.000.065.535)
 * MEDIUMINT(7): -0.008.388.608 → 0.008.388.607 (0.016.777.215)
 * INTEGER(10) : -2.147.483.648 → 2.147.483.647 (4.294.967.295)
 */


public class JSONPlayer {
  private final int id;
  private final Account account;
  private final JSONObject json;

  public JSONPlayer(int id, JSONObject json, Account account) {
    this.id = id;
    this.json = json;
    this.account = account;
  }

  public Object value(StoredStat storedStat) {
    if (storedStat.isChallenge() && json.has("challenges")) {
      return json.getJSONObject("challenges").get(storedStat.getKey());
    } else if (json.has(storedStat.getKey())) {
      return json.get(storedStat.getKey());
    }
    return null;
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
   *  TINYINT(3) : (00.255)
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

  public Short getSmall(StoredStat challenge, StoredStat alternative) {
    return getSmall(challenge) != null ? getSmall(challenge) : getSmall(alternative);
  }

  /**
   *  TINYINT(3) : -128 → 127
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

  public Account getAccount() {
    return account;
  }

  public int getId() {
    return id;
  }

  public JSONObject getJson() {
    return json;
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
}
