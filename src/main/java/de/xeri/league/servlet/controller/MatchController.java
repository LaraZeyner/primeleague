package de.xeri.league.servlet.controller;

import javax.faces.bean.RequestScoped;
import javax.inject.Named;

import de.xeri.league.util.Const;

/**
 * Created by Lara on 04.04.2022 for web
 */
@Named
@RequestScoped
public class MatchController {
  private ScheduleEntry scheduleEntry;

  public ScheduleEntry getScheduleEntry() {
    return scheduleEntry;
  }

  public void setScheduleEntry(ScheduleEntry scheduleEntry) {
    this.scheduleEntry = scheduleEntry;
  }

  public String doLookup(int id) {
    scheduleEntry = new ScheduleTable().getScheduled().stream().filter(match -> id == match.getId())
        .findFirst().orElse(null);
    return "match";
  }

  // TODO: 18.04.2022 live
  public String doLookup(String id) {
    scheduleEntry = new ScheduleTable().getScheduled().stream().filter(match -> Const.TEAMID == match.getId())
        .findFirst().orElse(null);
    return "match";
  }
}