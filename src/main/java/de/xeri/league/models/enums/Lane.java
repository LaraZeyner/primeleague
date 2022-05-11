package de.xeri.league.models.enums;

import java.util.Arrays;
import java.util.List;

import de.xeri.league.game.events.location.MapArea;
import de.xeri.league.game.events.location.Position;
import de.xeri.league.util.Util;

/**
 * Created by Lara on 25.03.2022 for TRUES
 */
public enum Lane {
  TOP("TOP", "TOP_LANE", MapArea.TOPLANE),
  JUNGLE("JGL", "none", MapArea.TOP_BLUE_JUNGLE, MapArea.BOT_BLUE_JUNGLE, MapArea.TOP_RED_JUNGLE, MapArea.BOT_RED_JUNGLE),
  MIDDLE("MID", "MID_LANE", MapArea.MIDLANE),
  BOTTOM("BOT", "BOT_LANE", MapArea.BOTLANE),
  UTILITY("SUP", "BOT_LANE", MapArea.BOTLANE),
  UNKNOWN("MAP", "MAP", MapArea.MAP);

  private final String abbreviation;
  private final String type;
  private final MapArea[] areas;

  Lane(String abbreviation, String type, MapArea... areas) {
    this.abbreviation = abbreviation;
    this.type = type;
    this.areas = areas;
  }

  public String getAbbreviation() {
    return abbreviation;
  }

  public String getType() {
    return type;
  }

  public static Lane findLane(String laneString) {
    return Arrays.stream(Lane.values()).filter(lane -> lane.getType().equals(laneString)).findFirst().orElse(null);
  }

  public List<MapArea> getAreas() {
    return Arrays.asList(areas);
  }

  public String getDisplayName() {
    if (name().equals("UTILITY")) {
      return "Support";
    }
    return Util.capitalizeFirst(name());
  }

  public Position getCenter(Position position, boolean blue) {
    if (areas.length == 1) {
      return areas[0].getCenter();
    }

    if (blue) {
      return areas[Util.distance(position, areas[0].getCenter()) <= Util.distance(position, areas[1].getCenter()) ? 0 : 1].getCenter();
    }

    return areas[Util.distance(position, areas[2].getCenter()) <= Util.distance(position, areas[3].getCenter()) ? 2 : 3].getCenter();
  }

  public boolean isInArea(Position position, boolean blue) {
    if (areas.length == 1) {
      return areas[0].isInArea(position);
    }

    if (blue) {
      return areas[0].isInArea(position) || areas[1].isInArea(position);
    }

    return areas[2].isInArea(position) || areas[3].isInArea(position);
  }
}
