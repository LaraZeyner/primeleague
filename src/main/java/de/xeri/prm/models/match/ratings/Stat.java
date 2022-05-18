package de.xeri.prm.models.match.ratings;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.match.playerperformance.Playerperformance;
import de.xeri.prm.models.match.playerperformance.Value;
import de.xeri.prm.util.Util;
import lombok.val;
import lombok.var;

/**
 * Created by Lara on 26.04.2022 for web
 */
public class Stat {
  protected List<Playerperformance> playerperformances;
  private final OutputType outputType;
  private final int digits;
  private ToDoubleFunction<? super Playerperformance> mapper;
  private boolean reverse = false;
  private boolean ignore = false;
  private boolean nullable = false;
  private final Map<String, Double> subvalues = new HashMap<>();
  private String text = "";
  private final String reference;
  private final Lane lane;

  public Stat(List<Playerperformance> playerperformances, OutputType outputType, int digits, Lane lane) {
    this(playerperformances, outputType, digits, lane,
        Util.uncapitalizeFirst(Thread.currentThread().getStackTrace()[2].getMethodName().replace("get", "")));
  }

  public Stat(List<Playerperformance> playerperformances, OutputType outputType, int digits, Lane lane, String reference) {
    val playerperformancesList = playerperformances.stream()
        .filter(playerperformance -> playerperformance.getTeamperformance().getTeam() != null).collect(Collectors.toList());
    this.playerperformances = playerperformancesList.size() > 5 ? playerperformancesList : playerperformances;
    this.outputType = outputType;
    this.digits = digits;
    this.reference = reference;
    this.lane = lane;
  }

  public Stat map(ToDoubleFunction<? super Playerperformance> mapper) {
    this.mapper = mapper;
    return this;
  }

  public Stat nullable() {
    this.nullable = true;
    return this;
  }

  public Stat reverse() {
    this.reverse = !reverse;
    return this;
  }

  public Stat ignore() {
    this.ignore = !ignore;
    return this;
  }

  public Stat subValue(String name, double value) {
    subvalues.put(name, value);
    return this;
  }

  public Stat text(String text) {
    this.text = text;
    return this;
  }

  public Stat sub(String name, ToDoubleFunction<? super Playerperformance> mapper) {
    var doubleStream = playerperformances.stream().mapToDouble(mapper);

    if (!nullable) {
      doubleStream = doubleStream.filter(d -> d != 0);
    }

    subvalues.put(name, doubleStream.average().orElse(0));
    return this;
  }

  public double calculate() {
    var doubleStream = playerperformances.stream().mapToDouble(mapper);

    if (!nullable) {
      doubleStream = doubleStream.filter(d -> d != 0);
    }

    return doubleStream.average().orElse(0);
  }

  public String display() {
    if (!nullable && calculate() == 0) {
      return "-";
    }
    return null;
  }

  public double average() {
    if (reference == null) {
      return .5;
    }
    final Value value = Playerperformance.getValues().get(lane).get(reference);
    return value.getAverage();
  }

  public double maximum() {
    if (reference == null) {
      return 1;
    }
    final Value value = Playerperformance.getValues().get(lane).get(reference);
    return value.getHighest();
  }

  public double minimum() {
    if (reference == null) {
      return 0;
    }
    final Value value = Playerperformance.getValues().get(lane).get(reference);
    return value.getLowest();
  }

  public double value() {
    if (!ignore) {
      final double value;

      if (calculate() < minimum()) {
        value = -2;

      } else if (calculate() > maximum()) {
        value = 2;

      } else if (calculate() > average()) {
        double relative = Math.abs(Util.div(calculate() - average(), maximum() - average()));
        value = relative > 0.75 ? (1 - relative) * 4 + 1 : relative / 3 * 4;

      } else {
        double relative = Math.abs(Util.div(calculate() - average(), average() - minimum()));
        value = (relative > 0.75 ? ((1 - relative) * 4 + 1) : (relative / 3 * 4)) * -1;
      }

      return reverse ? value * -1 : value;
    }
    return 0;
  }

  public String format() {
    if (display() != null) {
      return display();
    }

    final String value = String.valueOf(calculate());

    if (outputType.equals(OutputType.NUMBER) || outputType.equals(OutputType.PERCENT)) {
      final String preComma = getString(outputType, digits, value);
      if (preComma != null) return preComma;

    } else if (outputType.equals(OutputType.TEXT)) {
      if (value.length() >= digits) {
        return value.substring(0, digits);
      }
      return value;

    } else if (outputType.equals(OutputType.LIST_NUMBER)) {
      return Arrays.stream(value.split(","))
          .map(d -> getString(outputType, digits, d))
          .collect(Collectors.toList()).toString();

    } else if (outputType.equals(OutputType.TIME) || outputType.equals(OutputType.TIME_FROM_MILLIS)) {
      int intValue = (int) calculate();
      if (outputType.equals(OutputType.TIME_FROM_MILLIS)) {
        intValue = intValue / 1000;
      }

      final int seconds = intValue % 60;
      String secondsString = ("00" + seconds).substring(("00" + seconds).length() - 2);
      final int minutes = (intValue / 60) % 60;
      String minutesString = ("00" + minutes).substring(("00" + minutes).length() - 2);
      final int hours = (intValue / 3_600) % 24;
      String hoursString = ("00" + hours).substring(("00" + hours).length() - 2);
      final int days = (intValue / 864_000);
      String daysString = ("00" + days).substring(("00" + days).length() - 2);

      if (days > 0) {
        if (days < 10) daysString = String.valueOf(days);
        if (digits == 1) {
          return daysString + "d";
        } else if (digits == 2) {
          return daysString + "d " + hoursString + "h";
        } else if (digits == 3) {
          return daysString + "d " + hoursString + ":" + minutesString;
        } else {
          return daysString + "d " + hoursString + ":" + minutesString + ":" + secondsString;
        }

      } else if (hours > 0) {
        if (hours < 10) hoursString = String.valueOf(hours);
        if (digits == 1) {
          return hoursString + "h";
        } else if (digits == 2) {
          return hoursString + ":" + minutesString;
        } else {
          return hoursString + ":" + minutesString + ":" + secondsString;
        }

      } else if (minutes > 0) {
        if (minutes < 10) minutesString = String.valueOf(minutes);
        if (digits == 1) {
          return minutesString + "min";
        } else {
          return minutesString + ":" + secondsString;
        }

      } else {
        if (seconds < 10) secondsString = String.valueOf(minutes);
        return secondsString + "s";
      }
    }

    return value;
  }


  private String getString(OutputType outputType, int digits, String value) {
    if (value.contains(".")) {
      String digitsBefore = value.split("\\.")[0];
      if (digitsBefore.equals("0")) digitsBefore = "";
      final String digitsAfter = value.split("\\.")[1];
      final int lengthBefore = digitsBefore.length();

      final StringBuilder out = new StringBuilder();
      if (outputType.equals(OutputType.PERCENT)) {
        out.append(digitsBefore)
            .append(digitsAfter.length() == 1 ? digitsAfter.charAt(0) + "0" :
                String.valueOf(Integer.parseInt(digitsAfter.substring(0, 2))));
        if (digitsAfter.length() > 2 && out.toString().length() < digits) {
          if (digitsAfter.length() >= digits - digitsBefore.length()) {
            out.append(".")
                .append(digitsAfter, 2, digits - digitsBefore.length());
          } else {
            out.append(".")
                .append(digitsAfter.substring(2));
          }
        }
        out.append("%");

      } else if (lengthBefore >= digits) {
        out.append(digitsBefore);

      } else {
        out.append(digitsBefore);

        int beforeSize = digitsBefore.length() - (digitsBefore.contains("-") ? 1 : 0);
        out.append(".")
            .append(digitsAfter.length() >= digits - beforeSize ? digitsAfter.substring(0, digits - beforeSize) : digitsAfter);
      }
      return out.toString();
    }
    return null;
  }

  public boolean isRelevant() {
    return !ignore;
  }

  public String getText() {
    return text;
  }
}
