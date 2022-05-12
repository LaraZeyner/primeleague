package de.xeri.prm.models.league;

import java.io.Serializable;
import java.util.LinkedHashSet;
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

import de.xeri.prm.manager.Data;
import de.xeri.prm.models.enums.Teamrole;
import de.xeri.prm.util.HibernateUtil;
import org.hibernate.annotations.NamedQuery;

@Entity(name = "Player")
@Table(name = "player", indexes = @Index(name = "team", columnList = "team"))
@NamedQuery(name = "Player.findAll", query = "FROM Player p")
@NamedQuery(name = "Player.findById", query = "FROM Player p WHERE id = :pk")
@NamedQuery(name = "Player.findBy", query = "FROM Player p WHERE name = :name")
public class Player implements Serializable {

  @Transient
  private static final long serialVersionUID = -2823713148714882156L;

  public static Set<Player> get() {
    return new LinkedHashSet<>(HibernateUtil.findList(Player.class));
  }

  public static Player get(Player neu, Team team) {
    if (has(neu.getId())) {
      final Player player = find(neu.getId());
      player.setRole(neu.getRole());

      final Team currentTeam = player.getTeam();
      if (!currentTeam.equals(team)) {
        currentTeam.getPlayers().remove(player);
        team.getPlayers().add(player);
        player.setTeam(team);
      }
      return player;
    }
    team.getPlayers().add(neu);
    neu.setTeam(team);
    Data.getInstance().save(neu);
    return neu;
  }

  public static boolean has(int id) {
    return HibernateUtil.has(Player.class, id);
  }

  public static boolean has(String name) {
    return HibernateUtil.has(Player.class, new String[]{"name"}, new Object[]{name});
  }

  public static Player find(String name) {
    return HibernateUtil.find(Player.class, new String[]{"name"}, new Object[]{name});
  }

  public static Player find(int id) {
    return HibernateUtil.find(Player.class, id);
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

  public String getDisplayName() {
    return accounts.stream().filter(Account::isActive).map(Account::getName).findFirst().orElse(name);
  }

  public Account getActiveAccount() {
    return accounts.stream().filter(Account::isActive).findFirst().orElse(null);
  }

  public SeasonElo getCurrentElo() {
    int points = 0;
    int wins = 0;
    int losses = 0;
    Season season = null;
    for (Account account : accounts) {
      final SeasonElo mostRecentElo = account.getMostRecentElo();
      if (season == null || mostRecentElo.getSeason().getId() > season.getId()) {
        season = mostRecentElo.getSeason();
        points = 0;
        wins = 0;
        losses = 0;
      }
      points += mostRecentElo.getMmr() * mostRecentElo.getGames();
      wins += mostRecentElo.getWins();
      losses += mostRecentElo.getLosses();
    }
    return new SeasonElo((short) (points/(wins + losses)), (short) wins, (short) losses);
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