package de.xeri.prm.models.match.ratings;

/**
 * Created by Lara on 25.04.2022 for web
 */
public enum StatCategory {
  VISION_AND_OBJECTIVECONTROL("Vision & Objective Control"),
  ROAMING_AND_DIVING("Roaming & Diving"),
  AGGRESSION_AND_FIGHTING("Aggression & Fighting"),
  COMBAT_AND_INCOME("Combat & Income"),
  SURVIVABILITY_AND_UTILITY("Survivability & Utility"),

  LANING("Laneperformance & Consistency"),
  MENTALITY_AND_ADAPTION("Mentality & Adaption");

  private final String name;

  StatCategory(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
