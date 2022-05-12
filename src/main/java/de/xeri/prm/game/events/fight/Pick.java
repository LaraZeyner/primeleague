package de.xeri.prm.game.events.fight;

import java.util.Set;

/**
 * Created by Lara on 02.05.2022 for web
 */
public class Pick extends Fight {
  public Pick(Set<Kill> kills) {
    super(kills);
  }
}
