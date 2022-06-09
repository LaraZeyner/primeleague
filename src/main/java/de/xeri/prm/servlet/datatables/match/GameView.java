package de.xeri.prm.servlet.datatables.match;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.league.Team;
import de.xeri.prm.models.match.Game;
import de.xeri.prm.models.match.Teamperformance;
import de.xeri.prm.models.match.playerperformance.Playerperformance;
import de.xeri.prm.util.Util;
import lombok.Data;

/**
 * Created by Lara on 08.06.2022 for web
 */
@Data
public class GameView implements Serializable {
  private List<PerformanceView> performanceViews;
  private String mode;
  private String date;
  private String result;
  private String duration;

  public GameView(Game game, Team team) {
    final Teamperformance teamperformance = game.getPerformanceOf(team);
    if (teamperformance != null) {
      this.date = Util.until(game.getGameStart(), "");
      this.mode = game.getGametype().getName();
      this.duration = game.getDurationString();
      this.result = teamperformance.isWin() ? "VICTORY" : "DEFEAT";

      this.performanceViews = Arrays.asList(new PerformanceView(Lane.TOP), new PerformanceView(Lane.JUNGLE),
          new PerformanceView(Lane.MIDDLE), new PerformanceView(Lane.BOTTOM), new PerformanceView(Lane.UTILITY));

      for (Playerperformance playerperformance : teamperformance.getPlayerperformances()) {
        if (playerperformance.getLane() != null && playerperformance.getLane().ordinal() < 5) {
          performanceViews.get(playerperformance.getLane().ordinal()).setPlayerperformance(playerperformance);
        }
      }
    }
  }
}
