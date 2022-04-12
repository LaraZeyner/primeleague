package de.xeri.league.models.others;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.league.models.dynamic.Champion;
import de.xeri.league.models.ids.ChampionRelationshipId;

@Entity(name = "Champion_Relationship")
@Table(name = "champion_relationship", indexes = {
    @Index(name = "from_champion", columnList = "from_champion"),
    @Index(name = "to_champion", columnList = "to_champion")
})
public class ChampionRelationship implements Serializable {

  @Transient
  private static final long serialVersionUID = 3259168807202075733L;

  @EmbeddedId
  private ChampionRelationshipId id;

  @MapsId("fromChampion")
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "from_champion")
  private Champion fromChampion;

  @MapsId("toChampion")
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "to_champion")
  private Champion toChampion;

  //<editor-fold desc="getter and setter">
  public Champion getToChampion() {
    return toChampion;
  }

  public void setToChampion(Champion toChampion) {
    this.toChampion = toChampion;
  }

  public Champion getFromChampion() {
    return fromChampion;
  }

  public void setFromChampion(Champion fromChampion) {
    this.fromChampion = fromChampion;
  }

  public ChampionRelationshipId getId() {
    return id;
  }

  public void setId(ChampionRelationshipId id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ChampionRelationship)) return false;
    final ChampionRelationship championRelationship = (ChampionRelationship) o;
    return getId().equals(championRelationship.getId()) && getFromChampion().equals(championRelationship.getFromChampion()) && getToChampion().equals(championRelationship.getToChampion());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getFromChampion(), getToChampion());
  }

  @Override
  public String toString() {
    return "ChampionRelationship{" +
        "id=" + id +
        '}';
  }
  //</editor-fold>
}