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
  PRESEASON(482, "group"),
  KALIBRIERUNGSPHASE(506, "group"),
  GRUPPENPHASE(509, "group"),
  PLAYOFFS(512, "playoff");

  private final int value;
  private final String typeName;

}
