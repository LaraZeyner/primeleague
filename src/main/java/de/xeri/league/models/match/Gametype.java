package de.xeri.league.models.match;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.league.models.dynamic.LeagueMap;
import de.xeri.league.util.Data;
import de.xeri.league.util.Util;

@Entity(name = "Gametype")
@Table(name = "gametype", indexes = @Index(name = "idx_gametype", columnList = "gametype_name, map", unique = true))
public class Gametype implements Serializable {

  @Transient
  private static final long serialVersionUID = -8925136115533930187L;

  private static Set<Gametype> data;

  public static void save() {
    if (data != null) data.forEach(Data.getInstance().getSession()::saveOrUpdate);
  }

  public static Set<Gametype> get() {
    if (data == null) data = new LinkedHashSet<>((List<Gametype>) Util.query("Gametype"));
    return data;
  }

  public static Gametype get(Gametype neu) {
    get();
    final Gametype entry = find(neu.getId());
    if (entry == null) data.add(neu);
    return find(neu.getId());
  }

  public static Gametype find(int id) {
    get();
    return data.stream().filter(entry -> entry.getId() == (short) id).findFirst().orElse(null);
  }

  @Id
  @Column(name = "gametype_id")
  private short id;

  @Column(name = "gametype_name", length = 50, nullable = false)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
  @JoinColumn(name = "map")
  private LeagueMap leagueMap;

  @OneToMany(mappedBy = "gametype")
  private final Set<Game> games = new LinkedHashSet<>();

  public Gametype() {
  }

  public Gametype(short id, String name) {
    this.id = id;
    this.name = name;
  }

  public Game addGame(Game game, Gametype type) {
    return Game.get(game, type);
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

  public LeagueMap getMap() {
    return leagueMap;
  }

  public void setMap(LeagueMap leagueMap) {
    this.leagueMap = leagueMap;
  }

  public Set<Game> getGames() {
    return games;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Gametype)) return false;
    final Gametype gametype = (Gametype) o;
    return getId() == gametype.getId() && getName().equals(gametype.getName()) && getMap().equals(gametype.getMap()) && getGames().equals(gametype.getGames());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getName(), getMap(), getGames());
  }

  @Override
  public String toString() {
    return "Gametype{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", map=" + leagueMap +
        ", games=" + games +
        '}';
  }
  //</editor-fold>
}