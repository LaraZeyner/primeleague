package de.xeri.prm.game.events.fight;

import java.util.ArrayList;

import de.xeri.prm.game.models.JSONPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Lara on 02.05.2022 for web
 */
@Getter
@AllArgsConstructor
public class Gank {
  private JSONPlayer player;
  private Fight fight;

  public int getOutcome() {
    int gold1 = 0;
    int gold2 = 0;
    for (Kill kill : fight.getKills()) {
      if (kill.getVictim() < 6) {
        gold1 += kill.getGold();
      } else if (kill.getVictim() > 5) {
        gold2 += kill.getGold();
      }
    }
    return player.getId() < 6 ? gold2 - gold1 : gold1 - gold2;
  }

  public boolean wasSuccessful() {
    return getOutcome() > 0;
  }

  public int start() {
    return fight.getStart(player);
  }

  public int end() {
    return fight.getEnd(player);
  }

  public int duration() {
    return fight.duration(player);
  }

  public boolean isCounter() {
    final ArrayList<Kill> kills = new ArrayList<>(fight.getKills());
    return !kills.get(0).isInvolved(player.getId() + 1) ||
        kills.size() > 1 && kills.subList(1, fight.getKills().size()).stream()
            .anyMatch(kill -> kill.isInvolved(player.getId() + 1));
  }
}
