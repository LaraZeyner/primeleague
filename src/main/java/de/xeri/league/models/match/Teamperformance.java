package de.xeri.league.models.match;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.league.models.enums.DragonSoul;
import de.xeri.league.models.league.Account;
import de.xeri.league.models.league.Team;
import de.xeri.league.util.Data;
import de.xeri.league.util.Util;
import org.hibernate.annotations.Check;

@Entity(name = "Teamperformance")
@Table(name = "teamperformance", indexes = {
    @Index(name = "idx_teamperformance_win", columnList = "game, win", unique = true),
    @Index(name = "idx_teamperformance_side", columnList = "game, first_pick", unique = true),
    @Index(name = "team", columnList = "team")
})
public class Teamperformance implements Serializable {

  @Transient
  private static final long serialVersionUID = 8274298970011471960L;

  private static Set<Teamperformance> data;

  public static void save() {
    if (data != null) data.forEach(Data.getInstance().getSession()::saveOrUpdate);
  }

  public static Set<Teamperformance> get() {
    if (data == null) data = new LinkedHashSet<>((List<Teamperformance>) Util.query("Teamperformance"));
    return data;
  }

  public static Teamperformance get(Teamperformance neu, Game game, Team team) {
    get();
    if (find(game, neu.isFirstPick()) == null) {
      game.getTeamperformances().add(neu);
      neu.setGame(game);
      if (team != null) {
        team.getTeamperformances().add(neu);
        neu.setTeam(team);
      }
      data.add(neu);
    }
    return find(game, neu.isFirstPick());
  }

  public static Teamperformance find(Game game, boolean firstPick) {
    get();
    return data.stream().filter(entry -> entry.getGame().equals(game) && entry.isFirstPick() == firstPick).findFirst().orElse(null);
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "teamperformance_id", nullable = false)
  private int id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "game")
  private Game game;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "team")
  private Team team;

  @Column(name = "first_pick", nullable = false)
  private boolean firstPick;

  @Column(name = "win", nullable = false)
  private boolean win;

  @Column(name = "total_damage", nullable = false)
  private int totalDamage;

  @Column(name = "total_damage_taken", nullable = false)
  private int totalDamageTaken;

  @Column(name = "total_gold", nullable = false)
  private int totalGold;

  @Column(name = "total_cs", nullable = false)
  private short totalCs;

  @Column(name = "total_kills", nullable = false, columnDefinition = "TINYINT(3) UNSIGNED NOT NULL")
  private short totalKills;

  @Check(constraints = "towers <= 11")
  @Column(name = "towers", nullable = false)
  private byte towers;

  @Column(name = "drakes", nullable = false)
  private byte drakes;

  @Column(name = "inhibs", nullable = false)
  private byte inhibs;

  @Check(constraints = "heralds <= 2")
  @Column(name = "heralds", nullable = false)
  private byte heralds;

  @Column(name = "barons", nullable = false)
  private byte barons;

  @Column(name = "first_tower", nullable = false)
  private boolean firstTower;

  @Column(name = "first_drake", nullable = false)
  private boolean firstDrake;

  @Column(name = "perfect_soul")
  private boolean perfectSoul;

  @Check(constraints = "rift_turrets < 5")
  @Column(name = "rift_turrets", precision = 2, scale = 1)
  private BigDecimal riftTurrets;

  @Column(name = "elder_time")
  private short elderTime;

  @Column(name = "baron_powerplay")
  private short baronPowerplay;

  @Column(name = "surrender")
  private boolean surrendered;

  @Column(name = "ace_before_15")
  private byte earlyAces;

  @Column(name = "baron_time")
  private short baronTime;

  @Column(name = "dragon_time")
  private short firstDragonTime;

  @Column(name = "objective_onspawn")
  private byte objectiveAtSpawn;

  @Column(name = "objective_contest")
  private byte objectiveContests;

  @Column(name = "support_quest")
  private boolean questCompletedFirst;

  @Column(name = "inhibitors_time")
  private short inhibitorsTime;

  @Column(name = "ace_flawless")
  private byte flawlessAce;

  @Column(name = "rift_multiturret")
  private byte riftOnMultipleTurrets;

  @Column(name = "ace_fastest")
  private short fastestAcetime;

  @Column(name = "kills_deficit")
  private byte killDeficit;

  @Column(name = "dragon_soul")
  @Enumerated(EnumType.STRING)
  private DragonSoul soul;

  @OneToMany(mappedBy = "teamperformance")
  private final Set<Playerperformance> playerperformances = new LinkedHashSet<>();

  @OneToMany(mappedBy = "teamperformance")
  private final Set<TeamperformanceBounty> bounties = new LinkedHashSet<>();

  // TODO: 14.04.2022 Determine deprecated attributes + remove
  // default constructor
  public Teamperformance() {
  }

  public Teamperformance(boolean firstPick, boolean win, int totalDamage, int totalDamageTaken, int totalGold, int totalCs, int totalKills,
                         int towers, int drakes, int inhibs, int heralds, int barons, boolean firstTower, boolean firstDrake) {
    this.firstPick = firstPick;
    this.win = win;
    this.totalDamage = totalDamage;
    this.totalDamageTaken = totalDamageTaken;
    this.totalGold = totalGold;
    this.totalCs = (short) totalCs;
    this.totalKills = (short) totalKills;
    this.towers = (byte) towers;
    this.drakes = (byte) drakes;
    this.inhibs = (byte) inhibs;
    this.heralds = (byte) heralds;
    this.barons = (byte) barons;
    this.firstTower = firstTower;
    this.firstDrake = firstDrake;
  }

  public Playerperformance addPlayerperformance(Playerperformance playerperformance, Account account) {
    return Playerperformance.get(playerperformance, this, account);
  }

  public TeamperformanceBounty addBounty(TeamperformanceBounty bounty) {
    return TeamperformanceBounty.get(bounty,this);
  }

  public byte getInvadingKills() {
    return (byte) playerperformances.stream().mapToInt(Playerperformance::getTeamInvading).max().orElse(0);
  }

  public Teamperformance getOtherTeamperformance() {
    return game.getTeamperformances().stream().filter(teamperformance -> teamperformance.isFirstPick() != firstPick).findFirst().orElse(null);
  }

  public Playerperformance getPerformanceOf(Account account) {
    return playerperformances.stream().filter(playerperformance -> playerperformance.getAccount().equals(account)).findFirst().orElse(null);
  }

  public boolean isPerfect() {
    return getOtherTeamperformance() != null && getOtherTeamperformance().getTotalKills() == 0;
  }

  public boolean gotElder() {
    return elderTime != 0;
  }

  public short firstTurretTime() {
    return (short) (firstTower ? playerperformances.stream().mapToInt(Playerperformance::getFirstturretAdvantage).max().orElse(-1) :
        playerperformances.stream().mapToInt(Playerperformance::getFirstturretAdvantage).min().orElse(-1));
  }

  //<editor-fold desc="getter and setter">
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Game getGame() {
    return game;
  }

  public void setGame(Game game) {
    this.game = game;
  }

  public Team getTeam() {
    return team;
  }

  /**
   * Only inside of models
   *
   * @param team Team
   */
  public void setTeam(Team team) {
    this.team = team;
  }

  public boolean isFirstPick() {
    return firstPick;
  }

  public void setFirstPick(boolean firstPick) {
    this.firstPick = firstPick;
  }

  public boolean isWin() {
    return win;
  }

  public void setWin(boolean win) {
    this.win = win;
  }

  public int getTotalDamage() {
    return totalDamage;
  }

  public void setTotalDamage(int totalDamage) {
    this.totalDamage = totalDamage;
  }

  public int getTotalDamageTaken() {
    return totalDamageTaken;
  }

  public void setTotalDamageTaken(int totalDamageTaken) {
    this.totalDamageTaken = totalDamageTaken;
  }

  public int getTotalGold() {
    return totalGold;
  }

  public void setTotalGold(int totalGold) {
    this.totalGold = totalGold;
  }

  public short getTotalCs() {
    return totalCs;
  }

  public void setTotalCs(short totalCs) {
    this.totalCs = totalCs;
  }

  public short getTotalKills() {
    return totalKills;
  }

  public void setTotalKills(short totalKills) {
    this.totalKills = totalKills;
  }

  public byte getTowers() {
    return towers;
  }

  public void setTowers(byte towers) {
    this.towers = towers;
  }

  public byte getDrakes() {
    return drakes;
  }

  public void setDrakes(byte drakes) {
    this.drakes = drakes;
  }

  public byte getInhibs() {
    return inhibs;
  }

  public void setInhibs(byte inhibs) {
    this.inhibs = inhibs;
  }

  public byte getHeralds() {
    return heralds;
  }

  public void setHeralds(byte heralds) {
    this.heralds = heralds;
  }

  public byte getBarons() {
    return barons;
  }

  public void setBarons(byte barons) {
    this.barons = barons;
  }

  public boolean isFirstTower() {
    return firstTower;
  }

  public void setFirstTower(boolean firstTower) {
    this.firstTower = firstTower;
  }

  public boolean isFirstDrake() {
    return firstDrake;
  }

  public void setFirstDrake(boolean firstDrake) {
    this.firstDrake = firstDrake;
  }

  public boolean isPerfectSoul() {
    return perfectSoul;
  }

  public void setPerfectSoul(boolean perfectSoul) {
    this.perfectSoul = perfectSoul;
  }

  public double getRiftTurrets() {
    return riftTurrets.doubleValue();
  }

  public void setRiftTurrets(double riftTurrets) {
    this.riftTurrets = new BigDecimal(riftTurrets);
  }

  public short getElderTime() {
    return elderTime;
  }

  public void setElderTime(short elderTime) {
    this.elderTime = elderTime;
  }

  public short getBaronPowerplay() {
    return baronPowerplay;
  }

  public void setSurrendered(boolean surrender) {
    this.surrendered = surrender;
  }

  public boolean isSurrendered() {
    return surrendered;
  }

  public void setBaronPowerplay(short baronPowerplay) {
    this.baronPowerplay = baronPowerplay;
  }

  public Set<Playerperformance> getPlayerperformances() {
    return playerperformances;
  }

  public byte getEarlyAces() {
    return earlyAces;
  }

  public void setEarlyAces(byte earlyAces) {
    this.earlyAces = earlyAces;
  }

  public short getBaronTime() {
    return baronTime;
  }

  public void setBaronTime(short baronTime) {
    this.baronTime = baronTime;
  }

  public short getFirstDragonTime() {
    return firstDragonTime;
  }

  public void setFirstDragonTime(short firstDragonTime) {
    this.firstDragonTime = firstDragonTime;
  }

  public byte getObjectiveAtSpawn() {
    return objectiveAtSpawn;
  }

  public void setObjectiveAtSpawn(byte objectiveAtSpawn) {
    this.objectiveAtSpawn = objectiveAtSpawn;
  }

  public byte getObjectiveContests() {
    return objectiveContests;
  }

  public void setObjectiveContests(byte objectiveContests) {
    this.objectiveContests = objectiveContests;
  }

  public boolean isQuestCompletedFirst() {
    return questCompletedFirst;
  }

  public void setQuestCompletedFirst(boolean questCompletedFirst) {
    this.questCompletedFirst = questCompletedFirst;
  }

  public short getInhibitorsTime() {
    return inhibitorsTime;
  }

  public void setInhibitorsTime(short inhibitorsTime) {
    this.inhibitorsTime = inhibitorsTime;
  }

  public byte getFlawlessAce() {
    return flawlessAce;
  }

  public void setFlawlessAce(byte flawlessAce) {
    this.flawlessAce = flawlessAce;
  }

  public byte getRiftOnMultipleTurrets() {
    return riftOnMultipleTurrets;
  }

  public void setRiftOnMultipleTurrets(byte riftOnMultipleTurrets) {
    this.riftOnMultipleTurrets = riftOnMultipleTurrets;
  }

  public short getFastestAcetime() {
    return fastestAcetime;
  }

  public void setFastestAcetime(short fastestAcetime) {
    this.fastestAcetime = fastestAcetime;
  }

  public byte getKillDeficit() {
    return killDeficit;
  }

  public void setKillDeficit(byte killDeficit) {
    this.killDeficit = killDeficit;
  }

  public DragonSoul getSoul() {
    return soul;
  }

  public void setSoul(DragonSoul soul) {
    this.soul = soul;
  }

  public Set<TeamperformanceBounty> getBounties() {
    return bounties;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Teamperformance)) return false;
    final Teamperformance teamperformance = (Teamperformance) o;
    return getId() == teamperformance.getId() && isFirstPick() == teamperformance.isFirstPick() && isWin() == teamperformance.isWin() && getTotalDamage() == teamperformance.getTotalDamage() && getTotalDamageTaken() == teamperformance.getTotalDamageTaken() && getTotalGold() == teamperformance.getTotalGold() && getTotalCs() == teamperformance.getTotalCs() && getTotalKills() == teamperformance.getTotalKills() && getTowers() == teamperformance.getTowers() && getDrakes() == teamperformance.getDrakes() && getInhibs() == teamperformance.getInhibs() && getHeralds() == teamperformance.getHeralds() && getBarons() == teamperformance.getBarons() && isFirstTower() == teamperformance.isFirstTower() && isFirstDrake() == teamperformance.isFirstDrake() && isPerfectSoul() == teamperformance.isPerfectSoul() && getElderTime() == teamperformance.getElderTime() && getBaronPowerplay() == teamperformance.getBaronPowerplay() && isSurrendered() == teamperformance.isSurrendered() && getEarlyAces() == teamperformance.getEarlyAces() && getBaronTime() == teamperformance.getBaronTime() && getFirstDragonTime() == teamperformance.getFirstDragonTime() && getObjectiveAtSpawn() == teamperformance.getObjectiveAtSpawn() && getObjectiveContests() == teamperformance.getObjectiveContests() && isQuestCompletedFirst() == teamperformance.isQuestCompletedFirst() && getInhibitorsTime() == teamperformance.getInhibitorsTime() && getFlawlessAce() == teamperformance.getFlawlessAce() && getRiftOnMultipleTurrets() == teamperformance.getRiftOnMultipleTurrets() && getFastestAcetime() == teamperformance.getFastestAcetime() && getGame().equals(teamperformance.getGame()) && getKillDeficit() == teamperformance.getKillDeficit() && getTeam().equals(teamperformance.getTeam()) && Objects.equals(getRiftTurrets(), teamperformance.getRiftTurrets());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getGame(), getTeam(), isFirstPick(), isWin(), getTotalDamage(), getTotalDamageTaken(), getTotalGold(),
        getTotalCs(), getTotalKills(), getTowers(), getDrakes(), getInhibs(), getHeralds(), getBarons(), isFirstTower(), isFirstDrake(),
        isPerfectSoul(), getRiftTurrets(), getElderTime(), getBaronPowerplay(), isSurrendered(), getEarlyAces(), getBaronTime(),
        getFirstDragonTime(), getObjectiveAtSpawn(), getObjectiveContests(), isQuestCompletedFirst(), getInhibitorsTime(),
        getFlawlessAce(), getRiftOnMultipleTurrets(), getFastestAcetime(), getKillDeficit());
  }

  @Override
  public String toString() {
    return "Teamperformance{" +
        "id=" + id +
        ", game=" + game +
        ", team=" + team +
        ", firstPick=" + firstPick +
        ", win=" + win +
        ", totalDamage=" + totalDamage +
        ", totalDamageTaken=" + totalDamageTaken +
        ", totalGold=" + totalGold +
        ", totalCs=" + totalCs +
        ", totalKills=" + totalKills +
        ", towers=" + towers +
        ", drakes=" + drakes +
        ", inhibs=" + inhibs +
        ", heralds=" + heralds +
        ", barons=" + barons +
        ", firstTower=" + firstTower +
        ", firstDrake=" + firstDrake +
        ", perfectSoul=" + perfectSoul +
        ", riftTurrets=" + riftTurrets +
        ", elderTime=" + elderTime +
        ", baronPowerplay=" + baronPowerplay +
        ", surrendered=" + surrendered +
        ", earlyAces=" + earlyAces +
        ", baronTime=" + baronTime +
        ", firstDragonTime=" + firstDragonTime +
        ", objectiveAtSpawn=" + objectiveAtSpawn +
        ", objectiveContests=" + objectiveContests +
        ", questCompletedFirst=" + questCompletedFirst +
        ", inhibitorsTime=" + inhibitorsTime +
        ", flawlessAce=" + flawlessAce +
        ", riftOnMultipleTurrets=" + riftOnMultipleTurrets +
        ", fastestAcetime=" + fastestAcetime +
        ", killDeficit=" + killDeficit +
        ", playerperformances=" + playerperformances.size() +
        '}';
  }
  //</editor-fold>

}