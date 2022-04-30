package de.xeri.league.models.match;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.league.models.dynamic.Champion;
import de.xeri.league.models.dynamic.Item;
import de.xeri.league.models.dynamic.Rune;
import de.xeri.league.models.dynamic.Summonerspell;
import de.xeri.league.models.enums.ItemType;
import de.xeri.league.models.enums.Lane;
import de.xeri.league.models.league.Account;
import de.xeri.league.util.Data;
import de.xeri.league.util.HibernateUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.val;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.NamedQuery;

@Entity(name = "Playerperformance")
@Table(name = "playerperformance", indexes = {
    @Index(name = "idx_playerperformance_player", columnList = "teamperformance, account", unique = true),
    @Index(name = "champion_enemy", columnList = "champion_enemy"),
    @Index(name = "idx_playerperformance_lane", columnList = "teamperformance, lane", unique = true),
    @Index(name = "champion_own", columnList = "champion_own"),
    @Index(name = "account", columnList = "account"),
})
@Filter(name = "team_filter", condition = "team= :team")
@Filter(name = "account_filter", condition = "account= :account")
@Filter(name = "filter_lane", condition = "lane= :lane")
@Filter(name = "filter_champion_own", condition = "championOwn= :champion")
@Filter(name = "filter_champion_enemy", condition = "championEnemy= :champion")
@Filter(name = "filter_gametype", condition = "gametype= :gametype")
@Filter(name = "filter_since", condition = "gameStart >= :minDate")
@NamedQuery(name = "Playerperformance.findAll", query = "FROM Playerperformance p")
@NamedQuery(name = "Playerperformance.findById", query = "FROM Playerperformance p WHERE id = :pk")
@NamedQuery(name = "Playerperformance.findBy", query = "FROM Playerperformance p WHERE teamperformance = :teamperformance AND account = :account")
@NamedQuery(name = "Playerperformance.findByLane", query = "FROM Playerperformance p WHERE teamperformance = :teamperformance AND lane = :lane")
@Getter
@Setter
@NoArgsConstructor
public class Playerperformance implements Serializable {

  @Transient
  private static final long serialVersionUID = 6290895798073708343L;

  //<editor-fold desc="Queries">
  public static Set<Playerperformance> get() {
    return new LinkedHashSet<>(HibernateUtil.findList(Playerperformance.class));
  }

  static Playerperformance get(Playerperformance neu, Teamperformance performance, Account account) {
    if (has(performance, account)) {
      return find(performance, account);
    }
    performCreate(neu, performance, account);
    return find(performance, neu.getAccount());
  }

  static Playerperformance get(Playerperformance neu, Teamperformance performance, Account account, Lane lane) {
    if (has(performance, lane)) {
      return find(performance, lane);
    }
    neu.setLane(lane);
    performCreate(neu, performance, account);
    return find(performance, neu.getAccount());
  }

  private static void performCreate(Playerperformance neu, Teamperformance performance, Account account) {
    account.getPlayerperformances().add(neu);
    performance.getPlayerperformances().add(neu);
    neu.setTeamperformance(performance);
    neu.setAccount(account);
    Data.getInstance().save(neu);
  }

  public static boolean has(Teamperformance teamperformance, Account account) {
    return HibernateUtil.has(Playerperformance.class, new String[]{"teamperformance", "account"}, new Object[]{teamperformance, account});
  }

  public static boolean has(Teamperformance teamperformance, Lane lane) {
    return HibernateUtil.has(Playerperformance.class, new String[]{"teamperformance", "lane"}, new Object[]{teamperformance, lane}, "findByLane");
  }

  public static Playerperformance find(Teamperformance teamperformance, Lane lane) {
    return HibernateUtil.find(Playerperformance.class, new String[]{"teamperformance", "lane"}, new Object[]{teamperformance, lane}, "findByLane");
  }

  public static Playerperformance find(Teamperformance teamperformance, Account account) {
    return HibernateUtil.find(Playerperformance.class, new String[]{"teamperformance", "account"}, new Object[]{teamperformance, account});
  }
  //</editor-fold>

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "playerperformance_id", nullable = false)
  private int id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "teamperformance")
  private Teamperformance teamperformance;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "account")
  private Account account;

  @Enumerated(EnumType.STRING)
  @Column(name = "lane", nullable = false, length = 7)
  private Lane lane;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "champion_own")
  private Champion championOwn;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "champion_enemy")
  private Champion championEnemy;

  @Column(name = "q_usages", nullable = false)
  private short qUsages;

  @Column(name = "w_usages", nullable = false)
  private short wUsages;

  @Column(name = "e_usages", nullable = false)
  private short eUsages;

  @Column(name = "r_usages", nullable = false)
  private short rUsages;

  @Column(name = "spells_hit")
  private short spellsHit;

  @Column(name = "spells_dodged")
  private short spellsDodged;

  @Column(name = "quick_dodged")
  private short quickDodged;

  @Column(name = "damage_magical", nullable = false)
  private int damageMagical;

  @Column(name = "damage_physical", nullable = false)
  private int damagePhysical;

  @Column(name = "damage_total", nullable = false)
  private int damageTotal;

  @Column(name = "damage_taken", nullable = false)
  private int damageTaken;

  @Column(name = "damage_mitigated", nullable = false)
  private int damageMitigated;

  @Column(name = "damage_healed", nullable = false)
  private int damageHealed;

  @Column(name = "damage_shielded", nullable = false)
  private int damageShielded;

  @Column(name = "kills", nullable = false)
  private byte kills;

  @Column(name = "deaths", nullable = false)
  private byte deaths;

  @Column(name = "assists", nullable = false)
  private byte assists;

  @Column(name = "kills_solo")
  private byte soloKills;

  @Check(constraints = "allin_levelup IS NULL OR allin_levelup < 18")
  @Column(name = "allin_levelup")
  private byte levelUpAllin;

  @Column(name = "kills_multi_double", nullable = false)
  private byte doubleKills;

  @Column(name = "kills_multi_triple", nullable = false)
  private byte tripleKills;

  @Column(name = "kills_multi_quadra", nullable = false)
  private byte quadraKills;

  @Column(name = "kills_multi_penta", nullable = false)
  private byte pentaKills;

  @Column(name = "flash_aggressive")
  private byte flashAggressive;

  @Column(name = "time_alive", nullable = false)
  private short timeAlive;

  @Column(name = "time_dead", nullable = false)
  private short timeDead;

  @Column(name = "kills_teleport")
  private byte teleportKills;

  @Column(name = "immobilizations", columnDefinition = "TINYINT UNSIGNED")
  private short immobilizations;

  @Column(name = "wards_control", nullable = false)
  private byte controlWards;

  @Column(name = "wards_control_coverage", columnDefinition = "TINYINT UNSIGNED")
  private short controlWardUptime;

  @Column(name = "wards_placed", columnDefinition = "TINYINT UNSIGNED NOT NULL")
  private short wardsPlaced;

  @Column(name = "wards_cleared", columnDefinition = "TINYINT UNSIGNED NOT NULL")
  private short wardsCleared;

  @Column(name = "wards_guarded")
  private byte wardsGuarded;

  @Column(name = "visionscore", columnDefinition = "TINYINT UNSIGNED NOT NULL")
  private short visionScore;

  @Column(name = "visionscore_advantage")
  private byte visionscoreAdvantage;

  @Column(name = "objectives_stolen", nullable = false)
  private byte objectivesStolen;

  @Column(name = "firstturret_advantage")
  private short firstturretAdvantage;

  @Column(name = "objectives_damage", nullable = false)
  private int objectivesDamage;

  @Column(name = "baron_executes")
  private byte baronExecutes;

  @Column(name = "baron_kills", nullable = false)
  private byte baronKills;

  @Column(name = "buffs_stolen")
  private byte buffsStolen;

  @Check(constraints = "scuttles_initial IS NULL OR scuttles_initial <= 2")
  @Column(name = "scuttles_initial")
  private byte scuttlesInitial;

  @Column(name = "scuttles_total")
  private byte scuttlesTotal;

  @Check(constraints = "turrets_splitpushed IS NULL OR turrets_splitpushed <= 11")
  @Column(name = "turrets_splitpushed")
  private byte splitpushedTurrets;

  @Column(name = "team_invading")
  private byte teamInvading;

  @Column(name = "ganks_early")
  private byte ganksEarly;

  @Column(name = "ganks_total", nullable = false)
  private byte ganksTotal;

  @Column(name = "ganks_top", nullable = false)
  private byte ganksTop;

  @Column(name = "ganks_mid", nullable = false)
  private byte ganksMid;

  @Column(name = "ganks_bot", nullable = false)
  private byte ganksBot;

  @Column(name = "dives_done")
  private byte divesDone;

  @Column(name = "dives_successful")
  private byte divesSuccessful;

  @Column(name = "dives_gotten")
  private byte divesGotten;

  @Column(name = "dives_protected")
  private byte divesProtected;

  @Column(name = "gold_total", columnDefinition = "SMALLINT UNSIGNED NOT NULL")
  private int goldTotal;

  @Column(name = "gold_bounty")
  private short bountyGold;

  @Column(name = "experience_total", columnDefinition = "SMALLINT UNSIGNED NOT NULL")
  private int experience;

  @Column(name = "creeps_total", nullable = false)
  private short creepsTotal;

  @Column(name = "creeps_early", columnDefinition = "TINYINT UNSIGNED")
  private short creepsEarly;

  @Column(name = "creeps_invade", columnDefinition = "TINYINT UNSIGNED NOT NULL")
  private short creepsInvade;

  @Column(name = "early_lane_lead")
  private short earlyLaneLead;

  @Column(name = "lane_lead")
  private short laneLead;

  @Check(constraints = "turretplates IS NULL OR turretplates <= 15")
  @Column(name = "turretplates")
  private byte turretplates;

  @Column(name = "flamehorizon_advantage")
  private short flamehorizonAdvantage;

  @Column(name = "items_amount", nullable = false, columnDefinition = "TINYINT UNSIGNED NOT NULL")
  private short itemsAmount;

  @Column(name = "mejais_completed")
  private short mejaisCompleted;

  @Column(name = "first_blood", nullable = false)
  private boolean firstBlood;

  @Column(name = "outplayed_opponent")
  private byte outplayed;

  @Column(name = "turret_takedowns", nullable = false)
  private byte turretTakedowns;

  @Column(name = "dragon_takedowns")
  private byte dragonTakedowns;

  @Column(name = "fastest_legendary")
  private short fastestLegendary;

  @Column(name = "gank_setups")
  private byte gankSetups;

  @Check(constraints = "initial_buffs BETWEEN 0 and 2")
  @Column(name = "buffs_initial")
  private byte initialBuffs;

  @Column(name = "kills_early")
  private byte earlyKills;

  @Column(name = "objective_junglerkill")
  private byte junglerKillsAtObjective;

  @Column(name = "ambush_kill")
  private byte ambush;

  @Column(name = "turrets_early")
  private byte earlyTurrets;

  @Column(name = "experience_advantage")
  private byte levelLead;

  @Column(name = "pick_kill")
  private byte picksMade;

  @Column(name = "assassination")
  private byte assassinated;

  @Column(name = "guard_ally")
  private byte savedAlly;

  @Column(name = "survived_close")
  private byte survivedClose;

  @ManyToMany
  @JoinTable(name = "playerperformance_rune",
      joinColumns = @JoinColumn(name = "playerperformance"),
      inverseJoinColumns = @JoinColumn(name = "rune"),
      indexes = @Index(name = "idx_performancerune", columnList = "playerperformance, rune", unique = true))
  private final Set<Rune> runes = new LinkedHashSet<>();

  @OneToMany(mappedBy = "playerperformance")
  private final Set<PlayerperformanceSummonerspell> summonerspells = new LinkedHashSet<>();

  @OneToMany(mappedBy = "playerperformance")
  private final Set<PlayerperformanceItem> items = new LinkedHashSet<>();

  @OneToMany(mappedBy = "playerperformance")
  private final Set<PlayerperformanceInfo> infos = new LinkedHashSet<>();

  @OneToMany(mappedBy = "playerperformance")
  private final Set<PlayerperformanceKill> killEvents = new LinkedHashSet<>();

  @OneToMany(mappedBy = "playerperformance")
  private final Set<PlayerperformanceObjective> objectives = new LinkedHashSet<>();

  @OneToMany(mappedBy = "playerperformance")
  private final Set<PlayerperformanceLevel> levelups = new LinkedHashSet<>();

  /**
   * Stats that need other metrics and cannot be calculated
   * <p>
   * and Stats that need advanced calculation
   * <p>
   * Note: Don't implement stats that require multiple games
   */
  @Embedded
  private PlayerperformanceStats stats;

  public Playerperformance(Lane lane, short qUsages, short wUsages, short eUsages, short rUsages, int damageMagical, int damagePhysical,
                           int damageTotal, int damageTaken, int damageMitigated, int damageHealed, int damageShielded, byte kills,
                           byte deaths, byte assists, byte doubleKills, byte tripleKills, byte quadraKills, byte pentaKills,
                           short timeAlive, short timeDead, short wardsPlaced, byte objectivesStolen, int objectivesDamage,
                           byte baronKills, int goldTotal, int experience, short creepsTotal, short itemsAmount,
                           boolean firstBlood, byte controlWards, byte wardsCleared, short visionScore, byte turretTakedowns) {
    this.lane = lane;
    this.qUsages = qUsages;
    this.wUsages = wUsages;
    this.eUsages = eUsages;
    this.rUsages = rUsages;
    this.damageMagical = damageMagical;
    this.damagePhysical = damagePhysical;
    this.damageTotal = damageTotal;
    this.damageTaken = damageTaken;
    this.damageMitigated = damageMitigated;
    this.damageHealed = damageHealed;
    this.damageShielded = damageShielded;
    this.kills = kills;
    this.deaths = deaths;
    this.assists = assists;
    this.doubleKills = doubleKills;
    this.tripleKills = tripleKills;
    this.quadraKills = quadraKills;
    this.pentaKills = pentaKills;
    this.timeAlive = timeAlive;
    this.timeDead = timeDead;
    this.wardsPlaced = wardsPlaced;
    this.objectivesStolen = objectivesStolen;
    this.objectivesDamage = objectivesDamage;
    this.baronKills = baronKills;
    this.goldTotal = goldTotal;
    this.experience = experience;
    this.creepsTotal = creepsTotal;
    this.itemsAmount = itemsAmount;
    this.firstBlood = firstBlood;
    this.controlWards = controlWards;
    this.wardsCleared = wardsCleared;
    this.visionScore = visionScore;
    this.turretTakedowns = turretTakedowns;
  }

  public double getTimeIngame() {
    return timeDead / (teamperformance.getGame().getDuration() + 0.0);
  }

  public PlayerperformanceItem addItem(Item item, boolean remains, int buyTime) {
    val playerperformanceItem = new PlayerperformanceItem(this, item, remains);
    playerperformanceItem.setBuyTime(buyTime);
    return PlayerperformanceItem.get(playerperformanceItem);
  }

  public void addRune(Rune rune) {
    runes.add(rune);
    rune.getPlayerperformances().add(this);
  }

  public PlayerperformanceSummonerspell addSummonerspell(Summonerspell summonerspell, byte amount) {
    val performanceSpell = new PlayerperformanceSummonerspell(this, summonerspell, amount);
    return PlayerperformanceSummonerspell.get(performanceSpell);
  }

  public PlayerperformanceInfo addInfo(PlayerperformanceInfo info) {
    return PlayerperformanceInfo.get(info, this);
  }

  public PlayerperformanceKill addKill(PlayerperformanceKill kill) {
    return PlayerperformanceKill.get(kill, this);
  }

  public PlayerperformanceObjective addObjective(PlayerperformanceObjective objective) {
    return PlayerperformanceObjective.get(objective, this);
  }

  public PlayerperformanceLevel addLevelup(PlayerperformanceLevel levelup) {
    return PlayerperformanceLevel.get(levelup, this);
  }

  public PlayerperformanceItem getMythic() {
    return items.stream().filter(item -> item.getItem().getType().equals(ItemType.MYTHIC)).findFirst().orElse(null);
  }

  public PlayerperformanceItem getTrinket() {
    return items.stream().filter(item -> item.getItem().getType().equals(ItemType.TRINKET)).findFirst().orElse(null);
  }

  public Rune getKeystone() {
    return runes.stream().filter(rune -> rune.getSlot() == 0).findFirst().orElse(null);
  }

  public double getKillParticipation() {
    return (kills + assists) * 1d / teamperformance.getTotalKills();
  }

  public byte largestMultiKill() {
    if (pentaKills > 0) {
      return 5;
    } else if (quadraKills > 0) {
      return 4;
    } else if (tripleKills > 0) {
      return 3;
    } else if (doubleKills > 0) {
      return 2;
    } else if (kills > 0) {
      return 1;
    }
    return 0;
  }

  public double getKDA() {
    return (kills + assists) * 1d / deaths;
  }

  public short getAbilityUses() {
    return (short) (qUsages + wUsages + eUsages + rUsages);
  }

  public double getTankyShare() {
    return damageTaken * 1d / teamperformance.getTotalDamageTaken();
  }

  public byte countLegendaryItems() {
    return (byte) items.stream().filter(item -> item.getItem().getType().equals(ItemType.LEGENDARY)).count();
  }

  public boolean hasMythic() {
    return getMythic() != null;
  }

  public Gametype getGameType() {
    return teamperformance.getGame().getGametype();
  }

  public Playerperformance getLaneOpponent() {
    return teamperformance.getOtherTeamperformance().getPlayerperformances().stream()
        .filter(playerperformance -> playerperformance.getLane().equals(lane))
        .findFirst().orElse(null);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Playerperformance)) return false;
    final Playerperformance that = (Playerperformance) o;
    return getId() == that.getId() && qUsages == that.qUsages && wUsages == that.wUsages && eUsages == that.eUsages && rUsages == that.rUsages && getSpellsHit() == that.getSpellsHit() && getSpellsDodged() == that.getSpellsDodged() && getQuickDodged() == that.getQuickDodged() && getDamageMagical() == that.getDamageMagical() && getDamagePhysical() == that.getDamagePhysical() && getDamageTotal() == that.getDamageTotal() && getDamageTaken() == that.getDamageTaken() && getDamageMitigated() == that.getDamageMitigated() && getDamageHealed() == that.getDamageHealed() && getDamageShielded() == that.getDamageShielded() && getKills() == that.getKills() && getDeaths() == that.getDeaths() && getAssists() == that.getAssists() && getSoloKills() == that.getSoloKills() && getLevelUpAllin() == that.getLevelUpAllin() && getDoubleKills() == that.getDoubleKills() && getTripleKills() == that.getTripleKills() && getQuadraKills() == that.getQuadraKills() && getPentaKills() == that.getPentaKills() && getFlashAggressive() == that.getFlashAggressive() && getTimeAlive() == that.getTimeAlive() && getTimeDead() == that.getTimeDead() && getTeleportKills() == that.getTeleportKills() && getImmobilizations() == that.getImmobilizations() && getControlWards() == that.getControlWards() && getControlWardUptime() == that.getControlWardUptime() && getWardsPlaced() == that.getWardsPlaced() && getWardsCleared() == that.getWardsCleared() && getWardsGuarded() == that.getWardsGuarded() && getVisionScore() == that.getVisionScore() && getVisionscoreAdvantage() == that.getVisionscoreAdvantage() && getObjectivesStolen() == that.getObjectivesStolen() && getFirstturretAdvantage() == that.getFirstturretAdvantage() && getObjectivesDamage() == that.getObjectivesDamage() && getBaronExecutes() == that.getBaronExecutes() && getBaronKills() == that.getBaronKills() && getBuffsStolen() == that.getBuffsStolen() && getScuttlesInitial() == that.getScuttlesInitial() && getScuttlesTotal() == that.getScuttlesTotal() && getSplitpushedTurrets() == that.getSplitpushedTurrets() && getTeamInvading() == that.getTeamInvading() && getGanksEarly() == that.getGanksEarly() && getGanksTotal() == that.getGanksTotal() && getGanksTop() == that.getGanksTop() && getGanksMid() == that.getGanksMid() && getGanksBot() == that.getGanksBot() && getDivesDone() == that.getDivesDone() && getDivesSuccessful() == that.getDivesSuccessful() && getDivesGotten() == that.getDivesGotten() && getDivesProtected() == that.getDivesProtected() && getGoldTotal() == that.getGoldTotal() && getBountyGold() == that.getBountyGold() && getExperience() == that.getExperience() && getCreepsTotal() == that.getCreepsTotal() && getCreepsEarly() == that.getCreepsEarly() && getCreepsInvade() == that.getCreepsInvade() && getEarlyLaneLead() == that.getEarlyLaneLead() && getLaneLead() == that.getLaneLead() && getTurretplates() == that.getTurretplates() && getFlamehorizonAdvantage() == that.getFlamehorizonAdvantage() && getItemsAmount() == that.getItemsAmount() && getMejaisCompleted() == that.getMejaisCompleted() && isFirstBlood() == that.isFirstBlood() && getOutplayed() == that.getOutplayed() && getTurretTakedowns() == that.getTurretTakedowns() && getDragonTakedowns() == that.getDragonTakedowns() && getFastestLegendary() == that.getFastestLegendary() && getGankSetups() == that.getGankSetups() && getInitialBuffs() == that.getInitialBuffs() && getEarlyKills() == that.getEarlyKills() && getJunglerKillsAtObjective() == that.getJunglerKillsAtObjective() && getAmbush() == that.getAmbush() && getEarlyTurrets() == that.getEarlyTurrets() && getLevelLead() == that.getLevelLead() && getPicksMade() == that.getPicksMade() && getAssassinated() == that.getAssassinated() && getSavedAlly() == that.getSavedAlly() && getSurvivedClose() == that.getSurvivedClose() && getTeamperformance().equals(that.getTeamperformance()) && getAccount().equals(that.getAccount()) && getLane() == that.getLane() && getChampionOwn().equals(that.getChampionOwn()) && Objects.equals(getChampionEnemy(), that.getChampionEnemy()) && Objects.equals(getStats(), that.getStats());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getTeamperformance(), getAccount(), getLane(), getChampionOwn(), getChampionEnemy(), getQUsages(),
        getWUsages(), getEUsages(), getRUsages(), getSpellsHit(), getSpellsDodged(), getQuickDodged(), getDamageMagical(),
        getDamagePhysical(), getDamageTotal(), getDamageTaken(), getDamageMitigated(), getDamageHealed(), getDamageShielded(), getKills()
        , getDeaths(), getAssists(), getSoloKills(), getLevelUpAllin(), getDoubleKills(), getTripleKills(), getQuadraKills(),
        getPentaKills(), getFlashAggressive(), getTimeAlive(), getTimeDead(), getTeleportKills(), getImmobilizations(), getControlWards()
        , getControlWardUptime(), getWardsPlaced(), getWardsCleared(), getWardsGuarded(), getVisionScore(), getVisionscoreAdvantage(),
        getObjectivesStolen(), getFirstturretAdvantage(), getObjectivesDamage(), getBaronExecutes(), getBaronKills(), getBuffsStolen(),
        getScuttlesInitial(), getScuttlesTotal(), getSplitpushedTurrets(), getTeamInvading(), getGanksEarly(), getGanksTotal(),
        getGanksTop(), getGanksMid(), getGanksBot(), getDivesDone(), getDivesSuccessful(), getDivesGotten(), getDivesProtected(),
        getGoldTotal(), getBountyGold(), getExperience(), getCreepsTotal(), getCreepsEarly(), getCreepsInvade(), getEarlyLaneLead(),
        getLaneLead(), getTurretplates(), getFlamehorizonAdvantage(), getItemsAmount(), getMejaisCompleted(), isFirstBlood(),
        getOutplayed(), getTurretTakedowns(), getDragonTakedowns(), getFastestLegendary(), getGankSetups(), getInitialBuffs(),
        getEarlyKills(), getJunglerKillsAtObjective(), getAmbush(), getEarlyTurrets(), getLevelLead(), getPicksMade(), getAssassinated(),
        getSavedAlly(), getSurvivedClose(), getStats());
  }

  @Override
  public String toString() {
    return "Playerperformance{" +
        "id=" + id +
        ", teamperformance=" + teamperformance +
        ", account=" + account +
        ", lane=" + lane +
        ", championOwn=" + championOwn +
        ", championEnemy=" + championEnemy +
        ", qUsages=" + qUsages +
        ", wUsages=" + wUsages +
        ", eUsages=" + eUsages +
        ", rUsages=" + rUsages +
        ", spellsHit=" + spellsHit +
        ", spellsDodged=" + spellsDodged +
        ", quickDodged=" + quickDodged +
        ", damageMagical=" + damageMagical +
        ", damagePhysical=" + damagePhysical +
        ", damageTotal=" + damageTotal +
        ", damageTaken=" + damageTaken +
        ", damageMitigated=" + damageMitigated +
        ", damageHealed=" + damageHealed +
        ", damageShielded=" + damageShielded +
        ", kills=" + kills +
        ", deaths=" + deaths +
        ", assists=" + assists +
        ", killsSolo=" + soloKills +
        ", allinLevelup=" + levelUpAllin +
        ", doubleKills=" + doubleKills +
        ", tripleKills=" + tripleKills +
        ", quadraKills=" + quadraKills +
        ", pentaKills=" + pentaKills +
        ", flashAggressive=" + flashAggressive +
        ", timeAlive=" + timeAlive +
        ", timeDead=" + timeDead +
        ", teleportKills=" + teleportKills +
        ", immobilizations=" + immobilizations +
        ", controlWards=" + controlWards +
        ", controlWardUptime=" + controlWardUptime +
        ", wardsPlaced=" + wardsPlaced +
        ", wardsCleared=" + wardsCleared +
        ", wardsGuarded=" + wardsGuarded +
        ", visionScore=" + visionScore +
        ", visionscoreAdvantage=" + visionscoreAdvantage +
        ", objectivesStolen=" + objectivesStolen +
        ", firstturretAdvantage=" + firstturretAdvantage +
        ", objectivesDamage=" + objectivesDamage +
        ", baronExecutes=" + baronExecutes +
        ", baronKills=" + baronKills +
        ", buffsStolen=" + buffsStolen +
        ", scuttlesInitial=" + scuttlesInitial +
        ", scuttlesTotal=" + scuttlesTotal +
        ", splitpushedTurrets=" + splitpushedTurrets +
        ", teamInvading=" + teamInvading +
        ", ganksEarly=" + ganksEarly +
        ", ganksTotal=" + ganksTotal +
        ", ganksTop=" + ganksTop +
        ", ganksMid=" + ganksMid +
        ", ganksBot=" + ganksBot +
        ", divesDone=" + divesDone +
        ", divesSuccessful=" + divesSuccessful +
        ", divesGotten=" + divesGotten +
        ", divesProtected=" + divesProtected +
        ", goldTotal=" + goldTotal +
        ", bountyGold=" + bountyGold +
        ", experience=" + experience +
        ", creepsTotal=" + creepsTotal +
        ", creepsEarly=" + creepsEarly +
        ", creepsInvade=" + creepsInvade +
        ", earlyLaneLead=" + earlyLaneLead +
        ", laneLead=" + laneLead +
        ", turretplates=" + turretplates +
        ", flamehorizonAdvantage=" + flamehorizonAdvantage +
        ", itemsAmount=" + itemsAmount +
        ", mejaisCompleted=" + mejaisCompleted +
        ", firstBlood=" + firstBlood +
        ", outplayed=" + outplayed +
        ", turretTakedowns=" + turretTakedowns +
        ", dragonTakedowns=" + dragonTakedowns +
        ", fastestLegendary=" + fastestLegendary +
        ", gankSetups=" + gankSetups +
        ", initialBuffs=" + initialBuffs +
        ", earlyKills=" + earlyKills +
        ", junglerKillsAtObjective=" + junglerKillsAtObjective +
        ", ambush=" + ambush +
        ", earlyTurrets=" + earlyTurrets +
        ", levelLead=" + levelLead +
        ", picksMade=" + picksMade +
        ", assassinated=" + assassinated +
        ", savedAlly=" + savedAlly +
        ", survivedClose=" + survivedClose +
        ", stats=" + stats +
        '}';
  }

  // TODO: 12.04.2022 Group stats in embeddables

}