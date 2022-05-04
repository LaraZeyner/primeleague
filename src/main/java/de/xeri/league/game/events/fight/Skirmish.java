package de.xeri.league.game.events.fight;

import java.util.Set;

/**
 * Created by Lara on 02.05.2022 for web
 */
public class Skirmish extends Fight {
  public Skirmish(Set<Kill> kills) {
    super(kills);
  }
}
