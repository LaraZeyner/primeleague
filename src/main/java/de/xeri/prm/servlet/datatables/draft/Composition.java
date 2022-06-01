package de.xeri.prm.servlet.datatables.draft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.xeri.prm.models.dynamic.Champion;
import de.xeri.prm.util.Const;
import lombok.Data;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @since 24.05.2022
 */
@Data
public class Composition {
  private final TeamView teamView;

  /**
   * Alle gespielten Champions
   */
  private final List<List<Champion>> presentChampions;
  private final List<List<Champion>> availableChampions;
  private Map<CompositionAttribute, Double> compositionValues;
  /**
   * Dargestellte Werte
   */
  private final List<Champion> picks;
  private final List<Boolean> picked;
  private final List<Champion> banns;
  /**
   * Attribute
   */
  private List<CompositionAttribute> goodAttributes;
  private List<CompositionAttribute> badAttributes;
  private List<Champion> recommendedPicks;

  private List<CompositionAttribute> badAttributesDisplay;
  private List<Champion> recommendedPicksDisplay;

  public Composition(TeamView view) {
    final List<PlayerView> players = view.getPlayers();
    final List<List<Champion>> presentChampions = players.stream()
        .map(player -> player.getChampions().stream()
            .map(ChampionView::getChampion)
            .collect(Collectors.toList()))
        .collect(Collectors.toList());
    this.presentChampions = presentChampions;
    this.availableChampions = presentChampions;

    Map<CompositionAttribute, Double> values = new HashMap<>();
    final List<Champion> picks = IntStream.range(0, 5).mapToObj(i -> players.get(i).getChampions().get(0).getChampion()).collect(Collectors.toList());
    for (int i = 0; i < picks.size(); i++) {
      final Champion pick = picks.get(i);
      values = updateCompValues(pick, true, values, (int) picks.stream().filter(Objects::nonNull).count());
      picks.set(i, pick);
    }
    this.compositionValues = values;

    this.picks = picks;
    this.picked = Arrays.asList(false, false, false, false, false);
    this.banns = Arrays.asList(null, null, null, null, null);

    this.teamView = view;
  }

  void addPick(int id, Champion champion, Composition enemyComposition) {
    this.compositionValues = updateCompValues(champion, true);
    picks.set(id, champion);
    updateChampions(enemyComposition);
    updateRecommendedPicks(enemyComposition);
  }

  void removePick(int id, Composition enemyComposition) {
    this.compositionValues = updateCompValues(picks.get(id), false);
    picks.set(id, null);
    picked.set(id, false);
    updateRecommendedPicks(enemyComposition);
  }

  void addBan(int id, Champion champion, Composition enemyComposition) {
    banns.set(id, champion);
    removeSelectedChampionFrom(this, champion);
    removeSelectedChampionFrom(enemyComposition, champion);
    updateRecommendedPicks(enemyComposition);
  }

  void removeBan(int id, Composition enemyComposition) {
    banns.set(id, null);
    updateRecommendedPicks(enemyComposition);
  }

  void confirm(int id, Composition enemyComposition) {
    picked.set(id, true);
    removeSelectedChampionFrom(enemyComposition, picks.get(id));
    availableChampions.set(id, Collections.singletonList(picks.get(id)));
    updateRecommendedPicks(enemyComposition);
  }

  void unconfirm(int id, Composition enemyComposition) {
    picked.set(id, false);
    availableChampions.set(id, presentChampions.get(id));
    updateRecommendedPicks(enemyComposition);
  }

  private void removeSelectedChampionFrom(Composition composition, Champion champion) {
    availableChampions.forEach(availableChampion -> availableChampion.remove(champion));

    if (composition.getPicks().contains(champion)) {
      final int index = composition.getPicks().indexOf(champion);
      composition.getPicked().set(index, false);
      composition.getPicks().set(index, null);
    }
  }

  private Map<CompositionAttribute, Double> updateCompValues(Champion champion, boolean add) {
    return updateCompValues(champion, add, compositionValues, (int) picks.stream().filter(Objects::nonNull).count());
  }

  private Map<CompositionAttribute, Double> updateCompValues(Champion champion, boolean add, Map<CompositionAttribute, Double> values, int picks) {
    final List<List<CompositionAttribute>> attributes = getAttributes(values, picks);
    this.goodAttributes = attributes.get(0);
    this.badAttributes = attributes.get(1);
    return determineCompValues(values, champion, add);
  }

  private Map<CompositionAttribute, Double> determineCompValues(Map<CompositionAttribute, Double> previous, Champion champion, boolean add) {
    final Map<CompositionAttribute, Double> values = new HashMap<>(previous);
    final Map<CompositionAttribute, Double> stats = champion.getStats();
    stats.forEach(((attribute, value) -> values.merge(attribute, add ? value : value * -1, Double::sum)));
    return values;
  }

  private List<List<CompositionAttribute>> getAttributes(Map<CompositionAttribute, Double> attributes, int count) {
    List<CompositionAttribute> good = new ArrayList<>();
    List<CompositionAttribute> bad = new ArrayList<>();
    for (CompositionAttribute compositionAttribute : attributes.keySet()) {
      final double value = attributes.get(compositionAttribute);

      if (compositionAttribute.equals(CompositionAttribute.PLAYSTYLE_SPLITPUSH)) {
        if ((int) value == 2) {
          good.add(compositionAttribute);

        } else if ((int) value != 1) {
          bad.add(compositionAttribute);
        }

      } else {
        if (compositionAttribute.isAbove(value, count)) {
          good.add(compositionAttribute);

        } else if (compositionAttribute.isBelow(value, count)) {
          bad.add(compositionAttribute);
        }
      }
    }
    return Arrays.asList(good, bad);
  }

  /**
   * Champions die einen Gegnerischen Champion countern (wie auch immer)
   * Champions mit sehr gutem Matchup
   * Champions mit Synergie mit anderem Champion
   * Champions, die Composition abrunden würden
   * TODO pick away
   */
  void updateChampions(Composition composition) {
    final Map<Champion, Double> counters = determineCounter(composition.getPicks());
    final Map<Champion, Double> matchupsAll = determineMatchup(composition.getPicks());
    final Map<Champion, Double> synergies = determineSynergy();
    final Map<Champion, Double> compChange = determineCompChange();

    this.recommendedPicks = combine(counters, matchupsAll, synergies, compChange);
    updateRecommendedPicks(composition);
  }

  private void updateRecommendedPicks(Composition composition) {
    this.badAttributesDisplay = badAttributes.size() > 9 ? badAttributes.subList(0, 9) : badAttributes;

    final List<Champion> recommended = recommendedPicks.stream()
        .filter(pick -> !picks.contains(pick))
        .filter(pick -> !banns.contains(pick))
        .filter(pick -> !composition.getPicks().contains(pick))
        .filter(pick -> !composition.getBanns().contains(pick))
        .collect(Collectors.toList());
    int size = 15 - badAttributesDisplay.size();
    this.recommendedPicksDisplay = recommended.size() > size ? recommended.subList(0, size) : recommended;
  }

  @NotNull
  private List<Map<Champion, Double>> getChampionsList() {
    List<Map<Champion, Double>> ourChampions = new ArrayList<>();
    for (int i = 0; i < picks.size(); i++) {
      final Map<Champion, Double> champs = new HashMap<>();
      if (picks.get(i) == null) {
        champs.putAll(teamView.getPlayers().stream()
            .flatMap(player -> player.getChampions().stream())
            .collect(Collectors.toMap(ChampionView::getChampion, champion -> champion.getPresenceNum() / 100., (a, b) -> b)));
      } else {
        champs.put(picks.get(i), picked.get(i) ? 10d : 5d);
      }
      ourChampions.add(champs);
    }
    return ourChampions;
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

  private Map<Champion, Double> determineMatchup(List<Champion> enemyChampions) {
    Map<Champion, Double> champions = new HashMap<>();
    for (int i = 0; i < enemyChampions.size(); i++) {
      if (!picked.get(i) && enemyChampions.get(i) != null) {
        for (Champion champion : availableChampions.get(i)) {
          if (champion != null) {
            champions.merge(champion, champion.getMatchup(enemyChampions.get(i)).getWinrate() *
                Const.COMPOSITION_MATCHUP_OVERALL_FACTOR, Double::sum);
          }
        }
      }
    }
    return champions;
  }

  private Map<Champion, Double> determineSynergy() {
    final Map<Champion, Double> picksTeam = new HashMap<>();
    final List<Champion> available = availableChampions.stream().flatMap(Collection::stream).collect(Collectors.toList());
    for (Champion champion : available) {
      champion.getSynergies().stream().filter(available::contains)
          .forEach(synergy -> picksTeam.merge(champion, Const.COMPOSITION_SYNERGY_FACTOR * 1d, Double::sum));
    }
    return picksTeam;
  }

  private Map<Champion, Double> determineCompChange() {
    final Map<Champion, Double> list = new HashMap<>();
    final List<Map<Champion, Double>> picksTeam = getChampionsList();

    for (int j = 0; j < picksTeam.size(); j++) {
      final int i = j;
      if (!picked.get(i)) {
        Map<CompositionAttribute, Double> map = new HashMap<>(compositionValues);
        int count = (int) picks.stream().filter(Objects::nonNull).count();
        if (picks.get(i) != null) {
          map = determineCompValues(map, picks.get(i), false);
        } else {
          count++;
        }
        final List<Champion> collect = availableChampions.get(i).stream()
            .filter(champion -> picks.get(i) == null || champion != picks.get(i))
            .collect(Collectors.toList());
        for (Champion availableChampion : collect) {
          if (picks.get(i) == null || availableChampion != picks.get(i)) {
            map = determineCompValues(map, availableChampion, true);
            final List<List<CompositionAttribute>> attributes = getAttributes(map, count);
            final double value = Const.COMPOSITION_COMP_CHANGE_FACTOR * (attributes.get(0).size() * .1 + attributes.get(1).size());
            list.merge(availableChampion, value / collect.size(), Double::sum);
          }
        }
      }
    }
    return list;
  }
}
