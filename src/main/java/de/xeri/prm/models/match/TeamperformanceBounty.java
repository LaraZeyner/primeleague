package de.xeri.prm.models.match;

import java.io.Serializable;
import java.util.LinkedHashSet;
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
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.prm.manager.PrimeData;
import de.xeri.prm.util.HibernateUtil;
import org.hibernate.annotations.NamedQuery;

@Entity(name = "TeamperformanceBounty")
@Table(name = "teamperformance_bounty", indexes = @Index(name = "teamperformance", columnList = "teamperformance"))
@NamedQuery(name = "TeamperformanceBounty.findAll", query = "FROM TeamperformanceBounty t")
@NamedQuery(name = "TeamperformanceBounty.findById", query = "FROM TeamperformanceBounty t WHERE id = :pk")
@NamedQuery(name = "TeamperformanceBounty.findBy",
    query = "FROM TeamperformanceBounty t WHERE teamperformance = :performance AND t.start = :start")
@NamedQuery(name = "TeamperformanceBounty.findByEnd",
    query = "FROM TeamperformanceBounty t WHERE teamperformance = :performance AND t.end = :end")
public class TeamperformanceBounty implements Serializable {
  @Transient
  private static final long serialVersionUID = -51972098226067427L;

  //<editor-fold desc="Queries">

  public static Set<TeamperformanceBounty> get() {
    return new LinkedHashSet<>(HibernateUtil.findList(TeamperformanceBounty.class));
  }

  public static TeamperformanceBounty get(TeamperformanceBounty neu, Teamperformance performance) {
    if (has(performance, neu.getStart())) {
      return find(performance, neu.getStart());
    }
    performance.getBounties().add(neu);
    neu.setTeamperformance(performance);
    PrimeData.getInstance().save(neu);
    return neu;
  }

  public static boolean has(int id) {
    return HibernateUtil.has(Teamperformance.class, id);
  }

  public static boolean has(Teamperformance performance, int start) {
    return HibernateUtil.has(TeamperformanceBounty.class, new String[]{"performance", "start"}, new Object[]{performance, start});
  }

  public static TeamperformanceBounty find(Teamperformance performance, int start) {
    return HibernateUtil.find(TeamperformanceBounty.class, new String[]{"performance", "start"}, new Object[]{performance, start});
  }

  public static TeamperformanceBounty find(int id) {
    return HibernateUtil.find(TeamperformanceBounty.class, id);
  }
  //</editor-fold>

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "bounty_id", nullable = false)
  private int id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "teamperformance", nullable = false)
  private Teamperformance teamperformance;

  @Column(name = "bounty_start", nullable = false)
  private int start;

  @Column(name = "bounty_end")
  private int end;

  // default constructor
  public TeamperformanceBounty() {
  }

  public TeamperformanceBounty(int start, int end) {
    this.start = start;
    this.end = end;
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

  public int getStart() {
    return start;
  }

  public void setStart(int start) {
    this.start = start;
  }

  public int getEnd() {
    return end;
  }

  public void setEnd(int end) {
    this.end = end;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof TeamperformanceBounty)) return false;
    final TeamperformanceBounty that = (TeamperformanceBounty) o;
    return getId() == that.getId() && getStart() == that.getStart() && getEnd() == that.getEnd() && getTeamperformance().equals(that.getTeamperformance());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getTeamperformance(), getStart(), getEnd());
  }

  @Override
  public String toString() {
    return "TeamperformanceBounty{" +
        "id=" + id +
        ", teamperformance=" + teamperformance +
        ", start=" + start +
        ", end=" + end +
        '}';
  }
  //</editor-fold>
}