package de.xeri.prm.game.events.items;

import de.xeri.prm.models.dynamic.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Lara on 03.05.2022 for web
 */
@Getter
@Setter
@AllArgsConstructor
public class ItemTransaction {
  private int timestamp;
  private Item item;
  private ItemTransactionType type;
  private int balance;
}
