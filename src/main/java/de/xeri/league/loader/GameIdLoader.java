package de.xeri.league.loader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import de.xeri.league.models.enums.QueueType;
import de.xeri.league.models.league.Account;
import de.xeri.league.models.match.ScheduledGame;
import de.xeri.league.util.Data;
import de.xeri.league.util.io.json.JSON;
import de.xeri.league.util.io.riot.RiotURLGenerator;
import de.xeri.league.util.logger.Logger;
import lombok.val;

/**
 * Created by Lara on 29.04.2022 for web
 */
public final class GameIdLoader {

  private static int amount = 0;
  private final static int max;

  static {
    max = (int) Account.get().stream().filter(Account::isActive).count();
  }

  public static void loadGameIds(Account account) {
    loadCompetitive(account);
    if (account.isValueable()) {
      load(QueueType.OTHER, account);
      if (account.getOfficialTeam() == null) {
        account.setActive(account.isPlaying());
      }
    }
    account.setLastUpdate(new Date());

    Data.getInstance().commit();
    amount++;
      Logger.getLogger("Game-Ids laden").info("Account " + amount + " von " + max + " geladen.");
  }


  private static void loadCompetitive(Account account) {
    load(QueueType.TOURNEY, account);
    load(QueueType.CLASH, account);
  }


  private static void load(QueueType queueType, Account account) {
    int start = 0;
    while (true) {
      val scheduled = load(queueType, account, start);
      start += 100;
      if (scheduled != null) {
        if (scheduled.size() == 100) continue;
        break;
      }
      if (start > 10_000) break;
    }
  }


  private static List<ScheduledGame> load(QueueType queueType, Account account, int start) {
    if (account.getPuuid() != null) {
      val json = determineJSON(queueType, account, start);
      if (json != null) {
        val jsonList = json.getJSONArray().toList();
        val gameIds = jsonList.stream()
            .map(String::valueOf)
            .collect(Collectors.toList());
        return gameIds.stream()
            .map(gameId -> ScheduledGame.get(new ScheduledGame(gameId, queueType)))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
      }
    }
    return new ArrayList<>();
  }

  private static JSON determineJSON(QueueType queueType, Account account, int start) {
    val matchGenerator = RiotURLGenerator.getMatch();
    if (queueType.getQueueId() == -2) {
      return matchGenerator.getMatchList(account, start);
    }
    return matchGenerator.getMatchList(account, queueType.getQueueId(), start);
  }
}