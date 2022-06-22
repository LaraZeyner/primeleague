package de.xeri.prm.servlet.loader.scouting.composition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.xeri.prm.models.dynamic.Champion;
import de.xeri.prm.servlet.loader.scouting.performance.LaneView;
import de.xeri.prm.servlet.loader.scouting.performance.PlayerView;
import de.xeri.prm.servlet.loader.scouting.performance.TeamView;
import lombok.Data;

/**
 * @since 24.05.2022
 */
@Data
public class Composition {
  private final TeamView teamView;

  /**
   * Alle gespielten Champions
   */
  private Map<CompositionAttribute, Double> compositionValues;
  /**
   * Attribute
   */
  private List<CompositionAttribute> goodAttributes;
  private List<CompositionAttribute> badAttributes;

  public Composition(TeamView view) {
    final List<PlayerView> players = view.getViews().stream().map(LaneView::getView).collect(Collectors.toList());

    Map<CompositionAttribute, Double> values = new HashMap<>();
    final List<Champion> picks = IntStream.range(0, 5).mapToObj(i -> players.get(i).getSelectedChampion().getChampion()).collect(Collectors.toList());
    for (int i = 0; i < picks.size(); i++) {
      final Champion pick = picks.get(i);
      values = updateCompValues(pick, values, (int) picks.stream().filter(Objects::nonNull).count());
      picks.set(i, pick);
    }
    this.compositionValues = values;

    this.teamView = view;
  }

  private Map<CompositionAttribute, Double> updateCompValues(Champion champion, Map<CompositionAttribute, Double> values, int picks) {
    final List<List<CompositionAttribute>> attributes = getAttributes(values, picks);
    this.goodAttributes = attributes.get(0);
    this.badAttributes = attributes.get(1);
    return determineCompValues(values, champion, true);
  }

  Map<CompositionAttribute, Double> determineCompValues(Map<CompositionAttribute, Double> previous, Champion champion, boolean add) {
    final Map<CompositionAttribute, Double> values = new HashMap<>(previous);
    final Map<CompositionAttribute, Double> stats = champion.getStats();
    stats.forEach(((attribute, value) -> values.merge(attribute, add ? value : value * -1, Double::sum)));
    return values;
  }

  List<List<CompositionAttribute>> getAttributes(Map<CompositionAttribute, Double> attributes, int count) {
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

  public List<CompositionAttribute> getBadAttributesDisplay() {
    return badAttributes.size() > 9 ? badAttributes.subList(0, 9) : badAttributes;
  }

}
