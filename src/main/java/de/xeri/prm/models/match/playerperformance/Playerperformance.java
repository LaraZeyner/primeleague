package de.xeri.prm.models.match.playerperformance;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.prm.manager.PrimeData;
import de.xeri.prm.models.dynamic.Champion;
import de.xeri.prm.models.dynamic.Item;
import de.xeri.prm.models.dynamic.Rune;
import de.xeri.prm.models.dynamic.Summonerspell;
import de.xeri.prm.models.enums.ItemType;
import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.league.Account;
import de.xeri.prm.models.match.ChampionSelection;
import de.xeri.prm.models.match.Gametype;
import de.xeri.prm.models.match.Teamperformance;
import de.xeri.prm.util.HibernateUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.NamedQuery;

@Entity(name = "Playerperformance")
@Table(name = "playerperformance", indexes = {
    @Index(name = "idx_playerperformance_player", columnList = "teamperformance, account", unique = true),
    @Index(name = "champion_enemy", columnList = "champion_enemy"),
    @Index(name = "idx_playerperformance_lane", columnList = "teamperformance, lane", unique = true),
    @Index(name = "champion_own", columnList = "champion_own"),
    @Index(name = "account", columnList = "account"),
})
@Filter(name = "filter_team", condition = "team= :team")
@Filter(name = "filter_account", condition = "account= :account")
@Filter(name = "filter_lane", condition = "lane= :lane")
@Filter(name = "filter_champion_own", condition = "championOwn= :champion")
@Filter(name = "filter_champion_enemy", condition = "championEnemy= :champion")
@Filter(name = "filter_gametype", condition = "gametype= :gametype")
@Filter(name = "filter_since", condition = "gameStart >= :minDate")
@NamedQuery(name = "Playerperformance.findAll", query = "FROM Playerperformance p INNER JOIN p.teamperformance t WHERE t.team IS NOT NULL")
@NamedQuery(name = "Playerperformance.findById", query = "FROM Playerperformance p WHERE id = :pk")
@NamedQuery(name = "Playerperformance.findBy", query = "FROM Playerperformance p WHERE teamperformance = :teamperformance AND account = :account")
@NamedQuery(name = "Playerperformance.findByLane", query = "FROM Playerperformance p WHERE teamperformance = :teamperformance AND lane = :lane")
@NamedQuery(name = "Playerperformance.findStatAvg", query = "SELECT COUNT(id), AVG(qUsages), AVG(wUsages), AVG(eUsages), AVG(rUsages), AVG(spellsHit), AVG(spellsDodged), AVG(quickDodged), AVG(damageMagical), AVG(damagePhysical), AVG(damageTotal), AVG (damageTaken), AVG(damageMitigated), AVG(damageHealed), AVG(damageShielded), AVG(kills), AVG(deaths), AVG(assists), AVG(soloKills), AVG(levelUpAllin), AVG(doubleKills + tripleKills + quadraKills + pentaKills), AVG(aggressiveFlash), AVG(timeAlive), AVG(timeDead), AVG(teleportKills), AVG(immobilizations), AVG(controlWards), AVG(controlWardUptime), AVG(wardsPlaced), AVG(wardsCleared), AVG(guardedWards), AVG(visionScore), AVG(visionscoreAdvantage), AVG(objectivesStolen), AVG(firstturretAdvantage), AVG(objectivesDamage), AVG(baronExecutes), AVG(baronKills), AVG(buffsStolen), AVG(initialScuttles), AVG(totalScuttles), AVG(splitpushedTurrets), AVG(teamInvading), AVG(ganksEarly), AVG(ganksTotal), AVG(CASE WHEN ganksTop + ganksMid + ganksBot <> 0 THEN ((CAST(ganksTop AS int) - CAST(ganksBot AS int)) / (ganksTop + ganksMid + ganksBot)) ELSE 0 END), AVG(divesDone), AVG(divesSuccessful), AVG(divesGotten), AVG(divesProtected), AVG(goldTotal), AVG(bountyGold), AVG(experience), AVG(totalCreeps), AVG(earlyCreeps), AVG(invadedCreeps), AVG(earlyLaneLead), AVG(laneLead), AVG(turretplates), AVG(creepScoreAdvantage), AVG(itemsAmount), AVG(mejaisCompleted), AVG(firstBlood), AVG(outplayed), AVG(turretTakedowns), AVG(dragonTakedowns), AVG(fastestLegendary), AVG(gankSetups), AVG(initialBuffs), AVG(earlyKills), AVG(junglerKillsAtObjective), AVG(ambush), AVG(earlyTurrets), AVG(levelLead), AVG(picksMade), AVG(assassinated), AVG(savedAlly), AVG(survivedClose), AVG(stats.objectivesStolenAndContested), AVG(stats.objectivesKilledJunglerBefore), AVG(stats.baronTakedownsAttempts), AVG(stats.firstTrinketSwap), AVG(stats.firstWardTime), AVG(stats.firstControlwardTime), AVG(stats.controlWardInventoryTime), AVG(stats.turretParticipation), AVG(stats.invadingAndBuffs), AVG(stats.divesOwn), AVG(stats.divesEnemy), AVG(stats.divesDied), AVG(stats.teamDamage), AVG(stats.teamDamageTaken), AVG(stats.teamDamageMitigated), AVG(stats.bountyDifference), AVG(stats.duelWinrate), AVG(stats.duelWins), AVG(stats.deathsEarly), AVG(stats.kdEarly), AVG(stats.ahead), AVG(stats.behind), AVG(stats.extendingLead), AVG(stats.comeback), AVG(stats.xpLead), AVG(stats.acesAndClean), AVG(stats.firstFullItem), AVG(stats.earlyFarmEfficiency), AVG(stats.csPerMinute), AVG(stats.xpPerMinute), AVG(stats.goldPerMinute), AVG(stats.legendaryItems), AVG(CASE WHEN stats.antiHealTime <> 0 THEN 1 ELSE 0 END), AVG(CASE WHEN stats.penetrationTime <> 0 THEN 1 ELSE 0 END), AVG(CASE WHEN stats.amplifierTime <> 0 THEN 1 ELSE 0 END), AVG(CASE WHEN stats.durabilityTime <> 0 THEN 1 ELSE 0 END), AVG(stats.startItemSold), AVG(stats.timeAlivePercent), AVG(stats.soloKillAdvantage), AVG(stats.firstKillTime), AVG(stats.firstKillDeathTime), AVG(stats.earlyGoldAdvantage), AVG(stats.earlyObjectiveAdvantage), AVG(stats.earlyObjectives), AVG(stats.turretplateAdvantage), AVG(stats.enemyControlAdvantage), AVG(stats.enemyControlled), AVG(stats.keyspellsUsed), AVG(stats.totalSpellBilance), AVG(stats.hitBilance), AVG(stats.dodgeBilance), AVG(stats.reactionBilance), AVG(stats.enemySpellReaction), AVG(stats.leadDifferenceAfterDiedEarly), AVG(stats.killParticipation), AVG(stats.trueKdaValue), AVG(stats.trueKdaKills), AVG(stats.trueKdaDeaths), AVG(stats.trueKdaAssists), AVG(stats.enemyControlAdvantageEarly), AVG(stats.enemyControlledEarly), AVG(CASE WHEN stats.csDropAtMinute <> 0 THEN 1 ELSE 0 END), AVG(stats.trinketEfficiency), AVG(stats.midgameGoldXPEfficiency), AVG(stats.lategameLead), AVG(CASE WHEN stats.behind IS true THEN stats.farmingFromBehind ELSE NULL END), AVG(CASE WHEN stats.behind IS true THEN stats.wardingFromBehind ELSE NULL END), AVG(CASE WHEN stats.behind IS true THEN stats.deathsFromBehind ELSE NULL END), AVG(CASE WHEN stats.behind IS true THEN stats.goldFromBehind ELSE NULL END), AVG(CASE WHEN stats.behind IS true THEN stats.xpFromBehind ELSE NULL END), AVG(CASE WHEN stats.ahead IS true THEN stats.goldFromBehind ELSE NULL END), AVG(CASE WHEN stats.ahead IS true THEN stats.xpFromBehind ELSE NULL END), AVG(stats.levelupEarlier), AVG(stats.pickAdvantage), AVG(stats.teamfightAmount), AVG(stats.teamfightParticipation), AVG(stats.averageDeathOrder), AVG(stats.teamfightWinrate), AVG(stats.teamfightDamageRate), AVG(stats.skirmishAmount), AVG(stats.skirmishParticipation), AVG(stats.skirmishKillsPerSkirmish), AVG(stats.skirmishWinrate), AVG(stats.skirmishDamageRate), AVG(stats.roamCreepScoreAdvantage), AVG(stats.roamGoldXpAdvantage), AVG (stats.roamGoldAdvantage), AVG(stats.roamObjectiveDamageAdvantage), AVG(stats.roamSuccessScore), AVG(stats.relativeDeathPositioning), AVG(stats.lanePositioning), AVG(stats.midgamePositioning), AVG(stats.lategamePositioning), AVG(stats.laneKillDeathPositioning), AVG(stats.laneKillPositioning), AVG(stats.splitScore), AVG(stats.companionScore), AVG(stats.roamScore), AVG(stats.secondsInCombat), AVG(stats.firstBase), AVG(stats.firstBaseThroughRecall), AVG(stats.leadThroughDeaths), AVG(stats.firstBaseEnemyControlled), AVG(stats.firstBaseLead), AVG(stats.firstBaseResetGold), AVG(stats.firstBaseGoldUnspent), AVG(stats.resetsThroughRecall), AVG(stats.plannedResets), AVG(stats.resets), AVG(stats.resetDuration), AVG(stats.resetGold), AVG(stats.resetGoldUnspent), AVG(stats.resetGoldGain), AVG(stats.resetsTogether), AVG(stats.secondBase), AVG(stats.consumablesPurchased), AVG(stats.resourceConservation), AVG(stats.secondBaseEnemyControlled), AVG(stats.earlyDamage), AVG(stats.wardsEarlygame), AVG(stats.earlyXpEfficiency), AVG(stats.earlyDamageTrading), AVG(stats.averageLaneHealth), AVG(stats.averageLaneResource), (AVG(CASE WHEN lane IN ('TOP','MID','BOT') THEN stats.pushes ELSE NULL END) + AVG(CASE WHEN lane IN ('TOP','MID','BOT') THEN stats.freezes ELSE NULL END) * 3 + AVG(CASE WHEN lane IN ('TOP','MID','BOT') THEN stats.holds ELSE NULL END))/3, AVG(stats.utilityScore), AVG(stats.leadWithoutDying), AVG(stats.proximity), AVG(stats.laneProximityDifference), AVG(teamperformance.elderTime), AVG(teamperformance.firstDragonTime), AVG(teamperformance.objectiveAtSpawn), AVG(teamperformance.baronTime), AVG(teamperformance.baronPowerplay), AVG(teamperformance.riftTurrets), AVG(teamperformance.riftOnMultipleTurrets), AVG(teamperformance.jungleTimeWasted), AVG(CASE WHEN teamperformance.firstDrake IS TRUE THEN 1 ELSE 0 END), AVG(CASE WHEN teamperformance.drakes IS 0 THEN 1 ELSE 0 END), AVG(CASE WHEN teamperformance.drakes > 0 THEN 1 ELSE 0 END), AVG(CASE WHEN teamperformance.drakes > 1 THEN 1 ELSE 0 END), AVG(CASE WHEN teamperformance.drakes > 2 THEN 1 ELSE 0 END), AVG(CASE WHEN teamperformance.drakes > 3 THEN 1 ELSE 0 END), AVG(CASE WHEN teamperformance.drakes > 4 THEN 1 ELSE 0 END), AVG(teamperformance.objectiveContests), AVG(teamperformance.towers), AVG(stats.midgameGoldEfficiency), AVG(CASE WHEN stats.ahead IS true THEN (CASE WHEN teamperformance.win IS true THEN 1 ELSE 0 END) ELSE NULL END), AVG(CASE WHEN stats.ahead IS true THEN (CASE WHEN stats.extendingLead IS true THEN 1 ELSE 0 END) ELSE NULL END), AVG(CASE WHEN stats.ahead IS true THEN (CASE WHEN stats.comeback IS true THEN 1 ELSE 0 END) ELSE NULL END), AVG(CASE WHEN stats.behind IS true THEN (CASE WHEN stats.comeback IS true THEN 1 ELSE 0 END) ELSE NULL END), AVG(teamperformance.game.duration), AVG(CASE WHEN teamperformance.surrendered IS TRUE THEN 1 ELSE 0 END), AVG((CAST(kills AS int) + CAST(assists AS int)) / deaths), AVG(CASE WHEN deaths <> 0 THEN ((CAST(kills AS int) + CAST(assists AS int)) / deaths) ELSE (kills + assists) END), AVG(CASE WHEN teamperformance.firstPick IS true THEN (CASE WHEN teamperformance.win IS true THEN 1 ELSE 0 END) ELSE NULL END), AVG(CASE WHEN teamperformance.firstPick IS false THEN (CASE WHEN teamperformance.win IS true THEN 1 ELSE 0 END) ELSE NULL END) " +
    "FROM Playerperformance p " +
    "WHERE teamperformance.game.gameStart >= :since " +
    "AND lane IN :lanes " +
    "AND (p.teamperformance.team IS NOT NULL OR teamperformance.game.gametype.id NOT BETWEEN 2 AND 699) " +
    "AND account = :account")

@NamedQuery(name = "Playerperformance.findAvg", query = "SELECT COUNT(id), AVG(qUsages), AVG(wUsages), AVG(eUsages), AVG(rUsages), AVG(spellsHit), AVG(spellsDodged), AVG(quickDodged), AVG(damageMagical), AVG(damagePhysical), AVG(damageTotal), AVG (damageTaken), AVG(damageMitigated), AVG(damageHealed), AVG(damageShielded), AVG(kills), AVG(deaths), AVG(assists), AVG(soloKills), AVG(levelUpAllin), AVG(doubleKills + tripleKills + quadraKills + pentaKills), AVG(aggressiveFlash), AVG(timeAlive), AVG(timeDead), AVG(teleportKills), AVG(immobilizations), AVG(controlWards), AVG(controlWardUptime), AVG(wardsPlaced), AVG(wardsCleared), AVG(guardedWards), AVG(visionScore), AVG(visionscoreAdvantage), AVG(objectivesStolen), AVG(firstturretAdvantage), AVG(objectivesDamage), AVG(baronExecutes), AVG(baronKills), AVG(buffsStolen), AVG(initialScuttles), AVG(totalScuttles), AVG(splitpushedTurrets), AVG(teamInvading), AVG(ganksEarly), AVG(ganksTotal), AVG(CASE WHEN ganksTop + ganksMid + ganksBot <> 0 THEN ((CAST(ganksTop AS int) - CAST(ganksBot AS int)) / (ganksTop + ganksMid + ganksBot)) ELSE 0 END), AVG(divesDone), AVG(divesSuccessful), AVG(divesGotten), AVG(divesProtected), AVG(goldTotal), AVG(bountyGold), AVG(experience), AVG(totalCreeps), AVG(earlyCreeps), AVG(invadedCreeps), AVG(earlyLaneLead), AVG(laneLead), AVG(turretplates), AVG(creepScoreAdvantage), AVG(itemsAmount), AVG(mejaisCompleted), AVG(firstBlood), AVG(outplayed), AVG(turretTakedowns), AVG(dragonTakedowns), AVG(fastestLegendary), AVG(gankSetups), AVG(initialBuffs), AVG(earlyKills), AVG(junglerKillsAtObjective), AVG(ambush), AVG(earlyTurrets), AVG(levelLead), AVG(picksMade), AVG(assassinated), AVG(savedAlly), AVG(survivedClose), AVG(stats.objectivesStolenAndContested), AVG(stats.objectivesKilledJunglerBefore), AVG(stats.baronTakedownsAttempts), AVG(stats.firstTrinketSwap), AVG(stats.firstWardTime), AVG(stats.firstControlwardTime), AVG(stats.controlWardInventoryTime), AVG(stats.turretParticipation), AVG(stats.invadingAndBuffs), AVG(stats.divesOwn), AVG(stats.divesEnemy), AVG(stats.divesDied), AVG(stats.teamDamage), AVG(stats.teamDamageTaken), AVG(stats.teamDamageMitigated), AVG(stats.bountyDifference), AVG(stats.duelWinrate), AVG(stats.duelWins), AVG(stats.deathsEarly), AVG(stats.kdEarly), AVG(stats.ahead), AVG(stats.behind), AVG(stats.extendingLead), AVG(stats.comeback), AVG(stats.xpLead), AVG(stats.acesAndClean), AVG(stats.firstFullItem), AVG(stats.earlyFarmEfficiency), AVG(stats.csPerMinute), AVG(stats.xpPerMinute), AVG(stats.goldPerMinute), AVG(stats.legendaryItems), AVG(CASE WHEN stats.antiHealTime <> 0 THEN 1 ELSE 0 END), AVG(CASE WHEN stats.penetrationTime <> 0 THEN 1 ELSE 0 END), AVG(CASE WHEN stats.amplifierTime <> 0 THEN 1 ELSE 0 END), AVG(CASE WHEN stats.durabilityTime <> 0 THEN 1 ELSE 0 END), AVG(stats.startItemSold), AVG(stats.timeAlivePercent), AVG(stats.soloKillAdvantage), AVG(stats.firstKillTime), AVG(stats.firstKillDeathTime), AVG(stats.earlyGoldAdvantage), AVG(stats.earlyObjectiveAdvantage), AVG(stats.earlyObjectives), AVG(stats.turretplateAdvantage), AVG(stats.enemyControlAdvantage), AVG(stats.enemyControlled), AVG(stats.keyspellsUsed), AVG(stats.totalSpellBilance), AVG(stats.hitBilance), AVG(stats.dodgeBilance), AVG(stats.reactionBilance), AVG(stats.enemySpellReaction), AVG(stats.leadDifferenceAfterDiedEarly), AVG(stats.killParticipation), AVG(stats.trueKdaValue), AVG(stats.trueKdaKills), AVG(stats.trueKdaDeaths), AVG(stats.trueKdaAssists), AVG(stats.enemyControlAdvantageEarly), AVG(stats.enemyControlledEarly), AVG(CASE WHEN stats.csDropAtMinute <> 0 THEN 1 ELSE 0 END), AVG(stats.trinketEfficiency), AVG(stats.midgameGoldXPEfficiency), AVG(stats.lategameLead), AVG(CASE WHEN stats.behind IS true THEN stats.farmingFromBehind ELSE NULL END), AVG(CASE WHEN stats.behind IS true THEN stats.wardingFromBehind ELSE NULL END), AVG(CASE WHEN stats.behind IS true THEN stats.deathsFromBehind ELSE NULL END), AVG(CASE WHEN stats.behind IS true THEN stats.goldFromBehind ELSE NULL END), AVG(CASE WHEN stats.behind IS true THEN stats.xpFromBehind ELSE NULL END), AVG(CASE WHEN stats.ahead IS true THEN stats.goldFromBehind ELSE NULL END), AVG(CASE WHEN stats.ahead IS true THEN stats.xpFromBehind ELSE NULL END), AVG(stats.levelupEarlier), AVG(stats.pickAdvantage), AVG(stats.teamfightAmount), AVG(stats.teamfightParticipation), AVG(stats.averageDeathOrder), AVG(stats.teamfightWinrate), AVG(stats.teamfightDamageRate), AVG(stats.skirmishAmount), AVG(stats.skirmishParticipation), AVG(stats.skirmishKillsPerSkirmish), AVG(stats.skirmishWinrate), AVG(stats.skirmishDamageRate), AVG(stats.roamCreepScoreAdvantage), AVG(stats.roamGoldXpAdvantage), AVG (stats.roamGoldAdvantage), AVG(stats.roamObjectiveDamageAdvantage), AVG(stats.roamSuccessScore), AVG(stats.relativeDeathPositioning), AVG(stats.lanePositioning), AVG(stats.midgamePositioning), AVG(stats.lategamePositioning), AVG(stats.laneKillDeathPositioning), AVG(stats.laneKillPositioning), AVG(stats.splitScore), AVG(stats.companionScore), AVG(stats.roamScore), AVG(stats.secondsInCombat), AVG(stats.firstBase), AVG(stats.firstBaseThroughRecall), AVG(stats.leadThroughDeaths), AVG(stats.firstBaseEnemyControlled), AVG(stats.firstBaseLead), AVG(stats.firstBaseResetGold), AVG(stats.firstBaseGoldUnspent), AVG(stats.resetsThroughRecall), AVG(stats.plannedResets), AVG(stats.resets), AVG(stats.resetDuration), AVG(stats.resetGold), AVG(stats.resetGoldUnspent), AVG(stats.resetGoldGain), AVG(stats.resetsTogether), AVG(stats.secondBase), AVG(stats.consumablesPurchased), AVG(stats.resourceConservation), AVG(stats.secondBaseEnemyControlled), AVG(stats.earlyDamage), AVG(stats.wardsEarlygame), AVG(stats.earlyXpEfficiency), AVG(stats.earlyDamageTrading), AVG(stats.averageLaneHealth), AVG(stats.averageLaneResource), (AVG(CASE WHEN lane IN ('TOP','MID','BOT') THEN stats.pushes ELSE NULL END) + AVG(CASE WHEN lane IN ('TOP','MID','BOT') THEN stats.freezes ELSE NULL END) * 3 + AVG(CASE WHEN lane IN ('TOP','MID','BOT') THEN stats.holds ELSE NULL END))/3, AVG(stats.utilityScore), AVG(stats.leadWithoutDying), AVG(stats.proximity), AVG(stats.laneProximityDifference), AVG(teamperformance.elderTime), AVG(teamperformance.firstDragonTime), AVG(teamperformance.objectiveAtSpawn), AVG(teamperformance.baronTime), AVG(teamperformance.baronPowerplay), AVG(teamperformance.riftTurrets), AVG(teamperformance.riftOnMultipleTurrets), AVG(teamperformance.jungleTimeWasted), AVG(CASE WHEN teamperformance.firstDrake IS TRUE THEN 1 ELSE 0 END), AVG(CASE WHEN teamperformance.drakes IS 0 THEN 1 ELSE 0 END), AVG(CASE WHEN teamperformance.drakes > 0 THEN 1 ELSE 0 END), AVG(CASE WHEN teamperformance.drakes > 1 THEN 1 ELSE 0 END), AVG(CASE WHEN teamperformance.drakes > 2 THEN 1 ELSE 0 END), AVG(CASE WHEN teamperformance.drakes > 3 THEN 1 ELSE 0 END), AVG(CASE WHEN teamperformance.drakes > 4 THEN 1 ELSE 0 END), AVG(teamperformance.objectiveContests), AVG(teamperformance.towers), AVG(stats.midgameGoldEfficiency), AVG(CASE WHEN stats.ahead IS true THEN (CASE WHEN teamperformance.win IS true THEN 1 ELSE 0 END) ELSE NULL END), AVG(CASE WHEN stats.ahead IS true THEN (CASE WHEN stats.extendingLead IS true THEN 1 ELSE 0 END) ELSE NULL END), AVG(CASE WHEN stats.ahead IS true THEN (CASE WHEN stats.comeback IS true THEN 1 ELSE 0 END) ELSE NULL END), AVG(CASE WHEN stats.behind IS true THEN (CASE WHEN stats.comeback IS true THEN 1 ELSE 0 END) ELSE NULL END), AVG(teamperformance.game.duration), AVG(CASE WHEN teamperformance.surrendered IS TRUE THEN 1 ELSE 0 END), AVG((CAST(kills AS int) + CAST(assists AS int)) / deaths), AVG(CASE WHEN deaths <> 0 THEN ((CAST(kills AS int) + CAST(assists AS int)) / deaths) ELSE (kills + assists) END), AVG(CASE WHEN teamperformance.firstPick IS true THEN (CASE WHEN teamperformance.win IS true THEN 1 ELSE 0 END) ELSE NULL END), AVG(CASE WHEN teamperformance.firstPick IS false THEN (CASE WHEN teamperformance.win IS true THEN 1 ELSE 0 END) ELSE NULL END) " +
    "FROM Playerperformance p " +
    "WHERE teamperformance.game.gameStart >= :since " +
    "AND lane IN :lanes")

@NamedQuery(name = "Playerperformance.findMax", query = "SELECT COUNT(id), MAX(qUsages), MAX(wUsages), MAX(eUsages), MAX(rUsages), MAX(spellsHit), MAX(spellsDodged), MAX(quickDodged), MAX(damageMagical), MAX(damagePhysical), MAX(damageTotal), MAX (damageTaken), MAX(damageMitigated), MAX(damageHealed), MAX(damageShielded), MAX(kills), MAX(deaths), MAX(assists), MAX(soloKills), MAX(levelUpAllin), MAX(doubleKills + tripleKills + quadraKills + pentaKills), MAX(aggressiveFlash), MAX(timeAlive), MAX(timeDead), MAX(teleportKills), MAX(immobilizations), MAX(controlWards), MAX(controlWardUptime), MAX(wardsPlaced), MAX(wardsCleared), MAX(guardedWards), MAX(visionScore), MAX(visionscoreAdvantage), MAX(objectivesStolen), MAX(firstturretAdvantage), MAX(objectivesDamage), MAX(baronExecutes), MAX(baronKills), MAX(buffsStolen), MAX(initialScuttles), MAX(totalScuttles), MAX(splitpushedTurrets), MAX(teamInvading), MAX(ganksEarly), MAX(ganksTotal), MAX(CASE WHEN ganksTop + ganksMid + ganksBot <> 0 THEN ((CAST(ganksTop AS int) - CAST(ganksBot AS int)) / (ganksTop + ganksMid + ganksBot)) ELSE 0 END), MAX(divesDone), MAX(divesSuccessful), MAX(divesGotten), MAX(divesProtected), MAX(goldTotal), MAX(bountyGold), MAX(experience), MAX(totalCreeps), MAX(earlyCreeps), MAX(invadedCreeps), MAX(earlyLaneLead), MAX(laneLead), MAX(turretplates), MAX(creepScoreAdvantage), MAX(itemsAmount), MAX(mejaisCompleted), 1, MAX(outplayed), MAX(turretTakedowns), MAX(dragonTakedowns), MAX(fastestLegendary), MAX(gankSetups), MAX(initialBuffs), MAX(earlyKills), MAX(junglerKillsAtObjective), MAX(ambush), MAX(earlyTurrets), MAX(levelLead), MAX(picksMade), MAX(assassinated), MAX(savedAlly), MAX(survivedClose), MAX(stats.objectivesStolenAndContested), MAX(stats.objectivesKilledJunglerBefore), MAX(stats.baronTakedownsAttempts), MAX(stats.firstTrinketSwap), MAX(stats.firstWardTime), MAX(stats.firstControlwardTime), MAX(stats.controlWardInventoryTime), MAX(stats.turretParticipation), MAX(stats.invadingAndBuffs), MAX(stats.divesOwn), MAX(stats.divesEnemy), MAX(stats.divesDied), MAX(stats.teamDamage), MAX(stats.teamDamageTaken), MAX(stats.teamDamageMitigated), MAX(stats.bountyDifference), MAX(stats.duelWinrate), MAX(stats.duelWins), MAX(stats.deathsEarly), MAX(stats.kdEarly), 1, 1, 1, 1, MAX(stats.xpLead), MAX(stats.acesAndClean), MAX(stats.firstFullItem), MAX(stats.earlyFarmEfficiency), MAX(stats.csPerMinute), MAX(stats.xpPerMinute), MAX(stats.goldPerMinute), MAX(stats.legendaryItems), 1, 1, 1, 1, MAX(stats.startItemSold), MAX(stats.timeAlivePercent), MAX(stats.soloKillAdvantage), MAX(stats.firstKillTime), MAX(stats.firstKillDeathTime), MAX(stats.earlyGoldAdvantage), MAX(stats.earlyObjectiveAdvantage), MAX(stats.earlyObjectives), MAX(stats.turretplateAdvantage), MAX(stats.enemyControlAdvantage), MAX(stats.enemyControlled), MAX(stats.keyspellsUsed), MAX(stats.totalSpellBilance), MAX(stats.hitBilance), MAX(stats.dodgeBilance), MAX(stats.reactionBilance), MAX(stats.enemySpellReaction), MAX(stats.leadDifferenceAfterDiedEarly), MAX(stats.killParticipation), MAX(stats.trueKdaValue), MAX(stats.trueKdaKills), MAX(stats.trueKdaDeaths), MAX(stats.trueKdaAssists), MAX(stats.enemyControlAdvantageEarly), MAX(stats.enemyControlledEarly), 1, MAX(stats.trinketEfficiency), MAX(stats.midgameGoldXPEfficiency), MAX(stats.lategameLead), MAX(CASE WHEN stats.behind IS true THEN stats.farmingFromBehind ELSE NULL END), MAX(CASE WHEN stats.behind IS true THEN stats.wardingFromBehind ELSE NULL END), MAX(CASE WHEN stats.behind IS true THEN stats.deathsFromBehind ELSE NULL END), MAX(CASE WHEN stats.behind IS true THEN stats.goldFromBehind ELSE NULL END), MAX(CASE WHEN stats.behind IS true THEN stats.xpFromBehind ELSE NULL END), MAX(CASE WHEN stats.ahead IS true THEN stats.goldFromBehind ELSE NULL END), MAX(CASE WHEN stats.ahead IS true THEN stats.xpFromBehind ELSE NULL END), MAX(stats.levelupEarlier), MAX(stats.pickAdvantage), MAX(stats.teamfightAmount), MAX(stats.teamfightParticipation), MAX(stats.averageDeathOrder), MAX(stats.teamfightWinrate), MAX(stats.teamfightDamageRate), MAX(stats.skirmishAmount), MAX(stats.skirmishParticipation), MAX(stats.skirmishKillsPerSkirmish), MAX(stats.skirmishWinrate), MAX(stats.skirmishDamageRate), MAX(stats.roamCreepScoreAdvantage), MAX(stats.roamGoldXpAdvantage), MAX (stats.roamGoldAdvantage), MAX(stats.roamObjectiveDamageAdvantage), MAX(stats.roamSuccessScore), MAX(stats.relativeDeathPositioning), MAX(stats.lanePositioning), MAX(stats.midgamePositioning), MAX(stats.lategamePositioning), MAX(stats.laneKillDeathPositioning), MAX(stats.laneKillPositioning), MAX(stats.splitScore), MAX(stats.companionScore), MAX(stats.roamScore), MAX(stats.secondsInCombat), MAX(stats.firstBase), 1, MAX(stats.leadThroughDeaths), MAX(stats.firstBaseEnemyControlled), MAX(stats.firstBaseLead), MAX(stats.firstBaseResetGold), MAX(stats.firstBaseGoldUnspent), MAX(stats.resetsThroughRecall), MAX(stats.plannedResets), MAX(stats.resets), MAX(stats.resetDuration), MAX(stats.resetGold), MAX(stats.resetGoldUnspent), MAX(stats.resetGoldGain), MAX(stats.resetsTogether), MAX(stats.secondBase), 1, MAX(stats.resourceConservation), MAX(stats.secondBaseEnemyControlled), MAX(stats.earlyDamage), MAX(stats.wardsEarlygame), MAX(stats.earlyXpEfficiency), MAX(stats.earlyDamageTrading), MAX(stats.averageLaneHealth), MAX(stats.averageLaneResource), (MAX(CASE WHEN lane IN ('TOP','MID','BOT') THEN stats.pushes ELSE NULL END) + MAX(CASE WHEN lane IN ('TOP','MID','BOT') THEN stats.freezes ELSE NULL END) * 3 + MAX(CASE WHEN lane IN ('TOP','MID','BOT') THEN stats.holds ELSE NULL END))/3, MAX(stats.utilityScore), MAX(stats.leadWithoutDying), MAX(stats.proximity), MAX(stats.laneProximityDifference), MAX(teamperformance.elderTime), MAX(teamperformance.firstDragonTime), MAX(teamperformance.objectiveAtSpawn), MAX(teamperformance.baronTime), MAX(teamperformance.baronPowerplay), MAX(teamperformance.riftTurrets), MAX(teamperformance.riftOnMultipleTurrets), MAX(teamperformance.jungleTimeWasted), 1, 1, 1, 1, 1, 1, 1, MAX(teamperformance.objectiveContests), MAX(teamperformance.towers), MAX(stats.midgameGoldEfficiency), MAX(CASE WHEN stats.ahead IS true THEN (CASE WHEN teamperformance.win IS true THEN 1 ELSE 0 END) ELSE NULL END), MAX(CASE WHEN stats.ahead IS true THEN (CASE WHEN stats.extendingLead IS true THEN 1 ELSE 0 END) ELSE NULL END), MAX(CASE WHEN stats.ahead IS true THEN (CASE WHEN stats.comeback IS true THEN 1 ELSE 0 END) ELSE NULL END), MAX(CASE WHEN stats.behind IS true THEN (CASE WHEN stats.comeback IS true THEN 1 ELSE 0 END) ELSE NULL END), MAX(teamperformance.game.duration), 1, MAX((CAST(kills AS int) + CAST(assists AS int)) / deaths), MAX(CASE WHEN deaths <> 0 THEN ((CAST(kills AS int) + CAST(assists AS int)) / deaths) ELSE (kills + assists) END), 1, 1, 1 " +
    "FROM Playerperformance p " +
    "WHERE teamperformance.game.gameStart >= :since " +
    "AND lane IN :lanes")

@NamedQuery(name = "Playerperformance.findMin", query = "SELECT COUNT(id), MIN(qUsages), MIN(wUsages), MIN(eUsages), MIN(rUsages), MIN(spellsHit), MIN(spellsDodged), MIN(quickDodged), MIN(damageMagical), MIN(damagePhysical), MIN(damageTotal), MIN (damageTaken), MIN(damageMitigated), MIN(damageHealed), MIN(damageShielded), MIN(kills), MIN(deaths), MIN(assists), MIN(soloKills), MIN(levelUpAllin), MIN(doubleKills + tripleKills + quadraKills + pentaKills), MIN(aggressiveFlash), MIN(timeAlive), MIN(timeDead), MIN(teleportKills), MIN(immobilizations), MIN(controlWards), MIN(controlWardUptime), MIN(wardsPlaced), MIN(wardsCleared), MIN(guardedWards), MIN(visionScore), MIN(visionscoreAdvantage), MIN(objectivesStolen), MIN(firstturretAdvantage), MIN(objectivesDamage), MIN(baronExecutes), MIN(baronKills), MIN(buffsStolen), MIN(initialScuttles), MIN(totalScuttles), MIN(splitpushedTurrets), MIN(teamInvading), MIN(ganksEarly), MIN(ganksTotal), MIN(CASE WHEN ganksTop + ganksMid + ganksBot <> 0 THEN ((CAST(ganksTop AS int) - CAST(ganksBot AS int)) / (ganksTop + ganksMid + ganksBot)) ELSE 0 END), MIN(divesDone), MIN(divesSuccessful), MIN(divesGotten), MIN(divesProtected), MIN(goldTotal), MIN(bountyGold), MIN(experience), MIN(totalCreeps), MIN(earlyCreeps), MIN(invadedCreeps), MIN(earlyLaneLead), MIN(laneLead), MIN(turretplates), MIN(creepScoreAdvantage), MIN(itemsAmount), MIN(mejaisCompleted), 0, MIN(outplayed), MIN(turretTakedowns), MIN(dragonTakedowns), MIN(fastestLegendary), MIN(gankSetups), MIN(initialBuffs), MIN(earlyKills), MIN(junglerKillsAtObjective), MIN(ambush), MIN(earlyTurrets), MIN(levelLead), MIN(picksMade), MIN(assassinated), MIN(savedAlly), MIN(survivedClose), MIN(stats.objectivesStolenAndContested), MIN(stats.objectivesKilledJunglerBefore), MIN(stats.baronTakedownsAttempts), MIN(stats.firstTrinketSwap), MIN(stats.firstWardTime), MIN(stats.firstControlwardTime), MIN(stats.controlWardInventoryTime), MIN(stats.turretParticipation), MIN(stats.invadingAndBuffs), MIN(stats.divesOwn), MIN(stats.divesEnemy), MIN(stats.divesDied), MIN(stats.teamDamage), MIN(stats.teamDamageTaken), MIN(stats.teamDamageMitigated), MIN(stats.bountyDifference), MIN(stats.duelWinrate), MIN(stats.duelWins), MIN(stats.deathsEarly), MIN(stats.kdEarly), 0, 0, 0, 0, MIN(stats.xpLead), MIN(stats.acesAndClean), MIN(stats.firstFullItem), MIN(stats.earlyFarmEfficiency), MIN(stats.csPerMinute), MIN(stats.xpPerMinute), MIN(stats.goldPerMinute), MIN(stats.legendaryItems), 0, 0, 0, 0, MIN(stats.startItemSold), MIN(stats.timeAlivePercent), MIN(stats.soloKillAdvantage), MIN(stats.firstKillTime), MIN(stats.firstKillDeathTime), MIN(stats.earlyGoldAdvantage), MIN(stats.earlyObjectiveAdvantage), MIN(stats.earlyObjectives), MIN(stats.turretplateAdvantage), MIN(stats.enemyControlAdvantage), MIN(stats.enemyControlled), MIN(stats.keyspellsUsed), MIN(stats.totalSpellBilance), MIN(stats.hitBilance), MIN(stats.dodgeBilance), MIN(stats.reactionBilance), MIN(stats.enemySpellReaction), MIN(stats.leadDifferenceAfterDiedEarly), MIN(stats.killParticipation), MIN(stats.trueKdaValue), MIN(stats.trueKdaKills), MIN(stats.trueKdaDeaths), MIN(stats.trueKdaAssists), MIN(stats.enemyControlAdvantageEarly), MIN(stats.enemyControlledEarly), 0, MIN(stats.trinketEfficiency), MIN(stats.midgameGoldXPEfficiency), MIN(stats.lategameLead), MIN(CASE WHEN stats.behind IS true THEN stats.farmingFromBehind ELSE NULL END), MIN(CASE WHEN stats.behind IS true THEN stats.wardingFromBehind ELSE NULL END), MIN(CASE WHEN stats.behind IS true THEN stats.deathsFromBehind ELSE NULL END), MIN(CASE WHEN stats.behind IS true THEN stats.goldFromBehind ELSE NULL END), MIN(CASE WHEN stats.behind IS true THEN stats.xpFromBehind ELSE NULL END), MIN(CASE WHEN stats.ahead IS true THEN stats.goldFromBehind ELSE NULL END), MIN(CASE WHEN stats.ahead IS true THEN stats.xpFromBehind ELSE NULL END), MIN(stats.levelupEarlier), MIN(stats.pickAdvantage), MIN(stats.teamfightAmount), MIN(stats.teamfightParticipation), MIN(stats.averageDeathOrder), MIN(stats.teamfightWinrate), MIN(stats.teamfightDamageRate), MIN(stats.skirmishAmount), MIN(stats.skirmishParticipation), MIN(stats.skirmishKillsPerSkirmish), MIN(stats.skirmishWinrate), MIN(stats.skirmishDamageRate), MIN(stats.roamCreepScoreAdvantage), MIN(stats.roamGoldXpAdvantage), MIN (stats.roamGoldAdvantage), MIN(stats.roamObjectiveDamageAdvantage), MIN(stats.roamSuccessScore), MIN(stats.relativeDeathPositioning), MIN(stats.lanePositioning), MIN(stats.midgamePositioning), MIN(stats.lategamePositioning), MIN(stats.laneKillDeathPositioning), MIN(stats.laneKillPositioning), MIN(stats.splitScore), MIN(stats.companionScore), MIN(stats.roamScore), MIN(stats.secondsInCombat), MIN(stats.firstBase), 0, MIN(stats.leadThroughDeaths), MIN(stats.firstBaseEnemyControlled), MIN(stats.firstBaseLead), MIN(stats.firstBaseResetGold), MIN(stats.firstBaseGoldUnspent), MIN(stats.resetsThroughRecall), MIN(stats.plannedResets), MIN(stats.resets), MIN(stats.resetDuration), MIN(stats.resetGold), MIN(stats.resetGoldUnspent), MIN(stats.resetGoldGain), MIN(stats.resetsTogether), MIN(stats.secondBase), 0, MIN(stats.resourceConservation), MIN(stats.secondBaseEnemyControlled), MIN(stats.earlyDamage), MIN(stats.wardsEarlygame), MIN(stats.earlyXpEfficiency), MIN(stats.earlyDamageTrading), MIN(stats.averageLaneHealth), MIN(stats.averageLaneResource), (MIN(CASE WHEN lane IN ('TOP','MID','BOT') THEN stats.pushes ELSE NULL END) + MIN(CASE WHEN lane IN ('TOP','MID','BOT') THEN stats.freezes ELSE NULL END) * 3 + MAX(CASE WHEN lane IN ('TOP','MID','BOT') THEN stats.holds ELSE NULL END))/3, MIN(stats.utilityScore), MIN(stats.leadWithoutDying), MIN(stats.proximity), MIN(stats.laneProximityDifference), MIN(teamperformance.elderTime), MIN(teamperformance.firstDragonTime), MIN(teamperformance.objectiveAtSpawn), MIN(teamperformance.baronTime), MIN(teamperformance.baronPowerplay), MIN(teamperformance.riftTurrets), MIN(teamperformance.riftOnMultipleTurrets), MIN(teamperformance.jungleTimeWasted), 0, 0, 0, 0, 0, 0, 0, MIN(teamperformance.objectiveContests), MIN(teamperformance.towers), MIN(stats.midgameGoldEfficiency), MIN(CASE WHEN stats.ahead IS true THEN (CASE WHEN teamperformance.win IS true THEN 1 ELSE 0 END) ELSE NULL END), MIN(CASE WHEN stats.ahead IS true THEN (CASE WHEN stats.extendingLead IS true THEN 1 ELSE 0 END) ELSE NULL END), MIN(CASE WHEN stats.ahead IS true THEN (CASE WHEN stats.comeback IS true THEN 1 ELSE 0 END) ELSE NULL END), MIN(CASE WHEN stats.behind IS true THEN (CASE WHEN stats.comeback IS true THEN 1 ELSE 0 END) ELSE NULL END), MIN(teamperformance.game.duration), 0, MIN(CASE WHEN deaths <> 0 THEN ((CAST(kills AS int) + CAST(assists AS int)) / deaths) ELSE (kills + assists) END), 0, 0, 0 " +
    "FROM Playerperformance p " +
    "WHERE teamperformance.game.gameStart >= :since " +
    "AND lane IN :lanes")

@NamedQuery(name = "Playerperformance.gamesOnLane", query = "SELECT SUM(CASE WHEN lane='TOP' THEN 1 ELSE 0 END), SUM(CASE WHEN lane='JUNGLE' THEN 1 ELSE 0 END), SUM(CASE WHEN lane='MIDDLE' THEN 1 ELSE 0 END), SUM(CASE WHEN lane='BOTTOM' THEN 1 ELSE 0 END), SUM(CASE WHEN lane='UTILITY' THEN 1 ELSE 0 END) " +
    "FROM Playerperformance p " +
    "WHERE teamperformance.game.gameStart >= :since " +
    "AND (teamperformance.team IS NOT NULL OR teamperformance.game.gametype.id NOT BETWEEN 2 AND 699) " +
    "AND account = :account")
@NamedQuery(name = "Playerperformance.gamesOnLaneRecently", query = "SELECT COUNT(id) " +
    "FROM Playerperformance p " +
    "WHERE teamperformance.game.gameStart >= :since " +
    "AND account = :account " +
    "AND lane = :lane")

@NamedQuery(name = "Playerperformance.championsPickedCompet", query = "SELECT championOwn.id " +
    "FROM Playerperformance p " +
    "WHERE teamperformance.game.gameStart >= :since " +
    "AND account = :account " +
    "AND lane = :lane " +
    "AND teamperformance.game.gametype.id NOT BETWEEN 2 AND 699")
@NamedQuery(name = "Playerperformance.championsPickedCompetitive", query = "SELECT championOwn.id " +
    "FROM Playerperformance p " +
    "WHERE teamperformance.game.gameStart >= :since " +
    "AND account = :account " +
    "AND lane = :lane " +
    "AND (teamperformance.team IS NOT NULL OR teamperformance.game.gametype.id NOT BETWEEN 2 AND 699)")
@NamedQuery(name = "Playerperformance.championsPickedOther", query = "SELECT championOwn.id FROM Playerperformance p " +
    "WHERE teamperformance.game.gameStart >= :since " +
    "AND account = :account " +
    "AND lane = :lane")
@NamedQuery(name = "Playerperformance.championWins", query = "SELECT COUNT(id), SUM(CASE WHEN teamperformance.win IS true THEN 1 ELSE 0 END) " +
    "FROM Playerperformance p " +
    "WHERE teamperformance.game.gameStart >= :since " +
    "AND account = :account " +
    "AND lane = :lane " +
    "AND championOwn.id = :championId")
@NamedQuery(name = "Playerperformance.championValues", query = "SELECT MIN(championOwn), AVG(damagePhysical), AVG(damageMagical), AVG(damageTotal), AVG(immobilizations), AVG(gankSetups) " +
    "FROM Playerperformance p " +
    "GROUP BY championOwn")
@NamedQuery(name = "Playerperformance.averageChampionValues", query = "SELECT AVG(damagePhysical), AVG(damageMagical), AVG(damageTotal), AVG(immobilizations), AVG(gankSetups) " +
    "FROM Playerperformance p")
@NamedQuery(name = "Playerperformance.matchupPlayer", query = "SELECT MIN(championEnemy), COUNT(championOwn), " +
    "SUM(CASE WHEN teamperformance.win IS true THEN 1 ELSE 0 END) " +
    "FROM Playerperformance p " +
    "WHERE teamperformance.game.gameStart >= :since " +
    "AND championOwn = :picked " +
    "AND account IN :accounts " +
    "GROUP BY championEnemy")
@NamedQuery(name = "Playerperformance.matchup",
    query = "SELECT MIN(CASE WHEN championEnemy = :picked THEN championOwn ELSE championEnemy END), " +
        "COUNT(championOwn), " +
        "SUM(CASE WHEN championEnemy = :picked THEN " +
        "(CASE WHEN teamperformance.win IS true THEN 0 ELSE 1 END) ELSE " +
        "(CASE WHEN teamperformance.win IS true THEN 1 ELSE 0 END) END) " +
        "FROM Playerperformance p " +
        "WHERE teamperformance.game.gameStart >= :since " +
        "AND (championOwn = :picked OR championEnemy <> NULL AND championEnemy = :picked) " +
        "GROUP BY (CASE WHEN championEnemy IS :picked THEN championOwn ELSE championEnemy END)")
@NamedQuery(name = "Playerperformance.forPlayer", query = "FROM Playerperformance  p WHERE account = :account AND teamperformance.game.gameStart >= :since ORDER BY id DESC")
@NamedQuery(name = "Playerperformance.forChampion", query = "FROM Playerperformance p WHERE championOwn = :champion AND teamperformance.team <> NULL ORDER BY id DESC")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Playerperformance implements Serializable {

  @Transient
  private static final long serialVersionUID = 6290895798073708343L;
  private static Map<Lane, Map<String, Value>> values;

  public static void update() {
    values = new HashMap<>();
    for (Lane lane : Lane.values()) {
      if (!lane.toString().startsWith("UNKNOWN") || lane.equals(Lane.UNKNOWN)) {
        values.put(lane, update(lane));
      }
    }
  }

  //<editor-fold desc="Queries">
  private static List<String> names = Arrays.asList(
      "count", "qUsages", "wUsages", "eUsages", "rUsages", "spellsHit", "spellsDodged", "quickDodged", "damageMagical", "damagePhysical",
      "damageTotal", "damageTaken", "damageMitigated", "damageHealed", "damageShielded", "kills", "deaths", "assists", "soloKills",
      "levelUpAllin", "multiKills", "aggressiveFlash", "timeAlive", "timeDead", "teleportKills", "immobilizations", "controlWards",
      "controlWardUptime", "wardsPlaced", "wardsCleared", "guardedWards", "visionScore", "visionscoreAdvantage", "objectivesStolen",
      "firstturretAdvantage", "objectivesDamage", "baronExecutes", "baronKills", "buffsStolen", "initialScuttles", "totalScuttles",
      "splitpushedTurrets", "teaminvading", "ganksEarly", "ganksTotal", "gankPriority", "divesDone", "divesSuccessful", "divesGotten",
      "divesProtected", "goldTotal", "bountyGold", "experience", "totalCreeps", "earlyCreeps", "invadedCreeps", "earlyLaneLead",
      "laneLead", "turretplates", "creepScoreAdvantage", "itemsAmount", "mejaisCompleted", "firstBlood", "outplayed", "turretTakedowns",
      "dragonTakedowns", "fastestLegendary", "gankSetups", "initialBuffs", "earlyKills", "junglerKillsAtObjective", "ambush",
      "earlyTurrets", "levelLead", "picksMade", "assassinated", "savedAlly", "survivedClose", "objectivesStolenAndContested",
      "objectivesKilledJunglerBefore", "baronTakedownsAttempts", "firstTrinketSwap", "firstWardTime", "firstControlwardTime",
      "controlWardInventoryTime", "turretParticipation", "invadingAndBuffs", "divesOwn", "divesEnemy", "divesDied", "teamDamage",
      "teamDamageTaken", "teamDamageMitigated", "bountyDifference", "duelWinrate", "duelWins", "deathsEarly", "kdEarly", "ahead", "behind",
      "extendingLead", "comeback", "xpLead", "acesAndClean", "firstFullItem", "earlyFarmEfficiency", "csPerMinute", "xpPerMinute",
      "goldPerMinute", "legendaryItems", "antiHealing", "penetration", "damageBuild", "resistanceBuild", "startItemSold",
      "timeAlivePercent", "soloKillAdvantage", "firstKillTime", "firstKillDeathTime", "earlyGoldAdvantage", "earlyObjectiveAdvantage",
      "earlyObjectives", "turretplateAdvantage", "enemyControlAdvantage", "enemyControlled", "keyspellsUsed", "totalSpellBilance",
      "hitBilance", "dodgeBilance", "reactionBilance", "enemySpellReaction", "leadDifferenceAfterDiedEarly", "killParticipation",
      "trueKdaValue", "trueKdaKills", "trueKdaDeaths", "trueKdaAssists", "enemyControlAdvantageEarly", "enemyControlledEarly", "farmstop",
      "trinketEfficiency", "midgameGoldXPEfficiency", "lategameLead", "farmingFromBehind", "wardingFromBehind", "deathsFromBehind",
      "goldFromBehind", "xpFromBehind", "goldFromAhead", "xpFromAhead", "levelupEarlier", "pickAdvantage", "teamfightAmount",
      "teamfightParticipation", "averageDeathOrder", "teamfightWinrate", "teamfightDamageRate", "skirmishAmount", "skirmishParticipation",
      "skirmishKillsPerSkirmish", "skirmishWinrate", "skirmishDamageRate", "roamCreepScoreAdvantage", "roamGoldXpAdvantage",
      "roamGoldAdvantage", "roamObjectiveDamageAdvantage", "roamSuccessScore", "relativeDeathPositioning", "lanePositioning",
      "midgamePositioning", "lategamePositioning", "laneKillDeathPositioning", "laneKillPositioning", "splitScore", "companionScore",
      "roamScore", "secondsInCombat", "firstBase", "firstBaseThroughRecall", "leadThroughDeaths", "firstBaseEnemyControlled",
      "firstBaseLead", "firstBaseResetGold", "firstBaseGoldUnspent", "resetsThroughRecall", "plannedResets", "resets", "resetDuration",
      "resetGold", "resetGoldUnspent", "resetGoldGain", "resetsTogether", "secondBase", "consumablesPurchased", "resourceConservation",
      "secondBaseEnemyControlled", "earlyDamage", "wardsEarlygame", "earlyXpEfficiency", "earlyDamageTrading", "averageLaneHealth",
      "averageLaneResource", "waveState", "utilityScore", "leadWithoutDying", "proximity", "laneProximityDifference", "elderTime",
      "dragonTime", "objectiveAfterSpawn", "baronTime", "baronPowerplay", "heraldTurrets", "heraldMulticharge", "jungleTimeWasted",
      "firstDrake", "noDrakes", "oneDrake", "twoDrakes", "threeDrakes", "fourDrakes", "moreDrakes", "objectiveContests", "towers",
      "midgameGoldEfficiency", "winsAhead", "aheadExtending", "aheadComeback", "behindComeback", "duration", "surrender", "kDA", "winrate",
      "blueWinrate", "redWinrate");

  public static Map<String, Double> loadPlayerRatings(Account account, Lane lane) {
    Map<String, Double> vals = new HashMap<>();
    Object[] avg = HibernateUtil.stats(lane, account);
    for (int i = 0; i < avg.length; i++) {
      Double average = avg[i] == null ? 0 : Double.parseDouble(avg[i].toString());
      String name = names.get(i);
      vals.put(name, average);
    }
    return vals;
  }

  public static Map<String, Value> update(Lane lane) {
    Object[] avg = HibernateUtil.stats("Playerperformance.findAvg", lane);
    Object[] max = HibernateUtil.stats("Playerperformance.findMax", lane);
    Object[] min = HibernateUtil.stats("Playerperformance.findMin", lane);


    Map<String, Value> vals = new HashMap<>();
    for (int i = 0; i < avg.length; i++) {
      Double average = avg[i] == null ? 0 : Double.parseDouble(avg[i].toString());
      Double highest = max[i] == null ? 0 : Double.parseDouble(max[i].toString());
      Double lowest = min[i] == null ? 0 : Double.parseDouble(min[i].toString());
      Value value = new Value(lowest, average, highest);

      String name = names.get(i);
      vals.put(name, value);
    }
    return vals;
  }

  public static Map<Lane, Map<String, Value>> getValues() {
    if (values == null) {
      update();
    }
    return values;
  }

  public static Set<Playerperformance> get() {
    return new LinkedHashSet<>(HibernateUtil.findList(Playerperformance.class));
  }

  public static Playerperformance get(Playerperformance neu, Teamperformance performance, Account account) {
    if (has(performance, account)) {
      return find(performance, account);
    }
    performCreate(neu, performance, account);
    return find(performance, neu.getAccount());
  }

  public static Playerperformance get(Playerperformance neu, Teamperformance performance, Account account, Lane lane) {
    if (has(performance, lane)) {
      return find(performance, lane);
    }
    neu.setLane(lane);
    performCreate(neu, performance, account);
    return find(performance, neu.getAccount());
  }

  private static void performCreate(Playerperformance neu, Teamperformance performance, Account account) {
    account.getPlayerperformances().add(neu);
    performance.getPlayerperformances().add(neu);
    neu.setTeamperformance(performance);
    neu.setAccount(account);
    PrimeData.getInstance().save(neu);
  }

  public static boolean has(Teamperformance teamperformance, Account account) {
    return HibernateUtil.has(Playerperformance.class, new String[]{"teamperformance", "account"}, new Object[]{teamperformance, account});
  }

  public static boolean has(Teamperformance teamperformance, Lane lane) {
    return HibernateUtil.has(Playerperformance.class, new String[]{"teamperformance", "lane"}, new Object[]{teamperformance, lane}, "findByLane");
  }

  public static Playerperformance find(Teamperformance teamperformance, Lane lane) {
    return HibernateUtil.find(Playerperformance.class, new String[]{"teamperformance", "lane"}, new Object[]{teamperformance, lane}, "findByLane");
  }

  public static Playerperformance find(Teamperformance teamperformance, Account account) {
    return HibernateUtil.find(Playerperformance.class, new String[]{"teamperformance", "account"}, new Object[]{teamperformance, account});
  }
  //</editor-fold>

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "playerperformance_id", nullable = false)
  private int id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "teamperformance")
  @ToString.Exclude
  private Teamperformance teamperformance;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "account")
  @ToString.Exclude
  private Account account;

  @Enumerated(EnumType.STRING)
  @Column(name = "lane", nullable = false, length = 8)
  private Lane lane;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "champion_own")
  @ToString.Exclude
  private Champion championOwn;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "champion_enemy")
  @ToString.Exclude
  private Champion championEnemy;

  @Column(name = "q_usages", nullable = false)
  private short qUsages;

  @Column(name = "w_usages", nullable = false)
  private short wUsages;

  @Column(name = "e_usages", nullable = false)
  private short eUsages;

  @Column(name = "r_usages", nullable = false)
  private short rUsages;

  @Column(name = "spells_hit")
  private short spellsHit;

  @Column(name = "spells_dodged")
  private short spellsDodged;

  @Column(name = "quick_dodged")
  private short quickDodged;

  @Column(name = "damage_magical", nullable = false)
  private int damageMagical;

  @Column(name = "damage_physical", nullable = false)
  private int damagePhysical;

  @Column(name = "damage_total", nullable = false)
  private int damageTotal;

  @Column(name = "damage_taken", nullable = false)
  private int damageTaken;

  @Column(name = "damage_mitigated", nullable = false)
  private int damageMitigated;

  @Column(name = "damage_healed", nullable = false)
  private int damageHealed;

  @Column(name = "damage_shielded", nullable = false)
  private int damageShielded;

  @Column(name = "kills", nullable = false)
  private byte kills;

  @Column(name = "deaths", nullable = false)
  private byte deaths;

  @Column(name = "assists", nullable = false)
  private byte assists;

  @Column(name = "kills_solo")
  private byte soloKills;

  @Check(constraints = "allin_levelup IS NULL OR allin_levelup < 18")
  @Column(name = "allin_levelup")
  private byte levelUpAllin;

  @Column(name = "kills_multi_double", nullable = false)
  private byte doubleKills;

  @Column(name = "kills_multi_triple", nullable = false)
  private byte tripleKills;

  @Column(name = "kills_multi_quadra", nullable = false)
  private byte quadraKills;

  @Column(name = "kills_multi_penta", nullable = false)
  private byte pentaKills;

  @Column(name = "flash_aggressive")
  private byte aggressiveFlash;

  @Column(name = "time_alive", nullable = false)
  private short timeAlive;

  @Column(name = "time_dead", nullable = false)
  private short timeDead;

  @Column(name = "kills_teleport")
  private byte teleportKills;

  @Column(name = "immobilizations", columnDefinition = "TINYINT UNSIGNED")
  private short immobilizations;

  @Column(name = "wards_control", nullable = false)
  private byte controlWards;

  @Column(name = "wards_control_coverage", columnDefinition = "TINYINT UNSIGNED")
  private short controlWardUptime;

  @Column(name = "wards_placed", columnDefinition = "TINYINT UNSIGNED NOT NULL")
  private short wardsPlaced;

  @Column(name = "wards_cleared", columnDefinition = "TINYINT UNSIGNED NOT NULL")
  private short wardsCleared;

  @Column(name = "wards_guarded")
  private byte guardedWards;

  @Column(name = "visionscore", columnDefinition = "TINYINT UNSIGNED NOT NULL")
  private short visionScore;

  @Column(name = "visionscore_advantage")
  private byte visionscoreAdvantage;

  @Column(name = "objectives_stolen", nullable = false)
  private byte objectivesStolen;

  @Column(name = "firstturret_advantage")
  private short firstturretAdvantage;

  @Column(name = "objectives_damage", nullable = false)
  private int objectivesDamage;

  @Column(name = "baron_executes")
  private byte baronExecutes;

  @Column(name = "baron_kills", nullable = false)
  private byte baronKills;

  @Column(name = "buffs_stolen")
  private byte buffsStolen;

  @Check(constraints = "scuttles_initial IS NULL OR scuttles_initial <= 2")
  @Column(name = "scuttles_initial")
  private byte initialScuttles;

  @Column(name = "scuttles_total")
  private byte totalScuttles;

  @Check(constraints = "turrets_splitpushed IS NULL OR turrets_splitpushed <= 11")
  @Column(name = "turrets_splitpushed")
  private byte splitpushedTurrets;

  @Column(name = "team_invading")
  private byte teamInvading;

  @Column(name = "ganks_early")
  private byte ganksEarly;

  @Column(name = "ganks_total", nullable = false)
  private byte ganksTotal;

  @Column(name = "ganks_top", nullable = false)
  private byte ganksTop;

  @Column(name = "ganks_mid", nullable = false)
  private byte ganksMid;

  @Column(name = "ganks_bot", nullable = false)
  private byte ganksBot;

  @Column(name = "dives_done")
  private byte divesDone;

  @Column(name = "dives_successful")
  private byte divesSuccessful;

  @Column(name = "dives_gotten")
  private byte divesGotten;

  @Column(name = "dives_protected")
  private byte divesProtected;

  @Column(name = "gold_total", columnDefinition = "SMALLINT UNSIGNED NOT NULL")
  private int goldTotal;

  @Column(name = "gold_bounty")
  private short bountyGold;

  @Column(name = "experience_total", columnDefinition = "SMALLINT UNSIGNED NOT NULL")
  private int experience;

  @Column(name = "creeps_total", nullable = false)
  private short totalCreeps;

  @Column(name = "creeps_early", columnDefinition = "TINYINT UNSIGNED")
  private short earlyCreeps;

  @Column(name = "creeps_invade", columnDefinition = "TINYINT UNSIGNED NOT NULL")
  private short invadedCreeps;

  @Column(name = "early_lane_lead")
  private short earlyLaneLead;

  @Column(name = "lane_lead")
  private short laneLead;

  @Check(constraints = "turretplates IS NULL OR turretplates <= 15")
  @Column(name = "turretplates")
  private byte turretplates;

  @Column(name = "flamehorizon_advantage")
  private short creepScoreAdvantage;

  @Column(name = "items_amount", nullable = false, columnDefinition = "TINYINT UNSIGNED NOT NULL")
  private short itemsAmount;

  @Column(name = "mejais_completed")
  private short mejaisCompleted;

  @Column(name = "first_blood", nullable = false)
  private boolean firstBlood;

  @Column(name = "outplayed_opponent")
  private byte outplayed;

  @Column(name = "turret_takedowns", nullable = false)
  private byte turretTakedowns;

  @Column(name = "dragon_takedowns")
  private byte dragonTakedowns;

  @Deprecated
  @Column(name = "fastest_legendary")
  private short fastestLegendary;

  @Column(name = "gank_setups")
  private byte gankSetups;

  @Column(name = "buffs_initial")
  private byte initialBuffs;

  @Column(name = "kills_early")
  private byte earlyKills;

  @Column(name = "objective_junglerkill")
  private byte junglerKillsAtObjective;

  @Column(name = "ambush_kill")
  private byte ambush;

  @Column(name = "turrets_early")
  private byte earlyTurrets;

  @Deprecated
  @Column(name = "experience_advantage")
  private byte levelLead;

  @Column(name = "pick_kill")
  private byte picksMade;

  @Column(name = "assassination")
  private byte assassinated;

  @Column(name = "guard_ally")
  private byte savedAlly;

  @Column(name = "survived_close")
  private byte survivedClose;

  @ManyToMany
  @JoinTable(name = "playerperformance_rune",
      joinColumns = @JoinColumn(name = "playerperformance"),
      inverseJoinColumns = @JoinColumn(name = "rune"),
      indexes = @Index(name = "idx_performancerune", columnList = "playerperformance, rune", unique = true))
  @ToString.Exclude
  private final Set<Rune> runes = new LinkedHashSet<>();

  @OneToMany(mappedBy = "playerperformance")
  @ToString.Exclude
  private final Set<PlayerperformanceSummonerspell> summonerspells = new LinkedHashSet<>();

  @OneToMany(mappedBy = "playerperformance")
  @ToString.Exclude
  private final Set<PlayerperformanceItem> items = new LinkedHashSet<>();

  @OneToMany(mappedBy = "playerperformance")
  @ToString.Exclude
  private final Set<PlayerperformanceInfo> infos = new LinkedHashSet<>();

  @OneToMany(mappedBy = "playerperformance")
  @ToString.Exclude
  private final Set<PlayerperformanceKill> killEvents = new LinkedHashSet<>();

  @OneToMany(mappedBy = "playerperformance")
  @ToString.Exclude
  private final Set<PlayerperformanceObjective> objectives = new LinkedHashSet<>();

  @OneToMany(mappedBy = "playerperformance")
  @ToString.Exclude
  private final Set<PlayerperformanceLevel> levelups = new LinkedHashSet<>();

  /**
   * Stats that need other metrics and cannot be calculated
   * <p>
   * and Stats that need advanced calculation
   * <p>
   * Note: Don't implement stats that require multiple games
   */
  @Embedded
  private PlayerperformanceStats stats;

  public Playerperformance(Lane lane, short qUsages, short wUsages, short eUsages, short rUsages, int damageMagical, int damagePhysical,
                           int damageTotal, int damageTaken, int damageMitigated, int damageHealed, int damageShielded, byte kills,
                           byte deaths, byte assists, byte doubleKills, byte tripleKills, byte quadraKills, byte pentaKills,
                           short timeAlive, short timeDead, short wardsPlaced, byte objectivesStolen, int objectivesDamage,
                           byte baronKills, int goldTotal, int experience, short totalCreeps, short itemsAmount,
                           boolean firstBlood, byte controlWards, byte wardsCleared, short visionScore, byte turretTakedowns) {
    this.lane = lane;
    this.qUsages = qUsages;
    this.wUsages = wUsages;
    this.eUsages = eUsages;
    this.rUsages = rUsages;
    this.damageMagical = damageMagical;
    this.damagePhysical = damagePhysical;
    this.damageTotal = damageTotal;
    this.damageTaken = damageTaken;
    this.damageMitigated = damageMitigated;
    this.damageHealed = damageHealed;
    this.damageShielded = damageShielded;
    this.kills = kills;
    this.deaths = deaths;
    this.assists = assists;
    this.doubleKills = doubleKills;
    this.tripleKills = tripleKills;
    this.quadraKills = quadraKills;
    this.pentaKills = pentaKills;
    this.timeAlive = timeAlive;
    this.timeDead = timeDead;
    this.wardsPlaced = wardsPlaced;
    this.objectivesStolen = objectivesStolen;
    this.objectivesDamage = objectivesDamage;
    this.baronKills = baronKills;
    this.goldTotal = goldTotal;
    this.experience = experience;
    this.totalCreeps = totalCreeps;
    this.itemsAmount = itemsAmount;
    this.firstBlood = firstBlood;
    this.controlWards = controlWards;
    this.wardsCleared = wardsCleared;
    this.visionScore = visionScore;
    this.turretTakedowns = turretTakedowns;
  }

  public double getTimeIngame() {
    return timeDead / (teamperformance.getGame().getDuration() + 0.0);
  }

  public PlayerperformanceItem addItem(Item item, boolean remains, int buyTime) {
    val playerperformanceItem = new PlayerperformanceItem(this, item, remains);
    playerperformanceItem.setBuyTime(buyTime);
    return PlayerperformanceItem.get(playerperformanceItem);
  }

  public void addRune(Rune rune) {
    runes.add(rune);
    rune.getPlayerperformances().add(this);
  }

  public PlayerperformanceSummonerspell addSummonerspell(Summonerspell summonerspell, byte amount) {
    val performanceSpell = new PlayerperformanceSummonerspell(this, summonerspell, amount);
    return PlayerperformanceSummonerspell.get(performanceSpell);
  }

  public PlayerperformanceInfo addInfo(PlayerperformanceInfo info) {
    return PlayerperformanceInfo.get(info, this);
  }

  public PlayerperformanceKill addKill(PlayerperformanceKill kill) {
    return PlayerperformanceKill.get(kill, this);
  }

  public PlayerperformanceObjective addObjective(PlayerperformanceObjective objective) {
    return PlayerperformanceObjective.get(objective, this);
  }

  public PlayerperformanceLevel addLevelup(PlayerperformanceLevel levelup) {
    return PlayerperformanceLevel.get(levelup, this);
  }

  public PlayerperformanceItem getMythic() {
    return items.stream().filter(item -> item.getItem().getType().equals(ItemType.MYTHIC)).findFirst().orElse(null);
  }

  public PlayerperformanceItem getTrinket() {
    return items.stream().filter(item -> item.getItem().getType().equals(ItemType.TRINKET)).findFirst().orElse(null);
  }

  public Rune getKeystone() {
    return runes.stream().filter(rune -> rune.getSlot() == 0).findFirst().orElse(null);
  }

  public String getKillParticipation() {
    return Math.round((kills + assists) * 100.0 / teamperformance.getTotalKills()) + "%";
  }

  public String getCSPerMinute() {
    return Math.round(stats.getCsPerMinute() * 10) / 10 + "";
  }

  public byte largestMultiKill() {
    if (pentaKills > 0) {
      return 5;
    } else if (quadraKills > 0) {
      return 4;
    } else if (tripleKills > 0) {
      return 3;
    } else if (doubleKills > 0) {
      return 2;
    } else if (kills > 0) {
      return 1;
    }
    return 0;
  }

  public double getKDA() {
    return (kills + assists) * 1d / deaths;
  }

  public short getAbilityUses() {
    return (short) (qUsages + wUsages + eUsages + rUsages);
  }

  public double getTankyShare() {
    return damageTaken * 1d / teamperformance.getTotalDamageTaken();
  }

  public byte countLegendaryItems() {
    return (byte) items.stream().filter(item -> item.getItem().getType().equals(ItemType.LEGENDARY)).count();
  }

  public boolean hasMythic() {
    return getMythic() != null;
  }

  public Gametype getGameType() {
    return teamperformance.getGame().getGametype();
  }

  public Playerperformance getLaneOpponent() {
    return teamperformance.getOtherTeamperformance().getPlayerperformances().stream()
        .filter(playerperformance -> playerperformance.getLane().equals(lane))
        .findFirst().orElse(null);
  }

  public PlayerperformanceInfo getInfoAt(int minute) {
    return infos.stream().filter(info -> info.getMinute() == minute).findFirst()
        .orElse(infos.stream().reduce((first, second) -> second).orElse(null));
  }

  public List<Champion> getPresentChampions() {
    return teamperformance.getGame().getChampionSelections().stream().map(ChampionSelection::getChampion).collect(Collectors.toList());
  }

  public List<String> getItemsEnded() {
    final List<String> collect = items.stream().filter(PlayerperformanceItem::remains).map(PlayerperformanceItem::getItem)
        .sorted((item1, item2) -> item2.getCost() - item1.getCost()).map(Item::getImage)
        .collect(Collectors.toList());
    final List<String> items = collect.size() <= 7 ? collect : collect.subList(0, 7);
    while (items.size() < 7) {
      items.add("http://ddragon.leagueoflegends.com/cdn/5.5.1/img/ui/items.png");
    }
    return items;
  }

  public boolean wasPresent(Champion champion) {
    return getPresentChampions().contains(champion);
  }

  public boolean isCompetitive() {
    return teamperformance.getGame().isCompetitive();
  }

  public boolean isCompetitiveLike() {
    return (teamperformance.getTeam() != null || teamperformance.getGame().isCompetitive()) && teamperformance.getGame().isRecently();
  }

  public boolean isRecently() {
    return teamperformance.getGame().isRecently();
  }

  public boolean isVeryRecently() {
    return teamperformance.getGame().isVeryRecently();
  }

  public int getSoulratePerfect() {
    return (teamperformance.isPerfectSoul() ? 1 : 0) * 5 + (teamperformance.getSoul() != null ? 1 : 0);
  }

  public String getKDAString() {
    return kills + "/" + deaths + "/" + assists + " (" + Math.round(stats.getTrueKdaValue() * 10 ) / 10d + ")";
  }

  public String getKDAStringLong() {
    return kills + "/" + deaths + "/" + assists;
  }

  public String getKDAStringShort() {
    return Math.round(stats.getTrueKdaValue() * 10 ) / 10d + "";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    final Playerperformance that = (Playerperformance) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}