package de.xeri.prm.models.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Created by Lara on 02.06.2022 for web
 */
@RequiredArgsConstructor
@Getter
@ToString
public enum AbsenceType {
  ABSENT("Abwesend"),
  MAYBE("Normalerweise abwesend"),
  AVAILABLE("Anwesend");

  private final String displayName;

}
