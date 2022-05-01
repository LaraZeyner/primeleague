package de.xeri.league.util;

/**
 * Created by Lara on 04.04.2022 for web
 */
public final class Const {
  public static final int LOG_LEVEL = 500;
  public static final int TEAMID = 142116;

  public static final int AHEAD_XPGOLD = 1500;
  public static final int ASSIST_BOUNTY = 150;
  public static final int ASSIST_BOUNTY_FIRST_BLOOD = 200;
  public static final int ASSIST_FACTOR_INCREASE_SECOND = 90;
  public static final int ASSIST_FACTOR_ENDING_SECOND = 210;
  public static final double ASSIST_FACTOR_START_VALUE = 0.5;
  public static final double ASSIST_FACTOR_END_VALUE = 1.0;
  public static final int DAYS_UNTIL_INACTIVE = 180;
  public static final int DAYS_UNTIL_MATCH_CLOSED = 7;
  public static final int DISTANCE_BETWEEN_FIGHTS = 5000;
  public static final int KILL_BOUNTY = 300;
  public static final int KILL_BOUNTY_FIRST_BLOOD = 400;
  public static final int MIDGAME_GOLD = 5935;
  public static final int MIDGAME_XP = 8896;
  public static final int TIME_BETWEEN_FIGHTS = 30;
  public static final int YELLOW_TRINKET_RECHARGE_TIME_START = 240;
  public static final int YELLOW_TRINKET_RECHARGE_TIME_END = 120;

  public static final long MILLIS_PER_DAY = 86_400_000L;

  public static final String API_KEY = "?api_key=RGAPI-780f0c26-3e9e-41e3-a9b4-837fb7bacb47";
  public static final String API_KEY2 = "?api_key=RGAPI-34cbc390-437c-469b-b09f-2d0fcb1ded27";
  public static final String QUEUE_FLEX = "RANKED_FLEX_SR";
  public static final String QUEUE_SOLO = "RANKED_SOLO_5x5";
  public static final String TIMEOUT_MESSAGE = "SLEEPY TIME";


  public static boolean check() {
    return ASSIST_FACTOR_INCREASE_SECOND <= ASSIST_FACTOR_ENDING_SECOND &&
        ASSIST_FACTOR_START_VALUE <= ASSIST_FACTOR_END_VALUE &&
        KILL_BOUNTY != KILL_BOUNTY_FIRST_BLOOD &&
        YELLOW_TRINKET_RECHARGE_TIME_START >= YELLOW_TRINKET_RECHARGE_TIME_END;
  }
}
