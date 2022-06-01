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
public enum FightStyle {
  DIVE("Dive"),
  TEAMFIGHT("Teamfight");

  private final String displayName;

  public static FightStyle fromName(String name) {
    return Arrays.stream(FightStyle.values())
        .filter(fightStyle -> fightStyle.getDisplayName().equals(name))
        .findFirst().orElse(null);
  }
}
