package de.xeri.league.models.dynamic;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.league.models.ids.WinconditionId;

@Entity(name = "Wincondition")
@Table(name = "wincondition")
public class Wincondition implements Serializable {

  @Transient
  private static final long serialVersionUID = 5967878319062261625L;

  @EmbeddedId
  private WinconditionId id;

  @MapsId("champion")
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "champion", nullable = false)
  private Champion champion;

  @Column(name = "wincondition_amount", nullable = false)
  private byte winconditionAmount;

  //<editor-fold desc="getter and setter">
  public byte getWinconditionAmount() {
    return winconditionAmount;
  }

  public void setWinconditionAmount(byte winconditionAmount) {
    this.winconditionAmount = winconditionAmount;
  }

  public Champion getChampion() {
    return champion;
  }

  public void setChampion(Champion champion) {
    this.champion = champion;
  }

  public WinconditionId getId() {
    return id;
  }

  public void setId(WinconditionId id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Wincondition)) return false;
    final Wincondition wincondition = (Wincondition) o;
    return getWinconditionAmount() == wincondition.getWinconditionAmount() && getId().equals(wincondition.getId()) && getChampion().equals(wincondition.getChampion());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getChampion(), getWinconditionAmount());
  }

  @Override
  public String toString() {
    return "Wincondition{" +
        "id=" + id +
        ", champion=" + champion +
        ", winconditionAmount=" + winconditionAmount +
        '}';
  }
  //</editor-fold>
}