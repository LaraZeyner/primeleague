package de.xeri.league.models.match.playerperformance;

import java.io.Serializable;
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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.league.models.enums.KillRole;
import de.xeri.league.models.enums.Lane;
import de.xeri.league.models.enums.ObjectiveSubtype;
import de.xeri.league.util.Data;
import de.xeri.league.util.HibernateUtil;
import org.hibernate.annotations.NamedQuery;

@Entity(name = "PlayerperformanceObjective")
@Table(name = "playerperformance_objective", indexes = @Index(name = "playerperformance", columnList = "playerperformance"))
@NamedQuery(name = "PlayerperformanceObjective.findAll", query = "FROM PlayerperformanceObjective p")
@NamedQuery(name = "PlayerperformanceObjective.findBy",
    query = "FROM PlayerperformanceObjective p WHERE playerperformance = :playerperformance AND time = :time AND type = :type")
public class PlayerperformanceObjective implements Serializable {
  @Transient
  private static final long serialVersionUID = -3804376366913178857L;

  //<editor-fold desc="Queries">
  public static Set<PlayerperformanceObjective> get() {
    return new LinkedHashSet<>(HibernateUtil.findList(PlayerperformanceObjective.class));
  }

  public static PlayerperformanceObjective get(PlayerperformanceObjective neu, Playerperformance playerperformance) {
    if (has(playerperformance, neu.getTime(), neu.getType())) {
      return find(playerperformance, neu.getTime(), neu.getType());
    }
    playerperformance.getObjectives().add(neu);
    neu.setPlayerperformance(playerperformance);
    Data.getInstance().save(neu);
    return neu;
  }

  public static boolean has(Playerperformance playerperformance, int time, ObjectiveSubtype type) {
    return HibernateUtil.has(PlayerperformanceObjective.class, new String[]{"playerperformance", "time", "type"},
        new Object[]{playerperformance, time, type});
  }

  public static PlayerperformanceObjective find(Playerperformance playerperformance, int time, ObjectiveSubtype type) {
    return HibernateUtil.find(PlayerperformanceObjective.class, new String[]{"playerperformance", "time", "type"},
        new Object[]{playerperformance, time, type});
  }
  //</editor-fold>

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "objective_id", nullable = false)
  private int id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "playerperformance", nullable = false)
  private Playerperformance playerperformance;

  @Column(name = "objective_time", nullable = false)
  private int time;

  @Enumerated(EnumType.STRING)
  @Column(name = "objective_type", nullable = false, length = 15)
  private ObjectiveSubtype type;

  @Enumerated(EnumType.STRING)
  @Column(name = "objective_lane", length = 6)
  private Lane lane;

  @Column(name = "objective_bounty", nullable = false)
  private short bounty;

  @Enumerated(EnumType.STRING)
  @Column(name = "objective_role", nullable = false, length = 6)
  private KillRole role;

  // default constructor
  public PlayerperformanceObjective() {
  }

  public PlayerperformanceObjective(int time, ObjectiveSubtype type, Lane lane, short bounty, KillRole role) {
    this.time = time;
    this.type = type;
    this.lane = lane;
    this.bounty = bounty;
    this.role = role;
  }

  //<editor-fold desc="getter and setter">
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Playerperformance getPlayerperformance() {
    return playerperformance;
  }

  public void setPlayerperformance(Playerperformance playerperformance) {
    this.playerperformance = playerperformance;
  }

  public int getTime() {
    return time;
  }

  public void setTime(int time) {
    this.time = time;
  }

  public ObjectiveSubtype getType() {
    return type;
  }

  public void setType(ObjectiveSubtype type) {
    this.type = type;
  }

  public Lane getLane() {
    return lane;
  }

  public short getBounty() {
    return bounty;
  }

  public KillRole getRole() {
    return role;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PlayerperformanceObjective)) return false;
    final PlayerperformanceObjective that = (PlayerperformanceObjective) o;
    return getId() == that.getId() && getTime() == that.getTime() && getBounty() == that.getBounty() && getPlayerperformance().equals(that.getPlayerperformance()) && getType() == that.getType() && getLane() == that.getLane() && getRole() == that.getRole();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getPlayerperformance(), getTime(), getType(), getLane(), getBounty(), getRole());
  }

  @Override
  public String toString() {
    return "PlayerperformanceObjective{" +
        "id=" + id +
        ", playerperformance=" + playerperformance +
        ", time=" + time +
        ", type=" + type +
        ", lane=" + lane +
        ", bounty=" + bounty +
        ", role=" + role +
        '}';
  }
  //</editor-fold>
}