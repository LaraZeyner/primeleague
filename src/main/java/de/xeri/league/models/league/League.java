package de.xeri.league.models.league;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.league.util.Data;
import de.xeri.league.util.HibernateUtil;
import org.hibernate.annotations.NamedQuery;

@Entity(name = "League")
@Table(name = "league", indexes = @Index(name = "idx_league_name", columnList = "stage, league_name", unique = true))
@NamedQuery(name = "League.findAll", query = "FROM League l")
@NamedQuery(name = "League.findById", query = "FROM League l WHERE id = :pk")
@NamedQuery(name = "League.findBy", query = "FROM League l WHERE stage = :stage AND name = :name")
public class League implements Serializable {

  @Transient
  private static final long serialVersionUID = 7621013514942624366L;

  public static Set<League> get() {
    return new LinkedHashSet<>(HibernateUtil.findList(League.class));
  }

  public static League get(League neu, Stage stage) {
    if (has(neu.getId())) {
      return find(neu.getId());
    }
    stage.getLeagues().add(neu);
    neu.setStage(stage);
    Data.getInstance().save(neu);
    return neu;
  }

  public static boolean has(Stage stage, String name) {
    return HibernateUtil.has(League.class, new String[]{"stage", "name"}, new Object[]{stage, name});
  }

  public static boolean has(short id) {
    return HibernateUtil.has(League.class, id);
  }

  public static League find(Stage stage, String name) {
    return HibernateUtil.find(League.class, new String[]{"stage", "name"}, new Object[]{stage, name});
  }

  public static League find(short id) {
    return HibernateUtil.find(League.class, id);
  }

  @Id
  @Column(name = "league_id", nullable = false)
  private short id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "stage")
  private Stage stage;

  @Column(name = "league_name", nullable = false, length = 25)
  private String name;

  @ManyToMany
  @JoinTable(name = "league_team",
      joinColumns = @JoinColumn(name = "league", referencedColumnName = "league_id"),
      inverseJoinColumns = @JoinColumn(name = "team", referencedColumnName = "team_id"),
      indexes = @Index(name = "idx_teamleagues", columnList = "league, team", unique = true))
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
    if (teams.stream().noneMatch(team1 -> team1.getTurneyId() == team.getTurneyId())) {
      teams.add(team);
      team.getLeagues().add(this);
      Data.getInstance().save(team);
      Data.getInstance().save(this);
    }
  }

  void addMatch(TurnamentMatch match) {
    if (!matches.contains(match)) {
      matches.add(match);
      match.setLeague(this);
    }
  }

  public Team atPlace(int place) {
    final Set<Team> teamList = teams.stream()
        .collect(Collectors.toMap(team -> team, team -> new TeamLeaguePerformance(this, team).getScore(), (a, b) -> b))
        .entrySet().stream()
        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new))
        .keySet();
    return new ArrayList<>(teamList).get(place - 1);
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