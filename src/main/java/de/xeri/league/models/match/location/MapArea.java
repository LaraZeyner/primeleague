package de.xeri.league.models.match.location;

import java.awt.Polygon;
import java.util.Arrays;
import java.util.List;

import de.xeri.league.util.Util;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by Lara on 02.05.2022 for web
 */
@RequiredArgsConstructor
@Getter
public class MapArea {
  private static final Position TOPLANE_BLUE_OUTER = new Position(0, 4090); // A
  private static final Position TOPLANE_BLUE_INNER = new Position(1870, 4090); // B
  private static final Position TOPLANE_INNER_LEFT = new Position(1870, 9950); // C
  private static final Position TOPLANE_INNER_RIGHT = new Position(6440, 14520); // D
  private static final Position TOPLANE_RED_INNER = new Position(12300, 14520); // E
  private static final Position TOPLANE_RED_OUTER = new Position(12300, 16390); // F
  private static final Position TOPLANE_ALCOVE = new Position(0, 16390); // G
  private static final Position BOTLANE_BLUE_OUTER = new Position(4090, 0); // H
  private static final Position BOTLANE_BLUE_INNER = new Position(4090, 1870); // I
  private static final Position BOTLANE_INNER_LEFT = new Position(9950, 1870); // J
  private static final Position BOTLANE_INNER_RIGHT = new Position(14520, 6440); // K
  private static final Position BOTLANE_RED_INNER = new Position(14520, 12300); // L
  private static final Position BOTLANE_RED_OUTER = new Position(16390, 12300); // M
  private static final Position BOTLANE_ALCOVE = new Position(16390, 0); // N
  private static final Position BASE_BLUE = new Position(0, 0); // O
  private static final Position BASE_RED = new Position(16390, 16390); // P
  private static final Position TOPLANE_BLUE_RIVER = new Position(3050, 11130); // Q
  private static final Position MID_TOP_BLUE_RIVER = new Position(5980, 8200); // R
  private static final Position MID_TOP_RED_RIVER = new Position(8200, 10420); // S
  private static final Position TOPLANE_RED_RIVER = new Position(5270, 13350); // T
  private static final Position MID_BOT_BLUE_RIVER = new Position(8200, 5980); // U
  private static final Position BOTLANE_BLUE_RIVER = new Position(11130, 3050); // V
  private static final Position BOTLANE_RED_RIVER = new Position(13350, 5270); // W
  private static final Position MID_BOT_RED_RIVER = new Position(10420, 8200); // X

  public static final MapArea MAP = null;

  public static final MapArea TOPLANE = new MapArea(SuperArea.LANE, new Position(1870, 14520)).add(
      TOPLANE_BLUE_OUTER, TOPLANE_ALCOVE, TOPLANE_RED_OUTER, TOPLANE_RED_INNER, TOPLANE_INNER_RIGHT, TOPLANE_INNER_LEFT, TOPLANE_BLUE_INNER);

  public static final MapArea MIDLANE = new MapArea(SuperArea.LANE, new Position(8200, 8200)).add(
      TOPLANE_BLUE_INNER, BOTLANE_BLUE_INNER, BOTLANE_RED_INNER, TOPLANE_RED_INNER);

  public static final MapArea BOTLANE = new MapArea(SuperArea.LANE, new Position(14520, 1870)).add(
      BOTLANE_BLUE_OUTER, BOTLANE_BLUE_INNER, BOTLANE_INNER_LEFT, BOTLANE_INNER_RIGHT, BOTLANE_RED_INNER, BOTLANE_RED_OUTER, BOTLANE_ALCOVE);

  public static final MapArea BLUE_BASE = new MapArea(SuperArea.BASE, new Position(0, 0)).add(
      BASE_BLUE, BOTLANE_BLUE_OUTER, BOTLANE_BLUE_INNER, TOPLANE_BLUE_INNER, TOPLANE_BLUE_OUTER);

  public static final MapArea RED_BASE = new MapArea(SuperArea.BASE, new Position(16390, 16390)).add(
      BASE_RED, TOPLANE_RED_OUTER, TOPLANE_RED_INNER, BOTLANE_RED_INNER, BOTLANE_RED_OUTER);

  public static final MapArea TOP_RIVER = new MapArea(SuperArea.RIVER, new Position(5630, 10780)).add(
      TOPLANE_BLUE_RIVER, MID_TOP_BLUE_RIVER, MID_TOP_RED_RIVER, TOPLANE_RED_RIVER);

  public static final MapArea BOT_RIVER = new MapArea(SuperArea.RIVER, new Position(10780, 5630)).add(
      MID_BOT_BLUE_RIVER, BOTLANE_BLUE_RIVER, BOTLANE_RED_RIVER, MID_BOT_RED_RIVER);

  public static final MapArea TOP_BLUE_JUNGLE = new MapArea(SuperArea.JUNGLE, new Position(3193, 8343)).add(
      TOPLANE_BLUE_INNER, TOPLANE_INNER_LEFT, TOPLANE_BLUE_RIVER, MID_TOP_BLUE_RIVER);

  public static final MapArea BOT_BLUE_JUNGLE = new MapArea(SuperArea.JUNGLE, new Position(8343, 3193)).add(
      BOTLANE_BLUE_INNER, BOTLANE_INNER_LEFT, BOTLANE_BLUE_RIVER, MID_BOT_BLUE_RIVER);

  public static final MapArea TOP_RED_JUNGLE = new MapArea(SuperArea.JUNGLE, new Position(8053, 13203)).add(
      TOPLANE_RED_INNER, TOPLANE_INNER_RIGHT, TOPLANE_RED_RIVER, MID_TOP_RED_RIVER);

  public static final MapArea BOT_RED_JUNGLE = new MapArea(SuperArea.JUNGLE, new Position(13203, 8053)).add(
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

  public MapArea add(Position... corners) {
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

  private Polygon getPolygon(Position... corners) {
    final int[] xCoordinates = Arrays.stream(corners).mapToInt(Position::getX).toArray();
    final int[] yCoordinates = Arrays.stream(corners).mapToInt(Position::getY).toArray();
    return new Polygon(xCoordinates, yCoordinates, 4);
  }

}
