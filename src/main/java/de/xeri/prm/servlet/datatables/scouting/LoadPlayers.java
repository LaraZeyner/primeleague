package de.xeri.prm.servlet.datatables.scouting;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.league.Player;
import de.xeri.prm.models.league.Team;
import lombok.Getter;
//TODO (Abgie) 18.05.2022: Wenn Spieler ausgewählt wird - Spiele suchen

/**
 * Created by Lara on 18.05.2022 for web
 */
@ManagedBean
@ApplicationScoped
@Getter
public class LoadPlayers implements Serializable {
  private static final long serialVersionUID = 4532805787883011744L;
  private Team homeTeam;
  private List<PlayerView> homePlayers = new ArrayList<>();
  private List<Player> homeTopPlayers = new ArrayList<>();
  private List<Player> homeJglPlayers = new ArrayList<>();
  private List<Player> homeMidPlayers = new ArrayList<>();
  private List<Player> homeBotPlayers = new ArrayList<>();
  private List<Player> homeSupPlayers = new ArrayList<>();

  private String guestTeam;
  private List<PlayerView> guestPlayers = new ArrayList<>();

  @PostConstruct
  public void init() {
    try {
      Team home = Team.find("Technical Really Unique Esports");
      this.homeTeam = home;
      List<Map<Player, Integer>> games = Arrays.asList(new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>());

      for (Player player : home.getPlayers()) {
        List<Integer> gamesOnLane = player.getGamesOn();
        for (int i = 0; i < gamesOnLane.size(); i++) {
          games.get(i).put(player, gamesOnLane.get(i));
        }
      }
      List<Player> highlightedPlayers = new ArrayList<>();
      for (int i = 0; i < games.size(); i++) {
        final Map<Player, Integer> game = games.get(i);
        Map<Player, Integer> players = game.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        games.set(i, players);
        highlightedPlayers.add(new ArrayList<>(players.keySet()).get(0));
      }

      this.homeTopPlayers = new ArrayList<>(games.get(0).keySet());
      this.homeJglPlayers = new ArrayList<>(games.get(1).keySet());
      this.homeMidPlayers = new ArrayList<>(games.get(2).keySet());
      this.homeBotPlayers = new ArrayList<>(games.get(3).keySet());
      this.homeSupPlayers = new ArrayList<>(games.get(4).keySet());
      this.homePlayers = IntStream.range(0, highlightedPlayers.size())
          .mapToObj(i -> new PlayerView(highlightedPlayers.get(i),
              i == 0 ? Lane.TOP : i == 1 ? Lane.JUNGLE : i == 2 ? Lane.MIDDLE : i == 3 ? Lane.BOTTOM : Lane.UTILITY))
          .collect(Collectors.toList());
      FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Geladen", "");
      FacesContext.getCurrentInstance().addMessage(null, message);
      System.out.println("GELADEN!!!!!");

    } catch (Exception exception) {
      exception.printStackTrace();
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      exception.printStackTrace(pw);
      FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Exception loading Table + ", sw.toString());
      FacesContext.getCurrentInstance().addMessage(null, message);
      System.out.println("NICHT GELADEN!!!!!");
    }
  }
}
