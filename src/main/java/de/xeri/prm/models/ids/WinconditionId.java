package de.xeri.prm.models.ids;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;

import de.xeri.prm.models.enums.WinconditionType;

@Embeddable
public class WinconditionId implements Serializable {

  @Transient
  private static final long serialVersionUID = -4919283373323390793L;

  @Column(name = "champion", nullable = false)
  private short champion;

  @Enumerated(EnumType.STRING)
  @Column(name = "wincondition_type", nullable = false, length = 18)
  private WinconditionType winconditionType;

  //<editor-fold desc="getter and setter">
  public WinconditionType getWinconditionType() {
    return winconditionType;
  }

  public void setWinconditionType(WinconditionType winconditionType) {
    this.winconditionType = winconditionType;
  }

  public short getChampion() {
    return champion;
  }

  public void setChampion(short champion) {
    this.champion = champion;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof WinconditionId)) return false;
    final WinconditionId winconditionId = (WinconditionId) o;
    return getChampion() == winconditionId.getChampion() && getWinconditionType() == winconditionId.getWinconditionType();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getChampion(), getWinconditionType());
  }

  @Override
  public String toString() {
    return "WinconditionId{" +
        "champion='" + champion + '\'' +
        ", winconditionType='" + winconditionType + '\'' +
        '}';
  }
  //</editor-fold>
}