package de.xeri.prm.game.events.items;

import de.xeri.prm.models.dynamic.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Lara on 03.05.2022 for web
 */
@Getter
@AllArgsConstructor
public class ItemStack {
  private Item item;
  private byte amount;

  public ItemStack(Item item) {
    this.item = item;
    this.amount = 1;
  }

  public void add() {
    amount++;
  }

  public void consume() {
    amount--;
  }
}
