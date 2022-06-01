package de.xeri.prm.models.match;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
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

import de.xeri.prm.manager.Data;
import de.xeri.prm.models.enums.DragonSoul;
import de.xeri.prm.models.league.Account;
import de.xeri.prm.models.league.Team;
import de.xeri.prm.models.match.playerperformance.JunglePath;
import de.xeri.prm.models.match.playerperformance.Playerperformance;
import de.xeri.prm.util.HibernateUtil;
import de.xeri.prm.util.Util;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.NamedQuery;

@Entity(name = "Teamperformance")
@Table(name = "teamperformance", indexes = {
    @Index(name = "idx_teamperformance_side", columnList = "game, first_pick", unique = true),
    @Index(name = "team", columnList = "team")
})
@NamedQuery(name = "Teamperformance.findAll", query = "FROM Teamperformance t")
@NamedQuery(name = "Teamperformance.findById", query = "FROM Teamperformance t WHERE id = :pk")
@NamedQuery(name = "Teamperformance.findBy", query = "FROM Teamperformance t WHERE game = :game AND firstPick = :first")
@NamedQuery(name = "x", query = "SELECT AVG(CASE WHEN ((ganksTop + ganksMid + ganksBot) <> 0) THEN ((CAST(ganksTop AS int) - CAST" +
    "(ganksBot AS int)) / (ganksTop + ganksMid + ganksBot)) ELSE 0 END) FROM Playerperformance p")
@NamedQuery(name = "Teamperformance.teamOwn",
    query = "SELECT SUM(CASE WHEN team = :team THEN 1 ELSE 0 END), " +
        "SUM(CASE WHEN team = :team THEN (CASE WHEN win IS true THEN 1 ELSE 0 END) ELSE NULL END), " +
        "AVG(CASE WHEN team = :team THEN totalKills ELSE NULL END), SUM(CASE WHEN team = :team THEN totalKills ELSE CAST(totalKills*-1.0 as double) END), " +
        "AVG(CASE WHEN team = :team THEN totalGold ELSE NULL END), SUM(CASE WHEN team = :team THEN totalGold ELSE CAST(totalGold*-1.0 as double) END), " +
        "AVG(CASE WHEN team = :team THEN totalCs ELSE NULL END), SUM(CASE WHEN team = :team THEN totalCs ELSE CAST(totalCs*-1.0 as double) END), " +
        "SUM(CASE WHEN team = :team THEN towers ELSE NULL END), AVG(CASE WHEN team = :team THEN towers ELSE NULL END), " +
        "SUM(CASE WHEN team = :team THEN drakes ELSE NULL END), AVG(CASE WHEN team = :team THEN drakes ELSE NULL END), " +
        "SUM(CASE WHEN team = :team THEN inhibs ELSE NULL END), AVG(CASE WHEN team = :team THEN inhibs ELSE NULL END), " +
        "SUM(CASE WHEN team = :team THEN heralds ELSE NULL END), AVG(CASE WHEN team = :team THEN heralds ELSE NULL END), " +
        "SUM(CASE WHEN team = :team THEN barons ELSE NULL END), AVG(CASE WHEN team = :team THEN barons ELSE NULL END), " +
        "AVG(CASE WHEN team = :team THEN game.duration ELSE NULL END), " +
        "AVG(CASE WHEN team = :team THEN (CASE WHEN win IS true THEN game.duration ELSE NULL END) ELSE NULL END), " +
        "AVG(CASE WHEN team = :team THEN (CASE WHEN win IS false THEN game.duration ELSE NULL END) ELSE NULL END) " +
        "FROM Teamperformance t " +
        "WHERE game.turnamentmatch <> NULL AND " +
        "game.turnamentmatch.id IN :matches")
@Getter
@Setter
@NoArgsConstructor
public class Teamperformance implements Serializable {

  @Transient
  private static final long serialVersionUID = 8274298970011471960L;

  public static Set<Teamperformance> get() {
    return new LinkedHashSet<>(HibernateUtil.findList(Teamperformance.class));
  }

  public static Teamperformance get(Teamperformance neu, Game game, Team team) {
    if (has(game, neu.isFirstPick())) {
      return find(game, neu.isFirstPick());
    }
    game.getTeamperformances().add(neu);
    neu.setGame(game);
    if (team != null) {
      team.getTeamperformances().add(neu);
      neu.setTeam(team);
    }
    Data.getInstance().save(neu);
    return neu;
  }

  public static boolean has(int id) {
    return HibernateUtil.has(Teamperformance.class, id);
  }

  public static boolean has(Game game, boolean firstPick) {
    return HibernateUtil.has(Teamperformance.class, new String[]{"game", "first"}, new Object[]{game, firstPick});
  }

  public static Teamperformance find(Game game, boolean firstPick) {
    return HibernateUtil.find(Teamperformance.class, new String[]{"game", "first"}, new Object[]{game, firstPick});
  }

  public static Teamperformance find(int id) {
    return HibernateUtil.find(Teamperformance.class, id);
  }

  public List<TeamperformanceBounty> getNotClosed() {
    return HibernateUtil.findList(TeamperformanceBounty.class, new String[]{"performance", "end"}, new Object[]{this, 0}, "findByEnd");
  }

  public List<TeamperformanceBounty> getNotOpened() {
    return HibernateUtil.findList(TeamperformanceBounty.class, new String[]{"performance", "start"}, new Object[]{this, 0});
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
  private byte flawlessAces;

  @Column(name = "rift_multiturret")
  private byte riftOnMultipleTurrets;

  @Column(name = "ace_fastest")
  private short fastestAcetime;

  @Column(name = "kills_deficit")
  private byte killDeficit;

  @Column(name = "dragon_soul")
  @Enumerated(EnumType.STRING)
  private DragonSoul soul;

  @Column(name = "team_damage_mitigated", nullable = false)
  private int damageMitigated;

  @Column(name = "team_immobilizations")
  private short immobilizations;

  @Column(name = "team_vision", nullable = false)
  private short vision;

  @Column(name = "jungletime_wasted")
  private short jungleTimeWasted;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "jungle_path")
  private JunglePath junglePath;

  @OneToMany(mappedBy = "teamperformance")
  private final Set<Playerperformance> playerperformances = new LinkedHashSet<>();

  @OneToMany(mappedBy = "teamperformance")
  private final Set<TeamperformanceBounty> bounties = new LinkedHashSet<>();

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
  public double getRiftTurrets() {
    return Util.getDouble(riftTurrets);
  }

  public void setRiftTurrets(double riftTurrets) {
    this.riftTurrets = BigDecimal.valueOf(riftTurrets);
  }

  //</editor-fold>

}