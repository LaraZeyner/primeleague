package de.xeri.prm.servlet.loader.match;

import java.io.Serializable;
import java.util.List;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.playerperformance.Playerperformance;
import lombok.Data;

/**
 * Created by Lara on 05.06.2022 for web
 */
@Data
public class LaningView implements Serializable {
  private static final transient long serialVersionUID = -9004827083326672529L;

  private final Lane lane;

  private List<Playerperformance> playerperformances;


}
