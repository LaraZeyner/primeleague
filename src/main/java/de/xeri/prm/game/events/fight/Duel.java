package de.xeri.prm.game.events.fight;

import java.util.Set;

/**
 * Created by Lara on 02.05.2022 for web
 */
public class Duel extends Fight {
  public Duel(Set<Kill> kills) {
    super(kills);
  }
}
