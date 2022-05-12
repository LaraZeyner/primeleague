package de.xeri.prm.game.models;

/**
 * Created by Lara on 03.05.2022 for web
 */
public enum TimelineStat {
  CREEP_SCORE("minionsKilled", "jungleMinionsKilled"),
  CURRENT_GOLD("currentGold"),
  CURRENT_HEALTH("/championStats", "health"),
  CURRENT_RESOURCE("/championStats", "power"),
  DAMAGE("/damageStats", "totalDamageDoneToChampions"),
  ENEMY_CONTROLLED("timeEnemySpentControlled"),
  EXPERIENCE("xp"),
  LEAD("xp", "currentGold"),
  MOVEMENT_SPEED("/championStats", "movementSpeed"),
  TOTAL_GOLD("totalGold"),
  TOTAL_HEALTH("/championStats", "healthMax"),
  TOTAL_RESOURCE("/championStats", "powerMax"),
  POSITION_X("/position", "x"),
  POSITION_Y("/position", "y");

  private final String[] queries;

  TimelineStat(String... queries) {
    this.queries = queries;
  }

  public String[] getQueries() {
    return queries;
  }
}
