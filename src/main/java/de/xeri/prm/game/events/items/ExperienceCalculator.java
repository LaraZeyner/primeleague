package de.xeri.prm.game.events.items;

/**
 * Created by Lara on 03.05.2022 for web
 */
public class ExperienceCalculator {

  public static int getLevelOf(int experience) {
    if (experience < 280) {
      return 1;
    } else if (experience < 660) {
      return 2;
    } else if (experience < 1140) {
      return 3;
    } else if (experience < 1720) {
      return 4;
    } else if (experience < 2400) {
      return 5;
    } else if (experience < 3180) {
      return 6;
    } else if (experience < 4060) {
      return 7;
    } else if (experience < 5040) {
      return 8;
    } else if (experience < 6120) {
      return 9;
    } else if (experience < 7300) {
      return 10;
    } else if (experience < 8580) {
      return 11;
    } else if (experience < 9960) {
      return 12;
    } else if (experience < 11440) {
      return 13;
    } else if (experience < 13020) {
      return 14;
    } else if (experience < 14700) {
      return 15;
    } else if (experience < 16480) {
      return 16;
    } else if (experience < 18360) {
      return 17;
    } else {
      return 18;
    }
  }
}
