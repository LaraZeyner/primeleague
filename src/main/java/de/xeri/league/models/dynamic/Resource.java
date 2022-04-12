package de.xeri.league.models.dynamic;

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

@Entity(name = "Resource")
@Table(name = "resource")
public class Resource implements Serializable {

  @Transient
  private static final long serialVersionUID = -726013433361561652L;

  @Id
  @Column(name = "resource_name", nullable = false, length = 12)
  private String name;

  @OneToMany(mappedBy = "resource")
  private Set<Champion> champions = new LinkedHashSet<>();

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

  public void setName(String id) {
    this.name = id;
  }

  public Set<Champion> getChampions() {
    return champions;
  }

  public void setChampions(Set<Champion> champions) {
    this.champions = champions;
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