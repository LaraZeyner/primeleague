package de.xeri.league.models.dynamic;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.league.models.ids.ItemStatId;

@Entity(name = "Item_Stat")
@Table(name = "item_stat")
@IdClass(ItemStatId.class)
public class Item_Stat implements Serializable {

  @Transient
  private static final long serialVersionUID = 8386432444986700070L;

  @Id
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "item")
  private Item item;

  @Id
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "stat")
  private ItemStat stat;

  @Column(name = "stat_amount", nullable = false, precision = 9, scale = 4)
  private BigDecimal statAmount;

  public Item_Stat() {

  }

  public Item_Stat(Item item, ItemStat stat, double statAmount) {
    this.item = item;
    this.stat = stat;
    this.statAmount = new BigDecimal(statAmount);
  }

  //<editor-fold desc="getter and setter">

  public double getStatAmount() {
    return statAmount.doubleValue();
  }

  public void setStatAmount(double statAmount) {
    this.statAmount = new BigDecimal(statAmount);
  }

  public ItemStat getStat() {
    return stat;
  }

  public void setStat(ItemStat stat) {
    this.stat = stat;
  }

  public Item getItem() {
    return item;
  }

  public void setItem(Item item) {
    this.item = item;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Item_Stat)) return false;
    final Item_Stat itemStat = (Item_Stat) o;
    return getItem().equals(itemStat.getItem()) && getStat().equals(itemStat.getStat()) && getStatAmount() == itemStat.getStatAmount();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getItem(), getStat(), getStatAmount());
  }

  @Override
  public String toString() {
    return "Item_Stat{" +
        "item=" + item +
        ", stat=" + stat +
        ", statAmount=" + statAmount +
        '}';
  }

  //</editor-fold>
}