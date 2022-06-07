package de.xeri.prm.servlet.datatables.draft;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import de.xeri.prm.loader.ScheduleLoader;
import de.xeri.prm.models.dynamic.Matchup;
import de.xeri.prm.models.league.Schedule;
import de.xeri.prm.models.league.Team;
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
      //this.enemyTeam = new TeamView(Team.find("Leipzig Esport eV Salty Badgers"));
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

  private Timing determineTiming(String name, List<String> values) {
    return new Timing(name, values.get(0), values.get(1), values.get(2), values.get(3));
  }

  private List<String> determineMatchups(TeamView ourTeam, Draft draft) {
    final List<String> collect = IntStream.range(0, 3)
        .mapToObj(i -> ourTeam.getViews().get(i).getSelectedPlayer().getMatchup(draft.getOur().getPicks().get(0), draft.getEnemy().getPicks().get(0)))
        .map(matchup -> matchup.getGames() + " " + (Math.round(matchup.getWinrate() * 100) / 100) + "%")
        .collect(Collectors.toList());
    final Matchup matchupBot = ourTeam.getViews().get(3).getSelectedPlayer().getMatchup(draft.getOur().getPicks().get(0), draft.getEnemy().getPicks().get(0));
    final Matchup matchupSup = ourTeam.getViews().get(3).getSelectedPlayer().getMatchup(draft.getOur().getPicks().get(0), draft.getEnemy().getPicks().get(0));
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
}
