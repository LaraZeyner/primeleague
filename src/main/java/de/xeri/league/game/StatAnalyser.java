package de.xeri.league.game;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.xeri.league.game.events.fight.Duel;
import de.xeri.league.game.events.fight.Fight;
import de.xeri.league.game.events.fight.Gank;
import de.xeri.league.game.events.fight.Kill;
import de.xeri.league.game.events.fight.Pick;
import de.xeri.league.game.events.fight.Skirmish;
import de.xeri.league.game.events.fight.Teamfight;
import de.xeri.league.game.events.items.ItemStack;
import de.xeri.league.game.events.items.Reset;
import de.xeri.league.game.events.location.MapArea;
import de.xeri.league.game.events.location.Position;
import de.xeri.league.game.models.JSONPlayer;
import de.xeri.league.game.models.JSONTeam;
import de.xeri.league.game.models.TimelineStat;
import de.xeri.league.models.dynamic.Item;
import de.xeri.league.models.enums.EventTypes;
import de.xeri.league.models.enums.ItemType;
import de.xeri.league.models.enums.Lane;
import de.xeri.league.models.enums.StoredStat;
import de.xeri.league.models.enums.WardType;
import de.xeri.league.models.match.Teamperformance;
import de.xeri.league.models.match.playerperformance.JunglePath;
import de.xeri.league.models.match.playerperformance.Playerperformance;
import de.xeri.league.models.match.playerperformance.PlayerperformanceInfo;
import de.xeri.league.models.match.playerperformance.PlayerperformanceLevel;
import de.xeri.league.models.match.playerperformance.PlayerperformanceStats;
import de.xeri.league.util.Const;
import de.xeri.league.util.Util;
import lombok.val;
import lombok.var;
import org.json.JSONObject;

/**
 * Created by Lara on 08.04.2022 for web
 */
public final class StatAnalyser {
  private final GameAnalyser game;

  public StatAnalyser(GameAnalyser game) {
    this.game = game;
  }

  public void handlePlayerStats(Playerperformance playerperformance, Teamperformance teamperformance, List<JSONTeam> jsonTeams,
                                JSONPlayer player, List<Fight> fights) {
    long millis = System.currentTimeMillis();
    final int pId = player.getId() + 1;
    val stats = new PlayerperformanceStats(playerperformance);
    val enemyPlayer = player.getEnemy();
    val jsonTeam = jsonTeams.get(teamperformance.isFirstPick() ? 0 : 1);
    val enemyTeam = jsonTeams.get(teamperformance.isFirstPick() ? 1 : 0);

    player.buildInventory();
    final List<Reset> resets = player.getInventory().getResets();

    final long recalls = resets.stream().filter(Reset::wasRecall).count();
    final double resetsThroughRecall = Util.div(recalls, resets.size());
    stats.setResetsThroughRecall(BigDecimal.valueOf(resetsThroughRecall));
    stats.setResets((short) resets.size());

    final long recallsPlanned = resets.stream()
        .filter(Reset::wasRecall)
        .filter(reset -> reset.getGoldUnspent() < Const.RESET_PLANNED_LIMIT).count();
    final double resetsPlanned = Util.div(recallsPlanned, resets.size());
    stats.setPlannedResets(BigDecimal.valueOf(resetsPlanned));

    final double averageGoldAmount = resets.stream()
        .filter(Objects::nonNull)
        .mapToInt(Reset::getGoldPreReset)
        .average().orElse(0);
    stats.setResetGold((short) averageGoldAmount);
    final double averageGoldUnspent = resets.stream()
        .filter(Objects::nonNull)
        .mapToInt(Reset::getGoldUnspent)
        .average().orElse(0);
    stats.setResetGoldUnspent((short) averageGoldUnspent);

    double averageResetDuration = resets.stream()
        .filter(Objects::nonNull)
        .mapToInt(Reset::getDuration)
        .average().orElse(0);
    if (averageResetDuration > 225_000) {
      averageResetDuration = 225_000;
    }
    if (averageResetDuration > 75_000) {
      averageResetDuration /= 3;
    }
    stats.setResetDuration((int) averageResetDuration);

    final int goldLost = resets.stream()
        .filter(Objects::nonNull)
        .mapToInt(reset -> player.getLeadDifferenceAt(reset.getStart() / 60_000, reset.getEnd() / 60_000 + 1, TimelineStat.TOTAL_GOLD))
        .sum();
    stats.setResetGoldGain((short) goldLost);

    val firstBase = player.getInventory().getResets().isEmpty() ? null : player.getInventory().getResets().get(0);
    if (firstBase != null) {
      final boolean firstBaseRecall = firstBase.wasRecall();
      stats.setFirstBaseThroughRecall(firstBaseRecall);

      final int start = firstBase.getStart();
      stats.setFirstBase((short) (start / 1000));

      final int underControl = player.getStatAt(start / 60_000, TimelineStat.ENEMY_CONTROLLED) / 1000;
      stats.setFirstBaseEnemyControlled((short) underControl);

      final int lead = player.getLeadAt(start / 60_000, TimelineStat.LEAD);
      stats.setFirstBaseLead((short) lead);

      final int resetGold = firstBase.getGoldPreReset();
      stats.setFirstBaseResetGold((short) resetGold);

      final int unspentGold = firstBase.getGoldUnspent();
      stats.setFirstBaseGoldUnspent((short) unspentGold);
    }

    val secondBase = player.getInventory().getResets().size() < 2 ? null : player.getInventory().getResets().get(1);
    if (secondBase != null) {

      final int start = secondBase.getStart();
      stats.setSecondBase((short) (start / 1000));

      // Resources
      double discrepance = 0;
      int amount = 0;
      if (firstBase != null && firstBase.getStart() != 0) {
        final double startpool = player.getPool(0);
        final double endpool = firstBase.getPool();
        final int minutes = firstBase.getStart() / 60_000;
        for (int min = 1; min < firstBase.getStart() / 60_000; min++) {
          final double pool = player.getPool(min);
          final double expectedPool = (startpool - endpool) * Util.div(min, minutes);
          if (pool < expectedPool) {
            discrepance += expectedPool - pool;
          }
          amount++;
        }

        if (secondBase.getStart() != 0) {
          final double startP = player.getPool(firstBase.getEnd() / 60_000 + 1);
          final double endP = secondBase.getPool();
          final int mins = secondBase.getStart() / 60_000 - firstBase.getEnd() / 60_000 + 1;
          for (int min = firstBase.getEnd() / 60_000 + 2; min < secondBase.getStart() / 60_000; min++) {
            final double pool = player.getPool(min);
            final double expectedPool = (startP - endP) * ((min - firstBase.getEnd() / 60_000.0 + 1) / mins);
            if (pool < expectedPool) {
              discrepance += expectedPool - pool;
            }
            amount++;
          }
        }
      }
      final double resourceConservation = Util.div(discrepance, amount);
      stats.setResourceConservation(BigDecimal.valueOf(1 - resourceConservation));

      // Consumables
      final List<ItemStack> items = player.getInventory().getItemsAt(secondBase.getEnd());
      final boolean hasConsumable = !items.isEmpty() &&
          items.stream().anyMatch(itemStack -> itemStack.getItem() != null && itemStack.getItem().getType() != null &&
              itemStack.getItem().getType().equals(ItemType.CONSUMABLE));
      stats.setConsumablesPurchased(hasConsumable);

      final int underControl = player.getStatAt(start / 60_000, TimelineStat.ENEMY_CONTROLLED) / 1000;
      stats.setSecondBaseEnemyControlled((short) underControl);

      // Damage
      final int earlyDamage = player.getStatAt(start / 60_000, TimelineStat.DAMAGE);
      final double damagePercentage = Util.div(earlyDamage, playerperformance.getDamageTotal());
      stats.setEarlyDamage(BigDecimal.valueOf(damagePercentage));
    }

    final int amount = (int) resets.stream()
        .filter(reset -> player.getTeam().getAllPlayers().stream()
            .anyMatch(teamPlayer -> teamPlayer != player && teamPlayer.getInventory().getResets().stream()
                .anyMatch(r -> Math.abs(r.getStart() - reset.getStart()) <= 45_000)))
        .count();
    final double resetsTogether = Util.div(amount, resets.size());
    stats.setResetsTogether(BigDecimal.valueOf(resetsTogether));


    final byte allObjectivesAmount = determineAllObjectivesAmount(jsonTeams);
    stats.setObjectivesStolenAndContested(playerperformance, allObjectivesAmount);
    stats.setObjectivesKilledJunglerBefore(playerperformance, allObjectivesAmount);

    final byte stolenBarons = determineStolenBarons(jsonTeam, enemyTeam);
    stats.setBaronTakedownsAttempts(playerperformance, stolenBarons);

    final int wardsEarlygame = (int) player.getEvents(EventTypes.WARD_PLACED).stream()
        .filter(event -> event.getInt("timestamp") <= Const.EARLYGAME_UNTIL_MINUTE * 60_000).count();
    stats.setWardsEarlygame((short) wardsEarlygame);

    final short firstWardTime = searchForFirstWardTime(player);
    if (firstWardTime != 0) {
      stats.setFirstWardTime(firstWardTime);
    }

    val controlPlacements = searchForControlPlacements(player);
    if (!controlPlacements.isEmpty()) {
      stats.setFirstControlwardTime(controlPlacements.get(0));
    }

    final short firstTrinketSwap = searchForTrinketSwap(player);
    stats.setFirstTrinketSwap(firstTrinketSwap);

    val yellowPlacementTimes = searchForTrinketPlacementsUntilSwap(player, firstTrinketSwap);
    final int twoChargesUp = searchForRechargeTimes(firstTrinketSwap, yellowPlacementTimes);
    final double twoChargesUpPercentage = Util.div(twoChargesUp, firstTrinketSwap * 1000);
    stats.setTrinketEfficiency(BigDecimal.valueOf(1 - twoChargesUpPercentage));

    val purchases = searchForControlPurchase(player);
    final short averageControlTime = (short) IntStream.range(0, controlPlacements.size())
        .filter(Objects::nonNull)
        .filter(i -> purchases.size() > i)
        .map(i -> controlPlacements.get(i) - purchases.get(i))
        .boxed().collect(Collectors.toCollection(ArrayList::new))
        .stream().mapToInt(Integer::intValue)
        .average().orElse(0);
    stats.setControlWardInventoryTime(averageControlTime);

    final int totalMitigation = jsonTeam.getAllPlayers().stream()
        .filter(Objects::nonNull)
        .mapToInt(p -> p.getMedium(StoredStat.DAMAGE_MITIGATED))
        .sum();
    stats.setTeamDamageMitigated(playerperformance, totalMitigation);

    boolean wasAhead = false;
    boolean wasBehind = false;
    int behindAheadStart = 0;
    int behindAheadEnd = 0;
    boolean comeback = false;

    int slightlyBehindStart = 0;
    int slightlyBehindEndWithoutDying = 0;
    int xpLead = player.getLeadAt(player.getLastMinute(), TimelineStat.EXPERIENCE);

    for (int minute = 0; minute < player.getLastMinute(); minute++) {
      final int lead = player.getLeadAt(minute, TimelineStat.LEAD);

      if (minute <= Const.EARLYGAME_UNTIL_MINUTE) {
        // noch nicht ahead
        if (!wasAhead && lead >= Const.AHEAD_LEAD) {
          wasAhead = true;
          behindAheadStart = minute;

          // noch nicht behind
        } else if (!wasBehind && lead <= (Const.AHEAD_LEAD * -1)) {
          wasBehind = true;
          behindAheadStart = minute;
        }

        if (slightlyBehindStart == 0 && lead <= (Const.KILL_BOUNTY * -1)) {
          slightlyBehindStart = minute;
        }

        if (slightlyBehindEndWithoutDying == 0 && slightlyBehindStart != 0 && lead >= 0) {
          slightlyBehindEndWithoutDying = minute;
        }
      }

      if (wasAhead && lead < 0 ||
          wasBehind && lead > 0) {
        behindAheadEnd = minute;
        comeback = true;
      }
    }

    // Behind endete nie
    if (behindAheadStart != 0 && behindAheadEnd == 0) {
      behindAheadEnd = player.getLastMinute();
    }
    stats.setAhead(wasAhead);
    stats.setBehind(wasBehind);
    stats.setComeback(comeback);
    stats.setXpLead((short) xpLead);

    if (slightlyBehindStart != 0 && slightlyBehindStart != Const.EARLYGAME_UNTIL_MINUTE && slightlyBehindEndWithoutDying == 0) {
      slightlyBehindEndWithoutDying = Const.EARLYGAME_UNTIL_MINUTE;
    }

    for (Fight fight : fights) {
      if (fight.isDying(player) && fight.isInsideMinutes(player, slightlyBehindStart, slightlyBehindEndWithoutDying)) {
        slightlyBehindEndWithoutDying = fight.getStart(player) / 60_000;
      }
    }

    if (slightlyBehindStart != slightlyBehindEndWithoutDying) {
      final int leadWithoutDying = player.getLeadDifferenceAt(slightlyBehindStart, slightlyBehindEndWithoutDying, TimelineStat.LEAD);
      stats.setLeadWithoutDying((short) leadWithoutDying);
    }


    int deathsFromBehind = 0;
    short bountyDrop = 0;
    int deathsEarly = 0;
    int firstKillTime = 0;
    int firstDeathTime = 0;

    val killBounties = new ArrayList<Short>();
    val assistBounties = new ArrayList<Short>();

    if (enemyPlayer != null) {
      for (JSONObject event : enemyPlayer.getEvents(EventTypes.CHAMPION_KILL)) {
        final Kill kill = Kill.getKillFromEvent(event);
        if (kill != null) {
          if (kill.getVictim() == enemyPlayer.getId() + 1) {
            int timestamp = event.getInt("timestamp");
            // from ahead
            if (timestamp / 60_000 > behindAheadStart && timestamp / 60_000 < behindAheadEnd) {
              deathsFromBehind--;
            }
          }
        }
      }
    }

    val deathPositioning = new HashMap<Integer, Double>();
    val killPositioning = new HashMap<Integer, Double>();
    int lead = 0;
    int deathsAmount = 0;
    for (JSONObject event : player.getEvents(EventTypes.CHAMPION_KILL)) {
      final Kill kill = Kill.getKillFromEvent(event);
      if (kill != null) {
        final short shutdownBounty = (short) (event.has("shutdownBounty") ? event.getInt("shutdownBounty") : 0);
        final short bounty = (short) event.getInt("bounty");
        int timestamp = event.getInt("timestamp");

        final Position position = kill.getPosition();
        final double relativePosition = position.getTotalAggression(player.isFirstPick());
        if (kill.getVictim() == pId) {
          if (timestamp / 60_000 + 1 <= player.getLastMinute()) {
            lead += player.getLeadDifferenceAt(timestamp / 60_000, timestamp / 60_000 + 1, TimelineStat.LEAD);
            deathsAmount ++;
          }

          //handle First Death time
          if (firstDeathTime == 0) {
            firstDeathTime = timestamp / 1000;
          }

          if (timestamp / 60_000 <= Const.EARLYGAME_UNTIL_MINUTE) {
            deathsEarly++;
          }

          // Bounties
          killBounties.add((short) (kill.getGold() * -1));
          bountyDrop += shutdownBounty;

          // from behind
          if (timestamp / 60_000 > behindAheadStart && timestamp / 60_000 < behindAheadEnd) {
            deathsFromBehind++;
          }

          //Positioning
          deathPositioning.put(timestamp, relativePosition);

        } else if (kill.getKiller() == pId) {
          //handle First Kill time
          if (firstKillTime == 0) {
            firstKillTime = timestamp / 1000;
          }

          // Bounties
          killBounties.add((short) kill.getGold());

          //Positioning
          killPositioning.put(timestamp, relativePosition);

        } else {
          // Bounties
          final double factor = determineAssistbountyFactor(timestamp);
          final int totalAssistBounty = bounty == Const.KILL_BOUNTY_FIRST_BLOOD ? Const.ASSIST_BOUNTY_FIRST_BLOOD :
              (int) (bounty * factor);
          final int participantAmount = event.getJSONArray("assistingParticipantIds").toList().size();
          final int assistBounty = (int) Util.div(totalAssistBounty, participantAmount);
          assistBounties.add((short) assistBounty);

          //Positioning
          killPositioning.put(timestamp, relativePosition);
        }
      }
    }

    if (!deathPositioning.isEmpty()) {
      final double averageDeathPosition = deathPositioning.values().stream()
          .filter(Objects::nonNull)
          .mapToDouble(Double::doubleValue)
          .average().orElse(0);
      if (averageDeathPosition != 0) {
        stats.setRelativeDeathPositioning(BigDecimal.valueOf(averageDeathPosition));
      }


      if (!killPositioning.isEmpty()) {
        final double laneKillPositioning = killPositioning.keySet().stream()
            .filter(Objects::nonNull)
            .filter(milli -> milli < Const.EARLYGAME_UNTIL_MINUTE * 60_000)
            .mapToDouble(killPositioning::get)
            .average().orElse(0);

        int laneKillDeaths = (int) killPositioning.keySet().stream()
            .filter(Objects::nonNull)
            .filter(milli -> milli < Const.EARLYGAME_UNTIL_MINUTE * 60_000)
            .count();

        if (laneKillPositioning != 0) {
          stats.setLaneKillPositioning(BigDecimal.valueOf(laneKillPositioning));
        }

        final double laneDeathPositioning = deathPositioning.keySet().stream()
            .filter(Objects::nonNull)
            .filter(milli -> milli < Const.EARLYGAME_UNTIL_MINUTE * 60_000)
            .mapToDouble(deathPositioning::get)
            .average().orElse(0);

        laneKillDeaths += (int) deathPositioning.keySet().stream()
            .filter(Objects::nonNull)
            .filter(milli -> milli < Const.EARLYGAME_UNTIL_MINUTE * 60_000)
            .count();

        if (laneKillDeaths > 0) {
          stats.setLaneKillDeathPositioning(BigDecimal.valueOf((laneKillPositioning + laneDeathPositioning) / laneKillDeaths));
        }

      }
    }

    stats.setLeadThroughDeaths((short) (Util.div(lead, deathsAmount)));


    if (firstDeathTime < 300) {
      int deathMinute = firstDeathTime / 60;
      final int leadAt = player.getLeadAt(deathMinute, TimelineStat.LEAD);
      final int leadAtEnd = player.getLeadAt(Const.EARLYGAME_UNTIL_MINUTE, TimelineStat.LEAD);
      stats.setLeadDifferenceAfterDiedEarly((short) (leadAtEnd - leadAt));
    }

    if (behindAheadStart != 0) {
      handleFromBehind(player, stats, behindAheadStart, behindAheadEnd, deathsFromBehind);
    }

    stats.setFirstKillTime((short) firstKillTime, (short) firstDeathTime);


    final Short startItemSold = determineStartItem(player);
    if (startItemSold != null) {
      stats.setStartItemSold(startItemSold);
    }


    final double kills = killBounties.stream().filter(b -> b > 0).mapToInt(b -> b).sum() * 1d / Const.KILL_BOUNTY;
    final double deaths = killBounties.stream().filter(b -> b < 0).mapToInt(b -> b).sum() * -1.0 / Const.KILL_BOUNTY;
    final double assists = assistBounties.stream().mapToInt(b -> b).sum() * 1d / Const.ASSIST_BOUNTY;
    stats.setTrueKda(kills, deaths, assists);


    byte objectivesEarlyWe = 0;
    byte objectivesEarlyEnemy = 0;
    for (JSONObject event : game.getAllEvents()) {
      val type = EventTypes.valueOf(event.getString("type"));
      if (type.equals(EventTypes.ELITE_MONSTER_KILL) || type.equals(EventTypes.BUILDING_KILL)) {
        int timestamp = event.getInt("timestamp");
        if (timestamp <= 14 * 60_000) {
          final int killerTeamId = event.has("killerTeamId") ? event.getInt("killerTeamId") : event.getInt("teamId");
          if (killerTeamId == jsonTeam.getId() * 100) {
            objectivesEarlyWe++;
          } else {
            objectivesEarlyEnemy++;
          }
        }
      }
    }
    stats.setEarlyObjectiveRate(objectivesEarlyWe, objectivesEarlyEnemy);


    byte turretPlatesWe = 0;
    byte turretPlatesEnemy = 0;
    for (JSONObject event : game.getAllEvents()) {
      val type = EventTypes.valueOf(event.getString("type"));
      if (type.equals(EventTypes.TURRET_PLATE_DESTROYED) && playerperformance.getLane() != null) {
        final int killerTeamId = event.getInt("teamId");
        val laneString = event.getString("laneType");
        var laneType = playerperformance.getLane().getType();
        if (laneString.equals(laneType)) {
          if (killerTeamId == jsonTeam.getId() * 100) {
            turretPlatesWe++;
          } else {
            turretPlatesEnemy++;
          }
        }

      }
    }
    playerperformance.setTurretplates(turretPlatesWe);
    stats.setTurretplateAdvantage((byte) (turretPlatesWe - turretPlatesEnemy));

    final int minute = player.getLastMinute();
    if (minute > 2 && wasAhead) {
      final int leadAt = player.getLeadAt(minute - 3, TimelineStat.LEAD);
      final boolean leadExtend = leadAt - Const.AHEAD_LEAD > Const.AHEAD_LEAD_EXTEND;
      stats.setExtendingLead(leadExtend);
    }

    stats.setDeathsEarly(playerperformance, (byte) deathsEarly);
    stats.setBountyDifference(playerperformance, bountyDrop);

    double csAt10 = 0;
    if (player.getLastMinute() >= 10) {
      csAt10 = player.getStatPerMinute(10, TimelineStat.CREEP_SCORE);
    }

    val positionList = new ArrayList<Double>();
    IntStream.range(0, game.getHighestMinute() + 1).forEach(i -> positionList.add(null));
    for (PlayerperformanceInfo info : playerperformance.getInfos()) {
      final Position position = player.getPositionAt(info.getMinute());
      final double aggression = position.getTotalAggression(player.isFirstPick());
      positionList.set(info.getMinute(), aggression);

      if (info.getMinute() > 10) {
        final double csPerMinute = player.getStatPerMinute(info.getMinute(), TimelineStat.CREEP_SCORE);
        if (csAt10 > 0 && csPerMinute < csAt10 * 0.8) {
          stats.setCsDropAtMinute(info.getMinute());
          break;
        }
      }
    }

    if (player.getLastMinute() >= Const.EARLYGAME_UNTIL_MINUTE) {
      final double csEarly = player.getStatAt(Const.EARLYGAME_UNTIL_MINUTE, TimelineStat.CREEP_SCORE) * 1d / Const.EARLYGAME_CS;
      stats.setEarlyFarmEfficiency(BigDecimal.valueOf(csEarly));

      final double xpEarly = player.getStatAt(Const.EARLYGAME_UNTIL_MINUTE, TimelineStat.EXPERIENCE) * 1d / Const.EARLYGAME_XP;
      stats.setEarlyXpEfficiency(BigDecimal.valueOf(xpEarly));

      final int earlyDamageDifference = player.getLeadAt(Const.EARLYGAME_UNTIL_MINUTE, TimelineStat.DAMAGE);
      stats.setEarlyDamageTrading((short) earlyDamageDifference);

      final double averageHealth = IntStream.range(2, Const.EARLYGAME_UNTIL_MINUTE)
          .filter(Objects::nonNull)
          .mapToDouble(min -> player.getStatPercentage(min, TimelineStat.CURRENT_HEALTH))
          .average().orElse(0);
      stats.setAverageLaneHealth(BigDecimal.valueOf(averageHealth));

      final double averageResource = IntStream.range(2, Const.EARLYGAME_UNTIL_MINUTE)
          .filter(Objects::nonNull)
          .mapToDouble(min -> player.getStatPercentage(min, TimelineStat.CURRENT_RESOURCE))
          .average().orElse(0);
      stats.setAverageLaneResource(BigDecimal.valueOf(averageResource));
    }

    if (player.getLastMinute() >= 10) {
      stats.setEarlyGoldAdvantage((short) player.getLeadAt(10, TimelineStat.TOTAL_GOLD));
    }

    int waveStatusPush = 0;
    int waveStatusFreeze = 0;
    int waveStatusHold = 0;
    int max = Math.min(Const.EARLYGAME_UNTIL_MINUTE, player.getLastMinute());
    for (int i = 2; i < max; i++) {
      if (player.getLane().isInArea(player.getPositionAt(i), player.isFirstPick())) {
        if (player.getPositionAt(i).getTotalAggression(player.isFirstPick()) > (player.getLane().equals(Lane.JUNGLE) ? 0.5 : 0)) {
          waveStatusPush++;
        } else if (player.getPositionAt(i).getTotalAggression(player.isFirstPick()) < (player.getLane().equals(Lane.JUNGLE) ? -0.5 :
            -0.25)) {
          waveStatusFreeze++;
        } else {
          waveStatusHold++;
        }
      }
    }
    stats.setPushes((byte) waveStatusPush);
    stats.setFreezes((byte) waveStatusFreeze);
    stats.setHolds((byte) waveStatusHold);

    final int endEarly = Math.min(positionList.size(), Const.EARLYGAME_UNTIL_MINUTE);
    final double earlyPosition = positionList.subList(1, endEarly).stream()
        .filter(Objects::nonNull)
        .mapToDouble(Double::doubleValue).average().orElse(0);
    if (earlyPosition != 0) {
      stats.setLanePositioning(BigDecimal.valueOf(earlyPosition));
    }

    if (positionList.size() > Const.EARLYGAME_UNTIL_MINUTE) {
      final int end = Math.min(positionList.size(), Const.MIDGAME_UNTIL_MINUTE);
      final double midPosition = positionList.subList(Const.EARLYGAME_UNTIL_MINUTE, end).stream()
          .filter(Objects::nonNull)
          .mapToDouble(Double::doubleValue).average().orElse(0);
      if (midPosition != 0) {
        stats.setMidgamePositioning(BigDecimal.valueOf(midPosition));
      }
    }


    if (positionList.size() > Const.MIDGAME_UNTIL_MINUTE) {
      final double latePosition = positionList.subList(Const.MIDGAME_UNTIL_MINUTE, positionList.size()).stream()
          .filter(Objects::nonNull)
          .mapToDouble(Double::doubleValue)
          .average().orElse(0);
      if (latePosition != 0) {
        stats.setLategamePositioning(BigDecimal.valueOf(latePosition));
      }
    }


    getMidgameStats(player, stats, player.getLastMinute());
    handleControlled(player, minute, stats);

    if (enemyPlayer != null) {
      byte earlierLevelups = 0;
      determineLevelups(playerperformance, enemyPlayer, earlierLevelups, stats);

      final Short spellsHit = enemyPlayer.getSmall(StoredStat.SPELL_LANDED);
      if (spellsHit != null) {
        stats.setSpellDodge(playerperformance, spellsHit, enemyPlayer.getSmall(StoredStat.SPELL_DODGE),
            enemyPlayer.getSmall(StoredStat.SPELL_DODGE_QUICK));
      }
    }

    val myGanks = new ArrayList<Gank>();
    val enemyGanks = new ArrayList<Gank>();
    int duelsWon = 0;
    int duelsLost = 0;
    byte pickAdvantage = 0;
    val myTeamfights = new ArrayList<Teamfight>();
    val mySkirmishes = new ArrayList<Skirmish>();
    int allSkirmishesAmount = 0;
    int allTeamfightsAmount = 0;
    for (Fight fight : fights) {
      if (fight instanceof Teamfight) {
        allTeamfightsAmount++;
      }

      if (fight instanceof Skirmish) {
        allSkirmishesAmount++;
      }

      if (fight.isInvolved(pId)) {
        if (playerperformance.getLane() != null) {
          if (fight.isGankOf(playerperformance.getLane(), player, enemyPlayer)) {
            myGanks.add(new Gank(player, fight));
          }
          if (fight.isGankOf(playerperformance.getLane(), enemyPlayer, player)) {
            enemyGanks.add(new Gank(player, fight));
          }
        }

        if (fight instanceof Duel) {
          if (fight.getKills().get(0).getKiller() == pId) {
            duelsWon++;

          } else if (fight.getKills().get(0).getVictim() == pId) {
            duelsLost++;
          }

        } else if (fight instanceof Pick) {
          if (fight.getKills().get(0).getVictim() == pId) {
            pickAdvantage--;
          } else {
            pickAdvantage++;
          }

        } else if (fight instanceof Teamfight && fight.isInvolved(pId)) {
          val teamfight = (Teamfight) fight;
          myTeamfights.add(teamfight);

        } else if (fight instanceof Skirmish) {
          val skirmish = (Skirmish) fight;
          mySkirmishes.add(skirmish);
        }
      }
    }

    final int combatTime = fights.stream()
        .filter(Objects::nonNull)
        .filter(fight -> fight.isInvolved(pId))
        .mapToInt(fight -> (fight.getEnd(player) - fight.getStart(player)) / 1000 + 10).sum();
    stats.setSecondsInCombat((short) combatTime);


    final double deathOrder = myTeamfights.stream()
        .filter(teamfight -> teamfight.getDeathOrder(pId) != 0)
        .mapToDouble(teamfight -> teamfight.getDeathOrder(pId))
        .average().orElse(0);
    final double teamfightWins = myTeamfights.stream()
        .filter(Objects::nonNull)
        .mapToInt(teamfight -> teamfight.isWinner(pId) ? 1 : 0)
        .average().orElse(0);
    final int teamfightDamage = myTeamfights.stream().mapToInt(teamfight -> teamfight.getFightDamage(player)).sum();
    final double teamfightDamageRate = Util.div(teamfightDamage, playerperformance.getDamageTotal());
    stats.setTeamfights(myTeamfights.size(), allTeamfightsAmount, deathOrder, teamfightWins, teamfightDamageRate);

    final int skirmishAmount = mySkirmishes.size();
    int skirmishKills = (int) mySkirmishes.stream()
        .filter(Objects::nonNull)
        .flatMap(skirmish -> skirmish.getKills().stream())
        .filter(kill -> kill.getKiller() == pId || kill.getParticipants().containsKey(pId)).count() -
        (int) mySkirmishes.stream()
            .filter(Objects::nonNull)
            .flatMap(skirmish -> skirmish.getKills().stream())
            .filter(kill -> kill.getVictim() == pId).count();
    final double skirmishWins = myTeamfights.stream()
        .filter(Objects::nonNull)
        .mapToInt(skirmish -> skirmish.isWinner(pId) ? 1 : 0)
        .average().orElse(0);
    final int skrimishDamage = mySkirmishes.stream().mapToInt(skirmish -> skirmish.getFightDamage(player)).sum();
    double skirmishDamageRate = Util.div(skrimishDamage, playerperformance.getDamageTotal());
    stats.setSkirmishes(skirmishAmount, allSkirmishesAmount, skirmishKills, skirmishWins, skirmishDamageRate);


    stats.setDuels(playerperformance, duelsWon, duelsLost);
    stats.setPickAdvantage(pickAdvantage);
    handleGanks(stats, myGanks, enemyGanks, player, playerperformance);

    val map = new HashMap<Integer, List<Position>>();
    for (int min = 5; min < 31; min++) {
      if (min <= player.getLastMinute()) {
        val list = new ArrayList<Position>();

        for (JSONPlayer allPlayer : jsonTeam.getAllPlayers()) {
          if (allPlayer != player) {
            val teammatePosition = allPlayer.getPositionAt(min);
            list.add(teammatePosition);
          }
        }
        map.put(min, list);
      }
    }
    // A measure of how far away a player is from all allied champions between minutes 15 and 30.
    final double splitScore = map.keySet().stream()
        .filter(min -> min >= 15)
        .filter(min -> min <= 30)
        .mapToDouble(min -> map.get(min).stream()
            .filter(Objects::nonNull)
            .mapToDouble(position -> Util.distance(position, player.getPositionAt(min)))
            .average().orElse(0))
        .average().orElse(0);
    stats.setSplitScore((int) splitScore);

    // A measure of how close a player is to the nearest allied champion between minutes 5 and 20.
    final double companionScore = map.keySet().stream()
        .filter(min -> min >= 5)
        .filter(min -> min <= 20)
        .mapToDouble(min -> map.get(min).stream()
            .filter(Objects::nonNull)
            .mapToDouble(position -> Util.distance(position, player.getPositionAt(min)))
            .min().orElse(0))
        .average().orElse(0);
    stats.setCompanionScore((int) companionScore);

    // A measure of how much a player rotated around the map between minutes 5 and 20.
    final double rotationScore = map.keySet().stream()
        .filter(min -> min >= 5)
        .filter(min -> min <= 20)
        .mapToDouble(min -> {
          val realPosition = player.getPositionAt(min);
          return Util.distance(player.getLane().getCenter(realPosition, player.isFirstPick()), realPosition);
        })
        .average().orElse(0);
    stats.setRoamScore((int) rotationScore);

    if (player.getLastMinute() >= 10) {
      playerperformance.setEarlyLaneLead((short) player.getLeadAt(10, TimelineStat.LEAD));
      if (player.getLastMinute() >= 15) {
        playerperformance.setLaneLead((short) player.getLeadAt(15, TimelineStat.LEAD));
      }
    }



    if (player.getLane().equals(Lane.JUNGLE) && player.getLastMinute() >= 7) {
      JunglePath.get(playerperformance.getTeamperformance(), player.getPositionAt(2), player.getPositionAt(3),
          player.getPositionAt(4), player.getPositionAt(5), player.getPositionAt(6), player.getPositionAt(7));
    }
    final Double proximity = determineProximity(player, playerperformance);
    if (proximity != null) {
      stats.setProximity(BigDecimal.valueOf(proximity));
      final double enemyProximity = determineProximity(player.getEnemy(), null);
      stats.setLaneProximityDifference(BigDecimal.valueOf(proximity - enemyProximity));
    }



    playerperformance.setStats(stats);

    System.out.println("Analysiert + " + (System.currentTimeMillis() - millis));
  }

  private Double determineProximity(JSONPlayer player, Playerperformance playerperformance) {
    if (player.getLane().equals(Lane.JUNGLE) && player.getLastMinute() >= 7) {
      final int cs = player.getStatAt(7, TimelineStat.CREEP_SCORE);
      final int durationInSeconds = (int) (cs * 7.5);

      final int resetTime = player.getInventory().getResets().stream()
          .filter(Objects::nonNull)
          .filter(reset -> reset.getStart() < 420_000)
          .mapToInt(Reset::getDuration).sum();
      final int maxSeconds = 330 - resetTime / 1000;
      if (playerperformance != null) {
        playerperformance.getTeamperformance().setJungleTimeWasted((short) (maxSeconds - durationInSeconds));
      }

      return durationInSeconds / 330.0;

    } else {
      if (player.getLastMinute() >= 7) {
        final int xpAt7 = player.getStatAt(7, TimelineStat.EXPERIENCE);
        final int totalXP = player.getLane().getType().equals("BOT_LANE") ? 468 : 750;
        final double proximity = xpAt7 * 1d / totalXP;

        final int averageDistance = (int) IntStream.range(2, 8)
            .mapToDouble(min -> Util.distance(player.getPositionAt(min),
                player.getLane().getCenter(player.getPositionAt(min), player.isFirstPick())))
            .average().orElse(0);
        final double percentDistance = 1 - averageDistance * 1d / Const.MAP_SIZE;
        return proximity - percentDistance;
      }

    }
    return null;
  }

  private void handleGanks(PlayerperformanceStats stats, List<Gank> myGanks, List<Gank> enemyGanks, JSONPlayer player,
                           Playerperformance playerperformance) {
    int roamSuccess = 0;
    int gold = 0;
    int xp = 0;
    int cs = 0;
    int plates = 0;
    for (List<Gank> ganks : Arrays.asList(myGanks, enemyGanks)) {
      final boolean normalMode = ganks.equals(myGanks);
      int total = (int) ganks.stream().filter(Gank::wasSuccessful).count();
      int top = 0;
      int mid = 0;
      int bot = 0;



      for (Gank gank : ganks) {
        final List<Integer> involvedPlayers = gank.getFight().getInvolvedPlayers();
        final int start = gank.start();
        final int end = gank.end() + 60_000;
        final Position gankPosition = gank.getFight().getLastPosition();

        final Lane nearestLane = MapArea.getNearestLane(gankPosition);
        if (nearestLane.equals(Lane.TOP)) {
          top ++;
        } else if (nearestLane.equals(Lane.BOTTOM)) {
          bot++;
        } else {
          mid++;
        }

        final List<Integer> teamPlayers = normalMode ? involvedPlayers.stream().filter(id -> player.getTeam().hasPlayer(id - 1))
            .collect(Collectors.toList()) : involvedPlayers.stream().filter(id -> player.getEnemy().getTeam().hasPlayer(id - 1))
            .collect(Collectors.toList());
        for (Integer teamPlayer : teamPlayers) {
          final JSONPlayer searchedPlayer = game.getPlayer(teamPlayer);
          if (searchedPlayer != null) {
            final int experience = searchedPlayer.getLeadDifferenceAt(start / 60_000, end / 60_000 + 1, TimelineStat.EXPERIENCE);
            xp += normalMode ? experience : experience * -1;

            final int goldEarned = searchedPlayer.getLeadDifferenceAt(start / 60_000, end / 60_000 + 1, TimelineStat.TOTAL_GOLD);
            gold += normalMode ? goldEarned : goldEarned * -1;

            final int csEarned = searchedPlayer.getLeadDifferenceAt(start / 60_000, end / 60_000 + 1, TimelineStat.CREEP_SCORE);
            cs += normalMode ? csEarned : csEarned * -1;
          }
          roamSuccess += normalMode ? player.getLeadDifferenceAt(start / 60_000, end / 60_000 + 1, TimelineStat.LEAD) :
              (player.getEnemy().getLeadDifferenceAt(start / 60_000, end / 60_000 + 1, TimelineStat.LEAD) * -1);
        }

        for (JSONObject event : player.getTeam().getEvents(EventTypes.TURRET_PLATE_DESTROYED)) {
          final int timestamp = event.getInt("timestamp");
          if (timestamp < end && timestamp > start) {
            final JSONObject positionObject = event.getJSONObject("position");
            final Position position = new Position(positionObject.getInt("x"), positionObject.getInt("y"));
            if (Util.distance(gankPosition, position) < Const.DISTANCE_BETWEEN_FIGHTS ||
                event.getString("laneType").equals(normalMode ? player.getLane().getType() : player.getEnemy().getLane().getType())) {
              plates += normalMode ? 1 : -1;
            }
          }
        }
      }

      playerperformance.setGanksTotal((byte) total);
      playerperformance.setGanksTop((byte) top);
      playerperformance.setGanksMid((byte) mid);
      playerperformance.setGanksBot((byte) bot);
    }
    stats.setRoamObjectiveDamageAdvantage((short) (plates * 200));
    stats.setRoamGoldXpAdvantage((short) (xp + cs));
    stats.setRoamCreepScoreAdvantage((byte) cs);
    stats.setRoamGoldAdvantage((short) gold);
    stats.setRoamSuccessScore((short) roamSuccess);
  }

  private void determineLevelups(Playerperformance playerperformance, JSONPlayer enemyPlayer, byte earlierLevelups,
                                 PlayerperformanceStats stats) {
    byte totalLevelups = 0;
    for (JSONObject event : enemyPlayer.getEvents(EventTypes.LEVEL_UP)) {
      int level = event.getInt("level");
      final int levelupTime = getLevelupTime(playerperformance, level);
      if (levelupTime != 0) { // byte not nullable
        int timestamp = event.getInt("timestamp");
        if (levelupTime < timestamp) {
          earlierLevelups++;
        }
        totalLevelups++;
      }
    }

    if (totalLevelups != 0) { // null division
      final double earlierLevelupsAdvantage = Util.div(earlierLevelups, totalLevelups);
      stats.setLevelupEarlier(BigDecimal.valueOf(earlierLevelupsAdvantage));
    }
  }

  private void handleFromBehind(JSONPlayer player, PlayerperformanceStats stats, int behindStart, int behindEnd,
                                int deathsFromBehind) {
    int wardsFromBehind = (int) player.getEvents(EventTypes.WARD_PLACED).stream()
        .mapToInt(event -> event.getInt("timestamp"))
        .filter(timestamp -> timestamp / 60_000 > behindStart && timestamp / 60_000 < behindEnd)
        .count();

    val enemyPlayer = player.getEnemy();
    if (enemyPlayer != null) {
      wardsFromBehind -= (int) enemyPlayer.getEvents(EventTypes.WARD_PLACED).stream()
          .mapToInt(event -> event.getInt("timestamp"))
          .filter(timestamp -> timestamp / 60_000 > behindStart && timestamp / 60_000 < behindEnd)
          .count();
    }

    final int creepScoreFromBehind = player.getLeadDifferenceAt(behindStart, behindEnd, TimelineStat.CREEP_SCORE);
    final int goldFromBehind = player.getLeadDifferenceAt(behindStart, behindEnd, TimelineStat.TOTAL_GOLD);
    final int xpFromBehind = player.getLeadDifferenceAt(behindStart, behindEnd, TimelineStat.EXPERIENCE);
    stats.setBehaviourFromBehindAhead((short) creepScoreFromBehind, (short) wardsFromBehind, (short) deathsFromBehind,
        (short) goldFromBehind, (short) xpFromBehind);

  }

  private int searchForRechargeTimes(short firstTrinketSwap, List<Integer> yellowPlacementTimes) {
    List<Integer> wardCharges = Arrays.asList(226, 431, 629, 820, 1004, 1180, 1349, 1504, 1652, 2053, 2187, 2314, 2434, 2554, 2674, 2794,
        2914, 3034, 3154, 3274, 3394, 3514, 3634, 3754, 3974, 4094, 4214, 4334, 4454, 4574, 4694, 4814, 4934, 5054, 5174, 5294, 5414,
        5534, 5654, 5774, 5894, 6014, 6134, 6254, 6374, 6494, 6614, 6734, 6854, 6974, 7094, 7214, 7334, 7454, 7574, 7694, 8054, 8175,
        8294, 8414, 8534, 8654, 8774, 8894, 9014, 9134, 9254, 9374, 9494, 9614, 9734, 9854, 9974);
    int twoCharges = 0;
    int nextYellow = 0;
    if (yellowPlacementTimes.size() > 1) {
      for (int i = 1; i < yellowPlacementTimes.size(); i++) {
        int yellowTime = yellowPlacementTimes.get(i);
        if (yellowTime < firstTrinketSwap * 1000) {
          int wardCharge = wardCharges.get(i - 1) * 1000;
          if (wardCharge > nextYellow && yellowTime > wardCharge) {
            twoCharges += yellowTime - wardCharge;
            nextYellow = yellowTime;
          }
        } else {
          break;
        }
      }
    }


    return twoCharges;
  }

  private void getMidgameStats(JSONPlayer player, PlayerperformanceStats stats, int endMinute) {
    if (player.getLastMinute() > Const.EARLYGAME_UNTIL_MINUTE) {
      int end = Math.min(player.getLastMinute(), Const.MIDGAME_UNTIL_MINUTE);

      final int goldDifference = player.getStatDifference(Const.EARLYGAME_UNTIL_MINUTE, end, TimelineStat.TOTAL_GOLD);
      final double goldPercentage = goldDifference * 1d / Const.MIDGAME_GOLD;
      stats.setMidgameGoldEfficiency(BigDecimal.valueOf(goldPercentage));

      final int xpDifference = player.getStatDifference(Const.EARLYGAME_UNTIL_MINUTE, end, TimelineStat.EXPERIENCE);
      final double xpPercentage = xpDifference * 1d / Const.MIDGAME_XP;
      stats.setMidgameGoldXPEfficiency(BigDecimal.valueOf((xpPercentage + goldPercentage) / 2.0));
    }

    if (player.getLastMinute() > Const.MIDGAME_UNTIL_MINUTE) {
      final int leadDifference = player.getLeadDifferenceAt(27, endMinute, TimelineStat.LEAD);
      stats.setLategameLead((short) leadDifference);
    }
  }

  private int getLevelupTime(Playerperformance playerperformance, int level) {
    return playerperformance.getLevelups().stream()
        .filter(levelup -> levelup.getLevel() == level)
        .map(PlayerperformanceLevel::getTime)
        .findFirst().orElse(0);
  }

  private double determineAssistbountyFactor(int timestamp) {
    final int second = timestamp / 1000;
    if (second <= Const.ASSIST_FACTOR_INCREASE_SECOND) {
      return Const.ASSIST_FACTOR_START_VALUE;

    } else if (second >= Const.ASSIST_FACTOR_ENDING_SECOND) {
      return Const.ASSIST_FACTOR_END_VALUE;

    } else {
      final int currentSecond = second - Const.ASSIST_FACTOR_INCREASE_SECOND;
      final int timespanDifference = Const.ASSIST_FACTOR_ENDING_SECOND - Const.ASSIST_FACTOR_INCREASE_SECOND;
      final double progressToMaxValue = Util.div(currentSecond, timespanDifference);
      final double valueDifference = Const.ASSIST_FACTOR_END_VALUE - Const.ASSIST_FACTOR_START_VALUE;
      return valueDifference * progressToMaxValue + Const.ASSIST_FACTOR_START_VALUE;
    }
  }

  /**
   * Bestimme das Startitem, dass ein Spieler gekauft hat
   *
   * @return Sekunden, wann es verkauft wurde
   */
  private Short determineStartItem(JSONPlayer player) {
    return player.getEvents(EventTypes.ITEM_SOLD).stream()
        .filter(event -> {
          final Item item = Item.find((short) event.getInt("itemId"));
          return item != null && item.getType().equals(ItemType.STARTING);
        })
        .map(event -> (short) (event.getInt("timestamp") / 1000))
        .findFirst().orElse(null);
  }

  /**
   * Handle control of enemy Player
   *
   * @param player Spieler
   * @param minute Spielminute
   * @param stats eintragen hier
   */
  private void handleControlled(JSONPlayer player, int minute, PlayerperformanceStats stats) {
    if (player.hasEnemy()) {
      final double statAt = player.getStatAt(minute, TimelineStat.ENEMY_CONTROLLED) / 1000.0;
      final double statAt1 = player.getEnemy().getStatAt(minute, TimelineStat.ENEMY_CONTROLLED) / 1000.0;
      stats.setEnemyControlAdvantage(statAt, statAt1);

      if (player.getHighestMinute() >= Const.EARLYGAME_UNTIL_MINUTE) {
        final double statEarly = player.getStatAt(Const.EARLYGAME_UNTIL_MINUTE, TimelineStat.ENEMY_CONTROLLED) / 1000.0;
        final double stat2Early = player.getEnemy().getStatAt(Const.EARLYGAME_UNTIL_MINUTE, TimelineStat.ENEMY_CONTROLLED) / 1000.0;
        stats.setEnemyControlAdvantageEarly(statEarly, stat2Early);
      }
    }
  }

  private byte determineAllObjectivesAmount(List<JSONTeam> jsonTeams) {
    byte allObjectivesAmount = 0;
    for (JSONTeam team : jsonTeams) {
      val objectives = team.getTeamObject().getJSONObject("objectives");
      val tower = objectives.getJSONObject("tower");
      allObjectivesAmount += tower.getInt("kills");
      val dragon = objectives.getJSONObject("dragon");
      allObjectivesAmount += dragon.getInt("kills");
      val riftHerald = objectives.getJSONObject("riftHerald");
      allObjectivesAmount += riftHerald.getInt("kills");
    }
    return allObjectivesAmount;
  }

  private byte determineStolenBarons(JSONTeam jsonTeam, JSONTeam enemyTeam) {
    byte stolenBarons = 0;
    for (JSONObject event : jsonTeam.getEvents(EventTypes.ELITE_MONSTER_KILL)) {
      val monsterType = event.getString("monsterType");
      if (monsterType.equals("BARON_NASHOR") && event.has("assistingParticipantIds")) {
        val participatingIds = event.getJSONArray("assistingParticipantIds").toList().stream()
            .map(id -> (Integer) id - 1)
            .collect(Collectors.toList());
        final long myTeam = participatingIds.stream().filter(jsonTeam::hasPlayer).count();
        final long enemies = participatingIds.stream().filter(enemyTeam::hasPlayer).count();
        if (enemies > myTeam) {
          stolenBarons++;
        }
      }
    }
    return stolenBarons;
  }

  private List<Short> searchForControlPurchase(JSONPlayer player) {
    return player.getEvents(EventTypes.ITEM_PURCHASED).stream()
        .filter(event -> Item.has((short) event.getInt("itemId")))
        .filter(event -> Item.find((short) event.getInt("itemId")).getItemName().equals(Const.TRUESIGHT_WARD_NAME))
        .mapToInt(event -> event.getInt("timestamp"))
        .mapToObj(timestamp -> (short) (timestamp / 1000))
        .collect(Collectors.toCollection(ArrayList::new));
  }

  private List<Short> searchForControlPlacements(JSONPlayer player) {
    return player.getEvents(EventTypes.WARD_PLACED).stream()
        .filter(event -> WardType.valueOf(event.getString("wardType")).equals(WardType.CONTROL_WARD))
        .mapToInt(event -> event.getInt("timestamp"))
        .mapToObj(timestamp -> (short) (timestamp / 1000))
        .collect(Collectors.toCollection(ArrayList::new));
  }

  private short searchForTrinketSwap(JSONPlayer player) {
    return player.getEvents(EventTypes.ITEM_PURCHASED).stream()
        .filter(event -> Item.has((short) event.getInt("itemId")))
        .filter(event -> Item.find((short) event.getInt("itemId")).getType().equals(ItemType.TRINKET))
        .filter(event -> !Item.find((short) event.getInt("itemId")).getItemName().equals(Const.DEFAULT_TRINKET_WARD_NAME))
        .mapToInt(event -> event.getInt("timestamp"))
        .mapToObj(timestamp -> (short) (timestamp / 1000))
        .findFirst().orElse((short) 0);
  }

  private List<Integer> searchForTrinketPlacementsUntilSwap(JSONPlayer player, short second) {
    return player.getEvents(EventTypes.WARD_PLACED).stream()
        .filter(event -> event.getInt("timestamp") / 1000 <= second)
        .filter(event -> WardType.valueOf(event.getString("wardType")).equals(WardType.YELLOW_TRINKET))
        .mapToInt(event -> event.getInt("timestamp"))
        .boxed().collect(Collectors.toCollection(ArrayList::new));
  }

  private short searchForFirstWardTime(JSONPlayer player) {
    return player.getEvents(EventTypes.WARD_PLACED).stream()
        .map(event -> (short) (event.getInt("timestamp") / 1000))
        .findFirst()
        .orElse((short) 0);
  }
}
