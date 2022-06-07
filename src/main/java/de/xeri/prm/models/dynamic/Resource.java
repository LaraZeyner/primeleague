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

@Entity(name = "Resource")
@Table(name = "resource")
@NamedQuery(name = "Resource.findAll", query = "FROM Resource r")
@NamedQuery(name = "Resource.findById", query = "FROM Resource r WHERE name = :pk")
public class Resource implements Serializable {

  @Transient
  private static final long serialVersionUID = -726013433361561652L;

  public static Set<Resource> get() {
    return new LinkedHashSet<>(HibernateUtil.findList(Resource.class));
  }

  public static Resource get(Resource neu) {
    if (has(neu.getName())) {
      return find(neu.getName());
    }
    PrimeData.getInstance().save(neu);
    return neu;
  }

  public static boolean has(String name) {
    return HibernateUtil.has(Resource.class, name);
  }

  public static Resource find(String name) {
    return HibernateUtil.find(Resource.class, name);
  }

  @Id
  @Column(name = "resource_name", nullable = false, length = 12)
  private String name;

  @OneToMany(mappedBy = "resource")
  private final Set<Champion> champions = new LinkedHashSet<>();

  public Resource() {
  }

  public Resource(String name) {
    this.name = name;
  }

  public void addChampion(Champion champion) {
    champions.add(champion);
    champion.setResource(this);
  }

  //<editor-fold desc="getter and setter">
  public String getName() {
    return name;
  }

  public Set<Champion> getChampions() {
    return champions;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Resource)) return false;
    final Resource resource = (Resource) o;
    return getName().equals(resource.getName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getName());
  }

  @Override
  public String toString() {
    return "Resource{" +
        "name='" + name + '\'' +
        '}';
  }

  //</editor-fold>
}