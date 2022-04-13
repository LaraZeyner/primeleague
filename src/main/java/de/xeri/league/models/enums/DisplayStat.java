package de.xeri.league.models.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Lara on 12.04.2022 for web
 */
public enum DisplayStat {
  KDA_LONG (StatCategory.AGGRESSION, Arrays.asList(StoredStat.KILLS, StoredStat.ASSISTS, StoredStat.DEATHS)),
  KDA_RATIO (StatCategory.AGGRESSION, Arrays.asList(StoredStat.KILLS, StoredStat.ASSISTS, StoredStat.DEATHS));

  private final StatCategory category;
  private final List<StoredStat> stats;

  DisplayStat(StatCategory category, List<StoredStat> stats) {
    this.category = category;
    this.stats = stats;
  }

  public StatCategory getCategory() {
    return category;
  }

  public List<StoredStat> getStats() {
    return stats;
  }
}
