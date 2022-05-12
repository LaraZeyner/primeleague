package de.xeri.league.servlet.datatables.table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import de.xeri.league.models.dynamic.Champion;
import de.xeri.league.models.league.Account;
import de.xeri.league.models.league.TurnamentMatch;
import de.xeri.league.models.match.neu.Ratings;
import de.xeri.league.models.match.playerperformance.Playerperformance;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.CellEditEvent;

/**
 * Created by Lara on 09.05.2022 for web
 */
@ManagedBean(eager = true)
@SessionScoped
@Getter
@Setter
public class ChampionTable implements Serializable {
  private static final transient long serialVersionUID = -7441859998261775332L;
  private String championName;
  private String presence;
  private String gamesComp;
  private String games;
  private String wins;
  private String lead;
  private String goldEfficency;

  private static String competitiveGames;
  private static String playername;
  private static String kda;
  private static String vision;
  private static String roaming;
  private static String fighting;
  private static String income;
  private static String survival;
  private static String laning;
  private static List<PerformanceObject> performances;
  private static List<PerformanceObject> allPerformances;
  private static List<TurnamentMatch> matchesToUpdate;
  private static List<String> champions;

  @PostConstruct
  public void init() {
    try {
      champions = Champion.get().stream().map(Champion::getName).collect(Collectors.toList());
      Account account = Account.findName("TRUE Whitelizard");
      final List<Playerperformance> playerperformances = new ArrayList<>(account.getPlayerperformances());

      competitiveGames = String.valueOf(playerperformances.stream()
          .filter(playerperformance -> playerperformance.getTeamperformance().getGame().isCompetitive())
          .count());

      final List<Champion> collect = Champion.get().stream()
          .collect(Collectors.toMap(champion -> champion, champion -> (int) playerperformances.stream()
              .filter(playerperformance -> playerperformance.getTeamperformance().getGame().isCompetitive())
              .filter(playerperformance -> playerperformance.wasPresent(champion)).count(), (a, b) -> b))
          .entrySet().stream()
          .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
          .map(Map.Entry::getKey)
          .collect(Collectors.toList()).subList(0, 10);


      for (Champion champion : Champion.get()) {
        final List<Playerperformance> collect1 = playerperformances.stream()
            .filter(playerperformance -> playerperformance.getTeamperformance().getGame().isCompetitive())
            .filter(playerperformance -> playerperformance.wasPresent(champion)).collect(Collectors.toList());
        String competitive = String.valueOf(collect1.size());
        final int presenceInt = collect1.size() * 100 / Integer.parseInt(competitiveGames);
        String presence = String.valueOf(presenceInt);

        final List<Playerperformance> collect2 = playerperformances.stream()
            .filter(playerperformance -> playerperformance.wasPresent(champion)).collect(Collectors.toList());
        final Ratings ratings = new Ratings(collect2);
        String games = String.valueOf(collect2.size());
        final String wins = ratings.winrate.format();
        final String lead = ratings.laneLead.format();
        final String efficiency = ratings.goldXpEfficiency.format();

        allPerformances.add(new PerformanceObject(champion.getName(), presence, competitive, games, wins, lead, efficiency));

        for (Champion champion1 : collect) {
          performances.add(allPerformances.stream()
              .filter(pO -> pO.getChampionName().equals(champion1.getName()))
              .findFirst().orElse(null));
        }
      }

    } catch (Exception ex) {
      FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Exception loading Table", ex.getMessage());
      FacesContext.getCurrentInstance().addMessage(null, message);
      performances.add(new PerformanceObject("Urgot", "38%", "1", "10", "70%", "939", "53%"));
      performances.add(new PerformanceObject("Urgot", "38%", "1", "10", "70%", "939", "53%"));
      performances.add(new PerformanceObject("Urgot", "38%", "1", "10", "70%", "939", "53%"));
      performances.add(new PerformanceObject("Urgot", "38%", "1", "10", "70%", "939", "53%"));
      performances.add(new PerformanceObject("Urgot", "38%", "1", "10", "70%", "939", "53%"));
      performances.add(new PerformanceObject("Urgot", "38%", "1", "10", "70%", "939", "53%"));
      performances.add(new PerformanceObject("Urgot", "38%", "1", "10", "70%", "939", "53%"));
      performances.add(new PerformanceObject("Urgot", "38%", "1", "10", "70%", "939", "53%"));
      performances.add(new PerformanceObject("Urgot", "38%", "1", "10", "70%", "939", "53%"));
      performances.add(new PerformanceObject("Urgot", "38%", "1", "10", "70%", "939", "53%"));
    }
  }

  public void onCellEdit(CellEditEvent event) {
    Object oldValue = event.getOldValue();
    Object newValue = event.getNewValue();

    if (newValue != null && !newValue.equals(oldValue)) {
      String neww = String.valueOf(newValue);
      final PerformanceObject newPerf = allPerformances.stream().filter(pO -> pO.getChampionName().equals(neww)).findFirst().orElse(null);
      IntStream.range(0, performances.size()).filter(i -> performances.get(i).getChampionName().equals(neww)).forEach(i -> performances.set(i, newPerf));
      FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Champion geändert", "");
      FacesContext.getCurrentInstance().addMessage(null, msg);
    }
  }

  public static List<PerformanceObject> getPerformances() {
    return performances;
  }

  public static List<String> getChampions() {
    return champions;
  }
}
