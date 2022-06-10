package de.xeri.prm.servlet.loader.match;

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
import de.xeri.prm.servlet.loader.league.LeagueTeam;
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
public class LoadTeam implements Serializable {
  private static final transient long serialVersionUID = -3399217305765614778L;

  private int teamId;

  private LeagueTeam requestedTeam;
  private boolean scrims;
  private List<LaningView> laneViews;
  private List<PlayerLaneView> playerViews;

  public int getTotalTop() {
    return playerViews.stream().mapToInt(PlayerLaneView::getTop).sum();
  }

  public int getTotalJungle() {
    return playerViews.stream().mapToInt(PlayerLaneView::getJungle).sum();
  }

  public int getTotalMiddle() {
    return playerViews.stream().mapToInt(PlayerLaneView::getMiddle).sum();
  }

  public int getTotalBottom() {
    return playerViews.stream().mapToInt(PlayerLaneView::getBottom).sum();
  }

  public int getTotalSupport() {
    return playerViews.stream().mapToInt(PlayerLaneView::getSupport).sum();
  }

  public LeagueTeam getRequestedTeam() {
    if (requestedTeam == null) {
      init();
    }
    return requestedTeam;
  }

  @PostConstruct
  public void init() {
    update();
  }

  public void update() {
    if (teamId != 0) {
      Team team = Team.findTid(teamId);
      this.requestedTeam = team.getLeagueTeam();
      this.scrims = team.isScrims();

      this.playerViews = new ArrayList<>();
      final List<Player> playerList = new ArrayList<>(team.getPlayers());
      for (Player player : playerList) {
        final List<Integer> gamesOn = player.getGamesOn();
        final PlayerLaneView playerLaneView = new PlayerLaneView(player);
        playerLaneView.setTop(gamesOn.get(0));
        playerLaneView.setJungle(gamesOn.get(1));
        playerLaneView.setMiddle(gamesOn.get(2));
        playerLaneView.setBottom(gamesOn.get(3));
        playerLaneView.setSupport(gamesOn.get(4));
        playerViews.add(playerLaneView);
      }

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

      for (int i = 0; i < highlightedPlayersHome.size(); i++) {
        final Player player = highlightedPlayersHome.get(i);
        views.get(i).setPlayerperformances(player.getActiveAccount().getAllPerformances());
        final Query<Playerperformance> namedQuery = PrimeData.getInstance().getSession().getNamedQuery("Playerperformance.forPlayer");
        namedQuery.setParameter("account", player.getActiveAccount());
        namedQuery.setParameter("since", new Date(System.currentTimeMillis() - 30 * Const.MILLIS_PER_DAY));
        namedQuery.setMaxResults(25);
        views.get(i).setPlayerperformances(namedQuery.list());
      }
      this.laneViews = views;
    }
  }

  public void setScrimmages() {
    Team team = requestedTeam.getTeam();
    if (!PrimeData.getInstance().getCurrentGroup().getTeams().contains(team)) {
      team.setScrims(scrims);
      PrimeData.getInstance().save(team);
      PrimeData.getInstance().commit();
      FacesUtil.sendMessage("Team aktualisiert", "Scrimpartner: " + scrims);
    }
  }

  public void setTeamId(int teamId) {
    this.teamId = teamId;
    update();
  }
}
