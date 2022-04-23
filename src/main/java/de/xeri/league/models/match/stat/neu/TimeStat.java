package de.xeri.league.models.match.stat.neu;

import java.util.List;

/**
 * Created by Lara on 22.04.2022 for web
 */
public class TimeStat extends Stat {
  private int seconds;

  public TimeStat(String displayName, Class sourceClass, String attribute, int digits, boolean percent, int seconds) {
    super(displayName, sourceClass, attribute, digits, percent);
    this.seconds = seconds;
  }

  @Override
  String avg() {
    return "0";
  }

  @Override
  String count() {
    return "0";
  }

  @Override
  String max() {
    return "0";
  }

  @Override
  String min() {
    return "0";
  }

  @Override
  String sum() {
    return "0";
  }

  @Override
  List<Double> list() {
    return null;
  }

  @Override
  double perMinute() {
    return 0;
  }

  public int getSeconds() {
    return seconds;
  }
}
