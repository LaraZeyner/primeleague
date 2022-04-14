package de.xeri.league.models.match;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by Lara on 13.04.2022 for web
 */
public class Positionss implements Serializable {
  private static final transient long serialVersionUID = 5974593977640429012L;

  private final int x;
  private final int y;

  public Positionss(int x, int y) {
    this.x = x;
    this.y = y;
  }


  //<editor-fold desc="getter and setter">
  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Positionss)) return false;
    final Positionss positionss = (Positionss) o;
    return getX() == positionss.getX() && getY() == positionss.getY();
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
