package de.xeri.prm.models.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * @since 24.03.2022
 */
@Getter
@RequiredArgsConstructor
@ToString
public enum Championclass {
  CONTROLLER("Controller"),
  FIGHTER("Fighter"),
  MAGE("Mage"),
  MARKSMAN("Marksman"),
  SLAYER("Assassin"),
  TANK("Tank");

  private final String displayName;

}
