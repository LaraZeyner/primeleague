package de.xeri.league.servlet.controller;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import de.xeri.league.servlet.plan.ScheduleBean;
import de.xeri.league.servlet.plan.ScheduleEntry;

/**
 * Created by Lara on 04.04.2022 for web
 */
@ManagedBean
@RequestScoped
public class PlayerController {
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
    return "player";
  }
}

