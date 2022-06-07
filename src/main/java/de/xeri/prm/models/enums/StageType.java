package de.xeri.prm.models.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * @since 25.03.2022
 */
@Getter
@RequiredArgsConstructor
@ToString
public enum StageType {
  PRESEASON(482, "group", "Pre"),
  KALIBRIERUNGSPHASE(506, "group", "Kali"),
  GRUPPENPHASE(509, "group", "Gruppe"),
  PLAYOFFS(512, "playoff", "Playoffs");

  private final int value;
  private final String typeName;
  private final String name;

}
