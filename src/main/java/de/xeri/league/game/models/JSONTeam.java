package de.xeri.league.game.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.xeri.league.models.enums.EventTypes;
import de.xeri.league.models.enums.QueueType;
import de.xeri.league.models.enums.StoredStat;
import de.xeri.league.models.league.Team;
import lombok.Getter;
import org.json.JSONObject;

/**
 * Created by Lara on 09.04.2022 for web
 */
@Getter
public class JSONTeam {

  private final int id;
  private final List<JSONPlayer> allPlayers = new ArrayList<>();
  private final List<JSONObject> events = new ArrayList<>();
  private JSONObject teamObject;

  public JSONTeam(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public List<JSONPlayer> getAllPlayers() {
    return allPlayers;
  }

  public JSONPlayer getPlayer(int id, JSONTeam otherTeam) {
    return allPlayers.stream().filter(player -> player.getId() == id).findFirst()
        .orElse(otherTeam.getAllPlayers().stream().filter(player -> player.getId() == id).findFirst().orElse(null));
  }

  public JSONPlayer getPlayer(String puuid, JSONTeam otherTeam) {
    return allPlayers.stream().filter(player -> player.getPuuid().equals(puuid)).findFirst()
        .orElse(otherTeam.getAllPlayers().stream().filter(player -> player.getPuuid().equals(puuid)).findFirst()
            .orElse(null));
  }

  public void addEvent(JSONObject event) {
    events.add(event);
  }

  public void addPlayer(JSONPlayer player) {
    allPlayers.add(player);
  }

  public boolean hasPlayer(int id) {
    return allPlayers.stream().anyMatch(player -> player.getId() == id);
  }

  public boolean hasPlayer(String puuid) {
    return allPlayers.stream().anyMatch(player -> player.getPuuid().equals(puuid));
  }

  public JSONObject getTeamObject() {
    return teamObject;
  }

  public void setTeamObject(JSONObject teamObject) {
    this.teamObject = teamObject;
  }

  public Map<Team, List<JSONPlayer>> getTeams() {
    final Map<Team, List<JSONPlayer>> map = new HashMap<>();
    for (JSONPlayer player : allPlayers) {
      if (player.hasTeam()) {
        for (Team team : player.getTeams()) {
          if (map.containsKey(team)) {
            map.get(team).add(player);
          } else {
            map.put(team, new ArrayList<>(Collections.singletonList(player)));
          }
        }
        if (player.getOfficialTeam() != null && !player.getTeams().contains(player.getOfficialTeam())) {
          if (map.containsKey(player.getOfficialTeam())) {
            map.get(player.getOfficialTeam()).add(player);
          } else {
            map.put(player.getOfficialTeam(), new ArrayList<>(Collections.singletonList(player)));
          }
        }
      }
    }
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

  public Integer getSum(StoredStat storedStat) {
    if (storedStat.isChallenge() && getAllPlayers().get(0).getJson().has("challenges")) {
      return allPlayers.stream().filter(player -> player.getMedium(storedStat) != null).mapToInt(player -> player.getMedium(storedStat)).sum();
    } else if (getAllPlayers().get(0).getJson().has(storedStat.getKey())) {
      return allPlayers.stream().filter(player -> player.getMedium(storedStat) != null).mapToInt(player -> player.getMedium(storedStat)).sum();
    }
    return null;
  }

  public Integer getMin(StoredStat storedStat) {
    if (storedStat.isChallenge() && getAllPlayers().get(0).getJson().has("challenges")) {
      return allPlayers.stream().mapToInt(player -> player.getMedium(storedStat)).min().orElse(0);
    } else if (getAllPlayers().get(0).getJson().has(storedStat.getKey())) {
      return allPlayers.stream().mapToInt(player -> player.getMedium(storedStat)).min().orElse(0);
    }
    return null;
  }

  public List<JSONPlayer> getListedPlayers() {
    return allPlayers.stream().filter(JSONPlayer::isListed).collect(Collectors.toList());
  }

  public List<JSONObject> getEvents(EventTypes type) {
    return events.stream()
        .filter(event -> type.equals(EventTypes.valueOf(event.getString("type"))))
        .collect(Collectors.toList());
  }

}
