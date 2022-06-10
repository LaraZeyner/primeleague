package de.xeri.prm.servlet.loader.ourteam.scrim;

import java.io.Serializable;
import java.util.Date;

import de.xeri.prm.models.league.Team;
import de.xeri.prm.util.Util;
import lombok.Data;
import lombok.NonNull;

/**
 * Created by Lara on 02.06.2022 for web
 */
@Data
public class ScheduledMatch implements Serializable {
  private final Team opponent;

  @NonNull
  private Date date;

  @NonNull
  private int games;
  private int home;
  private int guest;

  public void addWinHome() {
    this.home++;
    checkGameAmount();
  }

  public void addWinGuest() {
    this.guest++;
    checkGameAmount();
  }


  private void checkGameAmount() {
    if (home + guest > games) {
      this.games = home + guest;
    }
  }

  public String getScore() {
    if (date.after(new Date())) {
      return home + ":" + guest;
    }
    return Util.until(date, "in ");
  }

  public boolean isClosed() {
    return games <= home + guest;
  }
}
