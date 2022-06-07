package de.xeri.prm.util.io.riot;

import de.xeri.prm.manager.PrimeData;
import de.xeri.prm.models.league.Account;
import de.xeri.prm.util.Const;
import de.xeri.prm.util.io.json.JSON;

/**
 * Created by Lara on 08.04.2022 for web
 */
public class RiotMatchURLGenerator {

  public JSON getMatchList(Account account, int startIndex) {
    return PrimeData.getInstance().getRequester()
        .requestRiotJSON("https://europe.api.riotgames.com/lol/match/v5/matches/by-puuid/" + account.getPuuid() +
            "/ids?startTime=" + account.getLastUpdate().getTime() / 1000 +
            "&start=" + startIndex +
            "&count=100&" + Const.API_KEY.substring(1));
  }

  public JSON getMatchList(Account account, int queueId, int startIndex) {
    return PrimeData.getInstance().getRequester()
        .requestRiotJSON("https://europe.api.riotgames.com/lol/match/v5/matches/by-puuid/" + account.getPuuid() +
            "/ids?startTime=" + account.getLastUpdate().getTime() / 1000 +
            "&queue=" + queueId +
            "&start=" + startIndex +
            "&count=100&" + Const.API_KEY.substring(1));
  }

  public JSON getMatch(String matchid) {
    return PrimeData.getInstance().getRequester()
        .requestRiotJSON("https://europe.api.riotgames.com/lol/match/v5/matches/" + matchid + Const.API_KEY);
  }

  public JSON getTimeline(String matchid) {
    return PrimeData.getInstance().getRequester()
        .requestRiotJSON("https://europe.api.riotgames.com/lol/match/v5/matches/" + matchid + "/timeline" + Const.API_KEY2);
  }
}
