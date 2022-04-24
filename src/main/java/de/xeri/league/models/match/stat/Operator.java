package de.xeri.league.models.match.stat;

/**
 * Created by Lara on 23.04.2022 for web
 */
public enum Operator {
  EQUALS(" = :"),
  GREATER_OR_EQUAL(" >= :"),
  GREATER_THAN(" > :"),
  LESS_OR_EQUAL(" <= :"),
  LESS_THAN(" < :");


  private final String code;

  Operator(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }
}
