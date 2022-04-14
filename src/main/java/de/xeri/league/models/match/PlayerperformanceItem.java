package de.xeri.league.models.match;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

import de.xeri.league.models.dynamic.Item;
import de.xeri.league.util.Data;
import de.xeri.league.util.Util;


@Entity(name = "Playerperformance_Item")
@Table(name = "playerperformance_item")
public class PlayerperformanceItem implements Serializable {

  @Transient
  private static final long serialVersionUID = 4729798460691317464L;

  private static Set<PlayerperformanceItem> data;

  public static void save() {
    if (data != null) data.forEach(Data.getInstance().getSession()::saveOrUpdate);
  }

  public static Set<PlayerperformanceItem> get() {
    if (data == null)
      data = new LinkedHashSet<>((List<PlayerperformanceItem>) Util.query("Playerperformance_Item"));
    return data;
  }

  public static PlayerperformanceItem get(PlayerperformanceItem neu) {
    get();
    if (find(neu.getPlayerperformance(), neu.getItem(), neu.getBuyTime()) == null) {
      neu.getPlayerperformance().getItems().add(neu);
      neu.getItem().getPlayerperformances().add(neu);
      data.add(neu);
    }
    return find(neu.getPlayerperformance(), neu.getItem(), neu.getBuyTime());
  }

  public static PlayerperformanceItem find(Playerperformance playerperformance, Item item, int buyTime) {
    return findAll(playerperformance, item).stream().filter(entry -> entry.getBuyTime() == buyTime).findFirst().orElse(null);
  }

  public static PlayerperformanceItem find(Playerperformance playerperformance, Item item, boolean remains) {
    return findAll(playerperformance, item).stream().filter(entry -> entry.remains() == remains).findFirst().orElse(null);
  }

  public static List<PlayerperformanceItem> findAll(Playerperformance playerperformance, Item item, boolean remains) {
    return findAll(playerperformance, item).stream().filter(entry -> entry.remains() == remains).collect(Collectors.toList());
  }

  public static List<PlayerperformanceItem> findAll(Playerperformance playerperformance, Item item) {
    get();
    return data.stream().filter(entry -> entry.getPlayerperformance().equals(playerperformance) && entry.getItem().equals(item)).collect(Collectors.toList());
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

  public PlayerperformanceItem(Playerperformance playerperformance, Item item) {
    this.playerperformance = playerperformance;
    this.item = item;
    this.remains = true;
  }

  public PlayerperformanceItem(Playerperformance playerperformance, Item item, boolean remains) {
    this.playerperformance = playerperformance;
    this.item = item;
    this.remains = remains;
  }

  //<editor-fold desc="getter and setter">
  public static Set<PlayerperformanceItem> getData() {
    return data;
  }

  public static void setData(Set<PlayerperformanceItem> data) {
    PlayerperformanceItem.data = data;
  }

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