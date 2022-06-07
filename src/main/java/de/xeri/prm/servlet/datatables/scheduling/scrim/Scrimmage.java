package de.xeri.prm.servlet.datatables.scheduling.scrim;

import java.util.Date;

import de.xeri.prm.models.league.Team;
import lombok.Data;
import lombok.NonNull;

/**
 * Created by Lara on 02.06.2022 for web
 */
@Data
public class Scrimmage extends ScheduledMatch {

  public Scrimmage(Team opponent, @NonNull Date date, @NonNull int games) {
    super(opponent, date, games);
  }
}
