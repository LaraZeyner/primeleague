package de.xeri.league.models.enums;

import java.util.Arrays;

import de.xeri.league.models.match.location.MapArea;
import de.xeri.league.util.Util;

/**
 * Created by Lara on 25.03.2022 for TRUES
 */
public enum Lane {
  TOP("TOP", "TOP_LANE", MapArea.TOPLANE),
  JUNGLE("JGL", "none", null),
  MIDDLE("MID", "MID_LANE", MapArea.MIDLANE),
  BOTTOM("BOT", "BOT_LANE", MapArea.BOTLANE),
  UTILITY("SUP", "BOT_LANE", MapArea.BOTLANE);

  private String abbreviation;
  private String type;
  private MapArea area;

  Lane(String abbreviation, String type, MapArea area) {
    this.abbreviation = abbreviation;
    this.type = type;
    this.area = area;
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

  public MapArea getArea() {
    return area;
  }

  public String getDisplayName() {
    if (name().equals("UTILITY")) {
      return "Support";
    }
    return Util.capitalizeFirst(name());
  }
}
