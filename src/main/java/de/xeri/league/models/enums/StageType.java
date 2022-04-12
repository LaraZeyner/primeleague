package de.xeri.league.models.enums;

/**
 * Created by Lara on 25.03.2022 for TRUES
 */
public enum StageType {
  PRESEASON(482, "group"),
  KALIBRIERUNGSPHASE(506, "group"),
  GRUPPENPHASE(509, "group"),
  PLAYOFFS(512, "playoff");

  private final int value;
  private final String typeName;

  StageType(int value, String typeName) {
    this.value = value;
    this.typeName = typeName;
  }

  public int getValue() {
    return value;
  }

  public String getTypeName() {
    return typeName;
  }
}
