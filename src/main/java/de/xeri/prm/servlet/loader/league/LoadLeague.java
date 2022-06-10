package de.xeri.prm.servlet.loader.league;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import de.xeri.prm.loader.MatchLoader;
import de.xeri.prm.manager.PrimeData;
import de.xeri.prm.models.enums.StageType;
import de.xeri.prm.models.league.League;
import de.xeri.prm.models.league.Matchday;
import de.xeri.prm.models.league.Season;
import de.xeri.prm.models.league.TurnamentMatch;
import de.xeri.prm.util.Const;
import de.xeri.prm.util.FacesUtil;
import lombok.Getter;
import lombok.Setter;
//TODO (Abgie) 18.05.2022: Wenn Spieler ausgewählt wird - Spiele suchen

/**
 * Created by Lara on 18.05.2022 for web
 */
@ManagedBean
@ApplicationScoped
@Getter
@Setter
public class LoadLeague implements Serializable {
  private static final long serialVersionUID = 4532805787883011744L;
  private League league;
  private List<LeagueTeam> leagueTeams;
  private List<MatchdayMatches> matchdays;

  @PostConstruct
  public void init() {
    try {
      this.league = PrimeData.getInstance().getCurrentGroup();
      reload();

      updateAll();

    } catch (Exception exception) {
      FacesUtil.sendException("Ligatabelle wurde nicht geladen", exception);
    }
  }

  public void updateAll() {
    try {
      boolean changed = false;
      for (TurnamentMatch match : league.getMatches()) {
        if (match.isRecently()) {
          changed = MatchLoader.analyseMatchPage(match);
          if (match.isOpen()) {
            changed = changed || match.update();
          }
        }

      }
      if (changed) {
        reload();
      }
      FacesUtil.sendMessage("Ligatabelle geladen",changed + "");
      PrimeData.getInstance().commit();
    } catch (Exception exception) {
      FacesUtil.sendException("Ligatabelle wurde nicht geladen", exception);
    }
  }

  private void reload() {
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
  }

  public List<TurnamentMatch> getMatches() {
    return matchdays.stream().flatMap(matchday -> matchday.getMatches().stream()).collect(Collectors.toList());
  }

  public List<Matchday> getDays() {
    final Season season = league.getStage().getSeason();
    final List<Matchday> collects = season.getStages().stream()
        .flatMap(stage -> stage.getMatchdays().stream())
        .sorted(Comparator.comparing(Matchday::getStart))
        .collect(Collectors.toList());

    List<Matchday> collect = new ArrayList<>();
    Date kali = null;
    for (Matchday matchday : collects) {
      if (matchday.getStage().getStageType().equals(StageType.GRUPPENPHASE)) {
        collect.add(matchday);
      } else {
        if (kali == null || matchday.getStart().getTime() - kali.getTime() >= Const.MILLIS_PER_DAY) {
          kali = matchday.getStart();
          collect.add(matchday);
        }
      }
    }
    return collect.stream()
        .sorted(Comparator.comparing(Matchday::getStart))
        .collect(Collectors.toList());
  }


}
