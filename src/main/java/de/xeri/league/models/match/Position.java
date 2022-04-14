package de.xeri.league.models.match;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by Lara on 14.04.2022 for web
 */
public class Position implements Serializable {
  private static final transient long serialVersionUID = -7334079325539871684L;

  private short x;
  private short y;

  // default constructor
  public Position() {
  }

  public Position(short x, short y) {
    this.x = x;
    this.y = y;
  }

  //<editor-fold desc="getter and setter">
  public short getX() {
    return x;
  }

  public short getY() {
    return y;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Position)) return false;
    final Position position = (Position) o;
    return getX() == position.getX() && getY() == position.getY();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getX(), getY());
  }

  @Override
  public String toString() {
    return "Position{" +
        "x=" + x +
        ", y=" + y +
        '}';
  }
  //</editor-fold>
}
