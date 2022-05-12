package de.xeri.prm.models.enums;

/**
 * Created by Lara on 25.03.2022 for TRUES
 */
public enum ChampionPlaystyle {
  ALLIN ("Allin/Catch/Engage"),
  SCALE ("Disengage/Scaling"),
  DIVING ("Diving"),
  POKE ("Poke/Trade/Zone"),
  ROAMING ("Roaming/PushMove"),
  SPLITPUSH ("Splitpush"),
  TEAMFIGHT ("Front-to-back Teamfight");

  private String displayname;

  ChampionPlaystyle(String displayname) {
    this.displayname = displayname;
  }

  public String getDisplayname() {
    return displayname;
  }
}
