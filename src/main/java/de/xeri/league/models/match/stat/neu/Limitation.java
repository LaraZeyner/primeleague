package de.xeri.league.models.match.stat.neu;

/**
 * Created by Lara on 22.04.2022 for web
 */
public class Limitation {
  private final String column;
  private final Object value;

  public Limitation(String column, Object value) {
    this.column = column;
    this.value = value;
  }

  public String getColumn() {
    return column;
  }

  public Object getValue() {
    return value;
  }
}
