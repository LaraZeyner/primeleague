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

import de.xeri.league.models.match.playerperformance.PlayerperformanceSummonerspell;
import de.xeri.league.util.Data;
import de.xeri.league.util.HibernateUtil;
import org.hibernate.annotations.NamedQuery;

@Entity(name = "Summonerspell")
@Table(name = "summonerspell", indexes = @Index(name = "idx_summoner", columnList = "summonerspell_name", unique = true))
@NamedQuery(name = "Summonerspell.findAll", query = "FROM Summonerspell s")
@NamedQuery(name = "Summonerspell.findById", query = "FROM Summonerspell s WHERE id = :pk")
@NamedQuery(name = "Summonerspell.findBy", query = "FROM Summonerspell s WHERE name = :name")
public class Summonerspell implements Serializable {

  @Transient
  private static final long serialVersionUID = 1909398380615591390L;

  public static Set<Summonerspell> get() {
    return new LinkedHashSet<>(HibernateUtil.findList(Summonerspell.class));
  }

  public static Summonerspell get(Summonerspell neu) {
    if (has(neu.getId())) {
      return find(neu.getId());
    }
    Data.getInstance().save(neu);
    return neu;
  }

  public static boolean has(byte id) {
    return HibernateUtil.has(Summonerspell.class, id);
  }

  public static boolean has(String name) {
    return HibernateUtil.has(Summonerspell.class, new String[]{"name"}, new Object[]{name});
  }

  public static Summonerspell find(byte id) {
    return HibernateUtil.find(Summonerspell.class, id);
  }

  public static Summonerspell find(String name) {
    return HibernateUtil.find(Summonerspell.class, new String[]{"name"}, new Object[]{name});
  }

  @Id
  @Column(name = "summonerspell_id", nullable = false)
  private byte id;

  @Column(name = "summonerspell_name", nullable = false, length = 8)
  private String name;

  @OneToMany(mappedBy = "summonerspell")
  private final Set<PlayerperformanceSummonerspell> summonerspells = new LinkedHashSet<>();

  // default constructor
  public Summonerspell() {
  }

  public Summonerspell(byte id, String name) {
    this.id = id;
    this.name = name;
  }

  //<editor-fold desc="getter and setter">
  public Set<PlayerperformanceSummonerspell> getPlayerperformances() {
    return summonerspells;
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
    return getId() == summonerspell.getId() && getName().equals(summonerspell.getName()) && getPlayerperformances().equals(summonerspell.getPlayerperformances());
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