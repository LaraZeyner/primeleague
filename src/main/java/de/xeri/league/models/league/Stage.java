package de.xeri.league.models.league;

import java.io.Serializable;
import java.util.Calendar;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import de.xeri.league.models.enums.StageType;
import de.xeri.league.util.Data;
import de.xeri.league.util.Util;

@Entity(name = "Stage")
@Table(name = "stage", indexes = {
    @Index(name = "idx_stage_type", columnList = "season, stage_type", unique = true),
    @Index(name = "stage_start", columnList = "stage_start", unique = true),
    @Index(name = "stage_end", columnList = "stage_end", unique = true)
})
public class Stage implements Serializable {

  @Transient
  private static final long serialVersionUID = 3935879920437275466L;

  private static Set<Stage> data;

  public static void save() {
    if (data != null) data.forEach(Data.getInstance().getSession()::saveOrUpdate);
  }

  public static Set<Stage> get() {
    if (data == null) data = new LinkedHashSet<>((List<Stage>) Util.query("Stage"));
    return data;
  }

  public static Stage get(Stage neu, Season season) {
    get();
    if (find(season, neu.getStageType()) == null) {
      season.addStage(neu);
      data.add(neu);
    }
    return find(season, neu.getStageType());
  }

  public static Stage find(Season season, StageType type) {
    get();
    return data.stream().filter(entry -> entry.getStageType().equals(type) && entry.getSeason().equals(season)).findFirst().orElse(null);
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "stage_id", nullable = false)
  private byte id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "season")
  private Season season;

  @Enumerated(EnumType.STRING)
  @Column(name = "stage_type", nullable = false, length = 8)
  private StageType stageType;

  @Temporal(TemporalType.DATE)
  @Column(name = "stage_start", nullable = false)
  private Calendar stageStart;

  @Temporal(TemporalType.DATE)
  @Column(name = "stage_end", nullable = false)
  private Calendar stageEnd;

  @OneToMany(mappedBy = "stage")
  private final Set<Matchday> matchdays = new LinkedHashSet<>();

  @OneToMany(mappedBy = "stage")
  private final Set<League> leagues = new LinkedHashSet<>();

  // default constructor
  public Stage() {
  }

  public Stage(StageType stageType, Calendar stageStart, Calendar stageEnd) {
    this.stageType = stageType;
    this.stageStart = stageStart;
    this.stageEnd = stageEnd;
  }

  public void addMatchday(Matchday matchday) {
    matchdays.add(matchday);
    matchday.setStage(this);
  }

  public void addLeague(League league) {
    leagues.add(league);
    league.setStage(this);
  }

  //<editor-fold desc="getter and setter">
  public Set<League> getLeagues() {
    return leagues;
  }

  public Set<Matchday> getMatchdays() {
    return matchdays;
  }

  public Calendar getStageEnd() {
    return stageEnd;
  }

  public void setStageEnd(Calendar stageEnd) {
    this.stageEnd = stageEnd;
  }

  public Calendar getStageStart() {
    return stageStart;
  }

  public void setStageStart(Calendar stageStart) {
    this.stageStart = stageStart;
  }

  public StageType getStageType() {
    return stageType;
  }

  public void setStageType(StageType stageType) {
    this.stageType = stageType;
  }

  public Season getSeason() {
    return season;
  }

  public void setSeason(Season season) {
    this.season = season;
  }

  public byte getId() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Stage)) return false;
    final Stage stage = (Stage) o;
    return getId() == stage.getId() && getSeason().equals(stage.getSeason()) && getStageType() == stage.getStageType() && getStageStart().equals(stage.getStageStart()) && getStageEnd().equals(stage.getStageEnd()) && getMatchdays().equals(stage.getMatchdays()) && Objects.equals(getLeagues(), stage.getLeagues());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getSeason(), getStageType(), getStageStart(), getStageEnd(), getMatchdays(), getLeagues());
  }

  @Override
  public String toString() {
    return "Stage{" +
        "id=" + id +
        ", season=" + season +
        ", stageType=" + stageType +
        ", stageStart=" + stageStart +
        ", stageEnd=" + stageEnd +
        '}';
  }
  //</editor-fold>
}