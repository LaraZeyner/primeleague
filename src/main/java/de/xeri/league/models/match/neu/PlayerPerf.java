package de.xeri.league.models.match.neu;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.xeri.league.models.match.Playerperformance;

/**
 * Created by Lara on 25.04.2022 for web
 */
public class PlayerPerf {
  private final List<Playerperformance> playerperformances = new ArrayList<>();

  public PlayerPerf(Set<Playerperformance> playerperformances) {
    this.playerperformances.addAll(playerperformances);
  }

  public PlayerPerf(Playerperformance playerperformance) {
    this.playerperformances.add(playerperformance);
  }

  public void addPerformance(Playerperformance playerperformance) {
    if (!playerperformances.contains(playerperformance)) {
      playerperformances.add(playerperformance);
    }
  }

  public void removePerformance(Playerperformance playerperformance) {
    playerperformances.remove(playerperformance);
  }

  public String getStat(String statname) {
    return null;
  }

}
