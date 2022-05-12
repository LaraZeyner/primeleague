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

  private static final RelativePosition TOPLANE_BLUE_OUTER = new RelativePosition(0, 0.24954); // A
  private static final RelativePosition TOPLANE_BLUE_INNER = new RelativePosition(0.11409, 0.24954); // B
  private static final RelativePosition TOPLANE_INNER_LEFT = new RelativePosition(0.11409, 0.60708); // C
  private static final RelativePosition TOPLANE_INNER_RIGHT = new RelativePosition(0.39292, 0.88591); // D
  private static final RelativePosition TOPLANE_RED_INNER = new RelativePosition(0.75046, 0.88591); // E
  private static final RelativePosition TOPLANE_RED_OUTER = new RelativePosition(0.75046, 1); // F
  private static final RelativePosition TOPLANE_ALCOVE = new RelativePosition(0, 1); // G
  private static final RelativePosition BOTLANE_BLUE_OUTER = new RelativePosition(0.24954, 0); // H
  private static final RelativePosition BOTLANE_BLUE_INNER = new RelativePosition(0.24954, 0.11409); // I
  private static final RelativePosition BOTLANE_INNER_LEFT = new RelativePosition(0.60708, 0.11409); // J
  private static final RelativePosition BOTLANE_INNER_RIGHT = new RelativePosition(0.88591, 0.39292); // K
  private static final RelativePosition BOTLANE_RED_INNER = new RelativePosition(0.88591, 0.75046); // L
  private static final RelativePosition BOTLANE_RED_OUTER = new RelativePosition(1, 0.75046); // M
  private static final RelativePosition BOTLANE_ALCOVE = new RelativePosition(1, 0); // N
  private static final RelativePosition BASE_BLUE = new RelativePosition(0, 0); // O
  private static final RelativePosition BASE_RED = new RelativePosition(1, 1); // P
  private static final RelativePosition TOPLANE_BLUE_RIVER = new RelativePosition(0.18609, 0.67907); // Q
  private static final RelativePosition MID_TOP_BLUE_RIVER = new RelativePosition(0.36486, 0.5); // R
  private static final RelativePosition MID_TOP_RED_RIVER = new RelativePosition(0.5, 0.63575); // S
  private static final RelativePosition TOPLANE_RED_RIVER = new RelativePosition(0.32154, 0.81452); // T
  private static final RelativePosition MID_BOT_BLUE_RIVER = new RelativePosition(0.5, 0.36486); // U
  private static final RelativePosition BOTLANE_BLUE_RIVER = new RelativePosition(0.67907, 0.18609); // V
  private static final RelativePosition BOTLANE_RED_RIVER = new RelativePosition(0.81452, 0.32154); // W
  private static final RelativePosition MID_BOT_RED_RIVER = new RelativePosition(0.63575, 0.5); // X

  public static final MapArea MAP = new MapArea(SuperArea.ALL, new RelativePosition(0.5, 0.5)).add(
      BASE_BLUE, BOTLANE_ALCOVE, BASE_RED, TOPLANE_ALCOVE);

  public static final MapArea TOPLANE = new MapArea(SuperArea.LANE, new RelativePosition(0.11409, 0.88591)).add(
      TOPLANE_BLUE_OUTER, TOPLANE_ALCOVE, TOPLANE_RED_OUTER, TOPLANE_RED_INNER, TOPLANE_INNER_RIGHT, TOPLANE_INNER_LEFT, TOPLANE_BLUE_INNER);

  public static final MapArea MIDLANE = new MapArea(SuperArea.LANE, new RelativePosition(0.5, 0.5)).add(
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

  public static final MapArea TOP_BLUE_JUNGLE = new MapArea(SuperArea.JUNGLE, new RelativePosition(0.19481, 0.50903)).add(
      TOPLANE_BLUE_INNER, TOPLANE_INNER_LEFT, TOPLANE_BLUE_RIVER, MID_TOP_BLUE_RIVER);

  public static final MapArea BOT_BLUE_JUNGLE = new MapArea(SuperArea.JUNGLE, new RelativePosition(0.50903, 0.19481)).add(
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
