package de.xeri.prm.models.league;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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

import de.xeri.prm.manager.Data;
import de.xeri.prm.util.HibernateUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import org.hibernate.Hibernate;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.query.Query;

@Entity(name = "League")
@Table(name = "league", indexes = @Index(name = "idx_league_name", columnList = "stage, league_name", unique = true))
@NamedQuery(name = "League.findAll", query = "FROM League l")
@NamedQuery(name = "League.findById", query = "FROM League l WHERE id = :pk")
@NamedQuery(name = "League.findBy", query = "FROM League l WHERE stage = :stage AND name = :name")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
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
  @ToString.Exclude
  private Stage stage;

  @Column(name = "league_name", nullable = false, length = 25)
  private String name;

  @ManyToMany
  @JoinTable(name = "league_team",
      joinColumns = @JoinColumn(name = "league", referencedColumnName = "league_id"),
      inverseJoinColumns = @JoinColumn(name = "team", referencedColumnName = "team_id"),
      indexes = @Index(name = "idx_teamleagues", columnList = "league, team", unique = true))
  @ToString.Exclude
  private final Set<Team> teams = new LinkedHashSet<>();

  @OneToMany(mappedBy = "league")
  @ToString.Exclude
  private final Set<TurnamentMatch> matches = new LinkedHashSet<>();

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

  public Map<Matchday, List<TurnamentMatch>> getMatchdays() {
    final Query<TurnamentMatch> namedQuery = Data.getInstance().getSession().getNamedQuery("TurnamentMatch.leagueGames");
    namedQuery.setParameter("league", Data.getInstance().getCurrentGroup());
    final List<TurnamentMatch> list = namedQuery.list();
    Map<Matchday, List<TurnamentMatch>> map = new HashMap<>();
    for (TurnamentMatch objects : list) {
      val day = objects.getMatchday();
      if (map.containsKey(day)) {
        map.get(day).add(objects);
      } else {
        final List<TurnamentMatch> matches = new ArrayList<>();
        matches.add(objects);
        map.put(day, matches);
      }
    }

    return map;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    final League league = (League) o;
    return Objects.equals(id, league.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}