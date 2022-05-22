package de.xeri.prm.models.enums;

import de.xeri.prm.util.Util;
import lombok.val;
import lombok.var;

/**
 * Created by Lara on 29.03.2022 for TRUES
 */
public enum Elo {
  UNRANKED(50),
  IRON_IV(100),
  IRON_III(200),
  IRON_II(300),
  IRON_I(400),
  BRONZE_IV(600),
  BRONZE_III(700),
  BRONZE_II(800),
  BRONZE_I(900),
  SILVER_IV(1100),
  SILVER_III(1200),
  SILVER_II(1300),
  SILVER_I(1400),
  GOLD_IV(1600),
  GOLD_III(1700),
  GOLD_II(1800),
  GOLD_I(1900),
  PLATINUM_IV(2100),
  PLATINUM_III(2200),
  PLATINUM_II(2300),
  PLATINUM_I(2400),
  DIAMOND_IV(2600),
  DIAMOND_III(2700),
  DIAMOND_II(2800),
  DIAMOND_I(2900),
  MASTER(3100),
  GRANDMASTER(3600),
  CHALLENGER(4100);

  private final int mmr;

  Elo(int mmr) {
    this.mmr = mmr;
  }

  public int getMmr() {
    return mmr;
  }

  public static Elo getDivision(int mmr) {
    var selected = Elo.UNRANKED;
    for (Elo elo : Elo.values()) {
      if (elo.getMmr() > mmr) {
        return selected;
      }
      selected = elo;
    }
    return Elo.UNRANKED;
  }

  public String getTier() {
    val tierUnformatted = name().contains("_") ? name().split("_")[0] : name();
    return Util.capitalizeFirst(tierUnformatted.toLowerCase());
  }

  public String getSuffix() {
    if (name().contains("_")) {
      switch (name().split("_")[1]) {
        case "I":
          return "_1";
        case "II":
          return "_2";
        case "III":
          return "_3";
        case "IV":
          return "_4";
      }
    }
    return "";
  }


  public String getPositionalIconUrl(Lane lane) {
    if (this.equals(Elo.UNRANKED)) {
      return "images/ranked/Ranked_Unranked.png";
    }
    return "images/ranked/position/Position_" + getTier() + "-" + lane.getDisplayName() + ".png";
  }

  public String getRankedIconUrl() {
    return "images/ranked/Ranked_" + getTier() + getSuffix() + ".png";
  }
}