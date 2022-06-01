package de.xeri.prm.servlet.datatables.draft;

import java.util.Arrays;

import de.xeri.prm.util.HibernateUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * @since 24.05.2022
 */
@RequiredArgsConstructor
@ToString
public enum CompositionAttribute {
  //TODO (Abgie) 24.05.2022: Subject of Change
  PLAYSTYLE_SIEGE("Sieging", false, 2, Double.MAX_VALUE, true),
  PLAYSTYLE_SPLITPUSH("Splitpush", false, 1, 2, true),
  PLAYSTYLE_TEAMFIGHT("Teamfight", false, .7, Double.MAX_VALUE, false),

  STATISTIC_CROWDCONTROL("CC"),
  STATISTIC_DAMAGE_MAGICAL("AP"),
  STATISTIC_DAMAGE_PHYSICAL("AD"),
  STATISTIC_DAMAGE_TOTAL("Damage", .85, 1),
  STATISTIC_DAMAGE_TRUE("True", Double.MIN_VALUE, Double.MAX_VALUE), // truuuuuuuuuuuuuuuue,

  TYPE_AGGRESSION_ENGAGE("Engage", false, 5, 15, false),
  TYPE_AGGRESSION_DISENGAGE("Disengage", false, 20, 60, true),
  TYPE_DAMAGE_BURST("Burst"),
  TYPE_DAMAGE_DPS("DPS"),
  TYPE_DIVING("Dive"),
  TYPE_DURABILITY_FRONTLINE("Frontline", .9, 1.35),
  TYPE_DURABILITY_PEEL("Peel"),
  TYPE_DURABILITY_RANGE("Range"),
  TYPE_GAMEPHASE_EARLYGAME("Early", false, .48, 0.51, false),
  TYPE_GAMEPHASE_LATEGAME("Late", false, .49, 0.52, false),
  TYPE_GAMEPHASE_MIDGAME("Mid", false, .49, 0.51, false),
  TYPE_GANKSETUPS("Ganks"),
  TYPE_WAVECLEAR("Waveclear", .78, 1.62),

  WINCONDITION_ALLIN("Allin", false, 5, 15, false),
  WINCONDITION_SUSTAIN("Sustain", false, 5, 15, false),
  WINCONDITION_TRADE("Trade", false, 5, 15, false);

  public static CompositionAttribute fromName(String name) {
    return Arrays.stream(values()).filter(attribute -> attribute.getName().equals(name)).findFirst().orElse(null);
  }

  @Getter
  private final String name;
  /**
   * Wird benoetigt, wenn in Relation zum Mittelwert berechnet wird
   */
  private final boolean relative;
  private final double lower;
  private final double higher;
  /**
   * Wird benoetigt, wenn absoluter Wert noetig
   */
  private final boolean absolute;

  CompositionAttribute(String name) {
    this.relative = true;
    this.lower = .65;
    this.higher = 1.35;
    this.absolute = false;
    this.name = name;
  }

  CompositionAttribute(String name, double lower, double higher) {
    this.relative = true;
    this.lower = lower;
    this.higher = higher;
    this.absolute = false;
    this.name = name;
  }

  public boolean isBelow(double value, int count) {
    Double average = HibernateUtil.getAverageChampionStats().get(this);
    if (!absolute) {
      value /= count;
      average /= count;
    }

    return relative ? value < average * lower : value < lower;
  }

  public boolean isAbove(double value, int count) {
    Double average = HibernateUtil.getAverageChampionStats().get(this);
    if (!absolute) {
      value /= count;
      average /= count;
    }

    return relative ? value > average * higher : value > higher;
  }

  public double getRelativeValue(double valueAbsolute, int count) {
    Double averageAbsolute = HibernateUtil.getAverageChampionStats().get(this);
    if (!absolute) {
      valueAbsolute /= count;
      averageAbsolute /= count;
    }

    final double valueRelative = valueAbsolute / averageAbsolute;
    final double averageRelative = (higher + lower) / 2;
    final double value = relative ? valueRelative : valueAbsolute;
    return valueRelative > higher ? (value - higher) * 400 + 100 : valueRelative < lower ? (value - lower) * 400 - 100 :
        (value - averageRelative) * 100 / (valueAbsolute > averageAbsolute ? (higher - averageRelative) : (averageRelative - lower));
  }
}
