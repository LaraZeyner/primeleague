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
public enum Subclass {
  ARTILLERYMAGE (Championclass.MAGE),
  ASSASSIN (Championclass.SLAYER),
  CATCHER (Championclass.CONTROLLER),
  BATTLEMAGE (Championclass.MAGE),
  BURSTMAGE (Championclass.MAGE),
  DIVER (Championclass.FIGHTER),
  DOMINATOR (Championclass.MARKSMAN),
  ENCHANTER (Championclass.CONTROLLER),
  HYPERCARRY (Championclass.MARKSMAN),
  JUGGERNAUT (Championclass.FIGHTER),
  SCALER (Championclass.MARKSMAN),
  SKIRMISHER (Championclass.SLAYER),
  VANGUARD (Championclass.TANK),
  WARDEN (Championclass.TANK);

  private final Championclass championclass;
}
