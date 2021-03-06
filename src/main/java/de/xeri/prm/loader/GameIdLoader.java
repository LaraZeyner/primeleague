package de.xeri.prm.loader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import de.xeri.prm.models.enums.QueueType;
import de.xeri.prm.models.league.Account;
import de.xeri.prm.models.match.ScheduledGame;
import de.xeri.prm.manager.PrimeData;
import de.xeri.prm.util.io.json.JSON;
import de.xeri.prm.util.io.riot.RiotURLGenerator;
import lombok.val;

/**
 * Created by Lara on 29.04.2022 for web
 */
public final class GameIdLoader {

  /**
   * Bekannte Informationen
   * <br>
   * Queue-Types: TOURNEY, CLASH, OTHER
   * Team: 6x10 = 60
   *
   * @param account
   */
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

    PrimeData.getInstance().commit();
  }

  public static void load(QueueType queueType, Account account) {
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
            .map(gameId -> ScheduledGame.get(new ScheduledGame(gameId, queueType), account.getOfficialTeam() != null ? account.getOfficialTeam().getId() : null))
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
