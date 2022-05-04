package de.xeri.league.game.events.fight;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Lara on 02.05.2022 for web
 */
public class Teamfight extends Fight {

  public Teamfight(Set<Kill> kills) {
    super(kills);
  }

  public double getDeathOrder(int pId) {
    int totalKills = kills.size();
    for (int i = 0; i < kills.size(); i++) {
      Kill kill = new ArrayList<>(kills).get(i);
      if (kill.getVictim() == pId) {
        return i * 10.0 / totalKills;
      }
    }
    return 0;
  }
}
