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

import de.xeri.prm.models.match.Gametype;

@Entity
@Table(name = "map")
public class LeagueMap implements Serializable {

  @Transient
  private static final long serialVersionUID = 6425657449703606768L;

  @Id
  @Column(name = "map_name", nullable = false, length = 20)
  private String name;

  @OneToMany(mappedBy = "leagueMap")
  private final Set<Gametype> gametypes = new LinkedHashSet<>();

  public LeagueMap() {
  }

  public LeagueMap(String mapName) {
    this.name = mapName;
  }

  public void addGametype(Gametype gametype) {
    gametypes.add(gametype);
    gametype.setMap(this);
  }

  //<editor-fold desc="getter and setter">
  public Set<Gametype> getGametypes() {
    return gametypes;
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
    if (!(o instanceof LeagueMap)) return false;
    final LeagueMap leagueMap = (LeagueMap) o;
    return getName().equals(leagueMap.getName()) && getGametypes().equals(leagueMap.getGametypes());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getName());
  }

  @Override
  public String toString() {
    return "Map{" +
        "name='" + name +
        '}';
  }
  //</editor-fold>

}