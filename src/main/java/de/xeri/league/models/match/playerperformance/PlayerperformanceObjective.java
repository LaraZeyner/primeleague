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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.NamedQuery;

@Entity(name = "PlayerperformanceObjective")
@Table(name = "playerperformance_objective", indexes = @Index(name = "playerperformance", columnList = "playerperformance"))
@NamedQuery(name = "PlayerperformanceObjective.findAll", query = "FROM PlayerperformanceObjective p")
@NamedQuery(name = "PlayerperformanceObjective.findBy",
    query = "FROM PlayerperformanceObjective p WHERE playerperformance = :playerperformance AND time = :time AND type = :type")
@Getter
@Setter
@ToString
@NoArgsConstructor
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
  @ToString.Exclude
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

  public PlayerperformanceObjective(int time, ObjectiveSubtype type, Lane lane, short bounty, KillRole role) {
    this.time = time;
    this.type = type;
    this.lane = lane;
    this.bounty = bounty;
    this.role = role;
  }

  //<editor-fold desc="getter and setter">
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    final PlayerperformanceObjective objective = (PlayerperformanceObjective) o;
    return id == objective.getId();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getPlayerperformance(), getTime(), getType(), getLane(), getBounty(), getRole());
  }
  //</editor-fold>
}