package de.xeri.league.models.league;

import de.xeri.league.models.match.Teamperformance;

/**
 * Created by Lara on 20.04.2022 for web
 */
public class TeamLeaguePerformance {
  private final Team team;

  int wins;
  int losses;
  int siege;
  int ties;
  int defeats;
  int kills;
  int antikills;
  int gold;
  int antigold;
  int creeps;
  int anticreeps;
  int towers;
  int drakes;
  int inhibs;
  int heralds;
  int barons;
  int wintime;
  int losetime;

  public TeamLeaguePerformance(League league, Team team) {
    this.team = team;

    for (Teamperformance competitivePerformance : team.getCompetitivePerformances()) {
      final TurnamentMatch turnamentmatch = competitivePerformance.getGame().getTurnamentmatch();
      if (turnamentmatch.getLeague().equals(league)) {
        final String score = turnamentmatch.getScore();
        final int homePoints = Integer.parseInt(score.split(":")[0]);
        final int guestPoints = Integer.parseInt(score.split(":")[1]);
        if (!score.equals("0:0")) {
          if (homePoints > guestPoints) {
            if (turnamentmatch.getHomeTeam().equals(team)) {
              siege++;
            } else if (turnamentmatch.getGuestTeam().equals(team)) {
              defeats++;
            }
          } else if (homePoints < guestPoints) {
            if (turnamentmatch.getHomeTeam().equals(team)) {
              defeats++;
            } else if (turnamentmatch.getGuestTeam().equals(team)) {
              siege++;
            }
          } else {
            ties++;
          }
        }

        if (competitivePerformance.isWin()) {
          wins++;
        } else {
          losses++;
        }

        final Teamperformance othersTPerformance = competitivePerformance.getOtherTeamperformance();
        kills += competitivePerformance.getTotalKills();
        antikills += othersTPerformance.getTotalKills();
        gold += competitivePerformance.getTotalGold();
        antigold += othersTPerformance.getTotalGold();
        creeps += competitivePerformance.getTotalCs();
        anticreeps += othersTPerformance.getTotalCs();
        towers += competitivePerformance.getTowers();
        drakes += competitivePerformance.getDrakes();
        inhibs += competitivePerformance.getInhibs();
        heralds += competitivePerformance.getHeralds();
        barons += competitivePerformance.getBarons();
        if (competitivePerformance.isWin()) {
          wintime += competitivePerformance.getGame().getDuration();
        } else {
          losetime += competitivePerformance.getGame().getDuration();
        }
      }
    }
  }

  public int getMatchtime() {
    return wintime + losetime;
  }

  public long getScore() {
    final short idScore = team.getId();                                      //                -10.000   10.000
    final int csScore = getCreepDiff() * 10_000;                             //             10.000.000    1.000
    final long goldScore = getGoldDiff() * 10_000_000L;                      //      1.000.000.000.000  100.000
    final long killScore = getKillDiff() * 1_000_000_000_000L;               //    100.000.000.000.000      100
    final long winsScore = wins * 100L / getGames() * 100_000_000_000_000L;  // 10.000.000.000.000.000      100
    return idScore + csScore + goldScore + killScore + winsScore;
  }

  public Team getTeam() {
    return team;
  }

  public int getGames() {
    return wins + losses;
  }

  public int getWins() {
    return wins;
  }

  public double getWinsPerMatch() {
    if (getGames() == 0) {
      return 0;
    }
    return Math.round(wins * 20d / getGames()) / 10;
  }

  public int getLosses() {
    return losses;
  }

  public String getTeamScore() {
    return wins + ":" + losses;
  }

  public int getSiege() {
    return siege;
  }

  public int getTies() {
    return ties;
  }

  public int getDefeats() {
    return defeats;
  }

  public String getBilance() {
    return siege + "  " + ties + "  " + defeats;
  }

  public String getKills() {
    return kills + " : " + antikills;
  }

  public int getKillDiff() {
    return kills - antikills;
  }

  public String getKillsPerMatch() {
    if (getGames() == 0) {
      return "0";
    }
    return (kills * 2 / getGames()) + " : " + (antikills * 2 / getGames());
  }

  public String getGold() {
    return gold + " : " + antigold;
  }

  public int getGoldDiff() {
    return gold - antigold;
  }

  public String getGoldPerMatch() {
    if (getGames() == 0) {
      return "0";
    }
    return (gold * 2 / getGames()) + " : " + (antigold * 2 / getGames());
  }


  public String getCreeps() {
    return creeps + " : " + anticreeps;
  }

  public int getCreepDiff() {
    return creeps - anticreeps;
  }

  public String getCreepsPerMatch() {
    if (getGames() == 0) {
      return "0";
    }
    return (creeps * 2 / getGames()) + " : " + (anticreeps * 2 / getGames());
  }

  public int getObjectives() {
    return towers + drakes + heralds + inhibs + barons;
  }

  public int getObjectivesPerMatch() {
    if (getGames() == 0) {
      return 0;
    }
    return Math.round(getObjectives() * 20 / getGames()) / 10;
  }


  public int getTowers() {
    return towers;
  }

  public int getTowersPerMatch() {
    if (getGames() == 0) {
      return 0;
    }
    return Math.round(towers * 20 / getGames()) / 10;
  }

  public int getDrakes() {
    return drakes;
  }

  public int getDrakesPerMatch() {
    if (getGames() == 0) {
      return 0;
    }
    return Math.round(drakes * 20 / getGames()) / 10;
  }

  public int getInhibs() {
    return inhibs;
  }

  public int getInhibsPerMatch() {
    if (getGames() == 0) {
      return 0;
    }
    return Math.round(inhibs * 20 / getGames()) / 10;
  }

  public int getHeralds() {
    return heralds;
  }

  public int getHeraldsPerMatch() {
    if (getGames() == 0) {
      return 0;
    }
    return Math.round(heralds * 20 / getGames()) / 10;
  }

  public int getBarons() {
    return barons;
  }

  public int getBaronsPerMatch() {
    if (getGames() == 0) {
      return 0;
    }
    return Math.round(barons * 20 / getGames()) / 10;
  }

  public int getGametime() {
    return (wintime + losetime) / getGames();
  }

  public int getWintime() {
    return wintime / wins;
  }

  public int getLosetime() {
    return losetime / losses;
  }
}
