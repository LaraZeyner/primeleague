package de.xeri.league.models.league;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import de.xeri.league.models.enums.Elo;
import de.xeri.league.models.enums.Lane;
import de.xeri.league.models.enums.QueueType;
import de.xeri.league.models.match.Game;
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
    entry.setIcon(neu.getIcon());
    entry.setLevel(neu.getLevel());
    entry.setName(neu.getName());
    return find(neu.getPuuid());
  }

  public static Account find(String puuid) {
    get();
    return data.stream().filter(entry -> entry.getPuuid().equals(puuid)).findFirst().orElse(null);
  }
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "account_id", nullable = false, columnDefinition = "SMALLINT UNSIGNED NOT NULL")
  private int id;

  @Column(name = "puuid", length = 78)
  private String puuid;

  @Column(name = "summoner_id", length = 47)
  private String summonerId;

  @Column(name = "account_name", nullable = false, length = 16)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "player")
  private Player player;

  @Column(name = "icon")
  private short icon;

  @Column(name = "account_level")
  private short level;

  @Column(name = "account_active", nullable = false)
  private boolean active;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "last_update")
  private Date lastUpdate;

  @OneToMany(mappedBy = "account")
  private final Set<Playerperformance> playerperformances = new LinkedHashSet<>();

  @OneToMany(mappedBy = "account")
  private final Set<SeasonElo> seasonElos = new LinkedHashSet<>();

  // default constructor
  public Account() {
  }

  public Account(String name) {
    this.name = name;
    this.active = false;
  }

  public Account(String puuid, String summonerId, String name, short icon, short level) {
    this.puuid = puuid;
    this.summonerId = summonerId;
    this.name = name;
    this.icon = icon;
    this.level = level;
    this.active = true;
    this.lastUpdate = new Date(System.currentTimeMillis() - 15_552_000_000L);
  }

  public boolean isValueable() {
    return player != null && player.getTeam() != null && player.getTeam().isValueable();
  }

  public List<Game> getGames() {
    return playerperformances.stream().map(playerperformance -> playerperformance.getTeamperformance().getGame()).collect(Collectors.toList());
  }

  public List<Game> getCompetitiveGames() {
    final List<Short> ids = Arrays.asList((short) -1, (short) QueueType.TOURNEY.getQueueId(), (short) QueueType.CLASH.getQueueId());
    return getGames().stream()
        .filter(game -> ids.contains(game.getGametype().getId()))
        .filter(game -> Util.inRange(game.getGameStart()))
        .collect(Collectors.toList());
  }

  public List<Playerperformance> getCompetitivePerformances() {
    final List<Short> ids = Arrays.asList((short) -1, (short) QueueType.TOURNEY.getQueueId(), (short) QueueType.CLASH.getQueueId());
    return getPlayerperformances().stream()
        .filter(playerperformance -> ids.contains(playerperformance.getTeamperformance().getGame().getGametype().getId()))
        .filter(playerperformance -> Util.inRange(playerperformance.getTeamperformance().getGame().getGameStart()))
        .collect(Collectors.toList());
  }

  public List<Playerperformance> getAllPerformances() {
    return getPlayerperformances().stream()
        .filter(playerperformance -> Util.inRange(playerperformance.getTeamperformance().getGame().getGameStart()))
        .collect(Collectors.toList());
  }

  public List<Playerperformance> getGamesOn(Lane lane, boolean competitive) {
    if (competitive) {
      return getCompetitivePerformances().stream().filter(playerperformance -> playerperformance.getLane().equals(lane))
          .collect(Collectors.toList());
    } else {
      return getAllPerformances().stream().filter(playerperformance -> playerperformance.getLane().equals(lane))
          .collect(Collectors.toList());
    }

  }

  public Date getLastCompetitiveGame() {
    return getCompetitiveGames().stream().map(Game::getGameStart).max(Date::compareTo).orElse(null);
  }

  public SeasonElo getMostRecentElo() {
    return seasonElos.stream().min((elo1, elo2) -> elo2.getSeason().getId() - elo1.getSeason().getId()).orElse(null);
  }

  public void addSeasonElo(SeasonElo seasonElo) {
    seasonElos.add(seasonElo);
    seasonElo.setAccount(this);
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

  public Lane getMainRole() {
    int amount = 0;
    Lane lane = null;
    for (Lane laneIteration : Lane.values()) {
      final int gamesOnLaneAmount = getGamesOn(laneIteration, true).size();
      if (gamesOnLaneAmount > amount) {
        amount = gamesOnLaneAmount;
        lane = laneIteration;
      }
    }
    return lane;
  }

  public String getDisplayName(Lane lane) {
    final int amount = getGamesOn(lane, true).size();
    return name + " - " + amount + " Spiel" + (amount != 1 ? "e" : "");
  }

  public String getPositionalIcon(Lane lane) {
    if (getMostRecentElo() == null || getMostRecentElo().getElo().equals(Elo.UNRANKED)) {
      return "images/ranked/Ranked_Unranked.png";
    }
    return "images/ranked/position/Position_" + getMostRecentElo().getElo().getTier() + "-" + lane.getDisplayName() + ".png";
  }

  //<editor-fold desc="getter and setter">
  public Set<SeasonElo> getSeasonElos() {
    return seasonElos;
  }

  public Set<Playerperformance> getPlayerperformances() {
    return playerperformances;
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

  public String getSummonerId() {
    return summonerId;
  }

  public void setSummonerId(String accountId) {
    this.summonerId = accountId;
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
    return getIcon() == account.getIcon() && getLevel() == account.getLevel() && isActive() == account.isActive() && getPuuid().equals(account.getPuuid()) && getSummonerId().equals(account.getSummonerId()) && Objects.equals(getName(), account.getName()) && Objects.equals(getPlayer(), account.getPlayer()) && getLastUpdate().equals(account.getLastUpdate()) && getPlayerperformances().equals(account.getPlayerperformances()) && getSeasonElos().equals(account.getSeasonElos());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getPuuid(), getSummonerId(), getName(), getPlayer(), getIcon(), getLevel(), getLastUpdate(), isActive());
  }

  @Override
  public String toString() {
    return "Account{" +
        "puuid='" + puuid + '\'' +
        ", summonerId='" + summonerId + '\'' +
        ", name='" + name + '\'' +
        ", player=" + player +
        ", icon=" + icon +
        ", level=" + level +
        ", lastUpdate=" + lastUpdate +
        ", active=" + active +
        ", playerperformances=" + playerperformances.size() +
        ", seasonElos=" + seasonElos.size() +
        '}';
  }
  //</editor-fold>
}