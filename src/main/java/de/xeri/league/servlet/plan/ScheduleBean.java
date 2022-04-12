package de.xeri.league.servlet.plan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import de.xeri.league.models.enums.ScheduleType;

/**
 * Created by Lara on 04.04.2022 for web
 */
@ManagedBean(name = "scheduleTable", eager = true)
@SessionScoped
public class ScheduleBean implements Serializable {
  private static final long serialVersionUID = -1065245693876793527L;
  private int id;
  private ScheduleType type;
  private Date startTime;
  private Date endTime;
  private String enemyTeam;
  private String title;
  private String score;
  private static final List<ScheduleEntry> scheduled = new ArrayList<>(Arrays.asList(
      new ScheduleEntry(1, ScheduleType.CLASH, new GregorianCalendar(2022, Calendar.APRIL, 16,
          19, 0).getTime(), "Tier 3", "Piltover-Clash Tag 1", "-:-"),
      new ScheduleEntry(2, ScheduleType.CLASH, new GregorianCalendar(2022, Calendar.APRIL, 17,
          19, 0).getTime(), "-", "Piltover-Clash Tag 2", "-:-"),
      new ScheduleEntry(3, ScheduleType.CLASH, new GregorianCalendar(2022, Calendar.APRIL, 30,
          19, 0).getTime(), "-", "Piltover-Clash Tag 3", "-:-"),
      new ScheduleEntry(4, ScheduleType.CLASH, new GregorianCalendar(2022, Calendar.MAY, 1,
          19, 0).getTime(), "-", "Piltover-Clash Tag 4", "-:-"),
      new ScheduleEntry(5, ScheduleType.CLASH, new GregorianCalendar(2022, Calendar.MAY, 13,
          19, 0).getTime(), "-", "Clash Tag 1", "-:-"),
      new ScheduleEntry(6, ScheduleType.CLASH, new GregorianCalendar(2022, Calendar.MAY, 15,
          19, 0).getTime(), "-", "Clash Tag 2", "-:-"),
      new ScheduleEntry(7, ScheduleType.CLASH, new GregorianCalendar(2022, Calendar.MAY, 21,
          19, 0).getTime(), "-", "Clash Tag 3", "-:-"),
      new ScheduleEntry(8, ScheduleType.CLASH, new GregorianCalendar(2022, Calendar.MAY, 22,
          19, 0).getTime(), "-", "Clash Tag 4", "-:-"),
      new ScheduleEntry(10, ScheduleType.VORRUNDE_1, new GregorianCalendar(2022, Calendar.MAY, 28,
          14, 10).getTime(), "TBD", "Kalibrierung Runde 1", "-"),
      new ScheduleEntry(11, ScheduleType.VORRUNDE_2, new GregorianCalendar(2022, Calendar.MAY, 28,
          15, 25).getTime(), "TBD", "Kalibrierung Runde 2", "-:-"),
      new ScheduleEntry(12, ScheduleType.VORRUNDE_3, new GregorianCalendar(2022, Calendar.MAY, 28,
          16, 40).getTime(), "TBD", "Kalibrierung Runde 3", "-:-"),
      new ScheduleEntry(13, ScheduleType.VORRUNDE_4, new GregorianCalendar(2022, Calendar.MAY, 28,
          17, 55).getTime(), "TBD", "Kalibrierung Runde 4", "-:-"),
      new ScheduleEntry(14, ScheduleType.VORRUNDE_5, new GregorianCalendar(2022, Calendar.MAY, 28,
          19, 10).getTime(), "TBD", "Kalibrierung Runde 5", "-:-"),
      new ScheduleEntry(15, ScheduleType.VORRUNDE_6, new GregorianCalendar(2022, Calendar.MAY, 29,
          14, 10).getTime(), "TBD", "Kalibrierung Runde 6", "-:-"),
      new ScheduleEntry(16, ScheduleType.VORRUNDE_7, new GregorianCalendar(2022, Calendar.MAY, 29,
          15, 25).getTime(), "TBD", "Kalibrierung Runde 7", "-:-"),
      new ScheduleEntry(17, ScheduleType.VORRUNDE_8, new GregorianCalendar(2022, Calendar.MAY, 29,
          16, 40).getTime(), "TBD", "Kalibrierung Runde 8", "-:-"),
      new ScheduleEntry(18, ScheduleType.VORRUNDE_9, new GregorianCalendar(2022, Calendar.MAY, 29,
          17, 55).getTime(), "TBD", "Kalibrierung Runde 9", "-:-"),
      new ScheduleEntry(19, ScheduleType.VORRUNDE_10, new GregorianCalendar(2022, Calendar.MAY, 29,
          19, 10).getTime(), "TBD", "Kalibrierung Runde 10", "-:-"),
      new ScheduleEntry(20, ScheduleType.SIGN_IN, new GregorianCalendar(2022, Calendar.MAY, 28,
          13, 10).getTime(), "", "Ende Anmeldung", "")
      ));

  static {
    scheduled.sort((o1, o2) -> (int) (o1.getStartTimeDate().getTime() - o2.getStartTimeDate().getTime()));
  }

  public List<ScheduleEntry> getScheduled() {
    final ScheduleEntry last = scheduled.stream()
        .filter(entry -> entry.getStartTimeDate().getTime() <= new Date().getTime())
        .reduce((first, second) -> second).orElse(null);
    final List<ScheduleEntry> scheduledSelection = last == null ? new ArrayList<>() : new ArrayList<>(Collections.singletonList(last));
    scheduledSelection.addAll(
        scheduled.stream()
            .filter(entry -> entry.getStartTimeDate().getTime() > new Date().getTime())
            .collect(Collectors.toList())
    );
    return scheduledSelection.size() >= 15 ? scheduledSelection.subList(0, 15) : scheduledSelection;
  }

  public String addSchedule() {
    final ScheduleEntry schedule = new ScheduleEntry(id, type, startTime, enemyTeam, title, score);
    schedule.setEndTime(endTime);
    scheduled.add(schedule);
    return null;
  }

  public String deleteTeam(ScheduleEntry scheduleEntry) {
    scheduled.add(scheduleEntry);
    return null;
  }

  public String editTeam(ScheduleEntry scheduleEntry) {
    scheduleEntry.setCanEdit(true);
    return null;
  }

  public String saveTeams() {

    for (ScheduleEntry entry : scheduled) {
      entry.setCanEdit(false);
    }
    return null;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
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

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  public Date getEndTime() {
    return endTime;
  }

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }

  public String getEnemyTeam() {
    return enemyTeam;
  }

  public void setEnemyTeam(String enemyTeam) {
    this.enemyTeam = enemyTeam;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getScore() {
    return score;
  }

  public void setScore(String score) {
    this.score = score;
  }
}
