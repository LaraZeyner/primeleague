package de.xeri.prm.models.match.ratings;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RatingId implements Serializable {
  private static final transient long serialVersionUID = -2683308112571213415L;

  private StatSubcategory category;
  private DisplaystatSubtype subType;
}