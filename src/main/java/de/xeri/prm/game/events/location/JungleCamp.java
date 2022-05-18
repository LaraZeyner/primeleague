package de.xeri.prm.game.events.location;

import de.xeri.prm.util.Util;
import lombok.val;

/**
 * Created by Lara on 05.05.2022 for web
 */
public enum JungleCamp {
  TOP_SCUTTLE(new RelativePosition(.28947, .65697), new RelativePosition(.28947, .65697), "River 1"),
  BLUE(new RelativePosition(.24856, .53358), new RelativePosition(.75144, .46642), "Blue"),
  GROMP(new RelativePosition(.14733, .56836), new RelativePosition(.85267, .43164), "Gromp"),
  WOLVES(new RelativePosition(.25073, .43620), new RelativePosition(.74927, .56380), "Wolves"),
  BOT_SCUTTLE(new RelativePosition(.71053, .34303), new RelativePosition(.71053, .34303), "River 2"),
  RED(new RelativePosition(.52249, .25960), new RelativePosition(.47751, .74040), "Red"),
  KRUGS(new RelativePosition(.56918, .16873), new RelativePosition(.43082, .83127), "Krugs"),
  RAPTORS(new RelativePosition(.48000, .35000), new RelativePosition(.52000, .65000), "Raptors");

  private final RelativePosition position1;
  private final RelativePosition position2;
  private final String name;

  JungleCamp(RelativePosition position1, RelativePosition position2, String name) {
    this.position1 = position1;
    this.position2 = position2;
    this.name = name;
  }

  public double distance(Position position) {
    return Math.min(Util.distance(position, position1.getPosition()), Util.distance(position, position2.getPosition()));
  }

  public static String getClosestCampName(Position position, boolean blueSide) {
    val camp = getClosestCampToPosition(position);
    if (camp == null) {
      return "";
    }
    boolean blueCloser = Util.distance(position, camp.position1.getPosition()) <= Util.distance(position, camp.position2.getPosition());
    if (blueCloser != blueSide) {
      return "Enemy " + camp.name;
    }
    return camp.name;
  }

  public static JungleCamp getClosestCampToPosition(Position position) {
    JungleCamp campo = null;
    for (JungleCamp camp : JungleCamp.values()) {
      final double distance = camp.distance(position);
      if (camp.distance(position) > 2250 && (campo == null || campo.distance(position) > distance)) {
        campo = camp;
      }
    }
    return campo;
  }
}
