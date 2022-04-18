package de.xeri.league.util.io.riot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.xeri.league.models.enums.Elo;
import de.xeri.league.models.enums.QueueType;
import de.xeri.league.models.league.Account;
import de.xeri.league.models.league.Season;
import de.xeri.league.models.league.SeasonElo;
import de.xeri.league.models.match.ScheduledGame;
import de.xeri.league.util.Const;
import de.xeri.league.util.Data;
import de.xeri.league.util.io.json.JSON;
import org.json.JSONArray;
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

  public static Account getAccountFromPuuid(String puuid) {
    final JSON json = Data.getInstance().getRequester()
        .requestRiotJSON("https://euw1.api.riotgames.com/lol/summoner/v4/summoners/by-puuid/" + puuid + Const.API_KEY);
    if (json == null) return null;
    return getAccountFromRiot(puuid, json.getJSONObject());
  }

  public static Account getAccountFromName(String name) {
    final JSON json = Data.getInstance().getRequester()
        .requestRiotJSON("https://euw1.api.riotgames.com/lol/summoner/v4/summoners/by-name/" + name + Const.API_KEY);
    if (json == null) return null;
    final JSONObject summoner = json.getJSONObject();
    final String puuid = summoner.getString("puuid");
    return getAccountFromRiot(puuid, summoner);
  }

  private static Account getAccountFromRiot(String puuid, JSONObject summoner) {
    final String id = summoner.getString("id");
    final String summonerName = summoner.getString("name");
    final short iconId = (short) summoner.getInt("profileIconId");
    final short level = (short) summoner.getInt("summonerLevel");
    return Account.get(new Account(puuid, id, summonerName, iconId, level));
  }

  public static void loadElo(Account account) {
    for (Season season : Season.get()) {
      if (season.getSeasonStart().getTime().getTime() <= new Date().getTime() && season.getSeasonEnd().getTime().getTime() >= new Date().getTime()) {
        final JSON json = Data.getInstance().getRequester().requestRiotJSON(
            "https://euw1.api.riotgames.com/lol/league/v4/entries/by-summoner/" + account.getSummonerId() + Const.API_KEY);
        if (json != null) {
          final JSONArray rankedLeagues = json.getJSONArray();
          final Map<String, SeasonElo> elos = new HashMap<>();
          for (int i = 0; i < rankedLeagues.length(); i++) {
            final JSONObject league = rankedLeagues.getJSONObject(i);
            final String wins = league.getString("wins");
            final String losses = league.getString("losses");
            final String tier = league.getString("tier");
            final String rank = league.getString("rank");
            final short lp = (short) league.getInt("leaguePoints");
            final short mmr = determineMMR(tier, rank, lp);
            elos.put(league.getString("queueType"), SeasonElo.get(new SeasonElo(mmr, Short.parseShort(wins),
                Short.parseShort(losses)), season, account));
          }
          SeasonElo seasonElo = null;
          if (elos.get(Const.QUEUE_SOLO).getGames() >= 40) {
            seasonElo = elos.get(Const.QUEUE_SOLO);
          } else if (elos.get(Const.QUEUE_FLEX).getGames() >= 40) {
            seasonElo = elos.get(Const.QUEUE_FLEX);
          } else if (elos.get(Const.QUEUE_SOLO).getGames() + elos.get(Const.QUEUE_FLEX).getGames() >= 50) {
            seasonElo = add(elos.get(Const.QUEUE_FLEX), elos.get(Const.QUEUE_SOLO), account, season);
          }

          if (seasonElo != null) {
            account.addSeasonElo(seasonElo);
            season.addSeaonElo(seasonElo);
          }
        }
      }
    }
  }

  private static SeasonElo add(SeasonElo elo, SeasonElo elo2, Account account, Season season) {
    final float mmr = (elo.getMmr() * 1f * elo.getGames() + elo2.getMmr() * 1f * elo2.getGames()) / (elo.getGames() * 1f + elo2.getGames());
    return SeasonElo.get(new SeasonElo((short) mmr, (short) (elo.getWins() + elo2.getWins()),
        (short) (elo.getLosses() + elo2.getLosses())), season, account);
  }

  private static short determineMMR(String tier, String rank, short lp) {
    final Elo elo = Elo.valueOf(tier + "_" + rank);
    short mmr = (short) (elo.getMmr() + lp);
    if (elo.equals(Elo.GRANDMASTER)) mmr -= 500;
    if (elo.equals(Elo.CHALLENGER)) mmr -= 1000;
    return mmr;
  }

  public static void loadAll(Account account) {
    loadCompetitive(account);
    load(QueueType.OTHER, account);
    account.setLastUpdate(new Date());
    final Date lastCompetitiveGame = account.getLastCompetitiveGame();
    account.setActive(lastCompetitiveGame == null ||
        !lastCompetitiveGame.before(new Date(System.currentTimeMillis() - Const.ACTIVE_UNTIL * 86_400_000L)));
  }


  public static void loadCompetitive(Account account) {
    load(QueueType.TOURNEY, account);
    load(QueueType.CLASH, account); // TODO: 15.04.2022 Only if not to much
  }


  public static List<ScheduledGame> load(QueueType queueType, Account account) {
    final List<ScheduledGame> scheduledGames = new ArrayList<>();
    int start = 0;
    while (true) {
      final List<ScheduledGame> scheduled = load(queueType, account, start);
      start += 100;
      if (scheduled != null) {
        scheduledGames.addAll(scheduled);
        if (scheduled.size() == 100) continue;
        break;
      }
      if (start > 10_000) break;
    }
    return scheduledGames;
  }


  private static List<ScheduledGame> load(QueueType queueType, Account account, int start) {
    final JSON json = determineJSON(queueType, account, start);
    if (json != null) {
      final List<String> gameIds = json.getJSONArray().toList().stream().map(String::valueOf).collect(Collectors.toList());
      return gameIds.stream().map(id -> ScheduledGame.get(new ScheduledGame(id, queueType)))
          .collect(Collectors.toList());
    }
    return null;

  }

  private static JSON determineJSON(QueueType queueType, Account account, int start) {
    return queueType.getQueueId() == -2 ?
        RiotURLGenerator.getMatch().getMatchList(account, start) :
        RiotURLGenerator.getMatch().getMatchList(account, queueType.getQueueId(), start);
  }
}
