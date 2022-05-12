package de.xeri.prm.game.events.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.xeri.prm.game.GameAnalyser;
import de.xeri.prm.game.models.JSONPlayer;
import de.xeri.prm.models.dynamic.Item;
import de.xeri.prm.models.enums.EventTypes;
import de.xeri.prm.models.enums.ItemType;
import de.xeri.prm.util.Const;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import lombok.var;
import org.json.JSONObject;

/**
 * Created by Lara on 03.05.2022 for web
 */
@Getter
@Setter
public class Inventory {
  private JSONPlayer player;
  private final List<ItemTransaction> transactions = new ArrayList<>();

  public void build(List<JSONObject> events, JSONPlayer player) {
    this.player = player;

    for (JSONObject event : events) {
      final int timestamp = event.getInt("timestamp");
      val typeString = event.getString("type");
      val type = EventTypes.valueOf(typeString);
      final Item item;
      final int balance;
      final ItemTransactionType transactionType;
      if (type.equals(EventTypes.ITEM_PURCHASED)) {
        final int itemId = event.getInt("itemId");
        item = GameAnalyser.items.get((short) itemId);
        if (item != null) {
          balance = (int) (item.getCost() * -0.5);
          transactionType = ItemTransactionType.ADD;
        } else {
          continue;
        }

      } else if (type.equals(EventTypes.ITEM_DESTROYED)) {
        final int itemId = event.getInt("itemId");
        item = GameAnalyser.items.get((short) itemId);
        if (item != null) {
          balance = 0;
          transactionType = ItemTransactionType.REMOVE_USE;
        } else {
          continue;
        }

      } else if (type.equals(EventTypes.ITEM_SOLD)) {
        final int itemId = event.getInt("itemId");
        item = GameAnalyser.items.get((short) itemId);
        if (item != null) {
          balance = getRefundBalanceUponSelling(item);
          transactionType = ItemTransactionType.REMOVE;
        } else {
          continue;
        }

      } else if (type.equals(EventTypes.ITEM_UNDO)) {
        final int itemId = event.getInt("beforeId");
        item = GameAnalyser.items.get((short) itemId);
        if (item != null) {
          balance = getRefundBalanceUponSelling(item);
          transactionType = ItemTransactionType.REMOVE;

          final int afterItemId = event.getInt("afterId");
          Item afterItem = GameAnalyser.items.get((short) afterItemId);
          if (afterItemId != 0 && afterItem != null) {
            val transaction = new ItemTransaction(timestamp, afterItem, ItemTransactionType.ADD, afterItem.getCost() * -1);
            transactions.add(transaction);
          }
        } else {
          continue;
        }

      } else {
        throw new IllegalArgumentException("Falsches Event");
      }

      val transaction = new ItemTransaction(timestamp, item, transactionType, balance);
      transactions.add(transaction);
    }
  }

  private int getRefundBalanceUponSelling(Item item) {
    if (item.getType().equals(ItemType.STARTING) || item.getType().equals(ItemType.CONSUMABLE) ||
        item.getItemName().equals(Const.STASIS_ITEM_NAME) || item.getItemName().equals(Const.REVIVE_ITEM_NAME)) {
      return (int) (item.getCost() * Const.GOLD_REFUND_PENALTY);

    } else if (item.getItemName().equals(Const.BISCUIT_ITEM_NAME)) {
      return Const.GOLD_REFUND_PER_BISCUIT;

    } else {
      return (int) (item.getCost() * Const.GOLD_REFUND);
    }
  }

  public List<ItemStack> getItemsAt(int millis) {
    var items = new ArrayList<ItemStack>();
    var trinket = Item.find("Stealth Ward");
    for (ItemTransaction transaction : transactions) {
      if (transaction.getTimestamp() <= millis) {
        if (transaction.getType().equals(ItemTransactionType.ADD)) {
          if (transaction.getItem().getType().equals(ItemType.TRINKET)) {
            trinket = transaction.getItem();
          } else {
            addItem(items, transaction.getItem());
          }

        } else {
          if (transaction.getItem().getType().equals(ItemType.TRINKET)) {
            trinket = null;
          } else {
            removeItem(items, transaction.getItem());
          }
        }
      }
    }

    items.add(new ItemStack(trinket));
    checkItemSize(items);
    return items;
  }

  private void checkItemSize(ArrayList<ItemStack> items) {
    if (items.size() > 7) {
      boolean nullTrinket = items.contains(null);
      int min = items.stream()
          .filter(Objects::nonNull)
          .filter(itemStack -> itemStack.getItem() != null)
          .mapToInt(itemStack -> itemStack.getItem().getCost()).min().orElse(-1);
      if (min > -1) {
        final ItemStack toRemove = items.stream().filter(itemStack -> itemStack.getItem().getCost() == min).findFirst().orElse(null);
        if (toRemove != null) {
          items.remove(toRemove);
          if (items.size() > 6) {
            checkItemSize(items);
          }
        }
      }
      if (nullTrinket) {
        items.add(null);
      }
    }
  }

  private void addItem(List<ItemStack> items, Item item) {
    val stack = getStack(items, item);
    if (stack != null && item.getType().equals(ItemType.CONSUMABLE)) {
      stack.add();

    } else {
      val stack1 = new ItemStack(item);
      items.add(stack1);
    }
  }

  private void removeItem(List<ItemStack> items, Item item) {
    val stack = getStack(items, item);
    if (stack != null) {
      if (stack.getAmount() == 1) {
        items.remove(stack);
      } else {
        stack.consume();
      }
    }

  }

  private ItemStack getStack(List<ItemStack> items, Item item) {
    return items.stream()
        .filter(itemStack -> itemStack.getItem().equals(item))
        .findFirst().orElse(null);
  }

  public List<Reset> getResets() {
    val resets = new ArrayList<Reset>();
    for (ItemTransaction transaction : transactions) {
      if (transaction.getTimestamp() >= 60_000 && !transaction.getType().equals(ItemTransactionType.REMOVE_USE)) {
        val validReset = resets.stream()
            .filter(reset -> reset.getEnd() >= transaction.getTimestamp() - 90_000)
            .findFirst().orElse(null);
        if (validReset == null) {
          resets.add(new Reset(player, transaction));
        } else {
          validReset.addTransaction(transaction);
        }
      }
    }
    return resets;
  }

}