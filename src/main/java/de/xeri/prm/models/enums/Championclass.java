package de.xeri.prm.models.enums;

/**
 * Created by Lara on 24.03.2022 for TRUES
 */
public enum Championclass {
  CONTROLLER("Controller"),
  FIGHTER("Fighter"),
  MAGE("Mage"),
  MARKSMAN("Marksman"),
  SLAYER("Assassin"),
  TANK("Tank");

  String name;

  Championclass(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
