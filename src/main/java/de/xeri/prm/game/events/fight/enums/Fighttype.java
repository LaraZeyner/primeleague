package de.xeri.prm.game.events.fight.enums;

/**
 * Created by Lara on 02.05.2022 for web
 */
public enum Fighttype {
  /**
   * 1 gegen 1 Fight
   * <p>
   * Danach passiert nichts mehr (waehrend beim <b>Solokill</b> mehr passieren darf)
   */
  DUEL,

  /**
   * 2+ gegen 1 Fights
   */
  PICK,

  /**
   * 2 gegen 2+ Fights
   */
  SKIRMISH,

  /**
   * Fight mit 3 Spielern pro Team
   */
  TEAMFIGHT
}
