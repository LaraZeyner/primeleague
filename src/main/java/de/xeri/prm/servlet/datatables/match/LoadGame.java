package de.xeri.prm.servlet.datatables.match;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import de.xeri.prm.manager.PrimeData;
import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.league.Player;
import de.xeri.prm.models.league.Team;
import de.xeri.prm.models.match.playerperformance.Playerperformance;
import de.xeri.prm.util.Const;
import de.xeri.prm.util.FacesUtil;
import lombok.Data;
import org.hibernate.query.Query;

/**
 * Created by Lara on 08.06.2022 for web
 */
@ManagedBean
@RequestScoped
@Data
public class LoadGame implements Serializable {
  private static final transient long serialVersionUID = -3399217305765614778L;

  private Team team;
  private boolean scrims;
  private List<GameView> gameViews;
  private List<LaningView> laneViews;

  @PostConstruct
  public String doLookup(int teamId) {
    this.team = Team.find(teamId);
    this.scrims = team.isScrims();
    this.gameViews = new ArrayList<>();
    team.getLeaguePerformances().forEach(leaguePerformance -> gameViews.add(new GameView(leaguePerformance.getGame(), team)));

    List<Map<Player, Integer>> gamesHome = Arrays.asList(new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>());
    List<LaningView> views = Arrays.asList(new LaningView(Lane.TOP), new LaningView(Lane.JUNGLE), new LaningView(Lane.MIDDLE),
        new LaningView(Lane.BOTTOM), new LaningView(Lane.UTILITY));

    for (Player player : team.getPlayers()) {
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
      final Player player = highlightedPlayersHome.get(i);
      views.get(i).setSelected(player.getName());
      views.get(i).setSelectedPlayer(player);
      views.get(i).setPlayerperformances(player.getActiveAccount().getAllPerformances());
      final Query<Playerperformance> namedQuery = PrimeData.getInstance().getSession().getNamedQuery("Playerperformance.forPlayer");
      namedQuery.setParameter("account", player.getActiveAccount());
      namedQuery.setParameter("since", new Date(System.currentTimeMillis() - 180 * Const.MILLIS_PER_DAY));
      namedQuery.setMaxResults(25);
      views.get(i).setPlayerperformances(namedQuery.list());
    }
    this.laneViews = views;

    return "teaminfo";
  }

  public void setScrimmages() {
    if (!PrimeData.getInstance().getCurrentGroup().getTeams().contains(team)) {
      team.setScrims(scrims);
      PrimeData.getInstance().save(team);
      PrimeData.getInstance().commit();
      FacesUtil.sendMessage("Team aktualisiert", "Scrimpartner: " + scrims);
    }
  }
}
