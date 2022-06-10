package de.xeri.prm.servlet.loader.scouting.performance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.league.Player;
import de.xeri.prm.models.league.Team;
import de.xeri.prm.servlet.loader.league.LeagueTeam;
import lombok.Data;

/**
 * Created by Lara on 23.05.2022 for web
 */
@Data
public class TeamView implements Serializable {
  private Team homeTeam;
  private List<LaneView> views;
  private List<String> teamStyle;
  private List<String> teamTags; //TODO (Abgie) 24.05.2022:


  public TeamView(Team homeTeam) {
    this.homeTeam = homeTeam;
    List<Map<Player, Integer>> gamesHome = Arrays.asList(new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>());

    List<LaneView> views = Arrays.asList(new LaneView(Lane.TOP), new LaneView(Lane.JUNGLE), new LaneView(Lane.MIDDLE),
        new LaneView(Lane.BOTTOM), new LaneView(Lane.UTILITY));

    for (Player player : homeTeam.getPlayers()) {
      final List<Integer> gamesOn = player.getGamesOn();
      for (int i = 0; i < gamesOn.size(); i++) {
        gamesHome.get(i).put(player, gamesOn.get(i));
      }
    }
    final List<Player> highlightedPlayersHome = new ArrayList<>();
    for (int i = 0; i < gamesHome.size(); i++) {
      final Map<Player, Integer> game = gamesHome.get(i);
      final Map<Player, Integer> players = game.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
          .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
      gamesHome.set(i, players);
      highlightedPlayersHome.add(new ArrayList<>(players.keySet()).get(0));
    }

    for (int i = 0; i < 5; i++) {
      views.get(i).setPlayers(new ArrayList<>(gamesHome.get(i).keySet()));
      views.get(i).setPlayersList(gamesHome.get(i).keySet().stream().map(Player::getName).collect(Collectors.toList()));
    }
    for (int i = 0; i < highlightedPlayersHome.size(); i++) {
      final PlayerView playerView = new PlayerView(highlightedPlayersHome.get(i),
          i == 0 ? Lane.TOP : i == 1 ? Lane.JUNGLE : i == 2 ? Lane.MIDDLE : i == 3 ? Lane.BOTTOM : Lane.UTILITY);
      views.get(i).setView(playerView);
      views.get(i).setSelected(playerView.getName());
      views.get(i).setSelectedPlayer(playerView.getPlayer());
    }

    this.views = views;
  }

  public LeagueTeam getLeagueTeam() {
    return homeTeam.getLeagueTeam();
  }
}
