package de.xeri.league.util.io.riot;

import java.util.List;

import de.xeri.league.models.enums.StoredStat;
import de.xeri.league.models.league.Account;
import de.xeri.league.models.league.Team;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Lara on 09.04.2022 for web
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

  public String string(StoredStat storedStat) {
    if (storedStat.isChallenge() && json.has("challenges")) {
      return json.getJSONObject("challenges").getString(storedStat.getKey());
    } else if (json.has(storedStat.getKey())) {
      return json.getString(storedStat.getKey());
    }
    return null;
  }

  public Integer Int(StoredStat storedStat) {
    if (storedStat.isChallenge() && json.has("challenges")) {
      return json.getJSONObject("challenges").getInt(storedStat.getKey());
    } else if (json.has(storedStat.getKey())) {
      return json.getInt(storedStat.getKey());
    }
    return null;
  }

  public Short Short(StoredStat storedStat) {
    if (Int(storedStat) != null) {
      return Short.parseShort(String.valueOf(Int(storedStat)));
    }
    return null;
  }

  public Byte Byte(StoredStat storedStat) {
    if (Int(storedStat) != null) {
      return Byte.parseByte(String.valueOf(Int(storedStat)));
    }
    return null;
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
