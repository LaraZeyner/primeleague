package de.xeri.prm.servlet.loader.scouting.composition;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.xeri.prm.models.dynamic.Champion;
import de.xeri.prm.servlet.loader.scouting.performance.TeamView;
import lombok.Data;

/**
 * Created by Lara on 23.05.2022 for web
 */
@Data
public class Draft {
  private Composition our;
  private Composition enemy;

  private List<PickRow> picks;
  private List<Champion> allChampions;

  public Draft(TeamView ourTeam, TeamView enemyTeam) {
    final Composition our = new Composition(ourTeam);
    final Composition enemy = new Composition(enemyTeam);
    our.updateChampions(enemy);
    enemy.updateChampions(our);

    this.picks = IntStream.range(0, 5)
        .mapToObj(i -> new PickRow(our.getPicks().get(i) != null ? our.getPicks().get(i).getName() : "",
            our.getBanns().get(i) != null ? our.getBanns().get(i).getName() : "",
            enemy.getBanns().get(i) != null ? enemy.getBanns().get(i).getName() : "",
            enemy.getPicks().get(i) != null ? enemy.getPicks().get(i).getName() : ""))
        .collect(Collectors.toList());
    this.allChampions = new ArrayList<>(Champion.get());
    this.our = our;
    this.enemy = enemy;
  }

  public void addBan(boolean we, String championName, int id) {
    final Champion champion = Champion.find(championName);
    if (we) {
      picks.get(id).setBanWe(championName);
      our.addBan(id, champion, enemy);
    } else {
      picks.get(id).setBanEnemy(championName);
      enemy.addBan(id, champion, our);
    }
  }

  public void addPick(boolean we, String championName, int id) {
    final Champion champion = Champion.find(championName);
    if (we) {
      picks.get(id).setPickWe(championName);
      our.addPick(id, champion, enemy);
    } else {
      picks.get(id).setPickEnemy(championName);
      enemy.addPick(id, champion, our);
    }
  }

  public void removeBan(boolean we, int id) {
    if (we) {
      picks.get(id).setBanWe("");
      our.removeBan(id, enemy);
    } else {
      picks.get(id).setBanEnemy("");
      enemy.removeBan(id, our);
    }
  }

  public void removePick(boolean we, int id) {
    if (we) {
      picks.get(id).setPickWe("");
      our.removePick(id, enemy);
    } else {
      picks.get(id).setPickEnemy("");
      enemy.removePick(id, our);
    }
  }

}
