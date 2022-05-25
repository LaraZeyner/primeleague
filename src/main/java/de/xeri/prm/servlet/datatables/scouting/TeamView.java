package de.xeri.prm.servlet.datatables.scouting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.league.Player;
import de.xeri.prm.models.league.Team;
import lombok.Data;

/**
 * Created by Lara on 23.05.2022 for web
 */
@Data
public class TeamView implements Serializable {
  private Team homeTeam;
  private List<PlayerView> players;
  private List<Player> topPlayers;
  private List<Player> jglPlayers;
  private List<Player> midPlayers;
  private List<Player> botPlayers;
  private List<Player> supPlayers;
  private List<String> teamStyle;
  private List<String> teamTags; //TODO (Abgie) 24.05.2022:


  public TeamView(Team homeTeam) {
    this.homeTeam = homeTeam;
    List<Map<Player, Integer>> gamesHome = Arrays.asList(new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>());

    for (Player player : homeTeam.getPlayers()) {
      List<Integer> gamesOnLane = player.getGamesOn();
      for (int i = 0; i < gamesOnLane.size(); i++) {
        gamesHome.get(i).put(player, gamesOnLane.get(i));
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

    this.topPlayers = new ArrayList<>(gamesHome.get(0).keySet());
    this.jglPlayers = new ArrayList<>(gamesHome.get(1).keySet());
    this.midPlayers = new ArrayList<>(gamesHome.get(2).keySet());
    this.botPlayers = new ArrayList<>(gamesHome.get(3).keySet());
    this.supPlayers = new ArrayList<>(gamesHome.get(4).keySet());
    this.players = IntStream.range(0, highlightedPlayersHome.size())
        .mapToObj(i -> new PlayerView(highlightedPlayersHome.get(i),
            i == 0 ? Lane.TOP : i == 1 ? Lane.JUNGLE : i == 2 ? Lane.MIDDLE : i == 3 ? Lane.BOTTOM : Lane.UTILITY))
        .collect(Collectors.toList());
  }
}
