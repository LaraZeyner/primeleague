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

@Entity(name = "Abilitystyle")
@Table(name = "abilitystyle")
public class Abilitystyle implements Serializable {

  @Transient
  private static final long serialVersionUID = 518733243666124397L;

  @Id
  @Column(name = "style_name", nullable = false, length = 30)
  private String name;

  @ManyToMany(mappedBy = "abilitystyles")
  private final Set<Ability> abilities = new LinkedHashSet<>();

  // default constructor
  public Abilitystyle() {
  }

  public Abilitystyle(String name) {
    this.name = name;
  }

  //<editor-fold desc="getter and setter">
  public Set<Ability> getAbilities() {
    return abilities;
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
    if (!(o instanceof Abilitystyle)) return false;
    final Abilitystyle abilitystyle = (Abilitystyle) o;
    return getName().equals(abilitystyle.getName()) && getAbilities().equals(abilitystyle.getAbilities());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getName());
  }

  @Override
  public String toString() {
    return "Abilitystyle{" +
        "name='" + name + '\'' +
        '}';
  }
  //</editor-fold>
}