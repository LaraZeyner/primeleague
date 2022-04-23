package de.xeri.league.models.others;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.league.models.dynamic.Champion;
import de.xeri.league.models.enums.ChampionPlaystyle;
import de.xeri.league.models.ids.PlaystyleId;

@Entity(name = "Playstyle")
@Table(name = "playstyle")
public class Playstyle implements Serializable {
  @Transient
  private static final long serialVersionUID = 2605401179400003709L;

  @EmbeddedId
  private PlaystyleId id;

  @MapsId("champion")
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "champion")
  private Champion champion;

  @Enumerated(EnumType.STRING)
  @Column(name = "playstyle", nullable = false, length = 18)
  private ChampionPlaystyle playstyle;

  //<editor-fold desc="getter and setter">
  public ChampionPlaystyle getPlaystyle() {
    return playstyle;
  }

  public void setPlaystyle(ChampionPlaystyle playstyle) {
    this.playstyle = playstyle;
  }

  public Champion getChampion() {
    return champion;
  }

  public void setChampion(Champion champion) {
    this.champion = champion;
  }

  public PlaystyleId getId() {
    return id;
  }

  public void setId(PlaystyleId id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Playstyle)) return false;
    final Playstyle playstyle1 = (Playstyle) o;
    return getId().equals(playstyle1.getId()) && getChampion().equals(playstyle1.getChampion()) && getPlaystyle().equals(playstyle1.getPlaystyle());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getChampion(), getPlaystyle());
  }

  @Override
  public String toString() {
    return "Playstyle{" +
        "id=" + id +
        ", playstyle='" + playstyle + '\'' +
        '}';
  }
  //</editor-fold>
}