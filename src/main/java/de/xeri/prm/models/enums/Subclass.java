package de.xeri.prm.models.enums;

/**
 * Created by Lara on 24.03.2022 for TRUES
 */
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

  private Championclass championclass;

  Subclass(Championclass championclass) {
    this.championclass = championclass;
  }

  public Championclass getChampionclass() {
    return championclass;
  }

  public void setChampionclass(Championclass championclass) {
    this.championclass = championclass;
  }
}
