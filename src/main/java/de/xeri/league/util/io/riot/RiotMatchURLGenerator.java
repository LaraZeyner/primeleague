package de.xeri.league.util.io.riot;

import de.xeri.league.models.league.Account;
import de.xeri.league.util.Const;
import de.xeri.league.manager.Data;
import de.xeri.league.util.io.json.JSON;

/**
 * Created by Lara on 08.04.2022 for web
 */
public class RiotMatchURLGenerator {

  public JSON getMatchList(Account account, int startIndex) {
    return Data.getInstance().getRequester()
        .requestRiotJSON("https://europe.api.riotgames.com/lol/match/v5/matches/by-puuid/" + account.getPuuid() +
            "/ids?startTime=" + account.getLastUpdate().getTime() / 1000 +
            "&start=" + startIndex +
            "&count=100&" + Const.API_KEY.substring(1));
  }

  public JSON getMatchList(Account account, int queueId, int startIndex) {
    return Data.getInstance().getRequester()
        .requestRiotJSON("https://europe.api.riotgames.com/lol/match/v5/matches/by-puuid/" + account.getPuuid() +
            "/ids?startTime=" + account.getLastUpdate().getTime() / 1000 +
            "&queue=" + queueId +
            "&start=" + startIndex +
            "&count=100&" + Const.API_KEY.substring(1));
  }

  public JSON getMatch(String matchid) {
    return Data.getInstance().getRequester()
        .requestRiotJSON("https://europe.api.riotgames.com/lol/match/v5/matches/" + matchid + Const.API_KEY);
  }

  public JSON getTimeline(String matchid) {
    return Data.getInstance().getRequester()
        .requestRiotJSON("https://europe.api.riotgames.com/lol/match/v5/matches/" + matchid + "/timeline" + Const.API_KEY2);
  }
}
