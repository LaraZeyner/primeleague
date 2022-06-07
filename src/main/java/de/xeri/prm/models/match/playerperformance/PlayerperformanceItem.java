package de.xeri.prm.models.match.playerperformance;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.prm.models.dynamic.Item;
import de.xeri.prm.manager.PrimeData;
import de.xeri.prm.util.HibernateUtil;
import org.hibernate.annotations.NamedQuery;

@Entity(name = "PlayerperformanceItem")
@Table(name = "playerperformance_item")
@NamedQuery(name = "PlayerperformanceItem.findAll", query = "FROM PlayerperformanceItem p")
@NamedQuery(name = "PlayerperformanceItem.findById", query = "FROM PlayerperformanceItem p WHERE id = :id")
@NamedQuery(name = "PlayerperformanceItem.findBy",
    query = "FROM PlayerperformanceItem p WHERE playerperformance = :playerperformance AND item = :item AND buyTime = :buytime")
@NamedQuery(name = "PlayerperformanceItem.findByRemains",
    query = "FROM PlayerperformanceItem p WHERE playerperformance = :playerperformance AND item = :item AND remains = :remains")
@NamedQuery(name = "PlayerperformanceItem.findByItem",
    query = "FROM PlayerperformanceItem p WHERE playerperformance = :playerperformance AND item = :item")
public class PlayerperformanceItem implements Serializable {

  @Transient
  private static final long serialVersionUID = 4729798460691317464L;

  public static Set<PlayerperformanceItem> get() {
    return new LinkedHashSet<>(HibernateUtil.findList(PlayerperformanceItem.class));
  }

  public static PlayerperformanceItem get(PlayerperformanceItem neu) {
    neu.getPlayerperformance().getItems().add(neu);
    neu.getItem().getPlayerperformances().add(neu);
    PrimeData.getInstance().save(neu);
    return neu;
  }

  public static boolean has(Playerperformance playerperformance, Item item, int buyTime) {
    return HibernateUtil.has(PlayerperformanceItem.class, new String[]{"playerperformance", "item", "buytime"},
        new Object[]{playerperformance, item, buyTime});
  }

  public static boolean has(int id) {
    return HibernateUtil.has(PlayerperformanceItem.class, id);
  }

  public static boolean has(Playerperformance playerperformance, Item item, boolean remains) {
    return HibernateUtil.has(PlayerperformanceItem.class, new String[]{"playerperformance", "item", "remains"},
        new Object[]{playerperformance, item, remains}, "findByRemains");
  }

  public static PlayerperformanceItem find(int id) {
    return HibernateUtil.find(PlayerperformanceItem.class, id);
  }

  public static PlayerperformanceItem find(Playerperformance playerperformance, Item item, boolean remains) {
    return HibernateUtil.find(PlayerperformanceItem.class, new String[]{"playerperformance", "item", "remains"},
        new Object[]{playerperformance, item, remains}, "findByRemains");
  }

  public static PlayerperformanceItem find(Playerperformance playerperformance, Item item, int buyTime) {
    return HibernateUtil.find(PlayerperformanceItem.class, new String[]{"playerperformance", "item", "buytime"},
        new Object[]{playerperformance, item, buyTime});
  }

  public static List<PlayerperformanceItem> findAll(Playerperformance playerperformance, Item item, boolean remains) {
    return HibernateUtil.findList(PlayerperformanceItem.class, new String[]{"playerperformance", "item", "remains"},
        new Object[]{playerperformance, item, remains}, "findByRemains");
  }

  public static List<PlayerperformanceItem> findAll(Playerperformance playerperformance, Item item) {
    return HibernateUtil.findList(PlayerperformanceItem.class, new String[]{"playerperformance", "item"},
        new Object[]{playerperformance, item}, "findByItem");
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "playerperformance_item_id", nullable = false)
  private int id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "playerperformance")
  private Playerperformance playerperformance;

  @Column(name = "buy_timestamp", nullable = false)
  private int buyTime;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "item")
  private Item item;

  @Column(name = "item_remains", nullable = false)
  private boolean remains;

  // default constructor
  public PlayerperformanceItem() {
  }

  public PlayerperformanceItem(Playerperformance playerperformance, Item item, boolean remains) {
    this.playerperformance = playerperformance;
    this.item = item;
    this.remains = remains;
  }

  //<editor-fold desc="getter and setter">
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Playerperformance getPlayerperformance() {
    return playerperformance;
  }

  public int getBuyTime() {
    return buyTime;
  }

  public void setBuyTime(int buyTime) {
    this.buyTime = buyTime;
  }

  public Item getItem() {
    return item;
  }

  public boolean remains() {
    return remains;
  }

  public void setRemains(boolean remains) {
    this.remains = remains;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PlayerperformanceItem)) return false;
    final PlayerperformanceItem that = (PlayerperformanceItem) o;
    return getId() == that.getId() && getBuyTime() == that.getBuyTime() && remains() == that.remains() && getPlayerperformance().equals(that.getPlayerperformance()) && getItem().equals(that.getItem());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getPlayerperformance(), getBuyTime(), getItem(), remains());
  }

  @Override
  public String toString() {
    return "PlayerperformanceItem{" +
        "id=" + id +
        ", playerperformance=" + playerperformance +
        ", buyTime=" + buyTime +
        ", item=" + item +
        ", remains=" + remains +
        '}';
  }
  //</editor-fold>
}