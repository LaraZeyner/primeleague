package de.xeri.league.models.league;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import de.xeri.league.models.enums.ScheduleType;
import de.xeri.league.util.Data;
import de.xeri.league.util.HibernateUtil;
import org.hibernate.annotations.NamedQuery;

@Entity(name = "Schedule")
@Table(name = "schedule", indexes = @Index(name = "enemy_team", columnList = "enemy_team"))
@NamedQuery(name = "Schedule.findAll", query = "FROM Schedule p")
@NamedQuery(name = "Schedule.findById", query = "FROM Schedule p WHERE id = :pk")
@NamedQuery(name = "Schedule.findBy", query = "FROM Schedule p WHERE title = :title AND startTime = :start")
public class Schedule implements Serializable {
  @Transient
  private static final long serialVersionUID = 3439077356822417423L;

  public static Set<Schedule> get() {
    return new LinkedHashSet<>(HibernateUtil.findList(Schedule.class));
  }

  public static Schedule get(Schedule neu) {
    if (has(neu.getTitle(), neu.getStartTime())) {
      final Schedule schedule = find(neu.getTitle(), neu.getStartTime());
      schedule.setType(neu.getType());
      schedule.setSmallTitle(neu.getSmallTitle());
      return schedule;
    }
    Data.getInstance().save(neu);
    return neu;
  }

  public static boolean has(int id) {
    return HibernateUtil.has(Schedule.class, id);
  }

  public static boolean has(String title, Date start) {
    return HibernateUtil.has(Schedule.class, new String[]{"title", "start"}, new Object[]{title, start});
  }

  public static Schedule find(String title, Date start) {
    return HibernateUtil.find(Schedule.class, new String[]{"title", "start"}, new Object[]{title, start});
  }

  public static Schedule find(int id) {
    return HibernateUtil.find(Schedule.class, id);
  }



  public static List<Schedule> last() {
    return get().stream()
        .filter(schedule -> schedule.getEnemyTeam() != null)
        .collect(Collectors.toList());
  }

  public static List<Schedule> next() {
    return get().stream()
        .filter(schedule -> schedule.getEndTime().getTime() > System.currentTimeMillis() + 900_000L)
        .filter(schedule -> schedule.getEnemyTeam() != null)
        .collect(Collectors.toList());
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "schedule_id", nullable = false)
  private short id;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, length = 20)
  private ScheduleType type;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "start_time", nullable = false)
  private Date startTime;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "end_time", nullable = false)
  private Date endTime;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "enemy_team")
  private Team enemyTeam;

  @Column(name = "title", nullable = false, length = 100)
  private String title;

  @Column(name = "small_title", length = 20)
  private String smallTitle;

  @Column(name = "participants", length = 100)
  private String participants;

  // default constructor
  public Schedule() {
  }

  public Schedule(ScheduleType type, Date startTime, String title, String smallTitle) {
    this.type = type;
    this.startTime = startTime;
    this.title = title;
    this.smallTitle = smallTitle;
    this.endTime = new Date(type.getDuration() + startTime.getTime());
  }

  //<editor-fold desc="getter and setter">
  public short getId() {
    return id;
  }

  public void setId(short id) {
    this.id = id;
  }

  public ScheduleType getType() {
    return type;
  }

  public void setType(ScheduleType type) {
    this.type = type;
  }

  public Date getStartTime() {
    return startTime;
  }

  public Date getEndTime() {
    return endTime;
  }

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }

  public Team getEnemyTeam() {
    return enemyTeam;
  }

  void setEnemyTeam(Team enemyTeam) {
    this.enemyTeam = enemyTeam;
  }

  public void setSmallTitle(String smallTitle) {
    this.smallTitle = smallTitle;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getSmallTitle() {
    return smallTitle;
  }

  public String getParticipants() {
    return participants;
  }

  public void setParticipants(String participants) {
    this.participants = participants;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Schedule)) return false;
    final Schedule schedule = (Schedule) o;
    return getId() == schedule.getId() && getType() == schedule.getType() && getStartTime().equals(schedule.getStartTime()) && Objects.equals(getEndTime(), schedule.getEndTime()) && Objects.equals(getEnemyTeam(), schedule.getEnemyTeam()) && getTitle().equals(schedule.getTitle()) && Objects.equals(getSmallTitle(), schedule.getSmallTitle()) && Objects.equals(getParticipants(), schedule.getParticipants());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getType(), getStartTime(), getEndTime(), getEnemyTeam(), getTitle(), getSmallTitle(), getParticipants());
  }

  @Override
  public String toString() {
    return "Schedule{" +
        "id=" + id +
        ", type=" + type +
        ", startTime=" + startTime +
        ", endTime=" + endTime +
        ", enemyTeam=" + enemyTeam +
        ", title='" + title + '\'' +
        ", smallTitle='" + smallTitle + '\'' +
        ", participants='" + participants + '\'' +
        '}';
  }
  //</editor-fold>
}