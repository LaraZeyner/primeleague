package de.xeri.league.util.io.riot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.xeri.league.models.enums.QueueType;
import de.xeri.league.models.league.Team;

/**
 * Created by Lara on 09.04.2022 for web
 */
public class JSONTeam {
  private final int id;
  private final List<JSONPlayer> allPlayers = new ArrayList<>();

  public JSONTeam(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public List<JSONPlayer> getAllPlayers() {
    return allPlayers;
  }

  public void addPlayer(JSONPlayer player) {
    allPlayers.add(player);
  }

  public Map<Team, List<JSONPlayer>> getTeams() {
    final Map<Team, List<JSONPlayer>> map = new HashMap<>();
    allPlayers.stream().filter(JSONPlayer::hasTeam).forEach(player -> {
      player.getTeams().forEach(team -> {
        if (map.containsKey(team)) {
          map.get(team).add(player);
        } else  {
          map.put(team, new ArrayList<>(Collections.singletonList(player)));
        }
      });
      if (player.getOfficialTeam() != null && !player.getTeams().contains(player.getOfficialTeam())) {
        if (map.containsKey(player.getOfficialTeam())) {
          map.get(player.getOfficialTeam()).add(player);
        } else  {
          map.put(player.getOfficialTeam(), new ArrayList<>(Collections.singletonList(player)));
        }
      }
    });
    return map;
  }

  public boolean doesExist() {
    return !getListedPlayers().isEmpty();
  }

  public Team getMostUsedTeam(QueueType queueType) {
    return getTeams().keySet().stream()
        .filter(team -> getTeams().get(team).size() == 5 || !queueType.equals(QueueType.OTHER) && getTeams().get(team).size() >= 3)
        .findFirst().orElse(null);
  }

  public List<JSONPlayer> getPlayers(QueueType queueType) {
    if (queueType.equals(QueueType.TOURNEY)) {
      return getAllPlayers();
    } else {
      return getListedPlayers();
    }
  }

  public List<JSONPlayer> getListedPlayers() {
    return allPlayers.stream().filter(JSONPlayer::isListed).collect(Collectors.toList());
  }

}
