package de.xeri.league.loader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import de.xeri.league.models.enums.QueueType;
import de.xeri.league.models.league.Account;
import de.xeri.league.models.match.ScheduledGame;
import de.xeri.league.manager.Data;
import de.xeri.league.util.io.json.JSON;
import de.xeri.league.util.io.riot.RiotURLGenerator;
import lombok.val;

/**
 * Created by Lara on 29.04.2022 for web
 */
public final class GameIdLoader {

  public static void loadGameIds(Account account) {
    load(QueueType.TOURNEY, account);
    if (account.isValueable()) {
      load(QueueType.CLASH, account);
      load(QueueType.OTHER, account);
      if (account.getOfficialTeam() == null) {
        account.setActive(account.isPlaying());
      }
    }
    account.setLastUpdate(new Date());

    Data.getInstance().commit();
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
            .map(gameId -> ScheduledGame.get(new ScheduledGame(gameId, queueType, account.isValueable())))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
      }
    }
    return new ArrayList<>();
  }

  public static JSON determineJSON(QueueType queueType, Account account, int start) {
    val matchGenerator = RiotURLGenerator.getMatch();
    if (queueType.getQueueId() == -2) {
      return matchGenerator.getMatchList(account, start);
    }
    return matchGenerator.getMatchList(account, queueType.getQueueId(), start);
  }
}
