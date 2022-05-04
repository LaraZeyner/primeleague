package de.xeri.league.game.events.items;

/**
 * Created by Lara on 03.05.2022 for web
 */
public class ResetCalculator {

  public static int getDeathTimer(int level, int second) {
    final double baseTimer;

    if (level <= 6) {
      baseTimer = 4 + level * 2;
    } else if (level == 7) {
      baseTimer = 21;
    } else  {
      baseTimer = 7.5 + level * 2.5;
    }

    double factor = 1;
    int _30seconds = second / 30;
    if (_30seconds > 30) {
      int amount = _30seconds % 30;
      factor += amount * 0.00425;
    }

    if (_30seconds > 60) {
      int amount = _30seconds % 30;
      factor += amount * 0.003;
    }

    if (_30seconds > 90) {
      int amount = _30seconds - 90;
      factor += amount * 0.0145;
    }

    if (factor > 1.5) {
      factor = 1.5;
    }

    return (int) (baseTimer * factor);
  }
}
