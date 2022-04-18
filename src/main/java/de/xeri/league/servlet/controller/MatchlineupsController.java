package de.xeri.league.servlet.controller;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import de.xeri.league.models.enums.Lane;
import de.xeri.league.servlet.plan.ScheduleBean;
import de.xeri.league.servlet.plan.ScheduleEntry;
import de.xeri.league.util.Const;

/**
 * Created by Lara on 04.04.2022 for web
 */
@ManagedBean
@RequestScoped
public class MatchlineupsController {
  private ScheduleEntry scheduleEntry;

  public ScheduleEntry getScheduleEntry() {
    return scheduleEntry;
  }

  public void setScheduleEntry(ScheduleEntry scheduleEntry) {
    this.scheduleEntry = scheduleEntry;
  }

  public String doLookup(int id) {
    scheduleEntry = new ScheduleBean().getScheduled().stream().filter(match -> id == match.getId())
        .findFirst().orElse(null);
    return "lineupsoverview";
  }

  // TODO: 18.04.2022 live
  public String doLookup(String id) {
    scheduleEntry = new ScheduleBean().getScheduled().stream().filter(match -> Const.TEAMID == match.getId())
        .findFirst().orElse(null);
    return "lineupsoverview";
  }

  public String doLookup(int id, Lane lane) {
    scheduleEntry = new ScheduleBean().getScheduled().stream().filter(match -> id == match.getId())
        .findFirst().orElse(null);
    return "lineuplane";
  }

  // TODO: 18.04.2022 live
  public String doLookup(String id, String laneString) {
    Lane lane = Lane.valueOf(laneString);
    scheduleEntry = new ScheduleBean().getScheduled().stream().filter(match -> Const.TEAMID == match.getId())
        .findFirst().orElse(null);
    return "lineuplane";
  }
}

