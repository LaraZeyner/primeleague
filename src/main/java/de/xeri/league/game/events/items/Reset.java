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

  private ResetReason getResetReason() {
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

  public boolean wasRecall() {
    return getResetReason().equals(ResetReason.RECALL);
  }

  public double getLaneLead() {
    return player.getLeadDifferenceAt(getStart() / 60_000, getEnd() / 60_000, TimelineStat.LEAD);
  }

  public void addTransaction(ItemTransaction transaction) {
    this.transactions.add(transaction);
  }

  public int getStart() {
    final int start = transactions.stream().mapToInt(ItemTransaction::getTimestamp).min().orElse(11_999) - 12_000;
    return Math.max(start, 0);
  }

  public int getEnd() {
    final int end = transactions.stream().mapToInt(ItemTransaction::getTimestamp).max().orElse(-20_001) + 20_000;
    return Math.min(end, player.getHighestMinute() * 60_000);
  }

  public int getGoldUnspent() {
    return getGoldPreReset() + transactions.stream().mapToInt(ItemTransaction::getBalance).sum();
  }

  public List<ItemStack> getInventory() {
    return player.getInventory().getItemsAt(getEnd());
  }

  public int getGoldPreReset() {
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

  public int getStatPercentage(TimelineStat stat) {
    return player.getStatPercentage(getStart() / 60_000, stat);
  }

  public double getPool() {
    return player.getPool(getStart() / 60_000);
  }

  /**
   * Wie lange dauert ein Reset von Beginn bis zum Erreichen der Lane
   * @return Dauer des Resets in Millisekunden
   */
  public int getDuration() {
    return getEnd() - getStart();
  }

}
