package de.xeri.prm.util;

/**
 * Created by Lara on 04.04.2022 for web
 */
public final class Const {
  public static final int LOG_LEVEL = 500;
  public static final int TEAMID = 142116;

  public static final int AHEAD_LEAD = 1500;
  public static final int AHEAD_LEAD_EXTEND = 1000;
  public static final int ASSIST_BOUNTY = 150;
  public static final int ASSIST_BOUNTY_FIRST_BLOOD = 200;
  public static final int ASSIST_FACTOR_INCREASE_SECOND = 90;
  public static final int ASSIST_FACTOR_ENDING_SECOND = 210;
  public static final double ASSIST_FACTOR_START_VALUE = 0.5;
  public static final double ASSIST_FACTOR_END_VALUE = 1.0;
  public static final int COMPOSITION_COUNTER_FACTOR = 1;
  public static final int COMPOSITION_MATCHUP_OVERALL_FACTOR = 1;
  public static final int COMPOSITION_SYNERGY_FACTOR = 1;
  public static final int COMPOSITION_COMP_CHANGE_FACTOR = 1;
  public static final int DAYS_UNTIL_INACTIVE = 180;
  public static final int DAYS_UNTIL_MATCH_CLOSED = 7;
  public static final int DISTANCE_BETWEEN_FIGHTS = 5000;
  public static final int EARLYGAME_CS = 158;
  public static final int EARLYGAME_UNTIL_MINUTE = 14;
  public static final int EARLYGAME_XP = 8896;
  public static final double GOLD_GENERATION_PER_SECOND = 1.9;
  public static final double GOLD_REFUND = 0.7;
  public static final double GOLD_REFUND_PENALTY = 0.3;
  public static final int GOLD_REFUND_PER_BISCUIT = 5;
  public static final int KILL_BOUNTY = 300;
  public static final int MAP_SIZE = 15_000;
  public static final int PATH_SIMILARITY = 750;
  public static final int KILL_BOUNTY_FIRST_BLOOD = 400;
  public static final int LATEGAME_UNTIL_MINUTE = 40;
  public static final int MIDGAME_GOLD = 5935;
  public static final int MIDGAME_UNTIL_MINUTE = 27;
  public static final int MIDGAME_XP = 8896;
  public static final int MULTIKILL_TIME_BETWEEN_KILLS = 10;
  public static final int MULTIKILL_TIME_BETWEEN_KILLS_PENTAKILL = 30;
  public static final double PRESENCE_PERCENT_LIMIT = 0.25;
  public static final int PRESENCE_RECENTLY_LIMIT = 5;
  public static final double PRESENCE_DISPLAY_LIMIT = 5;
  public static final double RATING_CAT_FACTOR = 6.66667;
  public static final int RATING_FACTOR = 15;
  public static final int RESET_PLANNED_LIMIT = 250;
  public static final int SKIRMISH_PLAYERS_REQUIRED = 2;
  public static final int TEAMFIGHT_PLAYERS_REQUIRED = 3;
  public static final int SECONDS_BETWEEN_FIGHTS = 30;
  public static final int YELLOW_TRINKET_RECHARGE_TIME_START = 240;
  public static final int YELLOW_TRINKET_RECHARGE_TIME_END = 120;

  public static final long MILLIS_PER_DAY = 86_400_000L;

  public static final String API_KEY = "?api_key=RGAPI-780f0c26-3e9e-41e3-a9b4-837fb7bacb47";
  public static final String API_KEY2 = "?api_key=RGAPI-34cbc390-437c-469b-b09f-2d0fcb1ded27";
  public static final String BISCUIT_ITEM_NAME = "Total Biscuit of Everlasting Will";
  public static final String DEFAULT_TRINKET_WARD_NAME = "Stealth Ward";
  public static final String QUEUE_FLEX = "RANKED_FLEX_SR";
  public static final String QUEUE_SOLO = "RANKED_SOLO_5x5";
  public static final String REVIVE_ITEM_NAME = "Guardian Angel";
  public static final String STASIS_ITEM_NAME = "Stopwatch";
  public static final String TIMEOUT_MESSAGE = "SLEEPY TIME";
  public static final String TRUESIGHT_WARD_NAME = "Control Ward";


  public static boolean check() {
    return ASSIST_FACTOR_INCREASE_SECOND <= ASSIST_FACTOR_ENDING_SECOND &&
        ASSIST_FACTOR_START_VALUE <= ASSIST_FACTOR_END_VALUE &&
        KILL_BOUNTY != KILL_BOUNTY_FIRST_BLOOD &&
        YELLOW_TRINKET_RECHARGE_TIME_START >= YELLOW_TRINKET_RECHARGE_TIME_END &&
        EARLYGAME_UNTIL_MINUTE < MIDGAME_UNTIL_MINUTE && MIDGAME_UNTIL_MINUTE < LATEGAME_UNTIL_MINUTE &&
        GOLD_REFUND >= GOLD_REFUND_PENALTY;
  }
}
