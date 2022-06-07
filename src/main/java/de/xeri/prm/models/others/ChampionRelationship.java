package de.xeri.prm.models.others;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.prm.manager.PrimeData;
import de.xeri.prm.models.dynamic.Champion;
import de.xeri.prm.models.enums.RelationshipType;
import de.xeri.prm.models.ids.ChampionRelationshipId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

@Entity(name = "Champion_Relationship")
@Table(name = "champion_relationship", indexes = {
    @Index(name = "from_champion", columnList = "from_champion"),
    @Index(name = "to_champion", columnList = "to_champion")
})
@IdClass(ChampionRelationshipId.class)
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class ChampionRelationship implements Serializable {

  @Transient
  private static final long serialVersionUID = -1662815460772674871L;

  @Id
  @Enumerated(EnumType.STRING)
  @Column(name = "relationship_type", nullable = false, length = 7)
  private RelationshipType relationshipType;

  @Id
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "from_champion")
  @ToString.Exclude
  private Champion fromChampion;

  @Id
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "to_champion")
  @ToString.Exclude
  private Champion toChampion;

  public void create() {
    fromChampion.addRelationship(this, true);
    toChampion.addRelationship(this, false);
    PrimeData.getInstance().save(this);
  }

  public void remove() {
    fromChampion.removeRelationship(this, true);
    toChampion.removeRelationship(this, false);
    PrimeData.getInstance().remove(this);
  }

  public boolean has(Champion champion) {
    return fromChampion.equals(champion) || toChampion.equals(champion);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    final ChampionRelationship that = (ChampionRelationship) o;
    return relationshipType != null && Objects.equals(relationshipType, that.relationshipType)
        && fromChampion != null && Objects.equals(fromChampion, that.fromChampion)
        && toChampion != null && Objects.equals(toChampion, that.toChampion);
  }

  @Override
  public int hashCode() {
    return Objects.hash(relationshipType, fromChampion, toChampion);
  }

}