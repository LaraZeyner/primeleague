package de.xeri.prm.servlet.datatables.league;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import de.xeri.prm.manager.Data;
import de.xeri.prm.models.league.League;
import de.xeri.prm.models.league.TurnamentMatch;
import lombok.Getter;
//TODO (Abgie) 18.05.2022: Wenn Spieler ausgewählt wird - Spiele suchen

/**
 * Created by Lara on 18.05.2022 for web
 */
@ManagedBean
@ApplicationScoped
@Getter
public class LoadLeague implements Serializable {
  private static final long serialVersionUID = 4532805787883011744L;
  private League league;
  private List<LeagueTeam> leagueTeams;
  private List<MatchdayMatches> matchdays;

  @PostConstruct
  public void init() {
    try {
      this.league = Data.getInstance().getCurrentGroup();
      this.leagueTeams = new ArrayList<>();
      league.getTeams().forEach(team -> leagueTeams.add(team.getLeagueTeam()));
      Collections.sort(leagueTeams);
      double winrate = -1;
      for (int i = 0; i < leagueTeams.size(); i++) {
        final LeagueTeam leagueTeam = leagueTeams.get(i);
        leagueTeam.setPlace(winrate != leagueTeam.getWinrate() ? String.valueOf(i + 1) : "");
        winrate = leagueTeam.getWinrate();
      }

      this.matchdays = league.getMatchdays().keySet().stream()
          .map(matchday -> new MatchdayMatches(matchday, league))
          .collect(Collectors.toList());
      Collections.sort(matchdays);



      // league.getMatches().forEach(TurnamentMatch::update);

    } catch (Exception exception) {
      exception.printStackTrace();
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      exception.printStackTrace(pw);
      FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Exception loading Ligaatable ", sw.toString());
      FacesContext.getCurrentInstance().addMessage(null, message);
      System.err.println("NICHT GELADEN!!!!!");
    }
  }


  public void update() {
    boolean updated = lookForUpdates();
    if (updated) {
      init();
    }
  }


  private boolean lookForUpdates() {
    return league.getMatches().stream()
        .filter(turnamentMatch -> turnamentMatch.isRunning() || turnamentMatch.isRecently())
        .findFirst().filter(TurnamentMatch::update).isPresent();
  }

}
