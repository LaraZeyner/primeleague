package de.xeri.prm.servlet.datatables.scouting.draft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import de.xeri.prm.models.dynamic.Champion;
import de.xeri.prm.servlet.datatables.scouting.ChampionView;
import de.xeri.prm.servlet.datatables.scouting.TeamView;
import de.xeri.prm.util.HibernateUtil;
import lombok.Data;

/**
 * Created by Lara on 24.05.2022 for web
 */
@Data
public class Composition {
  private List<Champion> champions;
  private List<String> goodAttributes;
  private List<String> badAttributes;
  private List<String> recommendedPicks;
  private TeamView teamView;
  private List<Champion> presentChampions;

  public Composition(TeamView view) {
    this.champions = Arrays.asList(null, null, null, null, null);
    this.teamView = view;
    this.presentChampions = view.getPlayers().stream()
        .flatMap(player -> player.getChampions().stream())
        .map(ChampionView::getChampion)
        .collect(Collectors.toList());
  }

  public void updateComposition() {
    final Map<Champion, Map<CompositionAttribute, Double>> championStats = HibernateUtil.getChampionStats();
    final Map<CompositionAttribute, Double> compChampionStats = new HashMap<>();
    for (Champion champion : champions) {
      if (champion != null) {
        championStats.get(champion).forEach((key, value) -> compChampionStats.merge(key, value, Double::sum));
      }
    }

    List<String> good = new ArrayList<>();
    List<String> bad = new ArrayList<>();
    final int count = (int) champions.stream().filter(Objects::nonNull).count();
    for (CompositionAttribute compositionAttribute : compChampionStats.keySet()) {
      final double value = compChampionStats.get(compositionAttribute);

      if (compositionAttribute.equals(CompositionAttribute.PLAYSTYLE_SPLITPUSH)) {
        if ((int) value == 2) {
          good.add(compositionAttribute.getName());

        } else if ((int) value != 1) {
          bad.add(compositionAttribute.getName());
        }

      } else {
        if (compositionAttribute.isAbove(value, count)) {
          good.add(compositionAttribute.getName());

        } else if (compositionAttribute.isBelow(value, count)) {
          bad.add(compositionAttribute.getName());
        }
      }
    }
    this.goodAttributes = good;
    this.badAttributes = bad;
  }

  /**
   * Champions die einen Gegnerischen Champion countern (wie auch immer)
   * Champions mit sehr gutem Matchup
   * Champions mit Synergie mit anderem Champion
   * Champions, die Composition abrunden würden
   */
  public void updateChampions() {

  }

}
