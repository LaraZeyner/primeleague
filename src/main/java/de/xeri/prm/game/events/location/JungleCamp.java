package de.xeri.prm.game.events.location;

import de.xeri.prm.util.Util;
import lombok.val;

/**
 * Created by Lara on 05.05.2022 for web
 */
public enum JungleCamp {
  TOP_SCUTTLE(new Position(4250, 9800), new Position(4250, 9800), "River 1"),
  BLUE(new Position(3650, 8000), new Position(11100, 6750), "Blue"),
  GROMP(new Position(2250, 8450), new Position(12750, 6550), "Gromp"),
  WOLVES(new Position(3550, 6525), new Position(11450, 8475), "Wolves"),
  BOT_SCUTTLE(new Position(10250, 5300), new Position(10250, 5300), "River 2"),
  RED(new Position(7650, 3800), new Position(7000, 11350), "Red"),
  KRUGS(new Position(8450, 2525), new Position(6550, 12475), "Krugs"),
  RAPTORS(new Position(7000, 5300), new Position(8000, 9700), "Raptors");

  private final Position position1;
  private final Position position2;
  private final String name;

  JungleCamp(Position position1, Position position2, String name) {
    this.position1 = position1;
    this.position2 = position2;
    this.name = name;
  }

  public double distance(Position position) {
    return Math.min(Util.distance(position, position1), Util.distance(position, position2));
  }

  public static String getClosestCampName(Position position, boolean blueSide) {
    val camp = getClosestCampToPosition(position);
    if (camp.distance(position) > 2000) {
      return "";
    }
    boolean blueCloser = Util.distance(position, camp.position1) <= Util.distance(position, camp.position2);
    if (blueCloser != blueSide) {
      return "Enemy " + camp.name;
    }
    return camp.name;
  }

  public static JungleCamp getClosestCampToPosition(Position position) {
    JungleCamp campo = null;
    for (JungleCamp camp : JungleCamp.values()) {
      final double distance = camp.distance(position);
      if (campo == null || campo.distance(position) > distance) {
        campo = camp;
      }
    }
    return campo;
  }
}
