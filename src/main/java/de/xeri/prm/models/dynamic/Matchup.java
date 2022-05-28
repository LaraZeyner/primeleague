package de.xeri.prm.models.dynamic;

import java.util.List;

import de.xeri.prm.util.Util;
import lombok.Data;
import lombok.NonNull;

/**
 * Created by Lara on 25.05.2022 for web
 */
@Data
public class Matchup {
  public static Matchup fromObjects(Object[] objects) {
    Champion champ = (Champion) objects[0];
    int games = Util.getInt(objects[1]);
    int wins = Util.getInt(objects[2]);
    return new Matchup(champ, games, wins);
  }

  @NonNull
  private Champion champion;

  @NonNull
  private int games;

  @NonNull
  private int wins;

  public List<Matchup> merge(List<Matchup> matchups) {
    boolean match = false;
    for (Matchup matchup : matchups) {
      if (matchup.getChampion().equals(champion)) {
        matchup.addGames(games);
        matchup.addWins(wins);
        match = true;
      }
    }

    if (!match) {
      matchups.add(this);
    }

    return matchups;
  }

  private void addGames(int games) {
    this.games += games;
  }

  private void addWins(int wins) {
    this.wins += wins;
  }

  public double getWinrate() {
    return Util.div(wins, games, wins);
  }
}
