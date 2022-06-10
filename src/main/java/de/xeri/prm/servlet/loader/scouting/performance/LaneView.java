package de.xeri.prm.servlet.loader.scouting.performance;

import java.io.Serializable;
import java.util.List;

import javax.faces.event.ValueChangeEvent;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.league.Player;
import lombok.Data;

/**
 * Created by Lara on 05.06.2022 for web
 */
@Data
public class LaneView implements Serializable {
  private static final transient long serialVersionUID = -9004827083326672529L;

  private final Lane lane;
  private PlayerView view;
  private List<Player> players;
  private List<String> playersList;
  private String selected;
  private Player selectedPlayer;

  public void PresetGroupChangeEvent(ValueChangeEvent event) {
    final String newValue = String.valueOf(event.getNewValue());
    this.selected = newValue;
    final Player player = players.stream().filter(p -> p.getName().equals(newValue)).findFirst().orElse(view.getPlayer());
    this.selectedPlayer = player;
    this.view = new PlayerView(player, lane);
  }
}
