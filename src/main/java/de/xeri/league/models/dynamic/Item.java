package de.xeri.league.models.dynamic;

import java.io.Serializable;
import java.util.LinkedHashSet;
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

import de.xeri.league.models.enums.ItemSubType;
import de.xeri.league.models.enums.ItemType;
import de.xeri.league.models.match.playerperformance.PlayerperformanceItem;
import de.xeri.league.manager.Data;
import de.xeri.league.util.HibernateUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NamedQuery;

@Entity(name = "Item")
@Table(name = "item", indexes = @Index(name = "item_name", columnList = "item_name", unique = true))
@NamedQuery(name = "Item.findAll", query = "FROM Item i")
@NamedQuery(name = "Item.findById", query = "FROM Item i WHERE id = :pk")
@NamedQuery(name = "Item.findBy", query = "FROM Item i WHERE itemName = :name")
@Getter
@Setter
@NoArgsConstructor
public class Item implements Serializable {

  @Transient
  private static final long serialVersionUID = -8359778382859330032L;

  public static Set<Item> get() {
    return new LinkedHashSet<>(HibernateUtil.findList(Item.class));
  }

  public static Item get(Item neu) {
    if (has(neu.getId())) {
      return find(neu.getId());
    }
    Data.getInstance().save(neu);
    return neu;
  }

  public static boolean has(short id) {
    return HibernateUtil.has(Item.class, id);
  }

  public static boolean has(String name) {
    return HibernateUtil.has(Item.class, new String[]{"name"}, new Object[]{name});
  }

  public static Item find(String name) {
    return HibernateUtil.find(Item.class, new String[]{"name"}, new Object[]{name});
  }

  public static Item find(short id) {
    return HibernateUtil.find(Item.class, id);
  }

  @Id
  @Column(name = "item_id", nullable = false)
  private short id;

  @Enumerated(EnumType.STRING)
  @Column(name = "itemtype", nullable = false, length = 10)
  private ItemType type;

  @Enumerated(EnumType.STRING)
  @Column(name = "item_subtype", length = 15)
  private ItemSubType subtype;

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

  @OneToMany(mappedBy = "item")
  private final Set<PlayerperformanceItem> playerperformances = new LinkedHashSet<>();

  @OneToMany(mappedBy = "item")
  private final Set<Item_Stat> itemStats = new LinkedHashSet<>();

  public Item(short id, ItemType type, String itemName, String itemDescription, String shortDescription, short cost) {
    this.id = id;
    this.type = type;
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
    final Item_Stat stat = Item_Stat.get(new Item_Stat(this, itemStat, amount));
    if (!itemStats.contains(stat)) {
      itemStats.add(stat);
      itemStat.getItemStats().add(stat);
    }
  }

  public String getImage() {
    return "http://ddragon.leagueoflegends.com/cdn/img/item/" + id + ".jpg";
  }

  //<editor-fold desc="getter and setter">
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Item)) return false;
    final Item item = (Item) o;
    return getId() == item.getId() && getCost() == item.getCost() && getType() == item.getType() && getItemName().equals(item.getItemName()) && getItemDescription().equals(item.getItemDescription()) && Objects.equals(getShortDescription(), item.getShortDescription());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getType(), getItemName(), getItemDescription(), getShortDescription(), getCost());
  }

  @Override
  public String toString() {
    return "Item{" +
        "id=" + id +
        ", itemtype=" + type +
        ", itemName='" + itemName + '\'' +
        ", itemDescription='" + itemDescription + '\'' +
        ", shortDescription='" + shortDescription + '\'' +
        ", cost=" + cost +
        ", itemstyles=" + itemstyles.size() +
        ", playerperformances=" + playerperformances.size() +
        '}';
  }
  //</editor-fold>
}