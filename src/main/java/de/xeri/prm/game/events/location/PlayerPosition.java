package de.xeri.prm.game.events.location;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

/**
 * Created by Lara on 14.04.2022 for web
 */
@Embeddable
public class PlayerPosition implements Serializable {
  @Transient
  private static final long serialVersionUID = -7334079325539871684L;

  @Column(name = "position_x", nullable = false)
  private short x;

  @Column(name = "position_y", nullable = false)
  private short y;

  // default constructor
  public PlayerPosition() {
  }

  public PlayerPosition(short x, short y) {
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
    if (!(o instanceof PlayerPosition)) return false;
    final PlayerPosition position = (PlayerPosition) o;
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
