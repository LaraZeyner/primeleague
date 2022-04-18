package de.xeri.league.models.league;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.league.models.enums.Teamrole;
import de.xeri.league.util.Data;
import de.xeri.league.util.Util;

@Entity(name = "Player")
@Table(name = "player", indexes = @Index(name = "team", columnList = "team"))
public class Player implements Serializable {

  @Transient
  private static final long serialVersionUID = -2823713148714882156L;

  private static Set<Player> data;

  public static void save() {
    if (data != null) data.forEach(Data.getInstance().getSession()::saveOrUpdate);
  }

  public static Set<Player> get() {
    if (data == null) data = new LinkedHashSet<>((List<Player>) Util.query("Player"));
    return data;
  }

  public static Player get(Player neu, Team team) {
    get();
    final Player entry = find(neu.getId());
    if (entry == null) {
      team.getPlayers().add(neu);
      neu.setTeam(team);
      data.add(neu);
    }
    final Player player = find(neu.getId());
    player.setRole(neu.getRole());
    if (!player.getTeam().equals(team)) {
      player.getTeam().getPlayers().remove(player);
      team.getPlayers().add(player);
      player.setTeam(team);
    }
    return player;
  }

  public static Player find(int id) {
    get();
    return data.stream().filter(entry -> entry.getId() == id).findFirst().orElse(null);
  }

  public static Player find(String name) {
    get();
    return data.stream().filter(entry -> entry.getName().equals(name)).findFirst().orElse(null);
  }

  @Id
  @Column(name = "player_id", nullable = false)
  private int id;

  @Column(name = "player_name", unique = true, length = 100)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "team")
  private Team team;

  @Enumerated(EnumType.STRING)
  @Column(name = "player_role", length = 7)
  private Teamrole role;

  @OneToMany(mappedBy = "player")
  private final Set<Account> accounts = new LinkedHashSet<>();

  @OneToMany(mappedBy = "player")
  private final Set<Matchlog> logEntries = new LinkedHashSet<>();

  // default constructor
  public Player() {
  }

  public Player(int id, String name, Teamrole role) {
    this.id = id;
    this.name = name;
    this.role = role;
  }

  public void addAccount(Account account) {
    accounts.add(account);
    account.setPlayer(this);
  }

  public void addLogEntry(Matchlog entry) {
    logEntries.add(entry);
    entry.setPlayer(this);
  }

  public String getLogoUrl() {
    return "https://cdn0.gamesports.net/user_pictures/" + id /10000  + "0000/" + id + ".jpg";
  }

  //<editor-fold desc="getter and setter">
  public Set<Account> getAccounts() {
    return accounts;
  }

  public Set<Matchlog> getLogEntries() {
    return logEntries;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Teamrole getRole() {
    return role;
  }

  public void setRole(Teamrole role) {
    this.role = role;
  }

  public Team getTeam() {
    return team;
  }

  void setTeam(Team team) {
    this.team = team;
  }

  public int getId() {
    return id;
  }

  public void setId(int competitiveId) {
    this.id = competitiveId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Player)) return false;
    final Player player = (Player) o;
    return getId() == player.getId() && Objects.equals(getTeam(), player.getTeam()) && getRole() == player.getRole() && getAccounts().equals(player.getAccounts());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getTeam(), getRole());
  }

  @Override
  public String toString() {
    return "Player{" +
        ", competitiveId=" + id +
        ", team=" + team +
        ", role=" + role +
        ", accounts=" + accounts.size() +
        '}';
  }
  //</editor-fold>
}