package de.xeri.league.others;

import java.util.ArrayList;
import java.util.List;

import de.xeri.league.models.match.PlayerperformanceKill;
import de.xeri.league.models.match.Position;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Lara on 01.05.2022 for web
 */
@Getter
@Setter
@AllArgsConstructor
public class Fight {
  private final List<PlayerperformanceKill> kills = new ArrayList<>();
  private Position currentPosition;
}
