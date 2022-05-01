package de.xeri.league.models.match;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import de.xeri.league.models.dynamic.Champion;
import de.xeri.league.models.enums.Abilitytype;
import de.xeri.league.models.enums.ItemSubType;
import de.xeri.league.models.enums.ItemType;
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

  @Column(name = "cs_advantage")
  private short csAdvantage;

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
  private BigDecimal timeAlive;

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

  @Column(name = "kda_true_kills")
  private BigDecimal trueKdaKills;

  @Column(name = "kda_true_deaths")
  private BigDecimal trueKdaDeaths;

  @Column(name = "kda_true_assists")
  private BigDecimal trueKdaAssists;

  @Column(name = "enemy_early_under_control_advantage", precision = 9, scale = 4)
  private BigDecimal enemyControlAdvantageEarly;

  @Column(name = "enemy_early_under_control", precision = 9, scale = 4)
  private BigDecimal enemyControlledEarly;

  @Column(name = "farm_drop_minute")
  private short csDropAtMinute;

  @Column(name = "trinket_efficiency")
  private BigDecimal trinketEfficiency;

  @Column(name = "xp_efficiency_midgame")
  private BigDecimal midgameXPEfficiency;

  @Column(name = "gold_efficiency_midgame")
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

  @Column(name = "levelup_lead")
  private BigDecimal levelupEarlier;

  public PlayerperformanceStats(Playerperformance playerperformance) {
    this();
    final Teamperformance teamperformance = playerperformance.getTeamperformance();

    this.invadingAndBuffs = BigDecimal.valueOf(playerperformance.getTeamInvading() + playerperformance.getBuffsStolen());

    double turretParticipation;
    if (teamperformance.getTowers() == 0) {
      turretParticipation = 0;
    } else {
      turretParticipation = playerperformance.getTurretTakedowns() * 1d / teamperformance.getTowers();
    }
    this.turretParticipation = BigDecimal.valueOf(turretParticipation);

    final double divesOwn;
    if (playerperformance.getDivesDone() == 0) {
      divesOwn = 0;
    } else {
      divesOwn = playerperformance.getDivesSuccessful() * 1d / playerperformance.getDivesDone();
    }
    this.divesOwn = BigDecimal.valueOf(divesOwn);

    final double divesEnemy;
    if (playerperformance.getDivesGotten() == 0) {
      divesEnemy = 0;
    } else {
      divesEnemy = playerperformance.getDivesProtected() * 1d / playerperformance.getDivesGotten();
    }

    this.divesEnemy = BigDecimal.valueOf(divesEnemy);

    this.divesDied = (byte) (playerperformance.getDivesGotten() - playerperformance.getDivesProtected());

    final double teamDamage = playerperformance.getDamageTotal() * 1d / teamperformance.getTotalDamage();
    this.teamDamage = BigDecimal.valueOf(teamDamage);

    final double teamDamageTaken = playerperformance.getDamageTaken() * 1d / teamperformance.getTotalDamageTaken();
    this.teamDamage = BigDecimal.valueOf(teamDamageTaken);

    this.acesAndClean = (byte) (teamperformance.getEarlyAces() +
        teamperformance.getFlawlessAces());

    this.firstFullItem = (short) playerperformance.getItems().stream()
        .filter(item -> item.getItem().getType().equals(ItemType.LEGENDARY) || item.getItem().getType().equals(ItemType.MYTHIC))
        .mapToInt(item -> item.getBuyTime() / 1000)
        .min().orElse(0);

    final double csPerMinute = playerperformance.getCreepsTotal() * 60 * 1d / teamperformance.getGame().getDuration();
    this.csPerMinute = BigDecimal.valueOf(csPerMinute);

    final double xpPerMinute = playerperformance.getExperience() * 60 * 1d / teamperformance.getGame().getDuration();
    this.xpPerMinute = BigDecimal.valueOf(xpPerMinute);

    final double goldPerMinute = playerperformance.getGoldTotal() * 60 * 1d / teamperformance.getGame().getDuration();
    this.goldPerMinute = BigDecimal.valueOf(goldPerMinute);

    this.legendaryItems = (byte) playerperformance.getItems().stream()
        .filter(item -> item.getItem().getType().equals(ItemType.LEGENDARY))
        .count();

    this.antiHealTime = (short) playerperformance.getItems().stream()
        .filter(item -> item.getItem().getSubtype().equals(ItemSubType.GRIEVOUS_WOUNDS))
        .mapToInt(item -> item.getBuyTime() / 1000)
        .min().orElse(0);

    this.penetrationTime = (short) playerperformance.getItems().stream()
        .filter(item -> item.getItem().getSubtype().equals(ItemSubType.PENETRATION))
        .mapToInt(item -> item.getBuyTime() / 1000)
        .min().orElse(0);

    this.amplifierTime = (short) playerperformance.getItems().stream()
        .filter(item -> item.getItem().getSubtype().equals(ItemSubType.AMPLIFIER))
        .mapToInt(item -> item.getBuyTime() / 1000)
        .min().orElse(0);

    this.durabilityTime = (short) playerperformance.getItems().stream()
        .filter(item -> item.getItem().getSubtype().equals(ItemSubType.DURABILITY))
        .mapToInt(item -> item.getBuyTime() / 1000)
        .min().orElse(0);

    final double timeAlive = 1 - (playerperformance.getTimeDead() * 1d / (playerperformance.getTeamperformance().getGame().getDuration()));
    this.timeAlive = BigDecimal.valueOf(timeAlive);

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

    final double killParticipation = playerperformance.getKills() * 1d / playerperformance.getTeamperformance().getTotalKills();
    this.killParticipation = BigDecimal.valueOf(killParticipation);
  }


  public void setObjectivesStolenAndContested(Playerperformance playerperformance, byte allObjectivesAmount) {
    if (allObjectivesAmount == 0) {
      this.objectivesStolenAndContested = BigDecimal.valueOf(1d * 0);
    } else {
      this.objectivesStolenAndContested = new BigDecimal((playerperformance.getTeamperformance().getObjectiveContests() +
          (playerperformance.getObjectivesStolen() * 3)) * 1d / allObjectivesAmount);
    }
  }

  public void setObjectivesKilledJunglerBefore(Playerperformance playerperformance, byte allObjectivesAmount) {
    if (allObjectivesAmount == 0) {
      this.objectivesKilledJunglerBefore = BigDecimal.valueOf(1d * 0);
    } else {
      final double value = playerperformance.getJunglerKillsAtObjective() * 1d / allObjectivesAmount;
      this.objectivesKilledJunglerBefore = new BigDecimal(value);
    }
  }

  public void setBaronTakedownsAttempts(Playerperformance playerperformance, byte steals) {
    final int divisor = playerperformance.getBaronKills() + steals + playerperformance.getBaronExecutes();

    if (divisor == 0) {
      this.baronTakedownsAttempts = BigDecimal.valueOf(1d * 0);
    } else {
      final double value = (playerperformance.getBaronKills() - steals) * 1d / divisor;
      this.baronTakedownsAttempts = BigDecimal.valueOf(value);
    }
  }

  public void setTeamDamageMitigated(Playerperformance playerperformance, int totalTeamDamage) {
    final double damageMitigated = (playerperformance.getDamageMitigated() + playerperformance.getDamageShielded()) * 1d / totalTeamDamage;
    this.teamDamageMitigated = BigDecimal.valueOf(damageMitigated);
  }

  public void setBountyDifference(Playerperformance playerperformance, short bountyGiven) {
    this.bountyDifference = (short) (playerperformance.getBountyGold() - bountyGiven);
  }

  public void setDuels(Playerperformance playerperformance, int duelWins, int duelLosses) {
    final double duelWinrate = duelWins * 1d / (duelWins + duelLosses);
    this.duelWinrate = BigDecimal.valueOf(duelWinrate);
    this.duelWins = (byte) duelWins;
    this.soloKillAdvantage = (byte) (playerperformance.getSoloKills() - duelLosses);
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

  public void setSpellDodge(Playerperformance playerperformance, short spellsHit, short spellsDodged, short quickDodged) {
    final double hitBilance = playerperformance.getSpellsHit() * 1d / (playerperformance.getSpellsHit() + spellsDodged);
    this.hitBilance = BigDecimal.valueOf(hitBilance);
    final double dodgeBilance = playerperformance.getSpellsDodged() * 1d / (playerperformance.getSpellsDodged() + spellsHit);
    this.hitBilance = BigDecimal.valueOf(dodgeBilance);
    final double totalSpellBilance = (hitBilance + dodgeBilance) / 2;
    this.totalSpellBilance = BigDecimal.valueOf(totalSpellBilance);
    this.reactionBilance = (short) (playerperformance.getQuickDodged() - quickDodged);
  }

  public void setTrueKda(double kills, double deaths, double assists) {
    this.trueKdaKills = BigDecimal.valueOf(kills);
    this.trueKdaDeaths = BigDecimal.valueOf(deaths);
    this.trueKdaAssists = BigDecimal.valueOf(assists);
    this.trueKdaValue = BigDecimal.valueOf((kills + assists) / deaths);
  }

  public void setBehaviourFromBehindAhead(short farm, short wards, short deaths, short gold, short xp) {
    this.farmingFromBehind = farm;
    this.wardingFromBehind = wards;
    this.deathsFromBehind = deaths;
    this.goldFromBehind = gold;
    this.xpFromBehind = xp;
  }

  //<editor-fold desc="getter">
  public double getObjectivesStolenAndContested() {
    return objectivesStolenAndContested.doubleValue();
  }

  public double getObjectivesKilledJunglerBefore() {
    return objectivesKilledJunglerBefore.doubleValue();
  }

  public double getBaronTakedownsAttempts() {
    return baronTakedownsAttempts.doubleValue();
  }

  public double getTurretParticipation() {
    return turretParticipation.doubleValue();
  }

  public double getInvadingAndBuffs() {
    return invadingAndBuffs.doubleValue();
  }

  public double getDivesOwn() {
    return divesOwn.doubleValue();
  }

  public double getDivesEnemy() {
    return divesEnemy.doubleValue();
  }

  public double getTeamDamage() {
    return teamDamage.doubleValue();
  }

  public double getTeamDamageTaken() {
    return teamDamageTaken.doubleValue();
  }

  public double getTeamDamageMitigated() {
    return teamDamageMitigated.doubleValue();
  }

  public double getDuelWinrate() {
    return duelWinrate.doubleValue();
  }

  public double getEarlyFarmEfficiency() {
    return earlyFarmEfficiency.doubleValue();
  }

  public double getCsPerMinute() {
    return csPerMinute.doubleValue();
  }

  public double getXpPerMinute() {
    return xpPerMinute.doubleValue();
  }

  public double getGoldPerMinute() {
    return goldPerMinute.doubleValue();
  }

  public short getSituationalTime() {
    return (short) Math.min(penetrationTime, antiHealTime);
  }

  public double getTimeAlive() {
    return timeAlive.doubleValue();
  }

  public double getEnemyControlAdvantage() {
    return enemyControlAdvantage.doubleValue();
  }

  public double getEnemyControlled() {
    return enemyControlled.doubleValue();
  }

  public double getHitBilance() {
    return hitBilance.doubleValue();
  }

  public double getDodgeBilance() {
    return dodgeBilance.doubleValue();
  }

  public double getTotalSpellBilance() {
    return totalSpellBilance.doubleValue();
  }

  public double getKillParticipation() {
    return killParticipation.doubleValue();
  }

  public double getTrueKdaValue() {
    return trueKdaValue.doubleValue();
  }

  public double getTrueKdaKills() {
    return trueKdaKills.doubleValue();
  }

  public double getTrueKdaDeaths() {
    return trueKdaDeaths.doubleValue();
  }

  public double getTrueKdaAssists() {
    return trueKdaAssists.doubleValue();
  }

  public double getEnemyControlAdvantageEarly() {
    return enemyControlAdvantageEarly.doubleValue();
  }

  public double getEnemyControlledEarly() {
    return enemyControlledEarly.doubleValue();
  }

  public double getTrinketEfficiency() {
    return trinketEfficiency.doubleValue();
  }

  public double getMidgameGoldEfficiency() {
    return midgameGoldEfficiency.doubleValue();
  }

  public double getMidgameXPEfficiency() {
    return midgameXPEfficiency.doubleValue();
  }

  public double getLevelupEarlier() {
    return levelupEarlier.doubleValue();
  }

  //</editor-fold>

}
