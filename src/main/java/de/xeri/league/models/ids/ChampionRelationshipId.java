package de.xeri.league.models.ids;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;

import de.xeri.league.models.enums.RelationshipType;
import org.hibernate.Hibernate;

@Embeddable
public class ChampionRelationshipId implements Serializable {

  @Transient
  private static final long serialVersionUID = 212088806768796892L;

  @Enumerated(EnumType.STRING)
  @Column(name = "relationship_type", nullable = false, length = 9)
  private RelationshipType relationshipType;

  @Column(name = "from_champion", nullable = false)
  private short fromChampion;

  @Column(name = "to_champion", nullable = false)
  private short toChampion;

  //<editor-fold desc="getter and setter">
  public short getToChampion() {
    return toChampion;
  }

  public void setToChampion(short toChampion) {
    this.toChampion = toChampion;
  }

  public short getFromChampion() {
    return fromChampion;
  }

  public void setFromChampion(short fromChampion) {
    this.fromChampion = fromChampion;
  }

  public RelationshipType getRelationshipType() {
    return relationshipType;
  }

  public void setRelationshipType(RelationshipType relationshipType) {
    this.relationshipType = relationshipType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(toChampion, relationshipType, fromChampion);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    final ChampionRelationshipId entity = (ChampionRelationshipId) o;
    return Objects.equals(this.toChampion, entity.toChampion) &&
        Objects.equals(this.relationshipType, entity.relationshipType) &&
        Objects.equals(this.fromChampion, entity.fromChampion);
  }

  @Override
  public String toString() {
    return "ChampionRelationshipId{" +
        "relationshipType=" + relationshipType +
        ", fromChampion='" + fromChampion + '\'' +
        ", toChampion='" + toChampion + '\'' +
        '}';
  }
  //</editor-fold>
}