package de.xeri.prm.servlet.datatables.match;

import java.io.Serializable;
import java.util.List;

import javax.faces.event.ValueChangeEvent;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.league.Player;
import de.xeri.prm.models.match.playerperformance.Playerperformance;
import lombok.Data;

/**
 * Created by Lara on 05.06.2022 for web
 */
@Data
public class LaningView implements Serializable {
  private static final transient long serialVersionUID = -9004827083326672529L;

  private final Lane lane;
  private List<Player> players;
  private List<String> playersList;

  private String selected;
  private Player selectedPlayer;
  private List<Playerperformance> playerperformances;

  public void PresetGroupChangeEvent(ValueChangeEvent event) {
    final String newValue = String.valueOf(event.getNewValue());
    this.selected = newValue;
    this.selectedPlayer = players.stream().filter(p -> p.getName().equals(newValue)).findFirst().orElse(selectedPlayer);
  }
}
