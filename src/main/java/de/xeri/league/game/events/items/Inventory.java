package de.xeri.league.game.events.items;

import java.util.ArrayList;
import java.util.List;

import de.xeri.league.game.models.JSONPlayer;
import de.xeri.league.models.dynamic.Item;
import de.xeri.league.models.enums.EventTypes;
import de.xeri.league.models.enums.ItemType;
import de.xeri.league.util.Const;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
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
      final String typeString = event.getString("type");
      final EventTypes type = EventTypes.valueOf(typeString);
      final Item item;
      final int balance;
      final ItemTransactionType transactionType;
      if (type.equals(EventTypes.ITEM_PURCHASED)) {
        final int itemId = event.getInt("itemId");
        item = Item.find((short) itemId);
        balance = item.getCost() * -1;
        transactionType = ItemTransactionType.ADD;

      } else if (type.equals(EventTypes.ITEM_DESTROYED)) {
        final int itemId = event.getInt("itemId");
        item = Item.find((short) itemId);
        balance = 0;
        transactionType = ItemTransactionType.REMOVE;

      } else if (type.equals(EventTypes.ITEM_SOLD)) {
        final int itemId = event.getInt("itemId");
        item = Item.find((short) itemId);
        balance = getRefundBalanceUponSelling(item);
        transactionType = ItemTransactionType.REMOVE;

      } else if (type.equals(EventTypes.ITEM_UNDO)) {
        final int itemId = event.getInt("beforeId");
        item = Item.find((short) itemId);
        balance = getRefundBalanceUponSelling(item);
        transactionType = ItemTransactionType.REMOVE;

        final int afterItemId = event.getInt("afterId");
        if (afterItemId != 0) {
          final Item afterItem = Item.find((short) afterItemId);
          final ItemTransaction transaction = new ItemTransaction(timestamp, afterItem, ItemTransactionType.ADD, afterItem.getCost() * -1);
          transactions.add(transaction);
        }

      } else {
        throw new IllegalArgumentException("Falsches Event");
      }


      if (item != null) {
        final ItemTransaction transaction = new ItemTransaction(timestamp, item, transactionType, balance);
        transactions.add(transaction);
      }
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
    List<ItemStack> items = new ArrayList<>();
    Item trinket = Item.find("Stealth Ward");
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
    return items;
  }

  private void addItem(List<ItemStack> items, Item item) {
    val stack = getStack(items, item);
    if (stack != null && item.getType().equals(ItemType.CONSUMABLE)) {
      stack.add();

    } else if (items.size() < 6) {
      val stack1 = new ItemStack(item);
      items.add(stack1);

    } else {
      throw new IndexOutOfBoundsException("Nur Platz fuer 6 Items + Trinket");
    }
  }

  private void removeItem(List<ItemStack> items, Item item) {
    val stack = getStack(items, item);
    if (stack.getAmount() == 1) {
      items.remove(stack);
    } else {
      stack.consume();
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
      val validReset = resets.stream()
          .filter(reset -> reset.getEnd() >= transaction.getTimestamp() - Const.TIME_BETWEEN_FIGHTS * 60_000)
          .findFirst().orElse(null);
      if (validReset == null) {
        resets.add(new Reset(player, transaction));
      } else {
        validReset.addTransaction(transaction);
      }
    }
    return resets;
  }

}