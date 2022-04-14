package de.xeri.league.models.enums;

/**
 * Created by Lara on 13.04.2022 for web
 */
public enum ObjectiveSubtype {
  BASE_TURRET(ObjectiveType.TOWER),
  INHIBITOR(ObjectiveType.INHIBITOR),
  INNER_TURRET(ObjectiveType.TOWER),
  NEXUS_TURRET(ObjectiveType.TOWER),
  OUTER_TURRET(ObjectiveType.TOWER),
  AIR_DRAGON(ObjectiveType.DRAGON),
  BARON_NASHOR(ObjectiveType.BARON_NASHOR),
  CHEMTECH_DRAGON(ObjectiveType.DRAGON),
  EARTH_DRAGON(ObjectiveType.DRAGON),
  ELDER_DRAGON(ObjectiveType.DRAGON),
  FIRE_DRAGON(ObjectiveType.DRAGON),
  HEXTECH_DRAGON(ObjectiveType.DRAGON),
  RIFTHERALD(ObjectiveType.RIFTHERALD),
  WATER_DRAGON(ObjectiveType.DRAGON);

  private final ObjectiveType type;

  ObjectiveSubtype(ObjectiveType type) {
    this.type = type;
  }

  public ObjectiveType getType() {
    return type;
  }
}
