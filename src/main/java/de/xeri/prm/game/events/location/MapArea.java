package de.xeri.prm.game.events.location;

import java.awt.Polygon;
import java.util.Arrays;
import java.util.List;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.util.Util;
import lombok.Getter;

/**
 * Created by Lara on 02.05.2022 for web
 */
@Getter
public class MapArea {

  public static Lane getNearestLane(Position position) {
    final double topDistance = Util.distance(TOPLANE.getCenter(), position);
    final double midDistance = Util.distance(MIDLANE.getCenter(), position);
    final double botDistance = Util.distance(BOTLANE.getCenter(), position);
    final double min = Math.min(Math.min(topDistance, midDistance), botDistance);
    if (min == topDistance) {
      return Lane.TOP;
    } else if (min == botDistance) {
      return Lane.BOTTOM;
    }
    return Lane.MIDDLE;
  }

  private static final double deapth = 0.11409; // 0.135-0.200  < 0.2
  private static final double length = 0.3375; // 0.230-0.450 >1.7x width
  private static final RelativePosition TOPLANE_BLUE_OUTER = new RelativePosition(0, (Math.sqrt(.5) + 1) * deapth); // A
  private static final RelativePosition TOPLANE_BLUE_INNER = new RelativePosition(deapth, (Math.sqrt(.5) + 1) * deapth); // B
  private static final RelativePosition TOPLANE_INNER_LEFT = new RelativePosition(deapth, (1 - length)); // C
  private static final RelativePosition TOPLANE_INNER_RIGHT = new RelativePosition(length, (1 - deapth)); // D
  private static final RelativePosition TOPLANE_RED_INNER = new RelativePosition(1 - (Math.sqrt(.5) + 1) * deapth, (1 - deapth)); // E
  private static final RelativePosition TOPLANE_RED_OUTER = new RelativePosition(1 - (Math.sqrt(.5) + 1) * deapth, 1); // F
  private static final RelativePosition TOPLANE_ALCOVE = new RelativePosition(0, 1); // G
  private static final RelativePosition BOTLANE_BLUE_OUTER = new RelativePosition((Math.sqrt(.5) + 1) * deapth, 0); // H
  private static final RelativePosition BOTLANE_BLUE_INNER = new RelativePosition((Math.sqrt(.5) + 1) * deapth, deapth); // I
  private static final RelativePosition BOTLANE_INNER_LEFT = new RelativePosition((1 - length), deapth); // J
  private static final RelativePosition BOTLANE_INNER_RIGHT = new RelativePosition((1 - deapth), length); // K
  private static final RelativePosition BOTLANE_RED_INNER = new RelativePosition((1 - deapth), (1 - (Math.sqrt(.5) + 1) * deapth)); // L
  private static final RelativePosition BOTLANE_RED_OUTER = new RelativePosition(1, (1 - (Math.sqrt(.5) + 1) * deapth)); // M
  private static final RelativePosition BOTLANE_ALCOVE = new RelativePosition(1, 0); // N
  private static final RelativePosition BASE_BLUE = new RelativePosition(0, 0); // O
  private static final RelativePosition BASE_RED = new RelativePosition(1, 1); // P
  private static final RelativePosition TOPLANE_BLUE_RIVER =
      new RelativePosition(((1 - Math.sqrt(.5)) * deapth + length) / 2, 1 - ((Math.sqrt(.5) + 1) * deapth + length) / 2); // Q
  private static final RelativePosition MID_TOP_BLUE_RIVER = new RelativePosition(.5 - Math.sqrt(.5) * deapth, .5); // R
  private static final RelativePosition MID_TOP_RED_RIVER = new RelativePosition(.5, Math.sqrt(.5) * deapth + .5); // S
  private static final RelativePosition TOPLANE_RED_RIVER =
      new RelativePosition(((Math.sqrt(.5) + 1) * deapth + length) / 2, ((Math.sqrt(.5) - 1) * deapth - length) / 2 + 1); // T
  private static final RelativePosition MID_BOT_BLUE_RIVER = new RelativePosition(.5, .5 - Math.sqrt(.5) * deapth); // U
  private static final RelativePosition BOTLANE_BLUE_RIVER =
      new RelativePosition(1 - ((1 + Math.sqrt(.5)) * deapth + length) / 2, ((1 - Math.sqrt(.5)) * deapth + length) / 2); // V
  private static final RelativePosition BOTLANE_RED_RIVER =
      new RelativePosition(((Math.sqrt(.5) - 1) * deapth - length) / 2 + 1, ((Math.sqrt(.5) + 1) * deapth + length) / 2); // W
  private static final RelativePosition MID_BOT_RED_RIVER = new RelativePosition(Math.sqrt(.5) * deapth + .5, .5); // X

  public static final MapArea MAP = new MapArea(SuperArea.ALL, new RelativePosition(.5, .5)).add(
      BASE_BLUE, BOTLANE_ALCOVE, BASE_RED, TOPLANE_ALCOVE);

  public static final MapArea TOPLANE = new MapArea(SuperArea.LANE, new RelativePosition(0.11409, 0.88591)).add(
      TOPLANE_BLUE_OUTER, TOPLANE_ALCOVE, TOPLANE_RED_OUTER, TOPLANE_RED_INNER, TOPLANE_INNER_RIGHT, TOPLANE_INNER_LEFT, TOPLANE_BLUE_INNER);

  public static final MapArea MIDLANE = new MapArea(SuperArea.LANE, new RelativePosition(.5, .5)).add(
      TOPLANE_BLUE_INNER, BOTLANE_BLUE_INNER, BOTLANE_RED_INNER, TOPLANE_RED_INNER);

  public static final MapArea BOTLANE = new MapArea(SuperArea.LANE, new RelativePosition(0.88591, 0.11409)).add(
      BOTLANE_BLUE_OUTER, BOTLANE_BLUE_INNER, BOTLANE_INNER_LEFT, BOTLANE_INNER_RIGHT, BOTLANE_RED_INNER, BOTLANE_RED_OUTER, BOTLANE_ALCOVE);

  public static final MapArea BLUE_BASE = new MapArea(SuperArea.BASE, new RelativePosition(0, 0)).add(
      BASE_BLUE, BOTLANE_BLUE_OUTER, BOTLANE_BLUE_INNER, TOPLANE_BLUE_INNER, TOPLANE_BLUE_OUTER);

  public static final MapArea RED_BASE = new MapArea(SuperArea.BASE, new RelativePosition(1, 1)).add(
      BASE_RED, TOPLANE_RED_OUTER, TOPLANE_RED_INNER, BOTLANE_RED_INNER, BOTLANE_RED_OUTER);

  public static final MapArea TOP_RIVER = new MapArea(SuperArea.RIVER, new RelativePosition(0.34350, 0.65772)).add(
      TOPLANE_BLUE_RIVER, MID_TOP_BLUE_RIVER, MID_TOP_RED_RIVER, TOPLANE_RED_RIVER);

  public static final MapArea BOT_RIVER = new MapArea(SuperArea.RIVER, new RelativePosition(0.65772, 0.34350)).add(
      MID_BOT_BLUE_RIVER, BOTLANE_BLUE_RIVER, BOTLANE_RED_RIVER, MID_BOT_RED_RIVER);

  public static final MapArea TOP_BLUE_JUNGLE = new MapArea(SuperArea.JUNGLE, new RelativePosition(0.19481, .50903)).add(
      TOPLANE_BLUE_INNER, TOPLANE_INNER_LEFT, TOPLANE_BLUE_RIVER, MID_TOP_BLUE_RIVER);

  public static final MapArea BOT_BLUE_JUNGLE = new MapArea(SuperArea.JUNGLE, new RelativePosition(.50903, 0.19481)).add(
      BOTLANE_BLUE_INNER, BOTLANE_INNER_LEFT, BOTLANE_BLUE_RIVER, MID_BOT_BLUE_RIVER);

  public static final MapArea TOP_RED_JUNGLE = new MapArea(SuperArea.JUNGLE, new RelativePosition(0.49134, 0.80555)).add(
      TOPLANE_RED_INNER, TOPLANE_INNER_RIGHT, TOPLANE_RED_RIVER, MID_TOP_RED_RIVER);

  public static final MapArea BOT_RED_JUNGLE = new MapArea(SuperArea.JUNGLE, new RelativePosition(0.80555, 0.49134)).add(
      BOTLANE_RED_INNER, BOTLANE_INNER_RIGHT, BOTLANE_RED_RIVER, MID_BOT_RED_RIVER);

  private static final List<MapArea> mapAreas = Arrays.asList(TOPLANE, MIDLANE, BOTLANE, BLUE_BASE, RED_BASE, TOP_RIVER, BOT_RIVER,
      TOP_BLUE_JUNGLE, BOT_BLUE_JUNGLE, TOP_RED_JUNGLE, BOT_RED_JUNGLE);

  public static MapArea getAreaOf(Position position) {
    return mapAreas.stream()
        .filter(mapArea -> mapArea.isInArea(position))
        .findFirst().orElse(MAP);
  }

  public static boolean hasNoArea(Position position) {
    return getAreaOf(position).equals(MAP);
  }

  private final SuperArea category;
  private final Position center;
  private Polygon area;

  public MapArea(SuperArea category, RelativePosition center) {
    this.category = category;
    this.center = center.getPosition();
  }

  public MapArea add(RelativePosition... corners) {
    this.area = getPolygon(corners);
    return this;
  }

  /**
   * Ist die aktuelle Position in diesem Gebiet
   *
   * @param position aktuelle Position
   * @return Position in im Gebiet
   */
  public boolean isInArea(Position position) {
    return area.contains(position.getX(), position.getY());
  }

  /**
   * Wie weit die Position vom Zentrum entfernt ist
   *
   * @param position aktuelle Position
   * @return Entfernung in Units
   */
  public double distance(Position position) {
    return Util.distance(center, position);
  }

  private Polygon getPolygon(RelativePosition... corners) {
    final int[] xCoordinates = Arrays.stream(corners).map(RelativePosition::getPosition).mapToInt(Position::getX).toArray();
    final int[] yCoordinates = Arrays.stream(corners).map(RelativePosition::getPosition).mapToInt(Position::getY).toArray();
    return new Polygon(xCoordinates, yCoordinates, 4);
  }

}
