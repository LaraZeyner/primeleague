package de.xeri.prm.game.events.location;

import de.xeri.prm.util.Const;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Lara on 04.05.2022 for web
 */
@AllArgsConstructor
@Getter
public class RelativePosition {
  private double x;
  private double y;

  public Position getPosition() {
    final int realX = (int) (x * Const.MAP_SIZE);
    final int realY = (int) (y * Const.MAP_SIZE);
    return new Position(realX, realY);
  }
}
