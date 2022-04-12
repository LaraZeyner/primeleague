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

@Entity
@Table(name = "runetree")
public class Runetree implements Serializable {

  @Transient
  private static final long serialVersionUID = 7893634102193519118L;

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
    return Objects.hash(getId(), getName(), getIconURL(), getRunes());
  }

  @Override
  public String toString() {
    return "Runetree{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", iconURL='" + iconURL + '\'' +
        ", runes=" + runes +
        '}';
  }
  //</editor-fold>
}