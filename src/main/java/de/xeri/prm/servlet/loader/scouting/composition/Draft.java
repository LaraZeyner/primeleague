package de.xeri.prm.servlet.loader.scouting.composition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import de.xeri.prm.models.dynamic.Champion;
import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.servlet.loader.scouting.performance.ChampionView;
import de.xeri.prm.servlet.loader.scouting.performance.LaneView;
import de.xeri.prm.servlet.loader.scouting.performance.PlayerView;
import de.xeri.prm.servlet.loader.scouting.performance.TeamView;
import de.xeri.prm.util.Const;
import lombok.Data;
import lombok.val;

/**
 * Created by Lara on 23.05.2022 for web
 */
@Data
public class Draft {
  private Composition our;
  private Composition enemy;

  private List<Champion> allChampions;

  public Draft(TeamView ourTeam, TeamView enemyTeam) {
    final Composition our = new Composition(ourTeam);
    final Composition enemy = new Composition(enemyTeam);
    determineRecommended(our, enemy);
    determineRecommended(enemy, our);

    this.allChampions = new ArrayList<>(Champion.get());
    this.our = our;
    this.enemy = enemy;
  }

  /**
   * Champions die einen Gegnerischen Champion countern (wie auch immer)
   * Champions mit sehr gutem Matchup
   * Champions mit Synergie mit anderem Champion
   * Champions, die Composition abrunden würden
   */
  public void determineRecommended(boolean our) {
    determineRecommended(our ? this.our : this.enemy, our ? this.enemy : this.our);
  }

  public void determineRecommended(Composition composition, Composition enemyComposition) {
    List<LaneView> views = composition.getTeamView().getViews();
    for (int i = 0; i < views.size(); i++) {
      final PlayerView playerView = views.get(i).getView();
      final List<Champion> champions = playerView.getChampions().stream().map(ChampionView::getChampion).collect(Collectors.toList());
      final PlayerView enemyView = enemyComposition.getTeamView().getViews().get(i).getView();
      final List<Champion> championsEnemy = enemyView.getChampions().stream().map(ChampionView::getChampion).collect(Collectors.toList());

      final Map<Champion, Double> counters = determineCounter(championsEnemy);
      final Map<Champion, Double> matchupsAll = determineMatchup(championsEnemy, champions);
      final Map<Champion, Double> synergies = determineSynergy(champions);
      final Map<Champion, Double> compChange = determineCompChange(composition, champions, views.get(i).getLane());
      List<Champion> combined = combine(counters, matchupsAll, synergies, compChange);
      if (combined.size() > 3) {
        combined = combined.subList(0, 3);
      }
      for (ChampionView champion : playerView.getChampions()) {
        champion.setRecommended(combined.contains(champion.getChampion()));
      }
    }
  }

  @SafeVarargs
  private final List<Champion> combine(Map<Champion, Double>... maps) {
    val finalMap = new HashMap<Champion, Double>();
    Arrays.stream(maps).forEach(map -> map.forEach((champion, value) -> finalMap.merge(champion, value, Double::sum)));
    return finalMap.entrySet().stream()
        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());
  }

  private Map<Champion, Double> determineCounter(List<Champion> enemyChampions) {
    return enemyChampions.stream()
        .filter(Objects::nonNull)
        .flatMap(enemyChampion -> enemyChampion.getCountered().stream())
        .collect(Collectors.toMap(champion -> champion, champion -> Const.COMPOSITION_COUNTER_FACTOR * 1d, (a, b) -> b));
  }

  private Map<Champion, Double> determineMatchup(List<Champion> enemyChampions, List<Champion> ownChampions) {
    Map<Champion, Double> champions = new HashMap<>();
    for (Champion enemyChampion : enemyChampions) {
      if (enemyChampion != null) {
        for (Champion champion : ownChampions) {
          if (champion != null) {
            champions.merge(champion, champion.getMatchup(enemyChampion).getWinrate() *
                Const.COMPOSITION_MATCHUP_OVERALL_FACTOR, Double::sum);
          }
        }
      }
    }
    return champions;
  }

  private Map<Champion, Double> determineSynergy(List<Champion> champions) {
    final Map<Champion, Double> picksTeam = new HashMap<>();
    for (Champion champion : champions) {
      champion.getSynergies().stream().filter(champions::contains)
          .forEach(synergy -> picksTeam.merge(champion, Const.COMPOSITION_SYNERGY_FACTOR * 1d, Double::sum));
    }
    return picksTeam;
  }

  private Map<Champion, Double> determineCompChange(Composition composition, List<Champion> champions, Lane lane) {
    int i = lane.ordinal();
    final Map<Champion, Double> list = new HashMap<>();

    Map<CompositionAttribute, Double> map = new HashMap<>(composition.getCompositionValues());
    int count = (int) champions.stream().filter(Objects::nonNull).count();


    map = composition.determineCompValues(map, composition.getTeamView().getSelected().get(i), false);
    final List<Champion> collect = champions.stream()
        .filter(champion -> champion != composition.getTeamView().getSelected().get(i))
        .collect(Collectors.toList());
    for (Champion availableChampion : collect) {
      map = composition.determineCompValues(map, availableChampion, true);
      final List<List<CompositionAttribute>> attributes = composition.getAttributes(map, count);
      final double value = Const.COMPOSITION_COMP_CHANGE_FACTOR * (attributes.get(0).size() * .1 + attributes.get(1).size());
      list.merge(availableChampion, value / collect.size(), Double::sum);
    }
    return list;
  }

}
