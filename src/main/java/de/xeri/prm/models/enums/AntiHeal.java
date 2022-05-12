package de.xeri.prm.models.enums;

import java.util.Arrays;

/**
 * Created by Lara on 28.04.2022 for web
 */
public enum AntiHeal {
  BRAMBLE_VEST("Bramble Vest"),
  EXECUTIONERS_CALLING("Executioner's Calling"),
  OBLIVION_ORB("Oblivion Orb"),
  CHEMPUNK_CHAINSWORD("Chempunk Chainsword"),
  CHEMTECH_PUTRIFIER("Chemtech Putrifier"),
  MORELLONOMICON("Morellonomicon"),
  MORTAL_REMINDER("Mortal_Reminder"),
  THORNMAIL("Thornmail");

  private String name;

  AntiHeal(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public static boolean has(String name) {
    return Arrays.stream(AntiHeal.values()).anyMatch(antiHeal -> antiHeal.getName().equalsIgnoreCase(name));
  }
}
