package de.xeri.prm.util.io.riot;

import java.util.HashMap;

import de.xeri.prm.models.enums.Elo;
import de.xeri.prm.models.enums.EloQueueType;
import de.xeri.prm.models.league.Account;
import de.xeri.prm.models.league.SeasonElo;
import de.xeri.prm.util.Const;
import de.xeri.prm.manager.PrimeData;
import lombok.val;
import org.json.JSONObject;

/**
 * Created by Lara on 07.04.2022 for web
 */
public final class RiotAccountRequester {
  /**
   * {
   * "id": "yYVHkU00NInbpd5DJyy3rlU4XVcgmW0v6a6CbnuP3hQunGfv",
   * "accountId": "pkk6cj2geKn6dk2WUtM7tNlIyPnyfkKdIW-p5oDrvvjpTGcPWgTiNpNT",
   * "puuid": "r_LSfzLc4ZEGzRaJqcKKQyEhaLZECy_M4DQIP5ASjbKePVojquEr3rfr6YlJxMixuXfoc4W_ZPYXYg",
   * "name": "TRUE Xeri",
   * "profileIconId": 5254,
   * "revisionDate": 1649188588000,
   * "summonerLevel": 674
   * }
   */

  public static void loadElo(Account account) {
    if (account != null && account.getSummonerId() != null) {
      val season = PrimeData.getInstance().getCurrentSeason();
      val json = PrimeData.getInstance().getRequester().requestRiotJSON(
          "https://euw1.api.riotgames.com/lol/league/v4/entries/by-summoner/" + account.getSummonerId() + Const.API_KEY);
      if (json != null) {
        val rankedLeagues = json.getJSONArray();
        val elos = new HashMap<String, SeasonElo>();

        for (int i = 0; i < rankedLeagues.length(); i++) {
          val league = rankedLeagues.getJSONObject(i);
          val queueType = league.getString("queueType");
          if (EloQueueType.has(queueType)) {
            handleQueue(elos, league, queueType);
          }
        }

        val neu = determineSeasonElo(elos);
        if (neu != null) {
          SeasonElo.get(neu, season, account);
        }
      }
    }
  }

  private static void handleQueue(HashMap<String, SeasonElo> elos, JSONObject league, String queueType) {
    val tier = league.getString("tier");
    val rank = league.getString("rank");
    final short lp = (short) league.getInt("leaguePoints");

    final short mmr = determineMMR(tier, rank, lp);
    final short wins = (short) league.getInt("wins");
    final short losses = (short) league.getInt("losses");
    val elo = new SeasonElo(mmr, wins, losses);
    elos.put(queueType, elo);
  }

  private static SeasonElo determineSeasonElo(HashMap<String, SeasonElo> elos) {
    val solo = elos.get(EloQueueType.RANKED_SOLO_5x5.name());
    val flex = elos.get(EloQueueType.RANKED_FLEX_SR.name());

    if (solo != null && solo.getGames() >= 40) {
      return elos.get(Const.QUEUE_SOLO);

    } else if (flex != null && flex.getGames() >= 40) {
      return elos.get(Const.QUEUE_FLEX);

    } else if ((solo == null ? 0 : solo.getGames()) + (flex == null ? 0 : flex.getGames()) >= 50) {
      // kann nie null sein
      return add(solo, flex);
    }

    return null;
  }

  private static SeasonElo add(SeasonElo elo, SeasonElo elo2) {
    final float mmr = (elo.getMmr() * 1f * elo.getGames() + elo2.getMmr() * 1f * elo2.getGames()) / (elo.getGames() * 1f + elo2.getGames());
    return new SeasonElo((short) mmr, (short) (elo.getWins() + elo2.getWins()), (short) (elo.getLosses() + elo2.getLosses()));
  }

  private static short determineMMR(String tier, String rank, short lp) {
    final Elo elo = Elo.valueOf(tier + "_" + rank);
    short mmr = (short) (elo.getMmr() + lp);
    if (elo.equals(Elo.GRANDMASTER)) mmr -= 500;
    if (elo.equals(Elo.CHALLENGER)) mmr -= 1000;
    return mmr;
  }
}
