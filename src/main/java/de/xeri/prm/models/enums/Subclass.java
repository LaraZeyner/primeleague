package de.xeri.prm.models.enums;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @since 24.03.2022
 */
@Getter
@RequiredArgsConstructor
public enum Subclass {
  ARTILLERYMAGE ("Artillerymage", Championclass.MAGE),
  ASSASSIN ("Assassin", Championclass.SLAYER),
  CATCHER ("Catcher", Championclass.CONTROLLER),
  BATTLEMAGE ("Battlemage", Championclass.MAGE),
  BURSTMAGE ("Burstmage", Championclass.MAGE),
  DIVER ("Diver", Championclass.FIGHTER),
  DOMINATOR ("Dominator", Championclass.MARKSMAN),
  ENCHANTER ("Enchanter", Championclass.CONTROLLER),
  HYPERCARRY ("Hypercarry", Championclass.MARKSMAN),
  JUGGERNAUT ("Juggernaut", Championclass.FIGHTER),
  SCALER ("Scaler", Championclass.MARKSMAN),
  SKIRMISHER ("Skirmisher", Championclass.SLAYER),
  VANGUARD ("Vanguard", Championclass.TANK),
  WARDEN ("Warden", Championclass.TANK);

  private final String displayName;
  private final Championclass championclass;

  @Override
  public String toString() {
    return championclass.getDisplayName() + " - " + displayName;
  }

  public static Subclass fromName(String name) {
    return Arrays.stream(Subclass.values()).filter(subclass -> subclass.toString().equals(name)).findFirst().orElse(null);
  }
}
