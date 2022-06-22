package de.xeri.prm.servlet.loader.league;

import java.util.Map;

import de.xeri.prm.models.league.TurnamentMatch;
import lombok.Data;

/**
 * Created by Lara on 21.06.2022 for web
 */
@Data
public class TurnamentMatchWay {
  private Map<TurnamentMatch, Integer> results;
  private double probability;

}
