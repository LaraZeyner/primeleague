package de.xeri.prm.servlet.loader.match;

import java.io.Serializable;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.playerperformance.Playerperformance;
import lombok.Data;

/**
 * Created by Lara on 08.06.2022 for web
 */
@Data
public class PerformanceView implements Serializable {
  private static final transient long serialVersionUID = 7988910050085923194L;

  private final Lane lane;
  private Playerperformance playerperformance;
}
