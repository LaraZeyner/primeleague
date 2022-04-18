package de.xeri.league.servlet.controller;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import de.xeri.league.servlet.plan.ScheduleBean;
import de.xeri.league.servlet.plan.ScheduleEntry;
import de.xeri.league.util.Const;

/**
 * Created by Lara on 04.04.2022 for web
 */
@ManagedBean
@RequestScoped
public class LeagueController {
  private ScheduleEntry scheduleEntry;

  public ScheduleEntry getScheduleEntry() {
    return scheduleEntry;
  }

  public void setScheduleEntry(ScheduleEntry scheduleEntry) {
    this.scheduleEntry = scheduleEntry;
  }

  public String doLookupOverview() {
    return doLookupOverview(Const.TEAMID);
  }

  public String doLookupOverview(int id) {
    scheduleEntry = new ScheduleBean().getScheduled().stream().filter(match -> id == match.getId())
        .findFirst().orElse(null);
    return "leagueoverview";
  }

  public String doLookupTable() {
    return doLookupTable(Const.TEAMID);
  }

  public String doLookupTable(int id) {
    scheduleEntry = new ScheduleBean().getScheduled().stream().filter(match -> id == match.getId())
        .findFirst().orElse(null);
    return "leaguetable";
  }

  public String doLookupMatches() {
    return doLookupMatches(Const.TEAMID);
  }

  public String doLookupMatches(int id) {
    scheduleEntry = new ScheduleBean().getScheduled().stream().filter(match -> id == match.getId())
        .findFirst().orElse(null);
    return "leaguematches";
  }

  public String doLookupTeams() {
    return doLookupMatches(Const.TEAMID);
  }

  public String doLookupTeams(int id) {
    scheduleEntry = new ScheduleBean().getScheduled().stream().filter(match -> id == match.getId())
        .findFirst().orElse(null);
    return "leaguematches";
  }
}

