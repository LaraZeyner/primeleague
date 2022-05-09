package de.xeri.league.servlet.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.xeri.league.models.enums.ScheduleType;

/**
 * Created by Lara on 04.04.2022 for web
 */
public class ScheduleEntry {
  private int id;
  private ScheduleType type;
  private Date startTime;
  private Date endTime;
  private String enemyTeam;
  private String title;
  private String score;
  private final String displayDate;
  private final String displayCountdown;
  private boolean canEdit;

  public ScheduleEntry(int id, ScheduleType type, Date startTime, String enemyTeam, String title, String score) {
    this.id = id;
    this.type = type;
    this.startTime = startTime;
    this.endTime = new Date(type.getDuration() + startTime.getTime());
    this.enemyTeam = enemyTeam;
    this.title = title;
    this.score = score;
    long duration;
    final String cd;
    if (type.getDuration() + startTime.getTime() <= new Date().getTime()) {
      cd = "vor ";
      duration = (type.getDuration() + startTime.getTime()) - new Date().getTime();
    } else if (startTime.getTime() <= new Date().getTime()) {
      cd = "seit ";
      duration = startTime.getTime() - new Date().getTime();
    } else {
      cd = "in ";
      duration = startTime.getTime() - new Date().getTime();
    }
    duration = duration / 1000;

    final Calendar calendar = Calendar.getInstance();
    calendar.setTime(startTime);
    final int millis = calendar.get(Calendar.HOUR_OF_DAY) * 3_600 + calendar.get(Calendar.MINUTE) * 60;

    if (duration - millis < -604_800) {
      displayDate = new SimpleDateFormat("EEE dd.MM. HH:mm").format(startTime);
    } else if (duration - millis < -259_200) {
      displayDate = "letzten " + new SimpleDateFormat("EEE").format(startTime)
          + " um " + new SimpleDateFormat("HH:mm").format(startTime);
    } else if (duration - millis < -172_800) {
      displayDate = "vorgestern um " + new SimpleDateFormat("HH:mm").format(startTime);
    } else if (duration - millis < -86_400) {
      displayDate = "gestern um " + new SimpleDateFormat("HH:mm").format(startTime);
    } else if (duration - millis <= 0) {
      displayDate = "heute um " + new SimpleDateFormat("HH:mm").format(startTime);
    } else if (duration - millis <= 86_400) {
      displayDate = "morgen um " + new SimpleDateFormat("HH:mm").format(startTime);
    } else if (duration - millis <= 172_800) {
      displayDate = "übermorgen um " + new SimpleDateFormat("HH:mm").format(startTime);
    } else if (duration - millis <= 604_800) {
      displayDate = "nächsten " + new SimpleDateFormat("EEE").format(startTime)
          + " um " + new SimpleDateFormat("HH:mm").format(startTime);
    } else {
      displayDate = new SimpleDateFormat("EEE dd.MM. HH:mm").format(startTime);
    }

    duration = Math.abs(duration);

    if (duration < 99) {
      displayCountdown = cd + duration + " Sekunden";
    } else if (cd.equals("in ") && duration < 901) {
      displayCountdown = cd + duration / 60 + ":" + duration % 60 + " min";
    } else if (cd.equals("seit ") && duration < 6000) {
      displayCountdown = cd + duration / 60 + ":" + duration % 60 + " min";
    } else if (duration < 6000) {
      displayCountdown = cd + duration / 60 + " Minuten";
    } else if (duration < 36_000) {
      displayCountdown = cd + duration / 3_600 + ":" + (duration % 3600) / 60 + " h";
    } else if (duration < 180_000) {
      displayCountdown = cd + duration / 60 + " Stunden";
    } else if (duration < 604_800) {
      displayCountdown = cd + duration / 86_400 + ":" + (duration % 86_400) / 3_600 + " d";
    } else {
      displayCountdown = cd + duration / 86_400 + " Tage";
    }


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

  public String getStartTime() {
    return new SimpleDateFormat("EEE dd.MM. HH:mm").format(startTime);
  }

  public Date getStartTimeDate() {
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

  public boolean isCanEdit() {
    return canEdit;
  }

  public void setCanEdit(boolean canEdit) {
    this.canEdit = canEdit;
  }

  public String getDisplayCountdown() {
    return displayCountdown;
  }

  public String getDisplayDate() {
    return displayDate;
  }
}