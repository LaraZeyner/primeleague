package de.xeri.league.servlet;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import de.xeri.league.util.Const;

/**
 * Created by Lara on 17.04.2022 for web
 */
@ManagedBean
@SessionScoped
public class Constants {
  private int teamId;

  public Constants() {
    this.teamId = Const.TEAMID;
  }

  public void setTeamId(int teamId) {
    this.teamId = teamId;
  }

  public int getTeamId() {
    return teamId;
  }
}
