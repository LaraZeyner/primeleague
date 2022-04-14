package de.xeri.league.models.match;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.league.models.dynamic.Summonerspell;
import de.xeri.league.models.ids.PlayerperformanceSummonerspellId;
import de.xeri.league.util.Data;
import de.xeri.league.util.Util;

@Entity(name = "Playerperformance_Summonerspell")
@Table(name = "playerperformance_summonerspell", indexes = @Index(name = "summonerspell", columnList = "summonerspell"))
@IdClass(PlayerperformanceSummonerspellId.class)
public class PlayerperformanceSummonerspell implements Serializable {

  @Transient
  private static final long serialVersionUID = 330336380785350420L;

  private static Set<PlayerperformanceSummonerspell> data;

  public static void save() {
    if (data != null) data.forEach(Data.getInstance().getSession()::saveOrUpdate);
  }

  public static Set<PlayerperformanceSummonerspell> get() {
    if (data == null)
      data = new LinkedHashSet<>((List<PlayerperformanceSummonerspell>) Util.query("Playerperformance_Summonerspell"));
    return data;
  }

  public static PlayerperformanceSummonerspell get(PlayerperformanceSummonerspell neu) {
    get();
    if (find(neu.getPlayerperformance(), neu.getSummonerspell()) == null) {
      neu.getPlayerperformance().getSummonerspells().add(neu);
      neu.getSummonerspell().getPlayerperformances().add(neu);
      data.add(neu);
    }
    return find(neu.getPlayerperformance(), neu.getSummonerspell());
  }

  public static PlayerperformanceSummonerspell find(Playerperformance playerperformance, Summonerspell summonerspell) {
    get();
    return data.stream().filter(entry -> entry.getPlayerperformance().equals(playerperformance) &&
        entry.getSummonerspell().equals(summonerspell)).findFirst().orElse(null);
  }
  
  @Id
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "playerperformance")
  private Playerperformance playerperformance;
  
  @Id
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "summonerspell")
  private Summonerspell summonerspell;

  @Column(name = "usages", nullable = false)
  private byte usages;

  // default constructor
  public PlayerperformanceSummonerspell() {
  }

  public PlayerperformanceSummonerspell(Playerperformance playerperformance, Summonerspell summonerspell, byte usages) {
    this.playerperformance = playerperformance;
    this.summonerspell = summonerspell;
    this.usages = usages;
  }

  //<editor-fold desc="getter and setter">
  public byte getUsages() {
    return usages;
  }

  public void setUsages(byte usages) {
    this.usages = usages;
  }

  public Summonerspell getSummonerspell() {
    return summonerspell;
  }

  public void setSummonerspell(Summonerspell summonerspell) {
    this.summonerspell = summonerspell;
  }

  public Playerperformance getPlayerperformance() {
    return playerperformance;
  }

  public void setPlayerperformance(Playerperformance playerperformance) {
    this.playerperformance = playerperformance;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PlayerperformanceSummonerspell)) return false;
    final PlayerperformanceSummonerspell playerperformanceSummonerspell = (PlayerperformanceSummonerspell) o;
    return getUsages() == playerperformanceSummonerspell.getUsages() && getPlayerperformance().equals(playerperformanceSummonerspell.getPlayerperformance()) && getSummonerspell().equals(playerperformanceSummonerspell.getSummonerspell());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getPlayerperformance(), getSummonerspell(), getUsages());
  }

  @Override
  public String toString() {
    return "PlayerperformanceSummonerspell{" +
        "playerperformance=" + playerperformance +
        ", summonerspell=" + summonerspell +
        ", usages=" + usages +
        '}';
  }
  //</editor-fold>
}