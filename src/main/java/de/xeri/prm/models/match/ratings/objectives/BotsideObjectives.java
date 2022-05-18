package de.xeri.prm.models.match.ratings.objectives;

import java.util.List;

import de.xeri.prm.models.enums.DragonSoul;
import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.OutputType;
import de.xeri.prm.models.match.ratings.Stat;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.playerperformance.Playerperformance;

/**
 * Created by Lara on 12.05.2022 for web
 */
public class BotsideObjectives extends RatingSubcategory {
  private final List<Playerperformance> playerperformances;
  private final Lane lane;

  public BotsideObjectives(List<Playerperformance> playerperformances, Lane lane) {
    this.playerperformances = playerperformances;
    this.lane = lane;
  }

  public double get() {
    return handleValues(getDragonTime(), getDragonTakedowns(), getElderTime(), getFirstDrake(), getSoulrateAndPerfect());
  }

  public Stat getDragonTime() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane)
        .map(p -> p.getTeamperformance().getFirstDragonTime())
        .reverse();
  }

  public Stat getDragonTakedowns() {
    return new Stat(playerperformances, OutputType.NUMBER, 3, lane)
        .map(Playerperformance::getDragonTakedowns)
        .nullable()
        .sub("Keine", p -> p.getTeamperformance().getDrakes() == 0 ? 1 : 0)
        .sub("1 Mal", p -> p.getTeamperformance().getDrakes() == 1 ? 1 : 0)
        .sub("2 Mal", p -> p.getTeamperformance().getDrakes() == 2 ? 1 : 0)
        .sub("3 Mal", p -> p.getTeamperformance().getDrakes() == 3 ? 1 : 0)
        .sub("4 Mal", p -> p.getTeamperformance().getDrakes() == 4 ? 1 : 0)
        .sub("öfter", p -> p.getTeamperformance().getDrakes() > 4 ? 1 : 0);
  }

  public Stat getElderTime() {
    return new Stat(playerperformances, OutputType.TIME, 2, lane)
        .map(p -> p.getTeamperformance().getElderTime())
        .reverse();
  }

  public Stat getFirstDrake() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane, null)
        .map(p -> p.getTeamperformance().isFirstDrake() ? 1 : 0)
        .nullable();
  }

  public Stat getSoulrateAndPerfect() {
    return new Stat(playerperformances, OutputType.PERCENT, 3, lane) {
      @Override
      public double average() {
        return .75;
      }

      @Override
      public double maximum() {
        return 6;
      }

      @Override
      public double minimum() {
        return 0;
      }
    }.map(Playerperformance::getSoulratePerfect)
    .nullable()
        .sub("Cloud-Soul",
            p -> p.getTeamperformance().getSoul() != null && p.getTeamperformance().getSoul().equals(DragonSoul.CLOUD) ? 1 : 0)
        .sub("Hextech-Soul",
            p -> p.getTeamperformance().getSoul() != null && p.getTeamperformance().getSoul().equals(DragonSoul.HEXTECH) ? 1 : 0)
        .sub("Infernal-Soul",
            p -> p.getTeamperformance().getSoul() != null && p.getTeamperformance().getSoul().equals(DragonSoul.INFERNAL) ? 1 : 0)
        .sub("Mountain-Soul",
            p -> p.getTeamperformance().getSoul() != null && p.getTeamperformance().getSoul().equals(DragonSoul.MOUNTAIN) ? 1 : 0)
        .sub("Ocean-Soul",
            p -> p.getTeamperformance().getSoul() != null && p.getTeamperformance().getSoul().equals(DragonSoul.OCEAN) ? 1 : 0);
  }

}
