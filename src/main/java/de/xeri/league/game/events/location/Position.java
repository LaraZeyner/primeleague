package de.xeri.league.game.events.location;

import java.io.Serializable;

import de.xeri.league.util.Const;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by Lara on 14.04.2022 for web
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Position implements Serializable {
  private static final transient long serialVersionUID = 8321719493844687789L;

  private int x;
  private int y;

  /**
   * Prozentuale Entfernung von der Mitte
   * <ul>
   *   <li><b>River:</b> 13.484%</li>
   *   <li><b>Outer:</b> 27.883% </li>
   *   <li><b>Inner:</b> 45.760%</li>
   *   <li><b>Inhib:</b> 63.636%</li>
   *   <li><b>Base:</b> 100%</li>
   * </ul>
   *
   * @param blue Spieler spielt auf linker Seite
   * @return Aggressionswert
   */
  public double getTotalAggression(boolean blue) {
    return blue ? ((x + y) * 1d / Const.MAP_SIZE - 1) : (((x + y) * 1d / Const.MAP_SIZE - 1) * -1);
  }

}
