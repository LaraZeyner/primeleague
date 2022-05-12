package de.xeri.prm.models.dynamic;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.prm.models.ids.ItemStatId;
import de.xeri.prm.manager.Data;
import de.xeri.prm.util.HibernateUtil;
import org.hibernate.annotations.NamedQuery;

@Entity(name = "Item_Stat")
@Table(name = "item_stat")
@IdClass(ItemStatId.class)
@NamedQuery(name = "Item_Stat.findAll", query = "FROM Item_Stat i")
@NamedQuery(name = "Item_Stat.findBy", query = "FROM Item_Stat i WHERE item = :name AND stat = :stat")
public class Item_Stat implements Serializable {

  @Transient
  private static final long serialVersionUID = 8386432444986700070L;

  public static Set<Item_Stat> get() {
    return new LinkedHashSet<>(HibernateUtil.findList(Item_Stat.class));
  }

  public static Item_Stat get(Item_Stat neu) {
    if (has(neu.getItem(), neu.getStat())) {
      return find(neu.getItem(), neu.getStat());
    }
    Data.getInstance().save(neu);
    return neu;
  }

  public static boolean has(Item item, ItemStat stat) {
    return HibernateUtil.has(Item_Stat.class, new String[]{"name", "stat"}, new Object[]{item, stat});
  }

  public static Item_Stat find(Item item, ItemStat stat) {
    return HibernateUtil.find(Item_Stat.class, new String[]{"name", "stat"}, new Object[]{item, stat});
  }

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

  // default constructor
  public Item_Stat() {

  }

  public Item_Stat(Item item, ItemStat stat, double statAmount) {
    this.item = item;
    this.stat = stat;
    this.statAmount = BigDecimal.valueOf(statAmount);
  }

  //<editor-fold desc="getter and setter">

  public double getStatAmount() {
    return statAmount.doubleValue();
  }

  public void setStatAmount(double statAmount) {
    this.statAmount = BigDecimal.valueOf(statAmount);
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