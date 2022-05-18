package de.xeri.prm.models.match.playerperformance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import de.xeri.prm.models.dynamic.Champion;
import de.xeri.prm.models.enums.Abilitytype;
import de.xeri.prm.models.enums.ItemSubType;
import de.xeri.prm.models.enums.ItemType;
import de.xeri.prm.models.match.Teamperformance;
import de.xeri.prm.util.Util;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Lara on 27.04.2022 for web
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class PlayerperformanceStats implements Serializable {
  private static final transient long serialVersionUID = -335345591230884880L;

  @Column(name = "objectives_stolen_contested", precision = 9, scale = 7)
  private BigDecimal objectivesStolenAndContested;

  @Column(name = "objectives_killed_jungler_before", precision = 9, scale = 7)
  private BigDecimal objectivesKilledJunglerBefore;

  @Column(name = "objectives_baron_attempts", precision = 9, scale = 7)
  private BigDecimal baronTakedownsAttempts;

  @Column(name = "trinket_swap_first")
  private short firstTrinketSwap;

  @Column(name = "ward_placed_first")
  private short firstWardTime;

  @Column(name = "ward_placed_first_control")
  private short firstControlwardTime;

  @Column(name = "ward_control_inventory_time")
  private short controlWardInventoryTime;

  @Column(name = "turret_participation", precision = 9, scale = 7)
  private BigDecimal turretParticipation;

  @Column(name = "invading_buffs", precision = 9, scale = 7)
  private BigDecimal invadingAndBuffs;

  @Column(name = "dives_own_rate", precision = 9, scale = 7)
  private BigDecimal divesOwn;

  @Column(name = "dives_enemy_rate", precision = 9, scale = 7)
  private BigDecimal divesEnemy;

  @Column(name = "dives_died")
  private byte divesDied;

  @Column(name = "team_damage_share", precision = 9, scale = 7)
  private BigDecimal teamDamage;

  @Column(name = "team_damage_taken", precision = 9, scale = 7)
  private BigDecimal teamDamageTaken;

  @Column(name = "team_damage_mitigated", precision = 9, scale = 7)
  private BigDecimal teamDamageMitigated;

  @Column(name = "bounty_difference")
  public short bountyDifference;

  @Column(name = "duel_win_rate", precision = 9, scale = 7)
  public BigDecimal duelWinrate;

  @Column(name = "duel_wins")
  public byte duelWins;

  @Deprecated
  @Column(name = "deaths_early")
  public byte deathsEarly;

  @Column(name = "kd_early")
  public byte kdEarly;

  @Column(name = "ahead")
  private boolean ahead;

  @Column(name = "behind")
  private boolean behind;

  @Column(name = "ahead_extend")
  private boolean extendingLead;

  @Column(name = "comeback")
  private boolean comeback;

  @Column(name = "xp_advantage")
  private short xpLead;

  @Column(name = "early_aces_clean")
  private byte acesAndClean;

  @Column(name = "first_full_item")
  private short firstFullItem;

  @Column(name = "efficiency_cs_early", precision = 9, scale = 7)
  private BigDecimal earlyFarmEfficiency;

  @Column(name = "cs_per_minute", precision = 9, scale = 6)
  private BigDecimal csPerMinute;

  @Column(name = "xp_per_minute", precision = 9, scale = 5)
  private BigDecimal xpPerMinute;

  @Column(name = "gold_per_minute", precision = 9, scale = 5)
  private BigDecimal goldPerMinute;

  @Column(name = "legendarys_amount")
  private byte legendaryItems;

  @Column(name = "grievous_wounds_time")
  private short antiHealTime;

  @Column(name = "penetration_time")
  private short penetrationTime;

  @Column(name = "amplifier_time")
  private short amplifierTime;

  @Column(name = "durability_time")
  private short durabilityTime;

  @Column(name = "start_item_sold")
  private short startItemSold;

  @Column(name = "time_alive_percentage", precision = 9, scale = 7)
  private BigDecimal timeAlivePercent;

  @Column(name = "kills_solo_advantage")
  private byte soloKillAdvantage;

  @Column(name = "first_kill_time")
  private short firstKillTime;

  @Column(name = "first_kill_death_time")
  private short firstKillDeathTime;

  @Column(name = "gold_lead_early")
  private short earlyGoldAdvantage;

  @Column(name = "objectives_advantage_early")
  private byte earlyObjectiveAdvantage;

  @Column(name = "objectives_taken_early")
  private byte earlyObjectives;

  @Column(name = "turretplate_advantage")
  private byte turretplateAdvantage;

  @Column(name = "enemy_under_control_advantage", precision = 9, scale = 4)
  private BigDecimal enemyControlAdvantage;

  @Column(name = "enemy_under_control", precision = 9, scale = 4)
  private BigDecimal enemyControlled;

  @Column(name = "keyspells_used")
  private short keyspellsUsed;

  @Column(name = "spell_bilance", precision = 9, scale = 7)
  private BigDecimal totalSpellBilance;

  @Column(name = "hit_bilance", precision = 9, scale = 7)
  private BigDecimal hitBilance;

  @Column(name = "dodge_bilance", precision = 9, scale = 7)
  private BigDecimal dodgeBilance;

  @Column(name = "reaction_bilance")
  private short reactionBilance;

  @Column(name = "enemy_reaction")
  private short enemySpellReaction;

  @Column(name = "lead_diff_after_death_early")
  private short leadDifferenceAfterDiedEarly;

  @Column(name = "kill_participation", precision = 9, scale = 7)
  private BigDecimal killParticipation;

  @Column(name = "kda_true", precision = 9, scale = 6)
  private BigDecimal trueKdaValue;

  @Column(name = "kda_true_kills", precision = 9, scale = 6)
  private BigDecimal trueKdaKills;

  @Column(name = "kda_true_deaths", precision = 9, scale = 6)
  private BigDecimal trueKdaDeaths;

  @Column(name = "kda_true_assists", precision = 9, scale = 6)
  private BigDecimal trueKdaAssists;

  @Column(name = "enemy_early_under_control_advantage", precision = 9, scale = 4)
  private BigDecimal enemyControlAdvantageEarly;

  @Column(name = "enemy_early_under_control", precision = 9, scale = 4)
  private BigDecimal enemyControlledEarly;

  @Column(name = "farm_drop_minute")
  private short csDropAtMinute;

  @Column(name = "trinket_efficiency", precision = 9, scale = 7)
  private BigDecimal trinketEfficiency;

  @Column(name = "goldxp_efficiency_midgame", precision = 9, scale = 7)
  private BigDecimal midgameGoldXPEfficiency;

  @Column(name = "gold_efficiency_midgame", precision = 9, scale = 7)
  private BigDecimal midgameGoldEfficiency;

  @Column(name = "lead_lategame")
  private short lategameLead;

  @Column(name = "behind_farm")
  private short farmingFromBehind;

  @Column(name = "behind_warding")
  private short wardingFromBehind;

  @Column(name = "behind_deaths")
  private short deathsFromBehind;

  @Column(name = "behind_gold")
  private short goldFromBehind;

  @Column(name = "behind_xp")
  private short xpFromBehind;

  @Column(name = "levelup_lead", precision = 9, scale = 7)
  private BigDecimal levelupEarlier;

  @Column(name = "pick_advantage")
  private byte pickAdvantage;

  @Column(name = "teamfight_amount")
  private byte teamfightAmount;

  @Column(name = "teamfight_participation", precision = 9, scale = 7)
  private BigDecimal teamfightParticipation;

  @Column(name = "death_order_average", precision = 9, scale = 7)
  private BigDecimal averageDeathOrder;

  @Column(name = "teamfight_winrate", precision = 9, scale = 7)
  private BigDecimal teamfightWinrate;

  @Column(name = "teamfight_damage_rate", precision = 9, scale = 7)
  private BigDecimal teamfightDamageRate;

  @Column(name = "skirmish_amount")
  private short skirmishAmount;

  @Column(name = "skirmish_participation", precision = 9, scale = 7)
  private BigDecimal skirmishParticipation;

  @Column(name = "skirmish_kills", precision = 9, scale = 8)
  private BigDecimal skirmishKillsPerSkirmish;

  @Column(name = "skirmish_winrate", precision = 9, scale = 7)
  private BigDecimal skirmishWinrate;

  @Column(name = "skirmish_damage_rate", precision = 9, scale = 7)
  private BigDecimal skirmishDamageRate;

  @Column(name = "roam_cs_advantage")
  private byte roamCreepScoreAdvantage;

  @Column(name = "roam_goldxp_advantage")
  private short roamGoldXpAdvantage;

  @Column(name = "roam_gold_advantage")
  private short roamGoldAdvantage;

  @Column(name = "roam_objectivedamage_advantage")
  private short roamObjectiveDamageAdvantage;

  @Column(name = "roam_successscore")
  private short roamSuccessScore;

  @Column(name = "death_positioning_relative", precision = 9, scale = 7)
  private BigDecimal relativeDeathPositioning;

  @Column(name = "positioning_lane", precision = 9, scale = 7)
  private BigDecimal lanePositioning;

  @Column(name = "positioning_mid", precision = 9, scale = 7)
  private BigDecimal midgamePositioning;

  @Column(name = "positioning_late", precision = 9, scale = 7)
  private BigDecimal lategamePositioning;

  @Column(name = "killdeath_positioning_lane", precision = 9, scale = 7)
  private BigDecimal laneKillDeathPositioning;

  @Column(name = "kill_positioning_lane", precision = 9, scale = 7)
  private BigDecimal laneKillPositioning;

  @Column(name = "positioning_split_score")
  private int splitScore;

  @Column(name = "positioning_companion_score")
  private int companionScore;

  @Column(name = "positioning_roam_score")
  private int roamScore;

  @Column(name = "time_combat")
  private short secondsInCombat;

  @Column(name = "base_first_time")
  private short firstBase;

  @Column(name = "base_first_recall")
  private boolean firstBaseThroughRecall;

  @Column(name = "lead_through_deaths")
  private short leadThroughDeaths;

  @Column(name = "base_first_controlled")
  private short firstBaseEnemyControlled;

  @Column(name = "base_first_lead")
  private short firstBaseLead;

  @Column(name = "base_first_gold")
  private short firstBaseResetGold;

  @Column(name = "base_first_gold_unspent")
  private short firstBaseGoldUnspent;

  @Column(name = "base_recall", precision = 9, scale = 7)
  private BigDecimal resetsThroughRecall;

  @Column(name = "base_planned", precision = 9, scale = 7)
  private BigDecimal plannedResets;

  @Column(name = "base_total")
  private short resets;

  @Column(name = "base_duration")
  private int resetDuration;

  @Column(name = "base_gold")
  private short resetGold;

  @Column(name = "base_gold_unspent")
  private short resetGoldUnspent;

  @Column(name = "base_gold_lost")
  private short resetGoldGain;

  @Column(name = "base_together", precision = 9, scale = 7)
  private BigDecimal resetsTogether;

  @Column(name = "base_second_time")
  private short secondBase;

  @Column(name = "base_consumables_purchased")
  private boolean consumablesPurchased;

  @Column(name = "base_resource_conservation", precision = 9, scale = 7)
  private BigDecimal resourceConservation;

  @Column(name = "base_second_controlled")
  private short secondBaseEnemyControlled;

  @Column(name = "damage_early_percentage", precision = 9, scale = 7)
  private BigDecimal earlyDamage;

  @Column(name = "wards_early")
  private short wardsEarlygame;

  @Column(name = "xp_early", precision = 9, scale = 7)
  private BigDecimal earlyXpEfficiency;

  @Column(name = "damage_early_difference")
  private short earlyDamageTrading;

  @Column(name = "lane_health", precision = 9, scale = 7)
  private BigDecimal averageLaneHealth;

  @Column(name = "lane_resource", precision = 9, scale = 7)
  private BigDecimal averageLaneResource;

  @Column(name = "wave_status_push")
  private byte pushes;

  @Column(name = "wave_status_freeze")
  private byte freezes;

  @Column(name = "wave_status_hold")
  private byte holds;

  @Column(name = "utility_score", precision = 9, scale = 7)
  private BigDecimal utilityScore;

  @Column(name = "lead_without_dying")
  private short leadWithoutDying;

  @Column(name = "proximity", precision = 9, scale = 6)
  private BigDecimal proximity;

  @Column(name = "lane_proximity", precision = 9, scale = 6)
  private BigDecimal laneProximityDifference;

  public PlayerperformanceStats(Playerperformance playerperformance) {
    this();
    final Teamperformance teamperformance = playerperformance.getTeamperformance();

    this.invadingAndBuffs = BigDecimal.valueOf(playerperformance.getTeamInvading() + playerperformance.getBuffsStolen());

    final double turretParticipation = Util.div(playerperformance.getTurretTakedowns(), teamperformance.getTowers());

    this.turretParticipation = BigDecimal.valueOf(turretParticipation);

    if (playerperformance.getDivesDone() != 0) {
      final double divesOwn = Util.div(playerperformance.getDivesSuccessful(), playerperformance.getDivesDone());
      this.divesOwn = BigDecimal.valueOf(divesOwn);
    }

    if (playerperformance.getDivesGotten() != 0) {
      final double divesEnemy = Util.div(playerperformance.getDivesProtected(), playerperformance.getDivesGotten());
      this.divesEnemy = BigDecimal.valueOf(divesEnemy);
    }

    this.divesDied = (byte) (playerperformance.getDivesGotten() - playerperformance.getDivesProtected());

    final double teamDamage = Util.div(playerperformance.getDamageTotal(), teamperformance.getTotalDamage());
    this.teamDamage = BigDecimal.valueOf(teamDamage);

    final double teamDamageTaken = Util.div(playerperformance.getDamageTaken(), teamperformance.getTotalDamageTaken());
    this.teamDamageTaken = BigDecimal.valueOf(teamDamageTaken);

    this.acesAndClean = (byte) (teamperformance.getEarlyAces() +
        teamperformance.getFlawlessAces());

    this.firstFullItem = (short) playerperformance.getItems().stream()
        .filter(item -> item.getItem().getType().equals(ItemType.LEGENDARY) || item.getItem().getType().equals(ItemType.MYTHIC))
        .mapToInt(item -> item.getBuyTime() / 1000)
        .min().orElse(0);

    final double csPerMinute = playerperformance.getTotalCreeps() * 60 * 1d / teamperformance.getGame().getDuration();
    this.csPerMinute = BigDecimal.valueOf(csPerMinute);

    final double xpPerMinute = playerperformance.getExperience() * 60 * 1d / teamperformance.getGame().getDuration();
    this.xpPerMinute = BigDecimal.valueOf(xpPerMinute);

    final double goldPerMinute = playerperformance.getGoldTotal() * 60 * 1d / teamperformance.getGame().getDuration();
    this.goldPerMinute = BigDecimal.valueOf(goldPerMinute);

    this.legendaryItems = (byte) playerperformance.getItems().stream()
        .filter(item -> item.getItem().getType().equals(ItemType.LEGENDARY))
        .count();

    final short antiHealTime = (short) playerperformance.getItems().stream()
        .filter(item -> item.getItem().getSubtype() != null)
        .filter(item -> item.getItem().getSubtype().equals(ItemSubType.GRIEVOUS_WOUNDS))
        .mapToInt(item -> item.getBuyTime() / 1000)
        .min().orElse(0);
    if (antiHealTime > 0) {
      this.antiHealTime = antiHealTime;
    }

    final short penetrationTime = (short) playerperformance.getItems().stream()
        .filter(item -> item.getItem().getSubtype() != null)
        .filter(item -> item.getItem().getSubtype().equals(ItemSubType.PENETRATION))
        .mapToInt(item -> item.getBuyTime() / 1000)
        .min().orElse(0);
    if (penetrationTime > 0) {
      this.penetrationTime = penetrationTime;
    }


    final short amplifierTime = (short) playerperformance.getItems().stream()
        .filter(item -> item.getItem().getSubtype() != null)
        .filter(item -> item.getItem().getSubtype().equals(ItemSubType.AMPLIFIER))
        .mapToInt(item -> item.getBuyTime() / 1000)
        .min().orElse(0);
    if (penetrationTime > 0) {
      this.amplifierTime = amplifierTime;
    }

    final short durabilityTime = (short) playerperformance.getItems().stream()
        .filter(item -> item.getItem().getSubtype() != null)
        .filter(item -> item.getItem().getSubtype().equals(ItemSubType.DURABILITY))
        .mapToInt(item -> item.getBuyTime() / 1000)
        .min().orElse(0);
    if (penetrationTime > 0) {
      this.durabilityTime = durabilityTime;
    }

    final double timeAlive = playerperformance.getTimeDead() * 1d / (playerperformance.getTeamperformance().getGame().getDuration());
    this.timeAlivePercent = BigDecimal.valueOf(timeAlive);

    short amount = 0;
    Champion champion = playerperformance.getChampionOwn();
    final List<Abilitytype> keyspells = champion.getKeyspells();
    if (keyspells.contains(Abilitytype.Q_SPELL)) {
      amount += playerperformance.getQUsages();
    }
    if (keyspells.contains(Abilitytype.W_SPELL)) {
      amount += playerperformance.getWUsages();
    }
    if (keyspells.contains(Abilitytype.E_SPELL)) {
      amount += playerperformance.getEUsages();
    }
    if (keyspells.contains(Abilitytype.ULTIMATE)) {
      amount += playerperformance.getRUsages();
    }

    this.keyspellsUsed = amount;

    final double killParticipation =
        Util.div(playerperformance.getKills(), playerperformance.getTeamperformance().getTotalKills());
    this.killParticipation = BigDecimal.valueOf(killParticipation);

    final double visionValue = Util.div(playerperformance.getVisionScore(), teamperformance.getVision());
    final double ccValue = Util.div(playerperformance.getImmobilizations(), teamperformance.getImmobilizations());
    final double mitigationValue = Util.div(playerperformance.getDamageMitigated(), teamperformance.getDamageMitigated());
    final double utilityScore = visionValue + ccValue + mitigationValue;
    this.utilityScore = BigDecimal.valueOf(utilityScore);
  }


  public void setObjectivesStolenAndContested(Playerperformance playerperformance, byte allObjectivesAmount) {
    if (allObjectivesAmount == 0) {
      this.objectivesStolenAndContested = BigDecimal.valueOf(1d * 0);
    } else {
      this.objectivesStolenAndContested = BigDecimal.valueOf((playerperformance.getTeamperformance().getObjectiveContests() +
          (playerperformance.getObjectivesStolen() * 3)) * 1d / allObjectivesAmount);
    }
  }

  public void setObjectivesKilledJunglerBefore(Playerperformance playerperformance, byte allObjectivesAmount) {
    if (allObjectivesAmount == 0) {
      this.objectivesKilledJunglerBefore = BigDecimal.valueOf(1d * 0);
    } else {
      final double value = playerperformance.getJunglerKillsAtObjective() * 1d / allObjectivesAmount;
      this.objectivesKilledJunglerBefore = BigDecimal.valueOf(value);
    }
  }

  public void setBaronTakedownsAttempts(Playerperformance playerperformance, byte steals) {
    final double value = Util.div(Math.max(playerperformance.getBaronKills() - steals, 0),
        playerperformance.getBaronKills() + steals + playerperformance.getBaronExecutes());
    this.baronTakedownsAttempts = BigDecimal.valueOf(value);
  }

  public void setTeamDamageMitigated(Playerperformance playerperformance, int totalTeamDamage) {
    final double damageMitigated =
        Util.div(playerperformance.getDamageMitigated() + playerperformance.getDamageShielded(), totalTeamDamage);
    this.teamDamageMitigated = BigDecimal.valueOf(damageMitigated);
  }

  public void setBountyDifference(Playerperformance playerperformance, short bountyGiven) {
    this.bountyDifference = (short) (playerperformance.getBountyGold() - bountyGiven);
  }

  public void setDuels(Playerperformance playerperformance, int duelWins, int duelLosses) {
    if (duelWins + duelLosses > 0) {
      final double duelWinrate = Util.div(duelWins, duelWins + duelLosses);
      this.duelWinrate = BigDecimal.valueOf(duelWinrate);
      this.duelWins = (byte) duelWins;
      this.soloKillAdvantage = (byte) (playerperformance.getSoloKills() - duelLosses);
    }
  }

  public void setDeathsEarly(Playerperformance playerperformance, byte deathsEarly) {
    this.deathsEarly = deathsEarly;
    this.kdEarly = (byte) (playerperformance.getEarlyKills() - deathsEarly);
  }

  public void setFirstKillTime(short firstKillTime, short firstDeathTime) {
    this.firstKillTime = firstKillTime;
    this.firstKillDeathTime = (short) (firstKillTime - firstDeathTime);
  }

  public void setEarlyObjectiveRate(byte objectivesWe, byte objectivesEnemy) {
    this.earlyObjectiveAdvantage = (byte) (objectivesWe - objectivesEnemy);
    this.earlyObjectives = objectivesWe;
  }

  public void setEnemyControlAdvantage(double controlled, double underControl) {
    this.enemyControlled = BigDecimal.valueOf(controlled);
    this.enemyControlAdvantage = BigDecimal.valueOf(controlled - underControl);
  }

  public void setEnemyControlAdvantageEarly(double controlled, double underControl) {
    this.enemyControlledEarly = BigDecimal.valueOf(controlled);
    this.enemyControlAdvantageEarly = BigDecimal.valueOf(controlled - underControl);
  }

  public void setSpellDodge(Playerperformance playerperformance, Short spellsHit, Short spellsDodged, Short quickDodged) {
    if (spellsHit != null && spellsDodged != null) {
      final double hitBilance = Util.div(playerperformance.getSpellsHit(), playerperformance.getSpellsHit() + spellsDodged);
      this.hitBilance = BigDecimal.valueOf(hitBilance);
      final double dodgeBilance = Util.div(playerperformance.getSpellsDodged(), playerperformance.getSpellsDodged() + spellsHit);
      this.dodgeBilance = BigDecimal.valueOf(dodgeBilance);
      final double totalSpellBilance = (hitBilance + dodgeBilance) / 2;
      this.totalSpellBilance = BigDecimal.valueOf(totalSpellBilance);
    }
    if (quickDodged != null) {
      this.reactionBilance = (short) (playerperformance.getQuickDodged() - quickDodged);
    }
  }

  public void setTrueKda(double kills, double deaths, double assists) {
    this.trueKdaKills = BigDecimal.valueOf(kills);
    this.trueKdaDeaths = BigDecimal.valueOf(deaths);
    this.trueKdaAssists = BigDecimal.valueOf(assists);
    this.trueKdaValue = BigDecimal.valueOf((kills + assists) / (deaths == 0 ? 1 : deaths));
  }

  public void setBehaviourFromBehindAhead(short farm, short wards, short deaths, short gold, short xp) {
    this.farmingFromBehind = farm;
    this.wardingFromBehind = wards;
    this.deathsFromBehind = deaths;
    this.goldFromBehind = gold;
    this.xpFromBehind = xp;
  }

  public void setSkirmishes(int amount, int total, int skirmishKills, double winrate, double damageRate) {
    this.skirmishAmount = (short) amount;
    final double skirmishPercentage = Util.div(amount, total);
    this.skirmishParticipation = BigDecimal.valueOf(skirmishPercentage);
    final double skirmishKillsPerSkirmish = Util.div(skirmishKills, amount);
    this.skirmishKillsPerSkirmish = BigDecimal.valueOf(skirmishKillsPerSkirmish);
    this.skirmishWinrate = BigDecimal.valueOf(winrate);
    this.skirmishDamageRate = BigDecimal.valueOf(damageRate);
  }

  public void setTeamfights(int amount, int total, double deathOrder, double winrate, double damageRate) {
    this.teamfightAmount = (byte) amount;
    final double teamfightPercentage = Util.div(amount, total);
    this.teamfightParticipation = BigDecimal.valueOf(teamfightPercentage);
    this.averageDeathOrder = BigDecimal.valueOf(deathOrder);
    this.teamfightWinrate = BigDecimal.valueOf(winrate);
    this.teamfightDamageRate = BigDecimal.valueOf(damageRate);
  }

  //<editor-fold desc="getter">
  public double getObjectivesStolenAndContested() {
    return Util.getDouble(objectivesStolenAndContested);
  }

  public double getObjectivesKilledJunglerBefore() {
    return Util.getDouble(objectivesKilledJunglerBefore);
  }

  public double getBaronTakedownsAttempts() {
    return Util.getDouble(baronTakedownsAttempts);
  }

  public double getTurretParticipation() {
    return Util.getDouble(turretParticipation);
  }

  public double getInvadingAndBuffs() {
    return Util.getDouble(invadingAndBuffs);
  }

  public double getDivesOwn() {
    return Util.getDouble(divesOwn);
  }

  public double getDivesEnemy() {
    return Util.getDouble(divesEnemy);
  }

  public double getTeamDamage() {
    return Util.getDouble(teamDamage);
  }

  public double getTeamDamageTaken() {
    return Util.getDouble(teamDamageTaken);
  }

  public double getTeamDamageMitigated() {
    return Util.getDouble(teamDamageMitigated);
  }

  public double getDuelWinrate() {
    return Util.getDouble(duelWinrate);
  }

  public double getEarlyFarmEfficiency() {
    return Util.getDouble(earlyFarmEfficiency);
  }

  public double getCsPerMinute() {
    return Util.getDouble(csPerMinute);
  }

  public double getXpPerMinute() {
    return Util.getDouble(xpPerMinute);
  }

  public double getGoldPerMinute() {
    return Util.getDouble(goldPerMinute);
  }

  public short getSituationalTime() {
    return (short) Math.min(penetrationTime, antiHealTime);
  }

  public double getTimeAlivePercent() {
    return Util.getDouble(timeAlivePercent);
  }

  public double getEnemyControlAdvantage() {
    return Util.getDouble(enemyControlAdvantage);
  }

  public double getEnemyControlled() {
    return Util.getDouble(enemyControlled);
  }

  public double getHitBilance() {
    return Util.getDouble(hitBilance);
  }

  public double getDodgeBilance() {
    return Util.getDouble(dodgeBilance);
  }

  public double getTotalSpellBilance() {
    return Util.getDouble(totalSpellBilance);
  }

  public double getKillParticipation() {
    return Util.getDouble(killParticipation);
  }

  public double getTrueKdaValue() {
    return Util.getDouble(trueKdaValue);
  }

  public double getTrueKdaKills() {
    return Util.getDouble(trueKdaKills);
  }

  public double getTrueKdaDeaths() {
    return Util.getDouble(trueKdaDeaths);
  }

  public double getTrueKdaAssists() {
    return Util.getDouble(trueKdaAssists);
  }

  public double getEnemyControlAdvantageEarly() {
    return Util.getDouble(enemyControlAdvantageEarly);
  }

  public double getEnemyControlledEarly() {
    return Util.getDouble(enemyControlledEarly);
  }

  public double getTrinketEfficiency() {
    return Util.getDouble(trinketEfficiency);
  }

  public double getMidgameGoldEfficiency() {
    return Util.getDouble(midgameGoldEfficiency);
  }

  public double getMidgameGoldXPEfficiency() {
    return Util.getDouble(midgameGoldXPEfficiency);
  }

  public double getLevelupEarlier() {
    return Util.getDouble(levelupEarlier);
  }

  public double getAverageDeathOrder() {
    return Util.getDouble(averageDeathOrder);
  }

  public double getTeamfightParticipation() {
    return Util.getDouble(teamfightParticipation);
  }

  public double getTeamfightWinrate() {
    return Util.getDouble(teamfightWinrate);
  }

  public double getTeamfightDamageRate() {
    return Util.getDouble(teamfightDamageRate);
  }

  public double getSkirmishParticipation() {
    return Util.getDouble(skirmishParticipation);
  }

  public double getSkirmishKillsPerSkirmish() {
    return Util.getDouble(skirmishKillsPerSkirmish);
  }

  public double getSkirmishWinrate() {
    return Util.getDouble(skirmishWinrate);
  }

  public double getSkirmishDamageRate() {
    return Util.getDouble(skirmishDamageRate);
  }

  public double getRelativeDeathPositioning() {
    return Util.getDouble(relativeDeathPositioning);
  }

  public double getLanePositioning() {
    return Util.getDouble(lanePositioning);
  }

  public double getMidgamePositioning() {
    return Util.getDouble(midgamePositioning);
  }

  public double getLategamePositioning() {
    return Util.getDouble(lategamePositioning);
  }

  public double getLaneKillDeathPositioning() {
    return Util.getDouble(laneKillDeathPositioning);
  }

  public double getLaneKillPositioning() {
    return Util.getDouble(laneKillPositioning);
  }

  public double getResetsThroughRecall() {
    return Util.getDouble(resetsThroughRecall);
  }

  public double getPlannedResets() {
    return Util.getDouble(plannedResets);
  }

  public double getResetsTogether() {
    return Util.getDouble(resetsTogether);
  }

  public double getResourceConservation() {
    return Util.getDouble(resourceConservation);
  }

  public double getEarlyDamage() {
    return Util.getDouble(earlyDamage);
  }

  public double getEarlyXpEfficiency() {
    return Util.getDouble(earlyXpEfficiency);
  }

  public double getAverageLaneHealth() {
    return Util.getDouble(averageLaneHealth);
  }

  public double getAverageLaneResource() {
    return Util.getDouble(averageLaneResource);
  }

  public double getUtilityScore() {
    return Util.getDouble(utilityScore);
  }

  public double getProximity() {
    return Util.getDouble(proximity);
  }

  public double getLaneProximityDifference() {
    return Util.getDouble(laneProximityDifference);
  }
  //</editor-fold>

}
