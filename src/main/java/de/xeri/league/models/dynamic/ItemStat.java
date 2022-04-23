package de.xeri.league.models.dynamic;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.league.util.Data;
import de.xeri.league.util.HibernateUtil;
import org.hibernate.annotations.NamedQuery;

@Entity(name = "Itemstat")
@Table(name = "itemstat", indexes = @Index(name = "idx_itemstat", columnList = "itemstat_name", unique = true))
@NamedQuery(name = "Itemstat.findAll", query = "FROM Itemstat i")
@NamedQuery(name = "Itemstat.findById", query = "FROM Itemstat i WHERE id = :pk")
@NamedQuery(name = "Itemstat.findBy", query = "FROM Itemstat i WHERE name = :name")
public class ItemStat implements Serializable {

  @Transient
  private static final long serialVersionUID = 8534420089380529755L;

  public static Set<ItemStat> get() {
    return new LinkedHashSet<>(HibernateUtil.findList(ItemStat.class));
  }

  public static ItemStat get(ItemStat neu) {
    if (has(neu.getId())) {
      return find(neu.getId());
    }
    Data.getInstance().save(neu);
    return neu;
  }

  public static boolean has(String id) {
    return HibernateUtil.has(ItemStat.class, id);
  }

  public static ItemStat find(String id) {
    return HibernateUtil.find(ItemStat.class, id);
  }

  @Id
  @Column(name = "itemstat_id", nullable = false, length = 35)
  private String id;


  @Column(name = "itemstat_name", length = 35)
  private String name;

  @OneToMany(mappedBy = "stat")
  private final Set<Item_Stat> itemStats = new LinkedHashSet<>();

  public ItemStat() {
  }

  public ItemStat(String id) {
    this.id = id;
  }

  //<editor-fold desc="getter and setter">
  public Set<Item_Stat> getItemStats() {
    return itemStats;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ItemStat)) return false;
    final ItemStat itemStat = (ItemStat) o;
    return getId().equals(itemStat.getId()) && getName().equals(itemStat.getName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getName());
  }

  @Override
  public String toString() {
    return "ItemStat{" +
        "id='" + id + '\'' +
        ", name='" + name + '\'' +
        '}';
  }
  //</editor-fold>
}