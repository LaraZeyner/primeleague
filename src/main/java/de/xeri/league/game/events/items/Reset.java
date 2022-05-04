package de.xeri.league.game.events.items;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.xeri.league.game.models.JSONPlayer;
import de.xeri.league.game.models.TimelineStat;
import de.xeri.league.models.dynamic.Item;
import de.xeri.league.models.enums.EventTypes;
import de.xeri.league.util.Const;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import org.json.JSONObject;

/**
 * Created by Lara on 03.05.2022 for web
 */
@AllArgsConstructor
@Getter
public class Reset {
  private JSONPlayer player;
  private final List<ItemTransaction> transactions = new ArrayList<>();

  public Reset(JSONPlayer player, ItemTransaction transaction) {
    this.player = player;
    this.transactions.add(transaction);
  }

  public ResetReason getResetReason() {
    final int level = player.getLevelAt(getStart() / 60_000);
    final int deathTimer = ResetCalculator.getDeathTimer(level, getStart() / 1000);
    final int minDeath = player.getEvents(EventTypes.CHAMPION_KILL).stream()
        .filter(event -> event.getInt("victimId") == player.getId() + 1)
        .mapToInt(event -> Math.abs(getEnd() - ((event.getInt("timestamp") + deathTimer * 1000)))).min().orElse(Integer.MAX_VALUE);
    if (minDeath < 30_000) {
      return ResetReason.DEATH;
    }

    return ResetReason.RECALL;
  }

  public void addTransaction(ItemTransaction transaction) {
    this.transactions.add(transaction);
  }

  public int getStart() {
    return transactions.stream().mapToInt(ItemTransaction::getTimestamp).min().orElse(11) - 12;
  }

  public int getEnd() {
    return transactions.stream().mapToInt(ItemTransaction::getTimestamp).max().orElse(-21) + 20;
  }

  public int getGoldUnspent() {
    return getGoldPreReset() - transactions.stream().mapToInt(ItemTransaction::getBalance).sum();
  }

  public List<ItemStack> getInventory() {
    return player.getInventory().getItemsAt(getEnd());
  }

  private int getGoldPreReset() {
    int amount = getResetStat(TimelineStat.CURRENT_GOLD);
    int minute = getStart() / 60_000;
    int millisSince = getEnd() - minute * 60_000;
    amount += millisSince * Const.GOLD_GENERATION_PER_SECOND / 1000;
    for (JSONObject event : player.getEvents(getStart(), getEnd())) {
      if (event.has("bounty")) {
        amount += event.getInt("bounty");
      }
    }
    return amount;
  }

  public List<Item> getItemsBought() {
    return transactions.stream().map(ItemTransaction::getItem).collect(Collectors.toList());
  }

  public int getResetStat(TimelineStat stat) {
    int minute = getStart() / 60_000;
    return player.getStatAt(minute, stat);
  }

  public int getResetStatPercentage(TimelineStat stat) {
    if (stat.name().contains("TOTAL") || stat.name().contains("CURRENT")) {
      val totalStat = (stat.name().contains("TOTAL")) ? stat : TimelineStat.valueOf(stat.name().replace("CURRENT", "TOTAL"));
      val currentStat = (stat.name().contains("CURRENT")) ? stat : TimelineStat.valueOf(stat.name().replace("TOTAL", "CURRENT"));
      int minute = getStart() / 60_000;
      return player.getStatAt(minute, totalStat) == 0 ? -1 : player.getStatAt(minute, currentStat) / player.getStatAt(minute, totalStat);
    }
    throw new IllegalArgumentException("Wert nicht zulässig");
  }

}
