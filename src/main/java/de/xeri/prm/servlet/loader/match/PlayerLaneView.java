package de.xeri.prm.servlet.loader.match;

import java.io.Serializable;

import de.xeri.prm.models.league.Player;
import lombok.Data;

/**
 * Created by Lara on 09.06.2022 for web
 */
@Data
public class PlayerLaneView implements Serializable {
  private final Player player;
  private int top;
  private int jungle;
  private int middle;
  private int bottom;
  private int support;
}
