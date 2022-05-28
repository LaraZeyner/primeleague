package de.xeri.prm.models.league;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
import de.xeri.prm.models.dynamic.Champion;
import de.xeri.prm.models.dynamic.Matchup;
import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.enums.Teamrole;
import de.xeri.prm.models.match.ratings.StatScope;
import de.xeri.prm.servlet.datatables.scouting.ChampionView;
import de.xeri.prm.util.Const;
import de.xeri.prm.util.HibernateUtil;
import de.xeri.prm.util.Util;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.NamedQuery;
import org.jetbrains.annotations.NotNull;

@Entity(name = "Player")
@Table(name = "player", indexes = @Index(name = "team", columnList = "team"))
@NamedQuery(name = "Player.findAll", query = "FROM Player p")
@NamedQuery(name = "Player.findById", query = "FROM Player p WHERE id = :pk")
@NamedQuery(name = "Player.findBy", query = "FROM Player p WHERE name = :name")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Player implements Serializable {

  @Transient
  private static final long serialVersionUID = -2823713148714882156L;

  @Transient
  private Map<Champion, List<Matchup>> matchups;

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
  @ToString.Exclude
  private Team team;

  @Enumerated(EnumType.STRING)
  @Column(name = "player_role", length = 7)
  private Teamrole role;

  @Column(name = "displayslot")
  @Check(constraints = "displayslot < 10")
  private Byte displayslot;

  @OneToMany(mappedBy = "player")
  @ToString.Exclude
  private final Set<Account> accounts = new LinkedHashSet<>();

  @OneToMany(mappedBy = "player")
  @ToString.Exclude
  private final Set<Matchlog> logEntries = new LinkedHashSet<>();

  public Player(int id, String name, Teamrole role) {
    this.id = id;
    this.name = name;
    this.role = role;
    this.displayslot = null;
  }

  public void addAccount(Account account) {
    accounts.add(account);
    account.setPlayer(this);
  }

  public void addLogEntry(Matchlog entry) {
    logEntries.add(entry);
    entry.setPlayer(this);
  }

  public void removeTeam() {
    team.getPlayers().remove(this);
    team = null;
    Data.getInstance().save(this);
  }

  public String getDisplayName() {
    return accounts.stream().filter(Account::isActive).map(Account::getName).findFirst().orElse(name);
  }

  public Account getActiveAccount() {
    return accounts.stream().filter(Account::isActive).findFirst()
        .orElse(accounts.isEmpty() ? null : new ArrayList<>(accounts).get(0));
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
    return new SeasonElo((short) (points / (wins + losses)), (short) wins, (short) losses);
  }

  public String getLogoUrl() {
    return "https://cdn0.gamesports.net/user_pictures/" + id / 10000 + "0000/" + id + ".jpg";
  }

  /**
   * Sortierung nach > 25% Presence
   * dann 5 Games in 30 Tagen
   * dann andere nach Presence mit 1 Game und 10 Gesamt
   *
   * @param lane Lane
   * @return
   */
  public List<ChampionView> getChampionsPresence(Lane lane) {
    final LinkedHashMap<Short, Integer> presentCompetitivelike = determineChampionsPresent(lane, StatScope.COMPETITIVELIKE, true);
    final LinkedHashMap<Short, Integer> presentRecently = determineChampionsPresent(lane, StatScope.OTHER, true);
    final LinkedHashMap<Short, Integer> presentVeryRecently = determineChampionsPresent(lane, StatScope.RECENT, true);
    final LinkedHashMap<Short, Integer> pickCLike = determineChampionsPresent(lane, StatScope.COMPETITIVELIKE, false);
    final LinkedHashMap<Short, Integer> pickRecent = determineChampionsPresent(lane, StatScope.OTHER, false);

    int cLikeMatches = pickCLike.keySet().stream().mapToInt(pickCLike::get).sum();
    final List<ChampionView> champs = presentCompetitivelike.keySet().stream()
        .filter(id -> presentCompetitivelike.get(id) >= cLikeMatches * Const.PRESENCE_PERCENT_LIMIT)
        .map(id -> getChampionView(lane, id, presentCompetitivelike, pickCLike, pickRecent))
        .sorted((o1, o2) -> o2.getPresenceNum() - o1.getPresenceNum())
        .distinct().collect(Collectors.toList());
    determineChampionOrderByPresence(lane, presentCompetitivelike, champs, presentVeryRecently, pickCLike, pickRecent);
    determineChampionOrderByPresence(lane, presentCompetitivelike, champs, presentRecently, pickCLike, pickRecent);

    final LinkedHashMap<Short, Integer> pickComp = determineChampionsPresent(lane, StatScope.COMPETITIVE, false);
    final List<String> champsToRemove = champs.stream()
        .filter(champ -> pickComp.getOrDefault(champ.getId(), 0) < 1
            && pickCLike.getOrDefault(champ.getId(), 0) < 5
            && pickRecent.getOrDefault(champ.getId(), 0) < 5)
        .map(ChampionView::getName).collect(Collectors.toList());

    champs.removeIf(champ -> champsToRemove.contains(champ.getName()));
    return new ArrayList<>(champs);
  }

  private LinkedHashMap<Short, Integer> determineChampionsPresent(Lane lane, StatScope scope, boolean presence) {
    final LinkedHashMap<Short, Integer> map = new LinkedHashMap<>();
    accounts.forEach(account -> determineSource(account, lane, scope, presence).forEach((key, value) -> map.merge(key, value, Integer::sum)));
    return map.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
  }

  private LinkedHashMap<Short, Integer> determineSource(Account account, Lane lane, StatScope scope, boolean presence) {
    return presence ? HibernateUtil.getChampionIdsPresentOn(account, lane, scope) :
        HibernateUtil.getChampionIdsPickedOn(account, lane, scope);
  }

  private void determineChampionOrderByPresence(Lane lane, Map<Short, Integer> presentCompetitivelike, List<ChampionView> champs,
                                                Map<Short, Integer> picked, Map<Short, Integer> pickCLike, Map<Short, Integer> pickRecent) {
    picked.keySet().stream()
        .filter(id -> picked.getOrDefault(id, 0) >= Const.PRESENCE_RECENTLY_LIMIT)
        .map(id -> getChampionView(lane, id, presentCompetitivelike, pickCLike, pickRecent))
        .sorted((o1, o2) -> o2.getPresenceNum() - o1.getPresenceNum())
        .distinct()
        .filter(championView -> champs.stream().noneMatch(championView1 -> championView1.getId() == championView.getId()))
        .forEach(champs::add);
  }

  @NotNull
  private ChampionView getChampionView(Lane lane, short id, Map<Short, Integer> presentCompetitivelike, Map<Short, Integer> pickCLike,
                                       Map<Short, Integer> pickRecent) {
    final int picked = pickCLike.getOrDefault(id, 0);

    final int presence = (int) Math.round(Util.div(100 * presentCompetitivelike.getOrDefault(id, 0),
        pickCLike.keySet().stream().mapToInt(pickCLike::get).sum()));

    final int pickedRecent = pickRecent.getOrDefault(id, 0);

    double winrate = -1;
    if (pickedRecent > 0) {
      final int games = accounts.stream().map(account -> HibernateUtil.getWins(account, lane, id)).mapToInt(wins -> wins.get(0)).sum();
      final int win = accounts.stream().map(account -> HibernateUtil.getWins(account, lane, id)).mapToInt(wins -> wins.get(1)).sum();
      if (games >= 5) {
        winrate = Util.div(1d * win, games);
      }
    }


    val winrateString = winrate != -1 ? Math.round(100 * winrate) + "%" : "-";
    final Champion champion = Champion.find(id);

    return new ChampionView(id, champion, champion.getName(), presence + "%", presence , picked, pickedRecent, winrateString);
  }

  public List<Integer> getGamesOn() {
    List<Integer> games = Arrays.asList(0, 0, 0, 0, 0);
    for (Account account : accounts) {
      final Object[] objects = HibernateUtil.gamesOnAllLanes(account);
      for (int i = 0; i < objects.length; i++) {
        if (objects[i] instanceof Long) {
          games.set(i, games.get(i) + (int) ((Long) objects[i]).longValue());
        } else {
          games.set(i, games.get(i));
        }
      }
    }
    return games;
  }

  public List<Matchup> getMatchups(Champion champion) {
    if (matchups == null) {
      matchups = new HashMap<>();
    }

    matchups.computeIfAbsent(champion, k -> HibernateUtil.determineMatchups(this, champion));

    return matchups.get(champion);
  }

  public Matchup getMatchup(Champion champion, Champion enemy) {
    return getMatchups(champion).stream().filter(matchup -> matchup.getChampion().equals(champion)).findFirst().orElse(HibernateUtil.determineMatchup(this, champion, enemy));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    final Player player = (Player) o;
    return Objects.equals(id, player.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getName(), getRole(), getDisplayslot());
  }
}