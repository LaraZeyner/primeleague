package de.xeri.prm.servlet.loader.league;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import de.xeri.prm.loader.GameIdLoader;
import de.xeri.prm.loader.MatchLoader;
import de.xeri.prm.manager.PrimeData;
import de.xeri.prm.models.enums.Matchstate;
import de.xeri.prm.models.enums.StageType;
import de.xeri.prm.models.league.League;
import de.xeri.prm.models.league.Matchday;
import de.xeri.prm.models.league.Season;
import de.xeri.prm.models.league.Team;
import de.xeri.prm.models.league.TurnamentMatch;
import de.xeri.prm.models.match.Teamperformance;
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


  /**
   * Szenarien
   * Spieltag  fuer 7er            fuer 8er
   * Tag 1: 10.460.353.203x   22.876.792.454.961x
   * Tag 2:    387.420.489x      282.429.536.481x
   * Tag 3:     14.348.907x        3.486.784.401x
   * Tag 4:        531.441x           43.046.721x
   * Tag 5:         19.683x              531.441x
   * Tag 6:            729x                6.561x
   * Tag 7:             27x                   81x
   */
  @PostConstruct
  public void init() {
    try {
      this.league = PrimeData.getInstance().getCurrentGroup();
      reload();

      updateAll();


      for (LeagueTeam leagueTeam : leagueTeams) {
        for (Teamperformance primePerformance : leagueTeam.getTeam().getLeaguePerformances()) {
          Team otherTeam = primePerformance.getOtherTeamperformance().getTeam();
          if (otherTeam != null) {
            LeagueTeam enemyLeagueTeam = leagueTeams.stream().filter(lt -> lt.getTeam().equals(otherTeam)).findFirst().orElse(null);
            if (enemyLeagueTeam != null) {
              final double teamMMR = leagueTeam.getTeamMMR();
              final double enemyMMR = enemyLeagueTeam.getTeamMMR();
              if (primePerformance.isWin()) {
                leagueTeam.setTeamMMR(teamMMR + enemyMMR / 40);
                enemyLeagueTeam.setTeamMMR(enemyMMR - enemyMMR / 40);
              } else {
                leagueTeam.setTeamMMR(teamMMR - teamMMR / 40);
                enemyLeagueTeam.setTeamMMR(enemyMMR + teamMMR / 40);
              }
            }
          }
        }
      }

      Map<LeagueTeam, Integer> points = new HashMap<>();

      final double sum = leagueTeams.stream().mapToDouble(LeagueTeam::getTeamMMR).sum();
      final List<TurnamentMatch> collect = new ArrayList<>();
      for (MatchdayMatches matchday : matchdays) {
        for (TurnamentMatch match : matchday.getMatches()) {
          final LeagueTeam homeTeam = leagueTeams.stream().filter(lt -> lt.getTeam().equals(match.getHomeTeam())).findFirst().orElse(null);
          final LeagueTeam guestTeam = leagueTeams.stream().filter(lt -> lt.getTeam().equals(match.getGuestTeam())).findFirst().orElse(null);
          if (homeTeam != null && guestTeam != null) {
            final int expectedResult = match.getExpectedResult(homeTeam.getTeamMMR(), guestTeam.getTeamMMR());
            if (expectedResult > -1) {
              points.put(homeTeam, points.containsKey(homeTeam) ? points.get(homeTeam) + expectedResult : expectedResult);
              points.put(guestTeam, points.containsKey(guestTeam) ? points.get(guestTeam) + 2 - expectedResult : 2 - expectedResult);
            }
          }
        }
      }
      final List<Integer> pts = points.values().stream().sorted(Comparator.comparing(Integer::intValue)).collect(Collectors.toList());
      final Integer minPoints = pts.get(2);
      final Integer maxPoints = pts.get(pts.size() - 2);

      List<TurnamentMatch> openMatches = matchdays.stream()
          .flatMap(matchday -> matchday.getMatches().stream()).filter(match -> !match.getState().equals(Matchstate.CLOSED))
          .collect(Collectors.toList());
      for (LeagueTeam leagueTeam : leagueTeams) {
        int pointsNeededToDecrease = minPoints - leagueTeam.getWinsGames();
        double totalProbability = 0;
        if (pointsNeededToDecrease > 0) {
          final List<List<Integer>> generate = generate(openMatches.size(), pointsNeededToDecrease, true);
          leagueTeam.setDownProbability(generate.size() < Math.pow(openMatches.size(), 3) ? determineProbability(openMatches, generate, leagueTeam) : 1);
        } else {
          leagueTeam.setDownProbability(0);
        }

        int pointsNeededToIncrease = maxPoints - leagueTeam.getWinsGames();
        if (pointsNeededToIncrease > 0) {
          final List<List<Integer>> generate = generate(openMatches.size(), pointsNeededToIncrease, false);
          leagueTeam.setUpProbability(!generate.isEmpty() ? determineProbability(openMatches, generate, leagueTeam) : 0);
        } else {
          leagueTeam.setUpProbability(1);
        }
      }
    } catch (Exception exception) {
      FacesUtil.sendException("Ligatabelle wurde nicht geladen", exception);
    }
  }

  private double determineProbability(List<TurnamentMatch> openMatches, List<List<Integer>> generate, LeagueTeam leagueTeam) {
    double totalProbability = 0;
    for (List<Integer> matches : generate) {
      double probability = 1;
      for (int i = 0; i < matches.size(); i++) {
        final Integer result = matches.get(i);
        final TurnamentMatch turnamentMatch = openMatches.get(i);
        final LeagueTeam homeTeam = leagueTeams.stream().filter(lt -> lt.getTeam().equals(turnamentMatch.getHomeTeam())).findFirst().orElse(null);
        final LeagueTeam guestTeam = leagueTeams.stream().filter(lt -> lt.getTeam().equals(turnamentMatch.getGuestTeam())).findFirst().orElse(null);
        if (homeTeam != null && guestTeam != null) {
          if (result == 0 && leagueTeam.equals(homeTeam) || result == 2 && leagueTeam.equals(guestTeam)) {
            probability *= turnamentMatch.getPercentageLose(homeTeam.getTeamMMR(), guestTeam.getTeamMMR());
          } else if (result == 1) {
            probability *= turnamentMatch.getPercentageTie(homeTeam.getTeamMMR(), guestTeam.getTeamMMR());
          } else if (result == 2 && leagueTeam.equals(homeTeam) || result == 0 && leagueTeam.equals(guestTeam)) {
            probability *= turnamentMatch.getPercentageWin(homeTeam.getTeamMMR(), guestTeam.getTeamMMR());
          }
        } else {
          probability *= 0;
        }
      }
      totalProbability += probability;
    }
    return totalProbability;
  }

  public void updateAll() {
    //TODO (Abgie) 12.06.2022: Für Scrimpartner - Nächstes Match
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

  public void updateGames() {
    league.getTeams().forEach(team -> team.getPlayers().forEach(player -> player.getAccounts().forEach(GameIdLoader::loadGameIds)));
    PrimeData.getInstance().commit();
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

  private List<List<Integer>> generate(int r, int needed, boolean low) {
    List<List<Integer>> combinations = new ArrayList<>();
    double amount = Math.pow(r, 3);
    for (int i = 0; i < amount; i++) {
      List<Integer> ints = IntStream.range(0, r).mapToObj(j -> 0).collect(Collectors.toList());
      int index = ints.size();
      int iterator = i;
      while (iterator > 0) {
        index--;
        ints.set(index, iterator % 3);
        iterator /= 3;
      }

      final Integer sum = ints.stream().reduce(0, Integer::sum);
      if (low && sum < needed || !low && sum >= needed) {
        combinations.add(ints);
      }
    }
    return combinations;
  }


}
