package de.xeri.prm.servlet.loader.scouting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import de.xeri.prm.loader.ScheduleLoader;
import de.xeri.prm.models.dynamic.Champion;
import de.xeri.prm.models.dynamic.Matchup;
import de.xeri.prm.models.league.Schedule;
import de.xeri.prm.models.league.Team;
import de.xeri.prm.servlet.loader.scouting.composition.Draft;
import de.xeri.prm.servlet.loader.scouting.composition.Timing;
import de.xeri.prm.servlet.loader.scouting.performance.ChampionView;
import de.xeri.prm.servlet.loader.scouting.performance.TeamView;
import de.xeri.prm.util.FacesUtil;
import de.xeri.prm.util.Util;
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
  private TeamView ourTeam;
  private TeamView enemyTeam;
  private Draft draft;
  private List<Timing> timings;
  private List<Champion> flexpicks;

  @PostConstruct
  public void init() {
    try {
      this.ourTeam = new TeamView(Team.find("Technical Really Unique Esports"));
      ScheduleLoader.load();
      final List<Schedule> allSchedules = new ArrayList<>(Schedule.get());

      this.enemyTeam = new TeamView(allSchedules.stream()
          .filter(schedule -> schedule.getEndTime().after(new Date()))
          .min(Comparator.comparing(Schedule::getStartTime))
          .orElse(allSchedules.get(allSchedules.size() - 1))
          .getEnemyTeam());
      this.draft = new Draft(ourTeam, enemyTeam);

      this.timings = Arrays.asList(
          determineTiming("Matchup", determineMatchups(ourTeam, draft)),
          determineTiming("LaneLead", determineLeads(enemyTeam)),
          determineTiming("1. Ward", determineFirstWard(enemyTeam)),
          determineTiming("1. Objec.", determineFirstObjective(enemyTeam)),
          determineTiming("1. Kill", determineFirstKill(enemyTeam)),
          determineTiming("1. Recall", determineFirstRecall(enemyTeam)),
          determineTiming("1. Item", determineFirstItem(enemyTeam)),
          new Timing("Lategame", "Split", "Engage", "Carry", "Carry")
      );

      List<Champion> we = ourTeam.getViews().stream().flatMap(view -> view.getView().getChampions().stream()).map(ChampionView::getChampion).collect(Collectors.toList());
      we.addAll(enemyTeam.getViews().stream().flatMap(view -> view.getView().getChampions().stream()).map(ChampionView::getChampion).collect(Collectors.toList()));
      Set<Champion> multiple = new HashSet<>();
      for (Champion c : we) {
        final int frequency = Collections.frequency(we, c);
        if (frequency > 1) {
          multiple.add(c);
        }
      }
      this.flexpicks = new ArrayList<>(multiple);

      ourTeam.getViews().stream()
          .flatMap(view -> view.getView().getChampions().stream())
          .filter(champion -> flexpicks.contains(champion.getChampion()))
          .forEach(champion -> champion.setFlexpick(true));

      enemyTeam.getViews().stream()
          .flatMap(view -> view.getView().getChampions().stream())
          .filter(champion -> flexpicks.contains(champion.getChampion()))
          .forEach(champion -> champion.setFlexpick(true));

      FacesUtil.sendMessage("Geladen", "");
      System.out.println("GELADEN!!!!!");

    } catch (Exception exception) {
      FacesUtil.sendException("Exception loading Table + ", exception);
      System.out.println("NICHT GELADEN!!!!!");
    }
  }

  private Timing determineTiming(String name, List<String> values) {
    return new Timing(name, values.get(0), values.get(1), values.get(2), values.get(3));
  }

  private List<String> determineMatchups(TeamView ourTeam, Draft draft) {
    final List<String> collect = IntStream.range(0, 3)
        .mapToObj(i -> ourTeam.getViews().get(i).getSelectedPlayer().getMatchup(draft.getOur().getTeamView().getSelected().get(0),
            draft.getEnemy().getTeamView().getSelected().get(0)))
        .map(matchup -> matchup.getGames() + " " + (Math.round(matchup.getWinrate() * 100) / 100) + "%")
        .collect(Collectors.toList());
    final Matchup matchupBot = ourTeam.getViews().get(3).getSelectedPlayer().getMatchup(draft.getOur().getTeamView().getSelected().get(3),
        draft.getEnemy().getTeamView().getSelected().get(3));
    final Matchup matchupSup = ourTeam.getViews().get(3).getSelectedPlayer().getMatchup(draft.getOur().getTeamView().getSelected().get(3),
        draft.getEnemy().getTeamView().getSelected().get(3));
    final String botSide = (matchupBot.getGames() + matchupSup.getGames()) + " " +
        (Math.round(Util.div(matchupBot.getWins() + matchupSup.getWins(), matchupBot.getGames() + matchupSup.getGames()) * 100) / 100) + "%";
    collect.add(botSide);
    return collect;
  }

  private List<String> determineLeads(TeamView team) {
    return IntStream.range(0, 5).mapToObj(i -> team.getViews().get(i).getView().getLead()).collect(Collectors.toList());
  }

  private List<String> determineFirstWard(TeamView team) {
    return IntStream.range(0, 5).mapToObj(i -> team.getViews().get(i).getView().getFirstWard()).collect(Collectors.toList());
  }

  private List<String> determineFirstObjective(TeamView team) {
    return IntStream.range(0, 5).mapToObj(i -> team.getViews().get(i).getView().getFirstObjective()).collect(Collectors.toList());
  }

  private List<String> determineFirstKill(TeamView team) {
    return IntStream.range(0, 5).mapToObj(i -> team.getViews().get(i).getView().getFirstKill()).collect(Collectors.toList());
  }

  private List<String> determineFirstRecall(TeamView team) {
    return IntStream.range(0, 5).mapToObj(i -> team.getViews().get(i).getView().getFirstRecall()).collect(Collectors.toList());
  }

  private List<String> determineFirstItem(TeamView team) {
    return IntStream.range(0, 5).mapToObj(i -> team.getViews().get(i).getView().getFirstItem()).collect(Collectors.toList());
  }

  public String redirectOPgg() {
    if (enemyTeam.getViews() != null) {
      return enemyTeam.getViews().stream()
          .map(view -> view.getSelectedPlayer().getActiveAccount().getName())
          .collect(Collectors.joining("%2C", "https://euw.op.gg/multisearch/euw?summoners=", ""));
    }
    init();
    return redirectOPgg();
  }

  public String redirectPorofessor() {
    if (enemyTeam.getViews() != null) {
      return enemyTeam.getViews().stream()
          .map(view -> view.getSelectedPlayer().getActiveAccount().getName())
          .collect(Collectors.joining(",", "https://porofessor.gg/pregame/euw/", "/season"));
    }
    init();
    return redirectOPgg();
  }

  public void update() {
    this.draft = new Draft(ourTeam, enemyTeam);
  }
}
