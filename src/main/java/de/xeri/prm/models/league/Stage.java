package de.xeri.prm.models.league;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import de.xeri.prm.manager.Data;
import de.xeri.prm.models.enums.StageType;
import de.xeri.prm.util.HibernateUtil;
import de.xeri.prm.util.Util;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.NamedQuery;

@Entity(name = "Stage")
@Table(name = "stage", indexes = {
    @Index(name = "idx_stage_type", columnList = "season, stage_type", unique = true),
    @Index(name = "stage_start", columnList = "stage_start", unique = true),
    @Index(name = "stage_end", columnList = "stage_end", unique = true)
})
@NamedQuery(name = "Stage.findAll", query = "FROM Stage s")
@NamedQuery(name = "Stage.findById", query = "FROM Stage s WHERE id = :pk")
@NamedQuery(name = "Stage.findBy", query = "FROM Stage s WHERE season = :season AND stageType = :type")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Stage implements Serializable {
  @Transient
  private static final long serialVersionUID = 3935879920437275466L;

  public static Set<Stage> get() {
    return new LinkedHashSet<>(HibernateUtil.findList(Stage.class));
  }

  public static Stage get(Stage neu, Season season) {
    if (has(season, neu.getStageType())) {
      final Stage stage = find(season, neu.getStageType());
      stage.setStageStart(neu.getStageStart());
      stage.setStageEnd(neu.getStageEnd());
      return stage;
    }
    season.getStages().add(neu);
    neu.setSeason(season);
    Data.getInstance().save(neu);
    return neu;
  }

  public static boolean has(short id) {
    return HibernateUtil.has(Stage.class, id);
  }

  public static boolean has(Season season, StageType stageType) {
    return HibernateUtil.has(Stage.class, new String[]{"season", "type"}, new Object[]{season, stageType});
  }

  public static Stage find(Season season, StageType stageType) {
    return HibernateUtil.find(Stage.class, new String[]{"season", "type"}, new Object[]{season, stageType});
  }

  public static Stage find(short id) {
    return HibernateUtil.find(Stage.class, id);
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "stage_id", nullable = false)
  private short id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "season")
  @ToString.Exclude
  private Season season;

  @Enumerated(EnumType.STRING)
  @Column(name = "stage_type", nullable = false, length = 18)
  private StageType stageType;

  @Temporal(TemporalType.DATE)
  @Column(name = "stage_start", nullable = false)
  private Calendar stageStart;

  @Temporal(TemporalType.DATE)
  @Column(name = "stage_end", nullable = false)
  private Calendar stageEnd;

  @OneToMany(mappedBy = "stage")
  @ToString.Exclude
  private final Set<Matchday> matchdays = new LinkedHashSet<>();

  @OneToMany(mappedBy = "stage")
  @ToString.Exclude
  private final Set<League> leagues = new LinkedHashSet<>();

  public Stage(StageType stageType, Calendar stageStart, Calendar stageEnd) {
    this.stageType = stageType;
    this.stageStart = stageStart;
    this.stageEnd = stageEnd;
  }

  public boolean isInSeason(Date date) {
    return season.getSeasonStart().before(Util.getCalendar(date)) && season.getSeasonEnd().after(Util.getCalendar(date));
  }

  public Matchday addMatchday(Matchday matchday) {
    return Matchday.get(matchday, this);
  }

  public League addLeague(League league) {
    return League.get(league, this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    final Stage stage = (Stage) o;
    return Objects.equals(id, stage.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}