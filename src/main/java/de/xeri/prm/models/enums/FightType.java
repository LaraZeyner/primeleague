package de.xeri.prm.models.enums;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Created by Lara on 24.03.2022 for TRUES
 */
@Getter
@RequiredArgsConstructor
@ToString
public enum FightType {
  SLOW("Slow"),
  FAST("Fast");

  private final String displayName;

  public static FightType fromName(String name) {
    return Arrays.stream(FightType.values())
        .filter(fightType -> fightType.getDisplayName().equals(name))
        .findFirst().orElse(null);
  }

}
