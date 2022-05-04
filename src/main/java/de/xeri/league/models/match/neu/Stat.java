package de.xeri.league.models.match.neu;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

import de.xeri.league.models.match.playerperformance.Playerperformance;
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

  public Stat(List<Playerperformance> playerperformances, OutputType outputType, int digits) {
    val playerperformanceStream = playerperformances.stream()
        .filter(playerperformance -> playerperformance.getTeamperformance().getGame().isCompetitive());

    if (playerperformanceStream.count() > 5) {
      this.playerperformances = playerperformanceStream.collect(Collectors.toList());
    } else {
      this.playerperformances = playerperformances;
    }

    this.outputType = outputType;
    this.digits = digits;
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

  public Stat sub(String name, ToDoubleFunction<? super Playerperformance> mapper) {
    var doubleStream = playerperformances.stream().mapToDouble(mapper);

    if (!nullable) {
      doubleStream = doubleStream.filter(d -> d != 0);
    }

    subvalues.put(name, doubleStream.average().orElse(0));
    return this;
  }

  public double calculate() {
    if (!ignore) {
      var doubleStream = playerperformances.stream().mapToDouble(mapper);

      if (!nullable) {
        doubleStream = doubleStream.filter(d -> d != 0);
      }

      return doubleStream.average().orElse(0);
    }
    return 0;
  }

  public String display() {
    if (!nullable && calculate() == 0) {
      return "-";
    }
    return null;
  }

  public double average() {
    var doubleStream = Playerperformance.get().stream().mapToDouble(mapper);

    if (!nullable) {
      doubleStream = doubleStream.filter(d -> d != 0);
    }

    return doubleStream.average().orElse(0);
  }

  public double maximum() {
    var doubleStream = Playerperformance.get().stream().mapToDouble(mapper);

    if (!nullable) {
      doubleStream = doubleStream.filter(d -> d != 0);
    }

    if (reverse) {
      return doubleStream.min().orElse(0);
    }

    return doubleStream.max().orElse(0);
  }

  public double minimum() {
    var doubleStream = Playerperformance.get().stream().mapToDouble(mapper);

    if (!nullable) {
      doubleStream = doubleStream.filter(d -> d != 0);
    }

    if (reverse) {
      return doubleStream.max().orElse(0);
    }

    return doubleStream.min().orElse(0);
  }

  public double value() {
    if (!ignore) {
      final double v = calculate();
      final double avg = average();

      if (v > avg) {
        final double diff = maximum() - avg;
        final double v1 = (v - avg) / diff;
        return v1 > 0.75 ? 4 * (v1 - 0.75) + 1 : 1.33 * v1;
      } else {
        final double diff = avg - minimum();
        final double v1 = (v - avg) / diff;
        return v1 < -0.75 ? 4 * (v1 - 0.75) - 1 : 1.33 * v1;
      }
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
      int intValue = Integer.parseInt(value);
      if (outputType.equals(OutputType.TIME_FROM_MILLIS)) {
        intValue = intValue / 1000;
      }

      final int seconds = intValue % 60;
      String secondsString = ("00" + seconds).substring(("00" + seconds).length()-2);
      final int minutes = (intValue / 60) % 60;
      String minutesString = ("00" + minutes).substring(("00" + minutes).length()-2);
      final int hours = (intValue / 3_600) % 24;
      String hoursString = ("00" + hours).substring(("00" + hours).length()-2);
      final int days = (intValue / 864_000);
      String daysString = ("00" + days).substring(("00" + days).length()-2);

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
    if (value.contains("\\.")) {
      String digitsBefore = value.split("\\.")[0];
      if (digitsBefore.equals("0")) digitsBefore = "";
      final String digitsAfter = value.split("\\.")[1];
      final int lengthBefore = digitsBefore.length();
      final String preComma;
      final String postComma;
      if (outputType.equals(OutputType.PERCENT)) {
        preComma = digitsBefore + (digitsAfter.length() == 1 ? digitsAfter.charAt(0) + "0" : digitsAfter.substring(0, 2));
        postComma = (preComma.length() < digits) ? "." +
            (digitsAfter.length() >= digits - preComma.length() ? digitsAfter.substring(0, digits - preComma.length()) : digitsAfter) +
            "%" : "%";
      } else if (lengthBefore >= digits) {
        preComma = digitsBefore;
        postComma = "";
      } else {
        preComma = digitsBefore;
        postComma = "." + digitsAfter.substring(0, digits - digitsBefore.length());
      }
      return preComma + postComma;
    }
    return null;
  }

  public boolean isRelevant() {
    return !ignore;
  }
}
