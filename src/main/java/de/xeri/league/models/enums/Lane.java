package de.xeri.league.models.enums;

/**
 * Created by Lara on 25.03.2022 for TRUES
 */
public enum Lane {
  TOP("TOP"),
  JUNGLE("JGL"),
  MIDDLE("MID"),
  BOTTOM("BOT"),
  UTILITY("SUP");

  private String abbreviation;

  Lane(String abbreviation) {
    this.abbreviation = abbreviation;
  }

  public String getAbbreviation() {
    return abbreviation;
  }

  public void setAbbreviation(String abbreviation) {
    this.abbreviation = abbreviation;
  }
}
