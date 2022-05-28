package de.xeri.prm.models.ids;

import java.io.Serializable;

import de.xeri.prm.models.dynamic.Champion;
import de.xeri.prm.models.enums.RelationshipType;
import lombok.Data;

@Data
public class ChampionRelationshipId implements Serializable {
  private static final transient long serialVersionUID = -7468401648160520149L;

  private RelationshipType relationshipType;
  private Champion fromChampion;
  private Champion toChampion;
}