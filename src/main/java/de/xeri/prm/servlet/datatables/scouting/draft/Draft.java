package de.xeri.prm.servlet.datatables.scouting.draft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.xeri.prm.models.dynamic.Champion;
import de.xeri.prm.servlet.datatables.scouting.PickRow;
import de.xeri.prm.servlet.datatables.scouting.TeamView;
import lombok.Data;

/**
 * Created by Lara on 23.05.2022 for web
 */
@Data
public class Draft {
  private List<Champion> banns;
  private Composition our;
  private Composition enemy;

  private List<PickRow> picks;
  private List<Champion> allChampions;

  public Draft(TeamView ourTeam, TeamView enemyTeam) {
    this.banns = Arrays.asList(null, null, null, null, null);
    this.our = new Composition(ourTeam);
    this.enemy = new Composition(enemyTeam);
    this.picks = Arrays.asList(
        new PickRow("", "", ""),
        new PickRow("", "", ""),
        new PickRow("", "", ""),
        new PickRow("", "", ""),
        new PickRow("", "", ""));
    this.allChampions = new ArrayList<>(Champion.get());
  }

  public void addBan(String championName, int id) {
    banns.set(id, Champion.find(championName));
    picks.get(id).setBan(championName);
  }

  public void addOurPick(String championName, int id) {
    our.getChampions().set(id, Champion.find(championName));
    picks.get(id).setPickWe(championName);
    our.updateComposition();
  }

  public void addEnemyPick(String championName, int id) {
    enemy.getChampions().set(id, Champion.find(championName));
    picks.get(id).setPickEnemy(championName);
    enemy.updateComposition();
  }

  public void removeBan(int id) {
    banns.set(id, null);
    picks.get(id).setBan("");
  }

  public void removeOurPick(int id) {
    our.getChampions().set(id, null);
    picks.get(id).setPickWe("");
    our.updateComposition();
  }

  public void removeEnemyPick(int id) {
    enemy.getChampions().set(id, null);
    picks.get(id).setPickEnemy("");
    enemy.updateComposition();
  }
}
