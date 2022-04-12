package de.xeri.league.models.match;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
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
import org.hibernate.annotations.Check;

@Entity(name = "Playerperformance")
@Table(name = "playerperformance", indexes = {
    @Index(name = "idx_playerperformance_player", columnList = "teamperformance, account", unique = true),
    @Index(name = "champion_enemy", columnList = "champion_enemy"),
    @Index(name = "idx_playerperformance_lane", columnList = "teamperformance, lane", unique = true),
    @Index(name = "champion_own", columnList = "champion_own"),
    @Index(name = "account", columnList = "account"),
})
public class Playerperformance implements Serializable {

  @Transient
  private static final long serialVersionUID = 8351390618689373240L;

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

  @Column(name = "damage_team_dealed", precision = 9, scale = 8)
  private BigDecimal damageTeamDealed;

  @Column(name = "damage_team_taken", precision = 9, scale = 8)
  private BigDecimal damageTeamTaken;

  @Column(name = "kills", nullable = false)
  private byte kills;

  @Column(name = "deaths", nullable = false)
  private byte deaths;

  @Column(name = "assists", nullable = false)
  private byte assists;

  @Column(name = "kills_solo")
  private byte killsSolo;

  @Check(constraints = "allin_levelup IS NULL OR allin_levelup < 18")
  @Column(name = "allin_levelup")
  private byte allinLevelup;

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
  private byte killsTeleport;

  @Column(name = "immobilizations", columnDefinition = "TINYINT UNSIGNED")
  private short immobilizations;

  @Column(name = "wards_control", nullable = false)
  private byte wardsControl;

  @Column(name = "wards_control_coverage", columnDefinition = "TINYINT UNSIGNED")
  private short wardsControlCoverage;

  @Column(name = "wards_placed", columnDefinition = "TINYINT UNSIGNED NOT NULL")
  private short wardsPlaced;

  @Column(name = "wards_cleared", columnDefinition = "TINYINT UNSIGNED NIT NULL")
  private short wardsCleared;

  @Column(name = "wards_guarded")
  private byte wardsGuarded;

  @Column(name = "visionscore_advantage", nullable = false)
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
  private byte turretsSplitpushed;

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

  @Column(name = "dives_protected")
  private byte divesProtected;

  @Column(name = "gold_total", nullable = false)
  private short goldTotal;

  @Column(name = "gold_bounty")
  private short goldBounty;

  @Column(name = "experience_total", nullable = false, columnDefinition = "SMALLINT UNSIGNED")
  private int experienceTotal;

  @Column(name = "creeps_total", nullable = false)
  private short creepsTotal;

  @Column(name = "creeps_early", columnDefinition = "TINYINT UNSIGNED")
  private short creepsEarly;

  @Column(name = "creeps_invade", nullable = false, columnDefinition = "TINYINT UNSIGNED NOT NULL")
  private short creepsInvade;

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

  @Column(name = "first_blood")
  private boolean firstBlood;

  @ManyToMany
  @JoinTable(name = "playerperformance_rune",
      joinColumns = @JoinColumn(name = "playerperformance"),
      inverseJoinColumns = @JoinColumn(name = "rune"),
      indexes = @Index(name = "idx_performancerune", columnList = "playerperformance, rune", unique = true))
  private final Set<Rune> runes = new LinkedHashSet<>();

  @OneToMany(mappedBy = "playerperformance")
  private final Set<PlayerperformanceSummonerspell> summonerspells = new LinkedHashSet<>();

  @ManyToMany
  @JoinTable(name = "playerperformance_item",
      joinColumns = @JoinColumn(name = "playerperformance"),
      inverseJoinColumns = @JoinColumn(name = "item"),
      indexes = @Index(name = "idx_performanceitem", columnList = "playerperformance, item", unique = true))
  private final Set<Item> items = new LinkedHashSet<>();

  // default constructor
  public Playerperformance() {
  }

  public Playerperformance(Lane lane, short qUsages, short wUsages, short eUsages, short rUsages, int damageMagical, int damagePhysical,
                           int damageTotal, int damageTaken, int damageMitigated, int damageHealed, int damageShielded, byte kills,
                           byte deaths, byte assists, byte doubleKills, byte tripleKills, byte quadraKills, byte pentaKills,
                           short timeAlive, short timeDead, short wardsPlaced, byte objectivesStolen, int objectivesDamage,
                           byte baronKills, short goldTotal, int experienceTotal, short creepsTotal, short itemsAmount,
                           boolean firstBlood, byte wardsControl, byte wardsCleared, byte visionScoreAdvantage) {
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
    this.experienceTotal = experienceTotal;
    this.creepsTotal = creepsTotal;
    this.itemsAmount = itemsAmount;
    this.firstBlood = firstBlood;
    this.wardsControl = wardsControl;
    this.wardsCleared = wardsCleared;
    this.visionscoreAdvantage = visionScoreAdvantage;
  }

  public double timeIngame() {
    return timeDead / (teamperformance.getGame().getDuration() + 0.0);
  }

  public void addItem(Item item) {
    items.add(item);
    item.getPlayerperformances().add(this);
  }

  public void addRune(Rune rune) {
    runes.add(rune);
    rune.getPlayerperformances().add(this);
  }

  public void addSummonerspell(Summonerspell summonerspell, byte amount) {
    final PlayerperformanceSummonerspell spell = PlayerperformanceSummonerspell.get(new PlayerperformanceSummonerspell(this, summonerspell, amount));
  }

  public Item getMythic() {
    return items.stream().filter(item -> item.getItemtype().equals(ItemType.MYTHIC))
        .findFirst().orElse(null);
  }

  public Item getTrinket() {
    return items.stream().filter(item -> item.getItemtype().equals(ItemType.TRINKET))
        .findFirst().orElse(null);
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

  //<editor-fold desc="getter and setter">
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Teamperformance getTeamperformance() {
    return teamperformance;
  }

  public void setTeamperformance(Teamperformance teamperformance) {
    this.teamperformance = teamperformance;
  }

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  public Lane getLane() {
    return lane;
  }

  public void setLane(Lane lane) {
    this.lane = lane;
  }

  public Champion getChampionOwn() {
    return championOwn;
  }

  public void setChampionOwn(Champion championOwn) {
    this.championOwn = championOwn;
  }

  public Champion getChampionEnemy() {
    return championEnemy;
  }

  public void setChampionEnemy(Champion championEnemy) {
    this.championEnemy = championEnemy;
  }

  public short getqUsages() {
    return qUsages;
  }

  public void setqUsages(short qUsages) {
    this.qUsages = qUsages;
  }

  public short getwUsages() {
    return wUsages;
  }

  public void setwUsages(short wUsages) {
    this.wUsages = wUsages;
  }

  public short geteUsages() {
    return eUsages;
  }

  public void seteUsages(short eUsages) {
    this.eUsages = eUsages;
  }

  public short getrUsages() {
    return rUsages;
  }

  public void setrUsages(short rUsages) {
    this.rUsages = rUsages;
  }

  public short getSpellsHit() {
    return spellsHit;
  }

  public void setSpellsHit(short spellsHit) {
    this.spellsHit = spellsHit;
  }

  public short getSpellsDodged() {
    return spellsDodged;
  }

  public void setSpellsDodged(short spellsDodged) {
    this.spellsDodged = spellsDodged;
  }

  public short getQuickDodged() {
    return quickDodged;
  }

  public void setQuickDodged(short quickDodged) {
    this.quickDodged = quickDodged;
  }

  public int getDamageMagical() {
    return damageMagical;
  }

  public void setDamageMagical(int damageMagical) {
    this.damageMagical = damageMagical;
  }

  public int getDamagePhysical() {
    return damagePhysical;
  }

  public void setDamagePhysical(int damagePhysical) {
    this.damagePhysical = damagePhysical;
  }

  public int getDamageTotal() {
    return damageTotal;
  }

  public void setDamageTotal(int damageTotal) {
    this.damageTotal = damageTotal;
  }

  public int getDamageTaken() {
    return damageTaken;
  }

  public void setDamageTaken(int damageTaken) {
    this.damageTaken = damageTaken;
  }

  public int getDamageMitigated() {
    return damageMitigated;
  }

  public void setDamageMitigated(int damageMitigated) {
    this.damageMitigated = damageMitigated;
  }

  public int getDamageHealed() {
    return damageHealed;
  }

  public void setDamageHealed(int damageHealed) {
    this.damageHealed = damageHealed;
  }

  public int getDamageShielded() {
    return damageShielded;
  }

  public void setDamageShielded(int damageShielded) {
    this.damageShielded = damageShielded;
  }

  public double getDamageTeamDealed() {
    return damageTeamDealed.doubleValue();
  }

  public void setDamageTeamDealed(double damageTeamDealed) {
    this.damageTeamDealed = new BigDecimal(damageTeamDealed);
  }

  public double getDamageTeamTaken() {
    return damageTeamTaken.doubleValue();
  }

  public void setDamageTeamTaken(double damageTeamTaken) {
    this.damageTeamTaken = new BigDecimal(damageTeamTaken);
  }

  public byte getKills() {
    return kills;
  }

  public void setKills(byte kills) {
    this.kills = kills;
  }

  public byte getDeaths() {
    return deaths;
  }

  public void setDeaths(byte deaths) {
    this.deaths = deaths;
  }

  public byte getAssists() {
    return assists;
  }

  public void setAssists(byte assists) {
    this.assists = assists;
  }

  public byte getKillsSolo() {
    return killsSolo;
  }

  public void setKillsSolo(byte killsSolo) {
    this.killsSolo = killsSolo;
  }

  public byte getAllinLevelup() {
    return allinLevelup;
  }

  public void setAllinLevelup(byte allinLevelup) {
    this.allinLevelup = allinLevelup;
  }

  public byte getDoubleKills() {
    return doubleKills;
  }

  public void setDoubleKills(byte killsMultiDouble) {
    this.doubleKills = killsMultiDouble;
  }

  public byte getTripleKills() {
    return tripleKills;
  }

  public void setTripleKills(byte killsMultiTriple) {
    this.tripleKills = killsMultiTriple;
  }

  public byte getQuadraKills() {
    return quadraKills;
  }

  public void setQuadraKills(byte killsMultiQuadra) {
    this.quadraKills = killsMultiQuadra;
  }

  public byte getPentaKills() {
    return pentaKills;
  }

  public void setPentaKills(byte killsMultiPenta) {
    this.pentaKills = killsMultiPenta;
  }

  public byte getFlashAggressive() {
    return flashAggressive;
  }

  public void setFlashAggressive(byte flashAggressive) {
    this.flashAggressive = flashAggressive;
  }

  public short getTimeAlive() {
    return timeAlive;
  }

  public void setTimeAlive(short timeAlive) {
    this.timeAlive = timeAlive;
  }

  public short getTimeDead() {
    return timeDead;
  }

  public void setTimeDead(short timeDead) {
    this.timeDead = timeDead;
  }

  public byte getKillsTeleport() {
    return killsTeleport;
  }

  public void setKillsTeleport(byte killsTeleport) {
    this.killsTeleport = killsTeleport;
  }

  public short getImmobilizations() {
    return immobilizations;
  }

  public void setImmobilizations(short immobilizations) {
    this.immobilizations = immobilizations;
  }

  public byte getWardsControl() {
    return wardsControl;
  }

  public void setWardsControl(byte wardsControl) {
    this.wardsControl = wardsControl;
  }

  public short getWardsControlCoverage() {
    return wardsControlCoverage;
  }

  public void setWardsControlCoverage(short wardsControlCoverage) {
    this.wardsControlCoverage = wardsControlCoverage;
  }

  public short getWardsPlaced() {
    return wardsPlaced;
  }

  public void setWardsPlaced(short wardsPlaced) {
    this.wardsPlaced = wardsPlaced;
  }

  public short getWardsCleared() {
    return wardsCleared;
  }

  public void setWardsCleared(short wardsCleared) {
    this.wardsCleared = wardsCleared;
  }

  public byte getWardsGuarded() {
    return wardsGuarded;
  }

  public void setWardsGuarded(byte wardsGuarded) {
    this.wardsGuarded = wardsGuarded;
  }

  public byte getVisionscoreAdvantage() {
    return visionscoreAdvantage;
  }

  public void setVisionscoreAdvantage(byte visionscoreAdvantage) {
    this.visionscoreAdvantage = visionscoreAdvantage;
  }

  public byte getObjectivesStolen() {
    return objectivesStolen;
  }

  public void setObjectivesStolen(byte objectivesStolen) {
    this.objectivesStolen = objectivesStolen;
  }

  public short getFirstturretAdvantage() {
    return firstturretAdvantage;
  }

  public void setFirstturretAdvantage(short firstturretAdvantage) {
    this.firstturretAdvantage = firstturretAdvantage;
  }

  public int getObjectivesDamage() {
    return objectivesDamage;
  }

  public void setObjectivesDamage(int objectivesDamage) {
    this.objectivesDamage = objectivesDamage;
  }

  public byte getBaronExecutes() {
    return baronExecutes;
  }

  public void setBaronExecutes(byte baronExecutes) {
    this.baronExecutes = baronExecutes;
  }

  public byte getBaronKills() {
    return baronKills;
  }

  public void setBaronKills(byte baronKills) {
    this.baronKills = baronKills;
  }

  public byte getBuffsStolen() {
    return buffsStolen;
  }

  public void setBuffsStolen(byte buffsStolen) {
    this.buffsStolen = buffsStolen;
  }

  public byte getScuttlesInitial() {
    return scuttlesInitial;
  }

  public void setScuttlesInitial(byte scuttlesInitial) {
    this.scuttlesInitial = scuttlesInitial;
  }

  public byte getScuttlesTotal() {
    return scuttlesTotal;
  }

  public void setScuttlesTotal(byte scuttlesTotal) {
    this.scuttlesTotal = scuttlesTotal;
  }

  public byte getTurretsSplitpushed() {
    return turretsSplitpushed;
  }

  public void setTurretsSplitpushed(byte turretsSplitpushed) {
    this.turretsSplitpushed = turretsSplitpushed;
  }

  public byte getTeamInvading() {
    return teamInvading;
  }

  public void setTeamInvading(byte teamInvading) {
    this.teamInvading = teamInvading;
  }

  public byte getGanksEarly() {
    return ganksEarly;
  }

  public void setGanksEarly(byte ganksEarly) {
    this.ganksEarly = ganksEarly;
  }

  public byte getGanksTotal() {
    return ganksTotal;
  }

  public void setGanksTotal(byte ganksTotal) {
    this.ganksTotal = ganksTotal;
  }

  public byte getGanksTop() {
    return ganksTop;
  }

  public void setGanksTop(byte ganksTop) {
    this.ganksTop = ganksTop;
  }

  public byte getGanksMid() {
    return ganksMid;
  }

  public void setGanksMid(byte ganksMid) {
    this.ganksMid = ganksMid;
  }

  public byte getGanksBot() {
    return ganksBot;
  }

  public void setGanksBot(byte ganksBot) {
    this.ganksBot = ganksBot;
  }

  public byte getDivesDone() {
    return divesDone;
  }

  public void setDivesDone(byte divesDone) {
    this.divesDone = divesDone;
  }

  public byte getDivesProtected() {
    return divesProtected;
  }

  public void setDivesProtected(byte divesProtected) {
    this.divesProtected = divesProtected;
  }

  public short getGoldTotal() {
    return goldTotal;
  }

  public void setGoldTotal(short goldTotal) {
    this.goldTotal = goldTotal;
  }

  public short getGoldBounty() {
    return goldBounty;
  }

  public void setGoldBounty(short goldBounty) {
    this.goldBounty = goldBounty;
  }

  public int getExperienceTotal() {
    return experienceTotal;
  }

  public void setExperienceTotal(int experienceTotal) {
    this.experienceTotal = experienceTotal;
  }

  public short getCreepsTotal() {
    return creepsTotal;
  }

  public void setCreepsTotal(short creepsTotal) {
    this.creepsTotal = creepsTotal;
  }

  public short getCreepsEarly() {
    return creepsEarly;
  }

  public void setCreepsEarly(short creepsEarly) {
    this.creepsEarly = creepsEarly;
  }

  public short getCreepsInvade() {
    return creepsInvade;
  }

  public void setCreepsInvade(short creepsInvade) {
    this.creepsInvade = creepsInvade;
  }

  public short getLaneLead() {
    return laneLead;
  }

  public void setLaneLead(short laneLead) {
    this.laneLead = laneLead;
  }

  public byte getTurretplates() {
    return turretplates;
  }

  public void setTurretplates(byte turretplates) {
    this.turretplates = turretplates;
  }

  public short getFlamehorizonAdvantage() {
    return flamehorizonAdvantage;
  }

  public void setFlamehorizonAdvantage(short flamehorizonAdvantage) {
    this.flamehorizonAdvantage = flamehorizonAdvantage;
  }

  public short getItemsAmount() {
    return itemsAmount;
  }

  public void setItemsAmount(short itemsAmount) {
    this.itemsAmount = itemsAmount;
  }

  public short getMejaisCompleted() {
    return mejaisCompleted;
  }

  public void setMejaisCompleted(short mejaisCompleted) {
    this.mejaisCompleted = mejaisCompleted;
  }

  public Set<Rune> getRunes() {
    return runes;
  }

  public Set<PlayerperformanceSummonerspell> getSummonerspells() {
    return summonerspells;
  }

  public Set<Item> getItems() {
    return items;
  }

  public boolean isFirstBlood() {
    return firstBlood;
  }

  public void setFirstBlood(boolean firstBlood) {
    this.firstBlood = firstBlood;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Playerperformance)) return false;
    final Playerperformance playerperformance = (Playerperformance) o;
    return getId() == playerperformance.getId() && getqUsages() == playerperformance.getqUsages() && getwUsages() == playerperformance.getwUsages() && geteUsages() == playerperformance.geteUsages() && getrUsages() == playerperformance.getrUsages() && getSpellsHit() == playerperformance.getSpellsHit() && getSpellsDodged() == playerperformance.getSpellsDodged() && getQuickDodged() == playerperformance.getQuickDodged() && getDamageMagical() == playerperformance.getDamageMagical() && getDamagePhysical() == playerperformance.getDamagePhysical() && getDamageTotal() == playerperformance.getDamageTotal() && getDamageTaken() == playerperformance.getDamageTaken() && getDamageMitigated() == playerperformance.getDamageMitigated() && getDamageHealed() == playerperformance.getDamageHealed() && getDamageShielded() == playerperformance.getDamageShielded() && Double.compare(playerperformance.getDamageTeamDealed(), getDamageTeamDealed()) == 0 && Double.compare(playerperformance.getDamageTeamTaken(), getDamageTeamTaken()) == 0 && getKills() == playerperformance.getKills() && getDeaths() == playerperformance.getDeaths() && getAssists() == playerperformance.getAssists() && getKillsSolo() == playerperformance.getKillsSolo() && getAllinLevelup() == playerperformance.getAllinLevelup() && getDoubleKills() == playerperformance.getDoubleKills() && getTripleKills() == playerperformance.getTripleKills() && getQuadraKills() == playerperformance.getQuadraKills() && getPentaKills() == playerperformance.getPentaKills() && getFlashAggressive() == playerperformance.getFlashAggressive() && getTimeAlive() == playerperformance.getTimeAlive() && getTimeDead() == playerperformance.getTimeDead() && getKillsTeleport() == playerperformance.getKillsTeleport() && getImmobilizations() == playerperformance.getImmobilizations() && getWardsControl() == playerperformance.getWardsControl() && getWardsControlCoverage() == playerperformance.getWardsControlCoverage() && getWardsPlaced() == playerperformance.getWardsPlaced() && getWardsCleared() == playerperformance.getWardsCleared() && getWardsGuarded() == playerperformance.getWardsGuarded() && Double.compare(playerperformance.getVisionscoreAdvantage(), getVisionscoreAdvantage()) == 0 && getObjectivesStolen() == playerperformance.getObjectivesStolen() && getFirstturretAdvantage() == playerperformance.getFirstturretAdvantage() && getObjectivesDamage() == playerperformance.getObjectivesDamage() && getBaronExecutes() == playerperformance.getBaronExecutes() && getBaronKills() == playerperformance.getBaronKills() && getBuffsStolen() == playerperformance.getBuffsStolen() && getScuttlesInitial() == playerperformance.getScuttlesInitial() && getScuttlesTotal() == playerperformance.getScuttlesTotal() && getTurretsSplitpushed() == playerperformance.getTurretsSplitpushed() && getTeamInvading() == playerperformance.getTeamInvading() && getGanksEarly() == playerperformance.getGanksEarly() && getGanksTotal() == playerperformance.getGanksTotal() && getGanksTop() == playerperformance.getGanksTop() && getGanksMid() == playerperformance.getGanksMid() && getGanksBot() == playerperformance.getGanksBot() && getDivesDone() == playerperformance.getDivesDone() && getDivesProtected() == playerperformance.getDivesProtected() && getGoldTotal() == playerperformance.getGoldTotal() && getGoldBounty() == playerperformance.getGoldBounty() && getExperienceTotal() == playerperformance.getExperienceTotal() && getCreepsTotal() == playerperformance.getCreepsTotal() && getCreepsEarly() == playerperformance.getCreepsEarly() && getCreepsInvade() == playerperformance.getCreepsInvade() && getLaneLead() == playerperformance.getLaneLead() && getTurretplates() == playerperformance.getTurretplates() && getFlamehorizonAdvantage() == playerperformance.getFlamehorizonAdvantage() && getItemsAmount() == playerperformance.getItemsAmount() && getMejaisCompleted() == playerperformance.getMejaisCompleted() && getTeamperformance().equals(playerperformance.getTeamperformance()) && getAccount().equals(playerperformance.getAccount()) && getLane() == playerperformance.getLane() && getChampionOwn().equals(playerperformance.getChampionOwn()) && Objects.equals(getChampionEnemy(), playerperformance.getChampionEnemy()) && getRunes().equals(playerperformance.getRunes()) && getSummonerspells().equals(playerperformance.getSummonerspells()) && getItems().equals(playerperformance.getItems()) && isFirstBlood() == playerperformance.isFirstBlood();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getTeamperformance(), getAccount(), getLane(), getChampionOwn(), getChampionEnemy(), getqUsages(),
        getwUsages(), geteUsages(), getrUsages(), getSpellsHit(), getSpellsDodged(), getQuickDodged(), getDamageMagical(),
        getDamagePhysical(), getDamageTotal(), getDamageTaken(), getDamageMitigated(), getDamageHealed(), getDamageShielded(),
        getDamageTeamDealed(), getDamageTeamTaken(), getKills(), getDeaths(), getAssists(), getKillsSolo(),
        getAllinLevelup(), getDoubleKills(), getTripleKills(), getQuadraKills(), getPentaKills(), getFlashAggressive(), getTimeAlive(),
        getTimeDead(), getKillsTeleport(), getImmobilizations(), getWardsControl(), getWardsControlCoverage(), getWardsPlaced(),
        getWardsCleared(), getWardsGuarded(), getVisionscoreAdvantage(), getObjectivesStolen(), getFirstturretAdvantage(),
        getObjectivesDamage(), getBaronExecutes(), getBaronKills(), getBuffsStolen(), getScuttlesInitial(), getScuttlesTotal(),
        getTurretsSplitpushed(), getTeamInvading(), getGanksEarly(), getGanksTotal(), getGanksTop(), getGanksMid(), getGanksBot(),
        getDivesDone(), getDivesProtected(), getGoldTotal(), getGoldBounty(), getExperienceTotal(), getCreepsTotal(), getCreepsEarly(),
        getCreepsInvade(), getLaneLead(), getTurretplates(), getFlamehorizonAdvantage(), getItemsAmount(), getMejaisCompleted(),
        getRunes(), getSummonerspells(), getItems(), isFirstBlood());
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
        ", damageTeamDealed=" + damageTeamDealed +
        ", damageTeamTaken=" + damageTeamTaken +
        ", kills=" + kills +
        ", deaths=" + deaths +
        ", assists=" + assists +
        ", killsSolo=" + killsSolo +
        ", allinLevelup=" + allinLevelup +
        ", killsMultiDouble=" + doubleKills +
        ", killsMultiTriple=" + tripleKills +
        ", killsMultiQuadra=" + quadraKills +
        ", killsMultiPenta=" + pentaKills +
        ", flashAggressive=" + flashAggressive +
        ", timeAlive=" + timeAlive +
        ", timeDead=" + timeDead +
        ", killsTeleport=" + killsTeleport +
        ", immobilizations=" + immobilizations +
        ", wardsControl=" + wardsControl +
        ", wardsControlCoverage=" + wardsControlCoverage +
        ", wardsPlaced=" + wardsPlaced +
        ", wardsCleared=" + wardsCleared +
        ", wardsGuarded=" + wardsGuarded +
        ", visionscoreAdvantage=" + visionscoreAdvantage +
        ", objectivesStolen=" + objectivesStolen +
        ", firstturretAdvantage=" + firstturretAdvantage +
        ", objectivesDamage=" + objectivesDamage +
        ", baronExecutes=" + baronExecutes +
        ", baronKills=" + baronKills +
        ", buffsStolen=" + buffsStolen +
        ", scuttlesInitial=" + scuttlesInitial +
        ", scuttlesTotal=" + scuttlesTotal +
        ", turretsSplitpushed=" + turretsSplitpushed +
        ", teamInvading=" + teamInvading +
        ", ganksEarly=" + ganksEarly +
        ", ganksTotal=" + ganksTotal +
        ", ganksTop=" + ganksTop +
        ", ganksMid=" + ganksMid +
        ", ganksBot=" + ganksBot +
        ", divesDone=" + divesDone +
        ", divesProtected=" + divesProtected +
        ", goldTotal=" + goldTotal +
        ", goldBounty=" + goldBounty +
        ", experienceTotal=" + experienceTotal +
        ", creepsTotal=" + creepsTotal +
        ", creepsEarly=" + creepsEarly +
        ", creepsInvade=" + creepsInvade +
        ", laneLead=" + laneLead +
        ", turretplates=" + turretplates +
        ", flamehorizonAdvantage=" + flamehorizonAdvantage +
        ", itemsAmount=" + itemsAmount +
        ", mejaisCompleted=" + mejaisCompleted +
        ", firstBlood=" + firstBlood +
        '}';
  }
  //</editor-fold>

}