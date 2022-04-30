package de.xeri.league.models.enums;

import java.util.Arrays;

import de.xeri.league.util.Util;

/**
 * Created by Lara on 25.03.2022 for TRUES
 */
public enum Lane {
  TOP("TOP", "TOP_LANE"),
  JUNGLE("JGL", "none"),
  MIDDLE("MID", "MID_LANE"),
  BOTTOM("BOT", "BOT_LANE"),
  UTILITY("SUP", "BOT_LANE");

  private String abbreviation;
  private String type;

  Lane(String abbreviation, String type) {
    this.abbreviation = abbreviation;
    this.type = type;
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

  public String getDisplayName() {
    if (name().equals("UTILITY")) {
      return "Support";
    }
    return Util.capitalizeFirst(name());
  }
}
