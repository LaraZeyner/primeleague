package de.xeri.league.models.league;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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

import de.xeri.league.util.Data;
import de.xeri.league.util.Util;

@Entity(name = "League")
@Table(name = "league", indexes = @Index(name = "idx_league_name", columnList = "stage, league_name", unique = true))
public class League implements Serializable {

  @Transient
  private static final long serialVersionUID = 7621013514942624366L;

  private static Set<League> data;

  public static void save() {
    if (data != null) data.forEach(Data.getInstance().getSession()::saveOrUpdate);
  }

  public static Set<League> get() {
    if (data == null) data = new LinkedHashSet<>((List<League>) Util.query("League"));
    return data;
  }

  public static League get(League neu) {
    get();
    final League entry = find(neu.getId());
    if (entry == null) data.add(neu);
    return find(neu.getId());
  }

  public static League find(int id) {
    get();
    return data.stream().filter(entry -> entry.getId() == (short) id).findFirst().orElse(null);
  }

  @Id
  @Column(name = "league_id", nullable = false)
  private short id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "stage")
  private Stage stage;

  @Column(name = "league_name", nullable = false, length = 13)
  private String name;

  @OneToMany(mappedBy = "league")
  private final Set<Team> teams = new LinkedHashSet<>();

  @OneToMany(mappedBy = "league")
  private final Set<TurnamentMatch> matches = new LinkedHashSet<>();

  // default constructor
  public League() {
  }

  public League(short id, String name) {
    this.id = id;
    this.name = name;
  }

  public void addTeam(Team team) {
    teams.add(team);
    team.setLeague(this);
  }

  public void addMatch(TurnamentMatch match) {
    matches.add(match);
    match.setLeague(this);
  }

  //<editor-fold desc="getter and setter">
  public Set<TurnamentMatch> getMatches() {
    return matches;
  }

  public Set<Team> getTeams() {
    return teams;
  }

  public String getName() {
    return name;
  }

  public void setName(String leagueName) {
    this.name = leagueName;
  }

  public Stage getStage() {
    return stage;
  }

  void setStage(Stage stage) {
    this.stage = stage;
  }

  public short getId() {
    return id;
  }

  public void setId(short id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof League)) return false;
    final League league = (League) o;
    return getId() == league.getId() && getStage().equals(league.getStage()) && getName().equals(league.getName()) && getMatches().equals(league.getMatches());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getStage(), getName());
  }

  @Override
  public String toString() {
    return "League{" +
        "id=" + id +
        ", stage=" + stage +
        ", leagueName='" + name + '\'' +
        ", matches='" + matches.size() + '\'' +
        '}';
  }
  //</editor-fold>
}