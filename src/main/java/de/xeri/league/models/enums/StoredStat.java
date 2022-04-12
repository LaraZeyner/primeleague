package de.xeri.league.models.enums;

/**
 * Created by Lara on 09.04.2022 for web
 */
public enum StoredStat {
  ASSISTS("assists", false),
  BARON_KILLS("baronKills", false),
  BARON_POWERPLAY("baronBuffGoldAdvantageOverThreshold", true),
  CHAMPION("championName", false),
  CONTROL_WARDS_BOUGHT("visionWardsBoughtInGame", false),
  CREEP_SCORE_JUNGLE("neutralMinionsKilled", false),
  CREEP_SCORE_MINIONS("totalMinionsKilled", false),
  DAMAGE_HEALED("totalHeal", false),
  DAMAGE_MAGICAL("magicDamageDealtToChampions", false),
  DAMAGE_MITIGATED("damageSelfMitigated", false),
  DAMAGE_PHYSICAL("physicalDamageDealtToChampions", false),
  DAMAGE_TAKEN("totalDamageTaken", false),
  DAMAGE_TEAM_HEAL("totalHealsOnTeammates", false),
  DAMAGE_TEAM_SHIELD("totalDamageShieldedOnTeammates", false),
  DAMAGE_TOTAL("totalDamageDealtToChampions", false),
  DEATHS("deaths", false),
  E_USAGE("spell3Casts", false),
  ELDER_TIME("earliestElderDragon", true),
  EXPERIENCE_TOTAL("champExperience", false),
  FIRST_BLOOD("firstBloodKill", false),
  FIRST_BLOOD_ASSIST("firstBloodAssist", false),
  GOLD_TOTAL("goldEarned", false),
  ITEM_1("item0", false),
  ITEM_2("item1", false),
  ITEM_3("item2", false),
  ITEM_4("item3", false),
  ITEM_5("item4", false),
  ITEM_6("item5", false),
  ITEM_7("item6", false),
  ITEMS_BOUGHT("itemsPurchased", false),
  KILLS("kills", false),
  KILLS_DOUBLE("doubleKills", false),
  KILLS_TRIPLE("tripleKills", false),
  KILLS_QUADRA("quadraKills", false),
  KILLS_PENTA("pentaKills", false),
  LANE("teamPosition", false),
  OBJECTIVE_DAMAGE("damageDealtToObjectives", false),
  OBJECTIVES_STOLEN("objectivesStolen", false),
  OBJECTIVES_STOLEN_TAKEDOWNS("objectivesStolenAssists", false),
  Q_USAGE("spell1Casts", false),
  PERFECT_SOUL("perfectDragonSoulsTaken", true),
  R_USAGE("spell4Casts", false),
  RUNES("perks", false),
  RIFT_TURRETS("turretsTakenWithRiftHerald", true),
  SUMMONER1_AMOUNT("summoner1Casts", false),
  SUMMONER1_ID("summoner1Id", false),
  SUMMONER2_AMOUNT("summoner2Casts", false),
  SUMMONER2_ID("summoner2Id", false),
  SURRENDER("gameEndedInSurrender", false),
  TIME_ALIVE("longestTimeSpentLiving", false),
  TIME_DEAD("totalTimeSpentDead", false),
  VISION_SCORE("visionScore", false),
  W_USAGE("spell2Casts", false),
  WARDS_CLEARED("wardsKilled", false),
  WARDS_PLACED("wardsPlaced", false);

  private final String key;
  private String key2;
  private final boolean challenge;

  StoredStat(String key, boolean challenge) {
    this.key = key;
    this.challenge = challenge;
  }

  public String getKey() {
    return key;
  }

  public boolean isChallenge() {
    return challenge;
  }
}
