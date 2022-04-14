package de.xeri.league.models.match;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.league.models.ids.PlayerperformanceInfoId;
import de.xeri.league.util.Data;
import de.xeri.league.util.Util;

@Entity(name = "Playerperformance_Info")
@Table(name = "playerperformance_info")
@IdClass(PlayerperformanceInfoId.class)
public class PlayerperformanceInfo extends Position {
  @Transient
  private static final long serialVersionUID = -1481745323015710010L;

  private static Set<PlayerperformanceInfo> data;

  public static void save() {
    if (data != null) data.forEach(Data.getInstance().getSession()::saveOrUpdate);
  }

  public static Set<PlayerperformanceInfo> get() {
    if (data == null)
      data = new LinkedHashSet<>((List<PlayerperformanceInfo>) Util.query("Playerperformance_Info"));
    return data;
  }

  public static PlayerperformanceInfo get(PlayerperformanceInfo neu, Playerperformance performance) {
    get();
    if (find(performance, neu.getMinute()) == null) {
      performance.getInfos().add(neu);
      neu.setPlayerperformance(performance);
      data.add(neu);
    }
    return find(performance, neu.getMinute());
  }

  public static PlayerperformanceInfo find(Playerperformance playerperformance, short minute) {
    return data.stream().filter(entry -> entry.getPlayerperformance().equals(playerperformance) && entry.getMinute() == minute)
        .findFirst().orElse(null);
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

  @Column(name = "enemy_controlled", nullable = false)
  private short enemyControlled;

  @Embedded
  private PlayerPosition position;

  @Column(name = "info_experience", columnDefinition = "SMALLINT UNSIGNED NOT NULL")
  private int experience;

  @Column(name = "info_creep_score", nullable = false)
  private short creepScore;

  @Column(name = "info_damage_total", nullable = false)
  private int totalDamage;

  // default constructor
  public PlayerperformanceInfo() {
  }

  public PlayerperformanceInfo(short minute, int totalGold, short currentGold, short enemyControlled, Position position, int experience, short creepScore, int totalDamage) {
    this.minute = minute;
    this.totalGold = totalGold;
    this.currentGold = currentGold;
    this.enemyControlled = enemyControlled;
    this.position = new PlayerPosition(position.getX(), position.getY());
    this.experience = experience;
    this.creepScore = creepScore;
    this.totalDamage = totalDamage;
  }

  //<editor-fold desc="getter and setter">
  public Playerperformance getPlayerperformance() {
    return playerperformance;
  }

  public void setPlayerperformance(Playerperformance playerperformance) {
    this.playerperformance = playerperformance;
  }

  public short getMinute() {
    return minute;
  }

  public int getTotalGold() {
    return totalGold;
  }

  public short getCurrentGold() {
    return currentGold;
  }

  public short getEnemyControlled() {
    return enemyControlled;
  }

  public Position getPosition() {
    return new Position(position.getX(), position.getY());
  }

  public int getExperience() {
    return experience;
  }

  public short getCreepScore() {
    return creepScore;
  }

  public int getTotalDamage() {
    return totalDamage;
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