package de.xeri.league.models.ids;

import java.io.Serializable;
import java.util.Objects;

public class ItemStatId implements Serializable {

  private static final long serialVersionUID = -6437228330012098290L;
  private short item;
  private String stat;

  // default constructor
  public ItemStatId() {
  }

  public ItemStatId(short item, String stat) {
    this.item = item;
    this.stat = stat;
  }

  //<editor-fold desc="getter and setter">
  public String getStat() {
    return stat;
  }

  public void setStat(String stat) {
    this.stat = stat;
  }

  public short getItem() {
    return item;
  }

  public void setItem(short item) {
    this.item = item;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ItemStatId)) return false;
    final ItemStatId itemStatId = (ItemStatId) o;
    return getItem() == itemStatId.getItem() && getStat().equals(itemStatId.getStat());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getItem(), getStat());
  }

  @Override
  public String toString() {
    return "ItemStatId{" +
        "item='" + item + '\'' +
        ", stat='" + stat + '\'' +
        '}';
  }
  //</editor-fold>
}