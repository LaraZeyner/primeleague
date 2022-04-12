package de.xeri.league.models.match;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
import javax.persistence.Transient;

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
  private static final long serialVersionUID = 3480982918520378839L;

  private static Set<Teamperformance> data;

  public static void save() {
    if (data != null) data.forEach(Data.getInstance().getSession()::saveOrUpdate);
  }

  public static Set<Teamperformance> get() {
    if (data == null) data = new LinkedHashSet<>((List<Teamperformance>) Util.query("Teamperformance"));
    return data;
  }

  public static Teamperformance get(Teamperformance neu, Game game) {
    get();
    if (find(game, neu.isFirstPick()) == null) data.add(neu);
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

  @OneToMany(mappedBy = "teamperformance")
  private final Set<Playerperformance> playerperformances = new LinkedHashSet<>();

  // default constructor
  public Teamperformance() {
  }

  public Teamperformance(boolean firstPick, boolean win, int totalGold, int totalCs, int totalKills, int towers, int drakes, int inhibs,
                         int heralds, int barons, boolean firstTower, boolean firstDrake) {
    this.firstPick = firstPick;
    this.win = win;
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

  public void addPlayerperformance(Playerperformance playerperformance) {
    playerperformances.add(playerperformance);
    playerperformance.setTeamperformance(this);
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Teamperformance)) return false;
    final Teamperformance teamperformance = (Teamperformance) o;
    return getId() == teamperformance.getId() && isFirstPick() == teamperformance.isFirstPick() && isWin() == teamperformance.isWin() && getTotalGold() == teamperformance.getTotalGold() && getTotalCs() == teamperformance.getTotalCs() && getTotalKills() == teamperformance.getTotalKills() && getTowers() == teamperformance.getTowers() && getDrakes() == teamperformance.getDrakes() && getInhibs() == teamperformance.getInhibs() && getHeralds() == teamperformance.getHeralds() && getBarons() == teamperformance.getBarons() && isFirstTower() == teamperformance.isFirstTower() && isFirstDrake() == teamperformance.isFirstDrake() && isPerfectSoul() == teamperformance.isPerfectSoul() && Double.compare(teamperformance.getRiftTurrets(), getRiftTurrets()) == 0 && getElderTime() == teamperformance.getElderTime() && getBaronPowerplay() == teamperformance.getBaronPowerplay() && getGame().equals(teamperformance.getGame()) && Objects.equals(getTeam(), teamperformance.getTeam()) && getPlayerperformances().equals(teamperformance.getPlayerperformances()) && surrendered == teamperformance.isSurrendered();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getGame(), getTeam(), isFirstPick(), isWin(), getTotalGold(), getTotalCs(), getTotalKills(), getTowers()
        , getDrakes(), getInhibs(), getHeralds(), getBarons(), isFirstTower(), isFirstDrake(), isPerfectSoul(), getRiftTurrets(),
        getElderTime(), getBaronPowerplay(), isSurrendered());
  }

  @Override
  public String toString() {
    return "Teamperformance{" +
        "id=" + id +
        ", game=" + game +
        ", team=" + team +
        ", firstPick=" + firstPick +
        ", win=" + win +
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
        '}';
  }
  //</editor-fold>

}