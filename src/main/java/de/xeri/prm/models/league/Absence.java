package de.xeri.prm.models.league;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import de.xeri.prm.manager.PrimeData;
import de.xeri.prm.models.enums.AbsenceType;
import de.xeri.prm.util.HibernateUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import org.hibernate.Hibernate;
import org.hibernate.annotations.NamedQuery;

/**
 * @since 02.06.2022
 */
@Entity(name = "Absence")
@Table(name = "absence", indexes = @Index(name = "idx_player", columnList = "player"))
@NamedQuery(name = "Absence.findAll", query = "FROM Absence a")
@NamedQuery(name = "Absence.findById", query = "FROM Absence a WHERE idx = :pk")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Absence implements Serializable {
  @Transient
  private static final long serialVersionUID = -1668449870548839650L;

  public static Set<Absence> get() {
    return new LinkedHashSet<>(HibernateUtil.findList(Absence.class));
  }

  public static Absence get(Absence neu) {
    val absence = find(neu.getIdx());
    if (absence != null) {
      absence.setPlayer(neu.getPlayer());
      absence.setType(neu.getType());
      absence.setStart(neu.getStart());
      absence.setEnd(neu.getEnd());
    }
    PrimeData.getInstance().save(neu);
    return neu;
  }

  public static Absence find(String id) {
    return HibernateUtil.find(Absence.class, id);
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "absence_id")
  private short id;

  @Column(name = "absence_index", nullable = false, length = 100)
  private String idx;

  @Column(name = "player", nullable = false, length = 25)
  @ToString.Exclude
  private String player;

  @Enumerated(EnumType.STRING)
  @Column(name = "absence_type", nullable = false, length = 12)
  private AbsenceType type;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "absence_start", nullable = false)
  private Date start;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "absence_end")
  private Date end;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    final Absence absence = (Absence) o;
    return Objects.equals(id, absence.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
