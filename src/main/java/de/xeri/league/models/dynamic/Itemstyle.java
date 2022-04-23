package de.xeri.league.models.dynamic;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.league.util.Data;
import de.xeri.league.util.HibernateUtil;
import org.hibernate.annotations.NamedQuery;

@Entity(name = "Itemstyle")
@Table(name = "itemstyle")
@NamedQuery(name = "Itemstyle.findAll", query = "FROM Itemstyle i")
@NamedQuery(name = "Itemstyle.findById", query = "FROM Itemstyle i WHERE name = :pk")
public class Itemstyle implements Serializable {

  @Transient
  private static final long serialVersionUID = -5751534381863311512L;

  public static Set<Itemstyle> get() {
    return new LinkedHashSet<>(HibernateUtil.findList(Itemstyle.class));
  }

  public static Itemstyle get(Itemstyle neu) {
    if (has(neu.getName())) {
      return find(neu.getName());
    }
    Data.getInstance().save(neu);
    return neu;
  }

  public static boolean has(String name) {
    return HibernateUtil.has(Itemstyle.class, name);
  }

  public static Itemstyle find(String name) {
    return HibernateUtil.find(Itemstyle.class, name);
  }

  @Id
  @Column(name = "style_name", nullable = false, length = 25)
  private String name;

  // default constructor
  public Itemstyle() {
  }

  public Itemstyle(String name) {
    this.name = name;
  }

  @ManyToMany(mappedBy = "itemstyles")
  private Set<Item> items = new LinkedHashSet<>();

  //<editor-fold desc="getter and setter">
  public Set<Item> getItems() {
    return items;
  }

  public void setItems(Set<Item> items) {
    this.items = items;
  }

  public String getName() {
    return name;
  }

  public void setName(String id) {
    this.name = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Itemstyle)) return false;
    final Itemstyle itemstyle = (Itemstyle) o;
    return getName().equals(itemstyle.getName()) && getItems().equals(itemstyle.getItems());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getName());
  }

  @Override
  public String toString() {
    return "Itemstyle{" +
        "name='" + name + '\'' +
        '}';
  }
  //</editor-fold>
}