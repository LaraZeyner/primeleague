package de.xeri.prm.models.match.ratings;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.ratings.adaption.Adaption;
import de.xeri.prm.models.match.ratings.adaption.Versatility;
import de.xeri.prm.models.match.ratings.fighting.Fighting;
import de.xeri.prm.models.match.ratings.income.Income;
import de.xeri.prm.models.match.ratings.laning.Laning;
import de.xeri.prm.models.match.ratings.objectives.Objectives;
import de.xeri.prm.models.match.ratings.roaming.Roaming;
import de.xeri.prm.models.match.ratings.survival.Survival;
import de.xeri.prm.models.match.playerperformance.Playerperformance;
import de.xeri.prm.util.Const;
import lombok.Getter;
import lombok.NonNull;

/**
 * <b>Ratings</b> berechnen die Wertung fuer einen Spieler und bestehen aus Kategorien, Unterkategorien und Stats.
 * <p>
 * Jedes Rating beinhaltet genau 7 Kategorien.
 * <br>
 * Jede Kategorie beinhaltet genau 5 Unterkategorien.
 * <br>
 * Jede Unterkategorie beinhaltet genau 5 Stats.
 * <p>
 * Jeder Stat nimmt immer Werte von -2 bis +2 an.
 * <br>
 * Jede Unterkategorie nimmt immer Werte von -10 bis +10 an.
 * <br>
 * 6 von 7 Kategorien nehmen Werte von -1500 bis +1500 an. (-100 bis +100)
 * <br>
 * 1 Kategorie nimmt Werte von -1000 bis +1000 an.
 * <br>
 * Das Rating nimmt Werte von -10.000 bis +10.000 an. (-667 bis +667)
 *
 * @since 26.04.2022
 */
@Getter
public class Ratings {
  public static Set<Rating> ratings;

  private final Lane lane;
  private final Objectives objectives;
  private final Roaming roaming;
  private final Fighting fighting;
  private final Income income;
  private final Survival survival;
  private final Laning laning;
  private final Adaption adaption;

  static {
    ratings = Rating.get();
  }

  public static Rating getRating(StatSubcategory subcategory, DisplaystatSubtype subtype) {
    return ratings.stream()
        .filter(rating -> rating.getCategory().equals(subcategory))
        .filter(rating -> rating.getSubType().equals(subtype))
        .findFirst().orElse(null);
  }

  public Ratings(@NonNull DisplaystatSubtype subtype, @NonNull List<Playerperformance> playerperformances) {
    this.lane = subtype.getLane();
    List<Playerperformance> pp = subtype.equals(DisplaystatSubtype.ALLGEMEIN) ? playerperformances :
        playerperformances.stream()
            .filter(playerperformance -> playerperformance.getLane().equals(subtype.getLane()))
            .collect(Collectors.toList());

    this.objectives = new Objectives(pp, subtype.getLane());
    this.roaming = new Roaming(pp, subtype.getLane());
    this.fighting = new Fighting(pp, subtype.getLane());
    this.income = new Income(pp, subtype.getLane());
    this.survival = new Survival(pp, subtype.getLane());
    this.laning = new Laning(pp, subtype.getLane());
    this.adaption = new Adaption(pp, subtype.getLane());
  }

  public double get() {
    return objectives.get() + roaming.get() + fighting.get() + income.get() + survival.get() + laning.get() + adaption.get() / 1.5 +
        getVersatility().get() * (Ratings.getRating(StatSubcategory.VERSATILTITY, lane.getSubtype()).getValue()) / Const.RATING_FACTOR;
  }

  public String format() {
    return String.valueOf(Math.round(get()));
  }

  public Versatility getVersatility() {
    return new Versatility(objectives.sum(), roaming.sum(), fighting.sum(), income.sum(), survival.sum());
  }

}
