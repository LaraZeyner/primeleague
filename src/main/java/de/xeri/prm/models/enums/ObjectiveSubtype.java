package de.xeri.prm.models.enums;

/**
 * Created by Lara on 13.04.2022 for web
 */
public enum ObjectiveSubtype {
  BASE_TURRET(ObjectiveType.TOWER, 50),
  INHIBITOR(ObjectiveType.INHIBITOR, 50),
  INNER_TURRET(ObjectiveType.TOWER, 550),
  NEXUS_TURRET(ObjectiveType.TOWER, 0),
  OUTER_TURRET(ObjectiveType.TOWER, 250),
  AIR_DRAGON(ObjectiveType.DRAGON, 25),
  BARON_NASHOR(ObjectiveType.BARON_NASHOR, 300),
  CHEMTECH_DRAGON(ObjectiveType.DRAGON, 25),
  EARTH_DRAGON(ObjectiveType.DRAGON, 25),
  ELDER_DRAGON(ObjectiveType.DRAGON, 25),
  FIRE_DRAGON(ObjectiveType.DRAGON, 25),
  HEXTECH_DRAGON(ObjectiveType.DRAGON, 25),
  RIFTHERALD(ObjectiveType.RIFTHERALD, 100),
  WATER_DRAGON(ObjectiveType.DRAGON, 25);

  private final ObjectiveType type;
  private final int bounty;

  ObjectiveSubtype(ObjectiveType type, int bounty) {
    this.type = type;
    this.bounty = bounty;
  }

  public ObjectiveType getType() {
    return type;
  }

  public int getBounty() {
    return bounty;
  }
}
