package de.xeri.league.models.dynamic;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.league.models.match.PlayerperformanceSummonerspell;
import de.xeri.league.util.Data;
import de.xeri.league.util.Util;

@Entity(name = "Summonerspell")
@Table(name = "summonerspell", indexes = @Index(name = "idx_summoner", columnList = "summonerspell_name", unique = true))
public class Summonerspell implements Serializable {

  @Transient
  private static final long serialVersionUID = 1909398380615591390L;

  private static Set<Summonerspell> data;

  public static void save() {
    if (data != null) data.forEach(Data.getInstance().getSession()::saveOrUpdate);
  }

  public static Set<Summonerspell> get() {
    if (data == null)
      data = new LinkedHashSet<>((List<Summonerspell>) Util.query("Summonerspell"));
    return data;
  }

  public static Summonerspell get(Summonerspell neu) {
    get();
    if (find(neu.getId()) == null) data.add(neu);
    return find(neu.getId());
  }

  public static Summonerspell find(int id) {
    get();
    return data.stream().filter(entry -> entry.getId() == (byte) id).findFirst().orElse(null);
  }

  @Id
  @Column(name = "summonerspell_id", nullable = false)
  private byte id;

  @Column(name = "summonerspell_name", nullable = false, length = 8)
  private String name;

  @OneToMany(mappedBy = "summonerspell")
  private final Set<PlayerperformanceSummonerspell> playerperformanceSummonerspells = new LinkedHashSet<>();

  // default constructor
  public Summonerspell() {
  }

  public Summonerspell(byte id, String name) {
    this.id = id;
    this.name = name;
  }

  //<editor-fold desc="getter and setter">
  public Set<PlayerperformanceSummonerspell> getPlayerperformanceSummonerspells() {
    return playerperformanceSummonerspells;
  }

  public byte getId() {
    return id;
  }

  public void setId(byte id) {
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
    if (!(o instanceof Summonerspell)) return false;
    final Summonerspell summonerspell = (Summonerspell) o;
    return getId() == summonerspell.getId() && getName().equals(summonerspell.getName()) && getPlayerperformanceSummonerspells().equals(summonerspell.getPlayerperformanceSummonerspells());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getName());
  }

  @Override
  public String toString() {
    return "Summonerspell{" +
        "id='" + id +
        ", name='" + name +
        '}';
  }
  //</editor-fold>
}