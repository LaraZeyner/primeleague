package de.xeri.league.models.match.playerperformance;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.league.game.events.location.PlayerPosition;
import de.xeri.league.game.events.location.Position;
import de.xeri.league.models.ids.PlayerperformanceInfoId;
import de.xeri.league.util.Data;
import de.xeri.league.util.HibernateUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NamedQuery;

@Entity(name = "PlayerperformanceInfo")
@Table(name = "playerperformance_info", indexes = @Index(name = "info_playerperformance", columnList = "playerperformance"))
@IdClass(PlayerperformanceInfoId.class)
@NamedQuery(name = "PlayerperformanceInfo.findAll", query = "FROM PlayerperformanceInfo p")
@NamedQuery(name = "PlayerperformanceInfo.findBy",
    query = "FROM PlayerperformanceInfo p WHERE playerperformance = :playerperformance AND minute = :minute")
@Getter
@Setter
@NoArgsConstructor
public class PlayerperformanceInfo extends Position {
  @Transient
  private static final long serialVersionUID = -1481745323015710010L;

  public static Set<PlayerperformanceInfo> get() {
    return new LinkedHashSet<>(HibernateUtil.findList(PlayerperformanceInfo.class));
  }

  public static PlayerperformanceInfo get(PlayerperformanceInfo neu, Playerperformance playerperformance) {
    if (has(playerperformance, neu.getMinute())) {
      return find(playerperformance, neu.getMinute());
    }
    playerperformance.getInfos().add(neu);
    neu.setPlayerperformance(playerperformance);
    Data.getInstance().save(neu);
    return neu;
  }

  public static boolean has(Playerperformance playerperformance, short minute) {
    return HibernateUtil.has(PlayerperformanceInfo.class, new String[]{"playerperformance", "minute"}, new Object[]{playerperformance, minute});
  }

  public static PlayerperformanceInfo find(Playerperformance playerperformance, short minute) {
    return HibernateUtil.find(PlayerperformanceInfo.class, new String[]{"playerperformance", "minute"}, new Object[]{playerperformance, minute});
  }

  @Id
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "playerperformance", nullable = false)
  private Playerperformance playerperformance;

  @Id
  @Column(name = "info_minute", columnDefinition = "TINYINT UNSIGNED NOT NULL")
  private short minute;

  @Column(name = "info_gold_total", columnDefinition = "SMALLINT UNSIGNED NOT NULL")
  private int totalGold;

  @Column(name = "info_gold_current", nullable = false)
  private short currentGold;

  @Column(name = "enemy_controlled", nullable = false, precision = 9, scale = 4)
  private BigDecimal enemyControlled;

  @Embedded
  private PlayerPosition position;

  @Column(name = "info_experience", columnDefinition = "SMALLINT UNSIGNED NOT NULL")
  private int experience;

  @Column(name = "info_lead")
  private short lead;

  @Column(name = "info_creep_score", nullable = false)
  private short creepScore;

  @Column(name = "info_damage_total", nullable = false)
  private int totalDamage;

  @Column(name = "info_health_max", nullable = false)
  private short maxHealth;

  @Column(name = "info_health_current", nullable = false)
  private short currentHealth;

  @Column(name = "info_resource_max", nullable = false)
  private short maxResource;

  @Column(name = "info_resource_current", nullable = false)
  private short currentResource;


  @Column(name = "info_movespeed", nullable = false)
  private short movementSpeed;

  public PlayerperformanceInfo(short minute, int totalGold, short currentGold, double enemyControlled, Position position, int experience,
                               short lead, short creepScore, int totalDamage, short maxHealth, short currentHealth, short maxResource,
                               short currentResource, short movementSpeed) {
    this.minute = minute;
    this.totalGold = totalGold;
    this.currentGold = currentGold;
    this.enemyControlled = BigDecimal.valueOf(enemyControlled);
    this.position = new PlayerPosition((short) position.getX(), (short) position.getY());
    this.experience = experience;
    this.lead = lead;
    this.creepScore = creepScore;
    this.totalDamage = totalDamage;
    this.maxHealth = maxHealth;
    this.currentHealth = currentHealth;
    this.maxResource = maxResource;
    this.currentResource = currentResource;
    this.movementSpeed = movementSpeed;
  }

  //<editor-fold desc="getter and setter">
  public Position getPosition() {
    return new Position(position.getX(), position.getY());
  }

  public double getEnemyControlled() {
    return enemyControlled.doubleValue();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PlayerperformanceInfo)) return false;
    if (!super.equals(o)) return false;
    final PlayerperformanceInfo that = (PlayerperformanceInfo) o;
    return getMinute() == that.getMinute() && getTotalGold() == that.getTotalGold() && getCurrentGold() == that.getCurrentGold() && getEnemyControlled() == that.getEnemyControlled() && getExperience() == that.getExperience() && getCreepScore() == that.getCreepScore() && getTotalDamage() == that.getTotalDamage() && getPlayerperformance().equals(that.getPlayerperformance()) && getPosition().equals(that.getPosition());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getPlayerperformance(), getMinute(), getTotalGold(), getCurrentGold(), getEnemyControlled(), getPosition(), getExperience(), getCreepScore(), getTotalDamage());
  }

  @Override
  public String toString() {
    return "PlayerperformanceInfo{" +
        "playerperformance=" + playerperformance +
        ", minute=" + minute +
        ", totalGold=" + totalGold +
        ", currentGold=" + currentGold +
        ", enemyControlled=" + enemyControlled +
        ", position=" + position +
        ", experience=" + experience +
        ", creepScore=" + creepScore +
        ", totalDamage=" + totalDamage +
        '}';
  }
  //</editor-fold>

}