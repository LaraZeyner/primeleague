package de.xeri.prm.models.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * @since 09.04.2022
 */
@Getter
@RequiredArgsConstructor
@ToString
public enum StoredStat {
  ACE_EARLY("acesBefore15Minutes", true),
  ACE_FLAWLESS("flawlessAces", true),
  ACE_TIME("shortestTimeToAceFromFirstTakedown", true),
  AMBUSH("killAfterHiddenWithAlly", true),
  AGGRESSIVE_FLASH("multikillsAfterAggressiveFlash", true),
  ASSASSINATION("quickSoloKills", true),
  ASSISTS("assists", false),
  BARON_TIME("earliestBaron", true),
  BARON_EXECUTES("killsWithHelpFromEpicMonster", true),
  BARON_KILLS("baronKills", false),
  BARON_POWERPLAY("baronBuffGoldAdvantageOverThreshold", true),
  BOUNTY_GOLD("bountyGold", true),
  BUFFS_STOLEN("buffsStolen", true),
  BUFFS_INITIAL("initialBuffCount", true),
  CHAMPION("championId", false),
  CONTROL_WARDS_BOUGHT("visionWardsBoughtInGame", false),
  CONTROL_WARDS_PLACED("controlWardsPlaced", true),
  CONTROL_WARDS_UPTIME("controlWardTimeCoverageInRiverOrEnemyHalf", true),
  CREEP_INVADED("enemyJungleMonsterKills", true),
  CREEP_SCORE_ADVANTAGE("maxCsAdvantageOnLaneOpponent", true),
  CREEP_SCORE_JUNGLE("neutralMinionsKilled", false),
  CREEP_SCORE_JUNGLE_EARLY("jungleCsBefore10Minutes", true),
  CREEP_SCORE_LANE("totalMinionsKilled", false),
  CREEP_SCORE_LANE_EARLY("laneMinionsFirst10Minutes", true),
  DAMAGE_HEALED("totalHeal", false),
  DAMAGE_HEALING_SHIELDING("effectiveHealAndShielding", true),
  DAMAGE_MAGICAL("magicDamageDealtToChampions", false),
  DAMAGE_MITIGATED("damageSelfMitigated", false),
  DAMAGE_PHYSICAL("physicalDamageDealtToChampions", false),
  DAMAGE_TAKEN("totalDamageTaken", false),
  DAMAGE_TEAM_HEAL("totalHealsOnTeammates", false),
  DAMAGE_TEAM_SHIELD("totalDamageShieldedOnTeammates", false),
  DAMAGE_TOTAL("totalDamageDealtToChampions", false),
  DEATHS("deaths", false),
  DIVES_DONE("killsNearEnemyTurret", true),
  DIVES_PROTECTED("killsUnderOwnTurret", true),
  DRAGON_TAKEDOWNS("dragonTakedowns", true),
  DRAGON_TIME("earliestDragonTakedown", true),
  E_USAGE("spell3Casts", false),
  ELDER_TIME("earliestElderDragon", true),
  EXPERIENCE_TOTAL("champExperience", false),
  EXPERIENCE_ADVANTAGE("maxLevelLeadLaneOpponent", true),
  FIRST_BLOOD("firstBloodKill", false),
  FIRST_BLOOD_ASSIST("firstBloodAssist", false),
  FIRST_TOWER("firstTowerKill", false),
  FIRST_TOWER_ASSIST("firstTowerAssist", false),
  FIRST_TOWER_TIME("firstTurretKilledTime", true),
  GANK_SETUP("immobilizeAndKillWithAlly", true),
  GOLD_TOTAL("goldEarned", false),
  GUARD_ALLY("saveAllyFromDeath", true),
  IMMOBILIZATIONS("enemyChampionImmobilizations", true),
  INHIBITORS_TAKEN("thirdInhibitorDestroyedTime", true),
  INVADING_KILLS("takedownsBeforeJungleMinionSpawn", true),
  ITEM_1("item0", false),
  ITEM_2("item1", false),
  ITEM_3("item2", false),
  ITEM_4("item3", false),
  ITEM_5("item4", false),
  ITEM_6("item5", false),
  ITEM_7("item6", false),
  ITEMS_BOUGHT("itemsPurchased", false),
  JUNGLER_ROAMS("killsOnLanersEarlyJungleAsJungler", true),
  KILLS("kills", false),
  KILLS_DISADVANTAGE("maxKillDeficit", true),
  KILLS_DOUBLE("doubleKills", false),
  KILLS_EARLY_JUNGLER("junglerKillsEarlyJungle", true),
  KILLS_EARLY_LANER("takedownsFirst25Minutes", true),
  KILLS_TRIPLE("tripleKills", false),
  KILLS_QUADRA("quadraKills", false),
  KILLS_PENTA("pentaKills", false),
  LANE("teamPosition", false),
  //LANE_LEAD("laningPhaseGoldExpAdvantage", true),
  //LANE_LEAD_EARLY("earlyLaningPhaseGoldExpAdvantage", true),
  LANER_ROAMS("killsOnOtherLanesEarlyJungleAsLaner", true),
  LEGENDARY_FASTEST("fastestLegendary", true),
  LEVELUP_TAKEDOWNS("takedownsAfterGainingLevelAdvantage", true),
  MEJAIS_TIME("mejaisFullStackInTime", true),
  OBJECTIVES_50_50("epicMonsterKillsNearEnemyJungler", true),
  OBJECTIVES_ON_SPAWN("epicMonsterKillsWithin30SecondsOfSpawn", true),
  OBJECTIVES_DAMAGE("damageDealtToObjectives", false),
  OBJECTIVES_JUNGLERKILL("junglerTakedownsNearDamagedEpicMonster", true),
  OBJECTIVES_STOLEN("objectivesStolen", false),
  OBJECTIVES_STOLEN_TAKEDOWNS("objectivesStolenAssists", false),
  OUTPLAYED("killedChampTookFullTeamDamageSurvived", true),
  Q_USAGE("spell1Casts", false),
  QUEST_FAST("fasterSupportQuestCompletion", true),
  PERFECT_SOUL("perfectDragonSoulsTaken", true),
  PICK_KILL("pickKillWithAlly", true),
  PUUID("puuid", false),
  R_USAGE("spell4Casts", false),
  RUNES("perks", false),
  RIFT_TURRETS("turretsTakenWithRiftHerald", true),
  RIFT_TURRETS_MULTI("multiTurretRiftHeraldCount", true),
  SCUTTLES_INITIAL("initialCrabCount", true),
  SCUTTLES_TOTAL("scuttleCrabKills", true),
  SPELL_DODGE("skillshotsDodged", true),
  SPELL_DODGE_QUICK("dodgeSkillShotsSmallWindow", true),
  SPELL_LANDED("skillshotsHit", true),
  SOLO_KILLS("soloKills", true),
  SUMMONER1_AMOUNT("summoner1Casts", false),
  SUMMONER1_ID("summoner1Id", false),
  SUMMONER2_AMOUNT("summoner2Casts", false),
  SUMMONER2_ID("summoner2Id", false),
  SURRENDER("gameEndedInSurrender", false),
  SURVIVED_CLOSE("survivedSingleDigitHpCount", true),
  SURVIVED_HIGH_DAMAGE("tookLargeDamageSurvived", true),
  SURVIVED_HIGH_CROWDCONTROL("survivedThreeImmobilizesInFight", true),
  TELEPORT_KILLS("teleportTakedowns", true),
  TIME_ALIVE("longestTimeSpentLiving", false),
  TIME_DEAD("totalTimeSpentDead", false),
  TOWERS_EARLY("kTurretsDestroyedBeforePlatesFall", true),
  TOWERS_PLATES("turretPlatesTaken", true),
  TOWERS_SPLITPUSHED("soloTurretsLategame", true),
  TOWERS_TAKEDOWNS("turretTakedowns", false),
  VISION_SCORE("visionScore", false),
  W_USAGE("spell2Casts", false),
  WARDS_CLEARED("wardsKilled", false),
  WARDS_GUARDED("wardsGuarded", true),
  WARDS_PLACED("wardsPlaced", false),
  WARDS_TAKEDOWN("wardTakedowns", true);

  private final String key;
  private final boolean challenge;

}