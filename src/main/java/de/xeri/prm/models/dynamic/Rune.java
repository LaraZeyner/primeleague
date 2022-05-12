package de.xeri.prm.models.dynamic;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.prm.models.match.playerperformance.Playerperformance;
import de.xeri.prm.manager.Data;
import de.xeri.prm.util.HibernateUtil;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.NamedQuery;

@Entity(name = "Rune")
@Table(name = "rune", indexes = @Index(name = "idx_rune", columnList = "runetree, rune_slot", unique = true))
@NamedQuery(name = "Rune.findAll", query = "FROM Rune r")
@NamedQuery(name = "Rune.findById", query = "FROM Rune r WHERE id = :pk")
@NamedQuery(name = "Rune.findBy", query = "FROM Rune r WHERE name = :name")
public class Rune implements Serializable {

  @Transient
  private static final long serialVersionUID = -7661693601692536521L;

  public static Set<Rune> get() {
    return new LinkedHashSet<>(HibernateUtil.findList(Rune.class));
  }

  public static Rune get(Rune neu) {
    if (has(neu.getId())) {
      return find(neu.getId());
    }
    Data.getInstance().save(neu);
    return neu;
  }

  public static boolean has(short id) {
    return HibernateUtil.has(Rune.class, id);
  }

  public static boolean has(String name) {
    return HibernateUtil.has(Rune.class, new String[]{"name"}, new Object[]{name});
  }

  public static Rune find(short id) {
    return HibernateUtil.find(Rune.class, id);
  }

  public static Rune find(String name) {
    return HibernateUtil.find(Rune.class, new String[]{"name"}, new Object[]{name});
  }

  @Id
  @Column(name = "rune_id", nullable = false)
  private short id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "runetree")
  private Runetree runeTree;

  @Column(name = "rune_slot")
  @Check(constraints = "slot < 40")
  private byte slot;

  @Column(name = "rune_name", nullable = false, length = 30)
  private String name;

  @Column(name = "rune_description", nullable = false, length = 750)
  private String description;

  @Column(name = "rune_short", nullable = false, length = 750)
  private String shortDescription;

  @ManyToMany(mappedBy = "runes")
  @LazyCollection(LazyCollectionOption.EXTRA)
  @OrderColumn
  private final Set<Playerperformance> playerperformances = new LinkedHashSet<>();

  // default constructor
  public Rune() {
  }

  public Rune(short id, byte slot, String name, String description, String shortDescription) {
    this.id = id;
    this.slot = slot;
    this.name = name;
    this.description = description;
    this.shortDescription = shortDescription;
  }

  //<editor-fold desc="getter and setter">
  public short getId() {
    return id;
  }

  public void setId(short id) {
    this.id = id;
  }

  public Runetree getRuneTree() {
    return runeTree;
  }

  public void setRuneTree(Runetree runetree) {
    this.runeTree = runetree;
  }

  public byte getSlot() {
    return slot;
  }

  public void setSlot(byte slot) {
    this.slot = slot;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getShortDescription() {
    return shortDescription;
  }

  public void setShortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
  }

  public Set<Playerperformance> getPlayerperformances() {
    return playerperformances;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Rune)) return false;
    final Rune rune = (Rune) o;
    return getId() == rune.getId() && getSlot() == rune.getSlot() && Objects.equals(getRuneTree(), rune.getRuneTree()) && getName().equals(rune.getName()) && getDescription().equals(rune.getDescription()) && getShortDescription().equals(rune.getShortDescription()) && getPlayerperformances().equals(rune.getPlayerperformances());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getSlot(), getName(), getDescription(), getShortDescription());
  }

  @Override
  public String toString() {
    return "Rune{" +
        "id=" + id +
        ", runeTree=" + runeTree +
        ", slot=" + slot +
        ", name='" + name + '\'' +
        ", description='" + description + '\'' +
        ", shortDescription='" + shortDescription + '\'' +
        ", playerperformances=" + playerperformances.size() +
        '}';
  }
  //</editor-fold>
}