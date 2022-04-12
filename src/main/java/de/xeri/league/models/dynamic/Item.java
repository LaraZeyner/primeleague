package de.xeri.league.models.dynamic;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.league.loader.ItemLoader;
import de.xeri.league.models.enums.ItemType;
import de.xeri.league.models.match.Playerperformance;
import de.xeri.league.util.Data;
import de.xeri.league.util.Util;

@Entity(name = "Item")
@Table(name = "item", indexes = @Index(name = "item_name", columnList = "item_name", unique = true))
public class Item implements Serializable {

  @Transient
  private static final long serialVersionUID = -8359778382859330032L;

  private static Set<Item> data;

  public static void save() {
    if (data != null) data.forEach(Data.getInstance().getSession()::saveOrUpdate);
  }

  public static Set<Item> get() {
    if (data == null) data = new LinkedHashSet<>((List<Item>) Util.query("Item"));
    return data;
  }

  public static Item get(Item neu) {
    get();
    if (find(neu.getId()) == null) data.add(neu);
    return find(neu.getId());
  }

  public static Item find(int id) {
    get();
    return data.stream().filter(entry -> entry.getId() == (short) id).findFirst().orElse(null);
  }

  @Id
  @Column(name = "item_id", nullable = false)
  private short id;

  @Enumerated(EnumType.STRING)
  @Column(name = "itemtype", nullable = false, length = 10)
  private ItemType itemtype;

  @Column(name = "item_name", nullable = false, length = 50)
  private String itemName;

  @Column(name = "item_description", nullable = false, length = 1250)
  private String itemDescription;

  @Column(name = "short_description", length = 250)
  private String shortDescription;

  @Column(name = "cost", nullable = false)
  private short cost;

  @ManyToMany
  @JoinTable(name = "item_style",
      joinColumns = @JoinColumn(name = "item"),
      inverseJoinColumns = @JoinColumn(name = "itemstyle"))
  private final Set<Itemstyle> itemstyles = new LinkedHashSet<>();

  @ManyToMany(mappedBy = "items")
  private final Set<Playerperformance> playerperformances = new LinkedHashSet<>();

  @OneToMany(mappedBy = "item")
  private final Set<Item_Stat> itemStats = new LinkedHashSet<>();

  // default constructor
  public Item() {
  }

  public Item(short id, ItemType itemtype, String itemName, String itemDescription, String shortDescription, short cost) {
    this.id = id;
    this.itemtype = itemtype;
    this.itemName = itemName;
    this.itemDescription = itemDescription;
    this.shortDescription = shortDescription;
    this.cost = cost;
  }

  public void addItemStyle(Itemstyle itemstyle) {
    itemstyles.add(itemstyle);
    itemstyle.getItems().add(this);
  }

  public void addItemStat(ItemStat itemStat, double amount) {
    final Item_Stat stat = new Item_Stat(this, itemStat, amount);
    if (!itemStats.contains(stat)) {
      itemStats.add(stat);
      itemStat.getItemStats().add(stat);
      ItemLoader.addStat(stat);
    }
  }

  //<editor-fold desc="getter and setter">
  public Set<Item_Stat> getItemStats() {
    return itemStats;
  }

  public Set<Playerperformance> getPlayerperformances() {
    return playerperformances;
  }

  public Set<Itemstyle> getItemstyles() {
    return itemstyles;
  }

  public short getCost() {
    return cost;
  }

  public String getItemDescription() {
    return itemDescription;
  }

  public String getShortDescription() {
    return shortDescription;
  }
  
  public String getItemName() {
    return itemName;
  }

  public ItemType getItemtype() {
    return itemtype;
  }

  public short getId() {
    return id;
  }

  public void setId(short id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Item)) return false;
    final Item item = (Item) o;
    return getId() == item.getId() && getCost() == item.getCost() && getItemtype() == item.getItemtype() && getItemName().equals(item.getItemName()) && getItemDescription().equals(item.getItemDescription()) && Objects.equals(getShortDescription(), item.getShortDescription());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getItemtype(), getItemName(), getItemDescription(), getShortDescription(), getCost());
  }

  @Override
  public String toString() {
    return "Item{" +
        "id=" + id +
        ", itemtype=" + itemtype +
        ", itemName='" + itemName + '\'' +
        ", itemDescription='" + itemDescription + '\'' +
        ", shortDescription='" + shortDescription + '\'' +
        ", cost=" + cost +
        ", itemstyles=" + itemstyles +
        ", playerperformances=" + playerperformances +
        '}';
  }
  //</editor-fold>
}