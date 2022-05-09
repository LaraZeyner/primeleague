package de.xeri.league.util.io.riot;

import de.xeri.league.models.league.Account;
import de.xeri.league.util.Const;
import de.xeri.league.manager.Data;
import lombok.val;
import org.json.JSONObject;

/**
 * Created by Lara on 08.04.2022 for web
 */
public class RiotAccountURLGenerator {
  // TODO Update in larger Time Distances
  public static Account fromPuuid(String puuid) {
    val json = Data.getInstance().getRequester()
        .requestRiotJSON("https://euw1.api.riotgames.com/lol/summoner/v4/summoners/by-puuid/" + puuid + Const.API_KEY);
    if (json == null) return null;
    return fromRiot(puuid, json.getJSONObject());
  }

  public static Account fromName(String name) {
    val json = Data.getInstance().getRequester()
        .requestRiotJSON("https://euw1.api.riotgames.com/lol/summoner/v4/summoners/by-name/" + name + Const.API_KEY);
    if (json == null) return null;
    val summoner = json.getJSONObject();
    final String puuid = summoner.getString("puuid");
    return fromRiot(puuid, summoner);
  }

  private static Account fromRiot(String puuid, JSONObject summoner) {
    final String id = summoner.getString("id");
    final String summonerName = summoner.getString("name");
    final short iconId = (short) summoner.getInt("profileIconId");
    final short level = (short) summoner.getInt("summonerLevel");
    return Account.get(new Account(puuid, id, summonerName, iconId, level));
  }
}

