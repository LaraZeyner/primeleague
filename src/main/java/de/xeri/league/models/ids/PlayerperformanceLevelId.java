package de.xeri.league.models.ids;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerperformanceLevelId implements Serializable {
  private static final transient long serialVersionUID = -2404731249852550567L;

  private int playerperformance;
  private byte level;
}