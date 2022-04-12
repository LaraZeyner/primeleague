package de.xeri.league.models.league;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import de.xeri.league.models.match.Playerperformance;
import de.xeri.league.util.Data;
import de.xeri.league.util.Util;

@Entity(name = "Account")
@Table(name = "account", indexes = {
    @Index(name = "account_id", columnList = "account_id", unique = true),
    @Index(name = "account_name", columnList = "account_name", unique = true),
    @Index(name = "player", columnList = "player")
})
public class Account implements Serializable {

  @Transient
  private static final long serialVersionUID = -3036623290602783787L;

  private static Set<Account> data;

  public static void save() {
    if (data != null) data.forEach(Data.getInstance().getSession()::saveOrUpdate);
  }

  public static Set<Account> get() {
    if (data == null) data = new LinkedHashSet<>((List<Account>) Util.query("Account"));
    return data;
  }

  public static Account get(Account neu) {
    get();
    final Account entry = find(neu.getPuuid());
    if (entry == null) data.add(neu);
    return find(neu.getPuuid());
  }

  public static Account find(String puuid) {
    get();
    return data.stream().filter(entry -> entry.getPuuid().equals(puuid)).findFirst().orElse(null);
  }

  @Id
  @Column(name = "puuid", nullable = false, length = 78)
  private String puuid;

  @Column(name = "account_id", nullable = false, length = 47)
  private String accountId;

  @Column(name = "account_name", nullable = false, length = 16)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "player")
  private Player player;

  @Column(name = "icon", nullable = false)
  private short icon;

  @Column(name = "account_level", nullable = false)
  private short level;

  @Column(name = "account_active", nullable = false)
  private boolean active = true;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "last_update")
  private Date lastUpdate;

  @OneToMany(mappedBy = "account")
  private Set<Playerperformance> playerperformances = new LinkedHashSet<>();

  @OneToMany(mappedBy = "account")
  private Set<SeasonElo> seasonElos = new LinkedHashSet<>();

  // default constructor
  public Account() {
  }

  public Account(String puuid, String accountId, String name, short icon, short level) {
    this.puuid = puuid;
    this.accountId = accountId;
    this.name = name;
    this.icon = icon;
    this.level = level;
    this.lastUpdate = new Date(System.currentTimeMillis() - 15_552_000_000L);
  }

  public boolean isValueable() {
    return player != null && player.getTeam() != null && player.getTeam().isValueable();
  }

  public void addSeasonElo(SeasonElo seasonElo) {
    seasonElos.add(seasonElo);
    seasonElo.setAccount(this);
  }

  public void addPlayerperformance(Playerperformance playerperformance) {
    playerperformances.add(playerperformance);
    playerperformance.setAccount(this);
  }

  public List<Team> getTeams() {
    final Map<Team, Integer> teamsPlayedFor = new HashMap<>();
    final List<Team> teamList = new ArrayList<>();
    final List<Team> teamsList = playerperformances.stream()
        .filter(playerperformance -> playerperformance.getTeamperformance().getTeam() != null)
        .filter(playerperformance -> new Date().getTime() - playerperformance.getTeamperformance().getGame().getGameStart().getTime() < 15_552_000_000L)
        .map(playerperformance -> playerperformance.getTeamperformance().getTeam())
        .collect(Collectors.toList());
    for (Team team : teamsList) {
      int amount = 1;
      if (teamsPlayedFor.containsKey(team)) {
        amount += teamsPlayedFor.get(team);
        if (amount == 5) {
          teamList.add(team);
        }
      }
      teamsPlayedFor.put(team, amount);
    }
    return teamList.isEmpty() ? (player != null ? Collections.singletonList(player.getTeam()) : null) : teamList;
  }

  public Team getOfficialTeam() {
    return player != null ? player.getTeam() : null;
  }

  //<editor-fold desc="getter and setter">
  public Set<SeasonElo> getSeasonElos() {
    return seasonElos;
  }

  public void setSeasonElos(Set<SeasonElo> seasonElos) {
    this.seasonElos = seasonElos;
  }

  public Set<Playerperformance> getPlayerperformances() {
    return playerperformances;
  }

  public void setPlayerperformances(Set<Playerperformance> playerperformances) {
    this.playerperformances = playerperformances;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public Player getPlayer() {
    return player;
  }

  void setPlayer(Player player) {
    this.player = player;
  }

  public String getName() {
    return name;
  }

  public void setName(String accountName) {
    this.name = accountName;
  }

  public String getAccountId() {
    return accountId;
  }

  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }

  public String getPuuid() {
    return puuid;
  }

  public void setPuuid(String id) {
    this.puuid = id;
  }

  public short getIcon() {
    return icon;
  }

  public void setIcon(short icon) {
    this.icon = icon;
  }

  public short getLevel() {
    return level;
  }

  public void setLevel(short level) {
    this.level = level;
  }

  public Date getLastUpdate() {
    return lastUpdate;
  }

  public void setLastUpdate(Date lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Account)) return false;
    final Account account = (Account) o;
    return getIcon() == account.getIcon() && getLevel() == account.getLevel() && isActive() == account.isActive() && getPuuid().equals(account.getPuuid()) && getAccountId().equals(account.getAccountId()) && Objects.equals(getName(), account.getName()) && Objects.equals(getPlayer(), account.getPlayer()) && getLastUpdate().equals(account.getLastUpdate()) && getPlayerperformances().equals(account.getPlayerperformances()) && getSeasonElos().equals(account.getSeasonElos());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getPuuid(), getAccountId(), getName(), getPlayer(), getIcon(), getLevel(), getLastUpdate(), isActive(), getPlayerperformances(), getSeasonElos());
  }

  @Override
  public String toString() {
    return "Account{" +
        "puuid='" + puuid + '\'' +
        ", accountId='" + accountId + '\'' +
        ", name='" + name + '\'' +
        ", player=" + player +
        ", icon=" + icon +
        ", level=" + level +
        ", lastUpdate=" + lastUpdate +
        ", active=" + active +
        ", playerperformances=" + playerperformances +
        ", seasonElos=" + seasonElos +
        '}';
  }
  //</editor-fold>
}