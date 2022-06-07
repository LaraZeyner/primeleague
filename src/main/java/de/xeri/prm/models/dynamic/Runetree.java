package de.xeri.prm.models.dynamic;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.prm.manager.PrimeData;
import de.xeri.prm.util.HibernateUtil;
import org.hibernate.annotations.NamedQuery;

@Entity(name = "Runetree")
@Table(name = "runetree")
@NamedQuery(name = "Runetree.findAll", query = "FROM Runetree r")
@NamedQuery(name = "Runetree.findById", query = "FROM Runetree r WHERE id = :pk")
@NamedQuery(name = "Runetree.findBy", query = "FROM Runetree r WHERE name = :name")
public class Runetree implements Serializable {

  @Transient
  private static final long serialVersionUID = 7893634102193519118L;

  public static Set<Runetree> get() {
    return new LinkedHashSet<>(HibernateUtil.findList(Runetree.class));
  }

  public static Runetree get(Runetree neu) {
    if (has(neu.getId())) {
      return find(neu.getId());
    }
    PrimeData.getInstance().save(neu);
    return neu;
  }

  public static boolean has(short id) {
    return HibernateUtil.has(Runetree.class, id);
  }

  public static boolean has(String name) {
    return HibernateUtil.has(Runetree.class, new String[]{"name"}, new Object[]{name});
  }

  public static Runetree find(short id) {
    return HibernateUtil.find(Runetree.class, id);
  }

  public static Runetree find(String name) {
    return HibernateUtil.find(Runetree.class, new String[]{"name"}, new Object[]{name});
  }

  @Id
  @Column(name = "runetree_id", nullable = false)
  private short id;

  @Column(name = "runetree_name", nullable = false, length = 11)
  private String name;

  @Column(name = "runetree_icon", nullable = false, length = 38)
  private String iconURL;

  @OneToMany(mappedBy = "runeTree")
  private Set<Rune> runes = new LinkedHashSet<>();

  public Runetree() {
  }

  public Runetree(short id, String name, String iconURL) {
    this.id = id;
    this.name = name;
    this.iconURL = iconURL;
  }

  public void addRune(Rune rune) {
    runes.add(rune);
    rune.setRuneTree(this);
  }

  //<editor-fold desc="getter and setter">
  public short getId() {
    return id;
  }

  public void setId(short id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getIconURL() {
    return iconURL;
  }

  public void setIconURL(String iconURL) {
    this.iconURL = iconURL;
  }

  public Set<Rune> getRunes() {
    return runes;
  }

  private void setRunes(Set<Rune> runes) {
    this.runes = runes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Runetree)) return false;
    final Runetree runetree = (Runetree) o;
    return getId() == runetree.getId() && getName().equals(runetree.getName()) && getIconURL().equals(runetree.getIconURL()) && getRunes().equals(runetree.getRunes());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getName(), getIconURL());
  }

  @Override
  public String toString() {
    return "Runetree{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", iconURL='" + iconURL + '\'' +
        ", runes=" + runes.size() +
        '}';
  }
  //</editor-fold>
}