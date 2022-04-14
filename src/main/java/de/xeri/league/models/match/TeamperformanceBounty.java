package de.xeri.league.models.match;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

import de.xeri.league.util.Data;
import de.xeri.league.util.Util;

@Entity(name = "Teamperformance_Bounty")
@Table(name = "teamperformance_bounty", indexes = @Index(name = "teamperformance", columnList = "teamperformance"))
public class TeamperformanceBounty implements Serializable {
  @Transient
  private static final long serialVersionUID = -51972098226067427L;

  //<editor-fold desc="Queries">
  private static Set<TeamperformanceBounty> data;

  public static void save() {
    if (data != null) data.forEach(Data.getInstance().getSession()::saveOrUpdate);
  }

  public static Set<TeamperformanceBounty> get() {
    if (data == null)
      data = new LinkedHashSet<>((List<TeamperformanceBounty>) Util.query("Teamperformance_Bounty"));
    return data;
  }

  static TeamperformanceBounty get(TeamperformanceBounty neu, Teamperformance performance) {
    get();
    if (find(performance, neu.getStart()) == null) {
      performance.getBounties().add(neu);
      neu.setTeamperformance(performance);
      data.add(neu);
    }
    return find(performance, neu.getStart());
  }

  public static TeamperformanceBounty find(Teamperformance performance, int start) {
    return data.stream().filter(entry -> entry.getTeamperformance().equals(performance) &&
        entry.getStart() == start).findFirst().orElse(null);
  }

  public static List<TeamperformanceBounty> getNotClosed() {
    return data.stream().filter(entry -> entry.getEnd() == 0).collect(Collectors.toList());
  }

  public static List<TeamperformanceBounty> getNotOpened() {
    return data.stream().filter(entry -> entry.getStart() == 0).collect(Collectors.toList());
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