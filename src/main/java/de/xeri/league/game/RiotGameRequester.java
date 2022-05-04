package de.xeri.league.game;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.xeri.league.game.events.fight.Duel;
import de.xeri.league.game.events.fight.Fight;
import de.xeri.league.game.events.fight.Gank;
import de.xeri.league.game.events.fight.Kill;
import de.xeri.league.game.events.fight.Pick;
import de.xeri.league.game.events.fight.Skirmish;
import de.xeri.league.game.events.fight.Teamfight;
import de.xeri.league.game.events.fight.enums.Fighttype;
import de.xeri.league.game.events.items.ItemStack;
import de.xeri.league.game.events.items.Reset;
import de.xeri.league.game.events.location.Position;
import de.xeri.league.game.models.JSONPlayer;
import de.xeri.league.game.models.JSONTeam;
import de.xeri.league.game.models.TimelineStat;
import de.xeri.league.models.dynamic.Champion;
import de.xeri.league.models.dynamic.Item;
import de.xeri.league.models.dynamic.Rune;
import de.xeri.league.models.dynamic.Summonerspell;
import de.xeri.league.models.enums.DragonSoul;
import de.xeri.league.models.enums.EventTypes;
import de.xeri.league.models.enums.ItemType;
import de.xeri.league.models.enums.KillRole;
import de.xeri.league.models.enums.KillType;
import de.xeri.league.models.enums.Lane;
import de.xeri.league.models.enums.ObjectiveSubtype;
import de.xeri.league.models.enums.QueueType;
import de.xeri.league.models.enums.SelectionType;
import de.xeri.league.models.enums.StoredStat;
import de.xeri.league.models.enums.WardType;
import de.xeri.league.models.league.Account;
import de.xeri.league.models.match.ChampionSelection;
import de.xeri.league.models.match.Game;
import de.xeri.league.models.match.GamePause;
import de.xeri.league.models.match.Gametype;
import de.xeri.league.models.match.ScheduledGame;
import de.xeri.league.models.match.Teamperformance;
import de.xeri.league.models.match.TeamperformanceBounty;
import de.xeri.league.models.match.playerperformance.Playerperformance;
import de.xeri.league.models.match.playerperformance.PlayerperformanceInfo;
import de.xeri.league.models.match.playerperformance.PlayerperformanceKill;
import de.xeri.league.models.match.playerperformance.PlayerperformanceLevel;
import de.xeri.league.models.match.playerperformance.PlayerperformanceObjective;
import de.xeri.league.models.match.playerperformance.PlayerperformanceStats;
import de.xeri.league.util.Const;
import de.xeri.league.util.Data;
import de.xeri.league.util.Util;
import de.xeri.league.util.io.json.JSON;
import de.xeri.league.util.io.riot.RiotAccountRequester;
import de.xeri.league.util.io.riot.RiotURLGenerator;
import lombok.val;
import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Lara on 08.04.2022 for web
 */
public final class RiotGameRequester {
  public static List<JSONTeam> jsonTeams;
  private static List<JSONObject> allEvents;

  public static void loadCompetitive(ScheduledGame scheduledGame) {
    loadGame(scheduledGame, QueueType.TOURNEY);
  }

  public static void loadClashGame(ScheduledGame scheduledGame) {
    loadGame(scheduledGame, QueueType.CLASH);
  }

  public static void loadMatchmade(ScheduledGame scheduledGame) {
    loadGame(scheduledGame, QueueType.OTHER);
  }

  private static void loadGame(ScheduledGame scheduledGame, QueueType queueType) {
    val matchGenerator = RiotURLGenerator.getMatch();
    val game = matchGenerator.getMatch(scheduledGame.getId());
    val timeline = matchGenerator.getTimeline(scheduledGame.getId());
    if (isValidGame(game, timeline, queueType))
      ScheduledGame.get().remove(scheduledGame);
    Data.getInstance().getSession().remove(scheduledGame);
    Data.getInstance().commit();
  }

  private static boolean isValidGame(JSON gameJson, JSON timelineJson, QueueType queueType) {
    val gameData = gameJson.getJSONObject();
    val info = gameData.getJSONObject("info");
    final int queueId = info.getInt("queueId");
    val participants = info.getJSONArray("participants");
    if ((queueId == 0 || queueId == 400 || queueId == 410 || queueId == 420 || queueId == 430 || queueId == 700) && participants.length() == 10) {

      val metadata = gameData.getJSONObject("metadata");
      val gameId = metadata.getString("matchId");
      final JSONObject timelineObject = timelineJson.getJSONObject();
      final Map<Integer, JSONObject> playerInfo = timelineObject != null ? loadTimeline(timelineObject) : new HashMap<>();

      val gametype = Gametype.find((info.has("tournamentCode") && !info.isNull("tournamentCode")) ? (short) -1 : (short) queueId);
      val game = handleGame(info, gameId, gametype);
      gametype.addGame(game, gametype);
      val fights = handleGameEvents(game);

      jsonTeams = getJsonTeams(participants, playerInfo);
      for (int i = 0; i < jsonTeams.size(); i++) {
        val jsonTeam = jsonTeams.get(i);
        val teams = info.getJSONArray("teams");
        jsonTeam.setTeamObject(teams.getJSONObject(i));
        if (jsonTeam.doesExist()) {
          val teamperformance = handleTeam(jsonTeam);
          val team = jsonTeam.getMostUsedTeam(queueType);
          game.addTeamperformance(teamperformance, team);
          handleTeamEvents(teamperformance);

          val players = determinePlayers(queueType, jsonTeam);
          players.forEach(player -> handlePlayer(player, teamperformance, fights));
        }
        determineBansAndPicks(teams.getJSONObject(i), i, game, participants);
      }
      return true;
    }
    return false;
  }

  private static void determineBansAndPicks(JSONObject jsonObject, int id, Game game, JSONArray participants) {
    val bans = jsonObject.getJSONArray("bans");
    val pickTurns = new ArrayList<Integer>();
    for (int j = 0; j < bans.length(); j++) {
      val selectionObject = bans.getJSONObject(j);
      final int championId = selectionObject.getInt("championId");
      val champion = Champion.find(championId);
      final int pickTurn = selectionObject.getInt("pickTurn");
      pickTurns.add(pickTurn);
      game.addChampionSelection(new ChampionSelection(SelectionType.BAN, (byte) (j + 1 + id * 5)), champion);
    }

    val indexes = new ArrayList<Integer>();
    int iterator = 1;
    while (!pickTurns.isEmpty()) {
      final int lowestIndex = pickTurns.indexOf(Collections.min(pickTurns));
      indexes.set(lowestIndex, iterator);
      pickTurns.remove(lowestIndex);
      iterator++;
    }

    for (int i : indexes) {
      val championName = participants.getJSONObject(i).getString("championName");
      val champion = Champion.find(championName);
      game.addChampionSelection(new ChampionSelection(SelectionType.PICK, (byte) (i + 1 + id * 5)), champion);
    }
  }

  private static void handlePlayer(JSONPlayer player, Teamperformance teamperformance, List<Fight> fights) {
    val enemyPlayer = player.getEnemy();
    val performance = handlePerformance(player, enemyPlayer);
    val account = (player.isListed()) ? player.getAccount() : Account.get(RiotAccountRequester.fromPuuid(player.get(StoredStat.PUUID)));
    if (account != null) {
      val playerperformance = teamperformance.addPlayerperformance(performance, account);
      handleSummonerspells(player, playerperformance);
      handleChampionsPicked(player, enemyPlayer, playerperformance);

      val styles = player.object(StoredStat.RUNES).getJSONArray("styles");
      for (int i = 0; i < styles.length(); i++) {
        val substyle = styles.getJSONObject(i);
        val runes = substyle.getJSONArray("selections");
        for (int j = 0; j < runes.length(); j++) {
          val runeObject = runes.getJSONObject(j);
          val perk = Rune.find((short) runeObject.getInt("perk"));
          playerperformance.addRune(perk);
        }
      }

      handlePlayerEvents(player, playerperformance);
      handlePlayerInfo(player, playerperformance);

      handlePlayerStats(playerperformance, teamperformance, jsonTeams, player, fights);
    }
  }

  private static void handlePlayerStats(Playerperformance playerperformance, Teamperformance teamperformance, List<JSONTeam> jsonTeams,
                                        JSONPlayer player, List<Fight> fights) {
    final int pId = player.getId() + 1;
    val stats = new PlayerperformanceStats(playerperformance);
    val enemyPlayer = player.getEnemy();
    val jsonTeam = jsonTeams.get(teamperformance.isFirstPick() ? 0 : 1);
    val enemyTeam = jsonTeams.get(teamperformance.isFirstPick() ? 1 : 0);

    player.buildInventory();
    final List<Reset> resets = player.getInventory().getResets();

    final long recalls = resets.stream().filter(Reset::wasRecall).count();
    final double resetsThroughRecall = resets.isEmpty() ? 0 : recalls * 1d / resets.size();
    stats.setResetsThroughRecall(BigDecimal.valueOf(resetsThroughRecall));
    stats.setResets((short) resets.size());

    final double averageGoldAmount = resets.stream().mapToInt(Reset::getGoldPreReset).average().orElse(0);
    stats.setResetGold((short) averageGoldAmount);
    final double averageGoldUnspent = resets.stream().mapToInt(Reset::getGoldUnspent).average().orElse(0);
    stats.setResetGoldUnspent((short) averageGoldUnspent);

    final double averageResetDuration = resets.stream().mapToInt(Reset::getDuration).average().orElse(0);
    stats.setResetDuration((int) averageResetDuration);

    final int goldLost = resets.stream()
        .mapToInt(reset -> player.getLeadDifferenceAt(reset.getStart() / 60_000, reset.getEnd() / 60_000 + 1, TimelineStat.TOTAL_GOLD))
        .sum();
    stats.setResetGoldLost((short) goldLost);

    val firstBase = player.getInventory().getResets().isEmpty() ? null : player.getInventory().getResets().get(0);
    if (firstBase != null) {
      final boolean firstBaseRecall = firstBase.wasRecall();
      stats.setFirstBaseThroughRecall(firstBaseRecall);

      final int start = firstBase.getStart();
      stats.setFirstBase((short) (start / 1000));

      final int underControl = player.getStatAt(start / 1000, TimelineStat.ENEMY_CONTROLLED) / 1000;
      stats.setFirstBaseEnemyControlled((short) underControl);

      final int lead = player.getStatAt(start / 60_000, TimelineStat.LEAD);
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
      if (firstBase.getStart() != 0) {
        final double startpool = player.getPool(0);
        final double endpool = firstBase.getPool();
        final int minutes = firstBase.getStart() / 60_000 - 0;
        for (int min = 1; min < firstBase.getStart() / 60_000; min++) {
          final double pool = player.getPool(min);
          final double expectedPool = (startpool - endpool) * (min * 1d / minutes);
          if (pool < expectedPool) {
            discrepance += expectedPool - pool;
          }
          amount++;
        }
      }
      if (secondBase.getStart() != 0) {
        final double startpool = player.getPool(firstBase.getEnd()/60_000+1);
        final double endpool = secondBase.getPool();
        final int minutes = secondBase.getStart() / 60_000 - firstBase.getEnd()/60_000+1;
        for (int min = firstBase.getEnd()/60_000+2; min < secondBase.getStart() / 60_000; min++) {
          final double pool = player.getPool(min);
          final double expectedPool = (startpool - endpool) * ((min - firstBase.getEnd() / 60_000 + 1) * 1d / minutes);
          if (pool < expectedPool) {
            discrepance += expectedPool - pool;
          }
          amount++;
        }
      }
      final double resourceConservation = discrepance * 1d / amount;
      stats.setResourceConservation(BigDecimal.valueOf(1 - resourceConservation));

      // Consumables
      final List<ItemStack> items = player.getInventory().getItemsAt(secondBase.getEnd());
      final boolean hasConsumable = items.stream().anyMatch(itemStack -> itemStack.getItem().getType().equals(ItemType.CONSUMABLE));
      stats.setConsumablesPurchased(hasConsumable);

      final int underControl = player.getStatAt(start / 1000, TimelineStat.ENEMY_CONTROLLED) / 1000;
      stats.setSecondBaseEnemyControlled((short) underControl);

      // Damage
      final int earlyDamage = player.getStatAt(start, TimelineStat.DAMAGE);
      final double damagePercentage = playerperformance.getDamageTotal() == 0 ? 0 : earlyDamage * 1d / playerperformance.getDamageTotal();
      stats.setEarlyDamage(BigDecimal.valueOf(damagePercentage));
    }

    final int amount = (int) resets.stream()
        .filter(reset -> player.getTeam().getAllPlayers().stream()
            .anyMatch(teamPlayer -> teamPlayer != player && teamPlayer.getInventory().getResets().stream()
                .anyMatch(r -> Math.abs(r.getStart() - reset.getStart()) <= 45_000)))
        .count();
    final double resetsTogether = resets.isEmpty() ? 0 : amount * 1d / resets.size();
    stats.setResetsTogether(BigDecimal.valueOf(resetsTogether));


    final byte allObjectivesAmount = determineAllObjectivesAmount(jsonTeams);
    stats.setObjectivesStolenAndContested(playerperformance, allObjectivesAmount);
    stats.setObjectivesKilledJunglerBefore(playerperformance, allObjectivesAmount);

    final byte stolenBarons = determineStolenBarons(jsonTeam, enemyTeam);
    stats.setBaronTakedownsAttempts(playerperformance, stolenBarons);

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
    final int twoChargesUp = searchForRechargeTimes(playerperformance, firstTrinketSwap, yellowPlacementTimes);
    final double twoChargesUpPercentage = (twoChargesUp - 240_000) * 1d / (firstTrinketSwap * 1000);
    stats.setTrinketEfficiency(BigDecimal.valueOf(1 - twoChargesUpPercentage));

    val purchases = searchForControlPurchase(player);
    final short averageControlTime = (short) IntStream.range(0, controlPlacements.size())
        .filter(i -> purchases.size() > i)
        .map(i -> controlPlacements.get(i) - purchases.get(i))
        .boxed().collect(Collectors.toCollection(ArrayList::new))
        .stream().mapToInt(Integer::intValue).average()
        .orElse(0);
    stats.setControlWardInventoryTime(averageControlTime);

    final int totalMitigation = jsonTeam.getAllPlayers().stream()
        .mapToInt(p -> p.getMedium(StoredStat.DAMAGE_MITIGATED))
        .sum();
    stats.setTeamDamageMitigated(playerperformance, totalMitigation);

    boolean wasAhead = false;
    boolean wasBehind = false;
    int behindStart = 0;
    int behindEnd = 0;
    boolean comeback = false;
    int endMinute = 0;
    int xpLead = 0;
    for (int minute = 0; minute < player.getInfos().size(); minute++) {
      endMinute = minute;
      final int leadAtEnd = player.getLeadAt(endMinute, TimelineStat.LEAD);
      if (endMinute <= 15) {
        if (leadAtEnd >= Const.AHEAD_XPGOLD && !wasAhead) {
          wasAhead = true;
          behindStart = minute;

        } else if (leadAtEnd <= (Const.AHEAD_XPGOLD * -1) && !wasBehind) {
          wasBehind = true;
          behindStart = minute;
        }
      }

      if (wasAhead && leadAtEnd < 0 ||
          wasBehind && leadAtEnd > 0) {
        behindEnd = minute;
        comeback = true;
      }

      xpLead = player.getLeadAt(endMinute, TimelineStat.EXPERIENCE);
    }

    if (behindStart != 0 && behindEnd == 0) {
      behindEnd = endMinute;
    }
    stats.setAhead(wasAhead);
    stats.setBehind(wasBehind);
    stats.setComeback(comeback);
    stats.setXpLead((short) xpLead);


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
            if (timestamp / 60_000 > behindStart && timestamp / 60_000 < behindEnd) {
              deathsFromBehind--;
            }
          }
        }
      }
    }

    val deathPositioning = new HashMap<Integer, Double>();
    val killPositioning = new HashMap<Integer, Double>();
    int lead = 0;
    for (JSONObject event : player.getEvents(EventTypes.CHAMPION_KILL)) {
      final Kill kill = Kill.getKillFromEvent(event);
      if (kill != null) {
        final short shutdownBounty = (short) event.getInt("shutdownBounty");
        final short bounty = (short) event.getInt("bounty");
        int timestamp = event.getInt("timestamp");
        lead += player.getLeadDifferenceAt(timestamp / 60_000, (timestamp + 60_000) / 60_000, TimelineStat.LEAD);

        final Position position = kill.getPosition();
        final double relativePosition = position.getTotalAggression(player.isFirstPick());
        if (kill.getVictim() == pId) {
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
          if (timestamp / 60_000 > behindStart && timestamp / 60_000 < behindEnd) {
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
          final int assistBounty = totalAssistBounty / participantAmount;
          assistBounties.add((short) assistBounty);

          //Positioning
          killPositioning.put(timestamp, relativePosition);
        }
      }
    }

    if (!deathPositioning.isEmpty()) {
      final double averageDeathPosition = deathPositioning.values().stream()
          .mapToDouble(Double::doubleValue)
          .average().orElse(0);
      stats.setRelativeDeathPositioning(BigDecimal.valueOf(averageDeathPosition));

      final double laneKillPositioning = killPositioning.keySet().stream()
          .filter(milli -> milli < Const.EARLYGAME_UNTIL_MINUTE * 60_000)
          .mapToDouble(deathPositioning::get)
          .average().orElse(0);
      stats.setLaneKillPositioning(BigDecimal.valueOf(laneKillPositioning));

      final double laneDeathPositioning = deathPositioning.keySet().stream()
          .filter(milli -> milli < Const.EARLYGAME_UNTIL_MINUTE * 60_000)
          .mapToDouble(deathPositioning::get)
          .average().orElse(0);
      stats.setLaneKillDeathPositioning(BigDecimal.valueOf((laneKillPositioning + laneDeathPositioning) / 2.0));
    }

    stats.setLeadThroughDeaths((short) lead);


    if (firstDeathTime < 300) {
      int deathMinute = firstDeathTime / 60;
      final int leadAt = player.getLeadAt(deathMinute, TimelineStat.LEAD);
      final int leadAtEnd = player.getLeadAt(Const.EARLYGAME_UNTIL_MINUTE, TimelineStat.LEAD);
      stats.setLeadDifferenceAfterDiedEarly((short) (leadAtEnd - leadAt));
    }

    handleFromBehind(player, stats, behindStart, behindEnd, deathsFromBehind);

    stats.setFirstKillTime((short) firstKillTime, (short) firstDeathTime);


    short startItemSold = determineStartItem(player);
    stats.setStartItemSold(startItemSold);


    final double kills = killBounties.stream().filter(b -> b > 0).mapToInt(b -> b).sum() * 1d / Const.KILL_BOUNTY;
    final double deaths = killBounties.stream().filter(b -> b < 0).mapToInt(b -> b).sum() * 1d / Const.KILL_BOUNTY;
    final double assists = assistBounties.stream().mapToInt(b -> b).sum() * 1d / Const.ASSIST_BOUNTY;
    stats.setTrueKda(kills, deaths, assists);


    byte objectivesEarlyWe = 0;
    byte objectivesEarlyEnemy = 0;
    for (JSONObject event : allEvents) {
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
    for (JSONObject event : allEvents) {
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
      final boolean leadExtend = leadAt - Const.AHEAD_XPGOLD > Const.AHEAD_XPGOLD_EXTEND;
      stats.setExtendingLead(leadExtend);
    }

    stats.setDeathsEarly(playerperformance, (byte) deathsEarly);
    stats.setBountyDifference(playerperformance, bountyDrop);

    double csAt10 = player.getStatPerMinute(10, TimelineStat.CREEP_SCORE);
    val positionList = new ArrayList<Double>();
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

    final double csAt = player.getStatAt(14, TimelineStat.CREEP_SCORE) * 1d / 158;
    stats.setEarlyFarmEfficiency(BigDecimal.valueOf(csAt));
    stats.setEarlyGoldAdvantage((short) player.getLeadAt(10, TimelineStat.TOTAL_GOLD));

    final double earlyPosition = positionList.subList(1, Const.EARLYGAME_UNTIL_MINUTE).stream()
        .mapToDouble(Double::doubleValue).average().orElse(0);
    if (earlyPosition != 0) {
      stats.setLanePositioning(BigDecimal.valueOf(earlyPosition));
    }

    final double midPosition = positionList.subList(Const.EARLYGAME_UNTIL_MINUTE, Const.MIDGAME_UNTIL_MINUTE).stream()
        .mapToDouble(Double::doubleValue).average().orElse(0);
    if (midPosition != 0) {
      stats.setMidgamePositioning(BigDecimal.valueOf(midPosition));
    }

    final double latePosition = positionList.subList(Const.MIDGAME_UNTIL_MINUTE, positionList.size()).stream()
        .mapToDouble(Double::doubleValue).average().orElse(0);
    if (latePosition != 0) {
      stats.setLategamePositioning(BigDecimal.valueOf(latePosition));
    }

    getMidgameStats(player, stats, endMinute);
    handleControlled(player, minute, stats);

    if (enemyPlayer != null) {
      byte earlierLevelups = 0;
      final byte totalLevelups = determineLevelups(playerperformance, enemyPlayer, earlierLevelups);
      if (totalLevelups != 0) { // null division
        final double earlierLevelupsAdvantage = earlierLevelups * 1d / totalLevelups;
        stats.setLevelupEarlier(BigDecimal.valueOf(earlierLevelupsAdvantage));
      }

      stats.setSpellDodge(playerperformance, enemyPlayer.getSmall(StoredStat.SPELL_LANDED), enemyPlayer.getSmall(StoredStat.SPELL_DODGE),
          enemyPlayer.getSmall(StoredStat.SPELL_DODGE_QUICK));
    }

    val myGanks = new ArrayList<Gank>();
    val enemyGanks = new ArrayList<Gank>();
    int duelsWon = 0;
    int duelsLost = 0;
    short pickAdvantage = 0;
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
        .filter(fight -> fight.isInvolved(pId))
        .mapToInt(fight -> (fight.end(player) - fight.start(player)) / 1000).sum();
    stats.setSecondsInCombat((short) combatTime);


    final double deathOrder = myTeamfights.stream().filter(teamfight -> teamfight.getDeathOrder(pId) != 0)
        .mapToDouble(teamfight -> teamfight.getDeathOrder(pId)).average().orElse(0);
    final double teamfightWins = myTeamfights.stream().mapToInt(teamfight -> teamfight.isWinner(pId) ? 1 : 0).average().orElse(0);
    final int teamfightDamage = myTeamfights.stream().mapToInt(teamfight -> teamfight.getFightDamage(player)).sum();
    final double teamfightDamageRate = teamfightDamage * 1d / playerperformance.getDamageTotal();
    stats.setTeamfights(myTeamfights.size(), allTeamfightsAmount, deathOrder, teamfightWins, teamfightDamageRate);

    final int skirmishAmount = mySkirmishes.size();
    int skirmishKills = (int) mySkirmishes.stream()
        .flatMap(skirmish -> skirmish.getKills().stream())
        .filter(kill -> kill.getKiller() == pId || kill.getParticipants().containsKey(pId)).count() -
        (int) mySkirmishes.stream()
            .flatMap(skirmish -> skirmish.getKills().stream())
            .filter(kill -> kill.getVictim() == pId).count();
    final double skirmishWins = myTeamfights.stream()
        .mapToInt(skirmish -> skirmish.isWinner(pId) ? 1 : 0)
        .average().orElse(0);
    final int skrimishDamage = mySkirmishes.stream().mapToInt(skirmish -> skirmish.getFightDamage(player)).sum();
    double skirmishDamageRate = skrimishDamage * 1d / playerperformance.getDamageTotal();
    stats.setSkirmishes(skirmishAmount, allSkirmishesAmount, skirmishKills, skirmishWins, skirmishDamageRate);


    stats.setDuels(playerperformance, duelsWon, duelsLost);
    stats.setPickAdvantage(pickAdvantage);
    handleGanks(stats, myGanks, enemyGanks, player);

    val map = new HashMap<Integer, List<Position>>();
    for (int min = 5; min < 31; min++) {
      val list = new ArrayList<Position>();

      for (JSONPlayer allPlayer : jsonTeam.getAllPlayers()) {
        if (allPlayer != player) {
          val teammatePosition = allPlayer.getPositionAt(min);
          list.add(teammatePosition);
        }
      }
      map.put(min, list);
    }
    // A measure of how far away a player is from all allied champions between minutes 15 and 30.
    final double splitScore = map.keySet().stream()
        .filter(min -> min >= 15)
        .filter(min -> min <= 30)
        .mapToDouble(min -> map.get(min).stream()
            .mapToDouble(position -> Util.distance(position, player.getPositionAt(min)))
            .average().orElse(0))
        .average().orElse(0);
    stats.setSplitScore((int) splitScore);

    // A measure of how close a player is to the nearest allied champion between minutes 5 and 20.
    final double companionScore = map.keySet().stream()
        .filter(min -> min >= 5)
        .filter(min -> min <= 20)
        .mapToDouble(min -> map.get(min).stream()
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

    playerperformance.setLaneLead((short) player.getLeadAt(15, TimelineStat.LEAD));
    playerperformance.setEarlyLaneLead((short) player.getLeadAt(10, TimelineStat.LEAD));


    playerperformance.setStats(stats);
  }

  private static void handleGanks(PlayerperformanceStats stats, List<Gank> myGanks, List<Gank> enemyGanks, JSONPlayer player) {
    int roamSuccess = 0;
    int gold = 0;
    int xp = 0;
    int cs = 0;
    int plates = 0;
    for (List<Gank> ganks : Arrays.asList(myGanks, enemyGanks)) {
      final boolean normalMode = ganks.equals(myGanks);
      for (Gank gank : ganks) {
        final List<Integer> involvedPlayers = gank.getFight().getInvolvedPlayers();
        final int start = gank.start();
        final int end = gank.end() + 60_000;

        final List<Integer> teamPlayers = normalMode ? involvedPlayers.stream().filter(id -> player.getTeam().hasPlayer(id - 1))
            .collect(Collectors.toList()) : involvedPlayers.stream().filter(id -> player.getEnemy().getTeam().hasPlayer(id - 1))
            .collect(Collectors.toList());
        for (Integer teamPlayer : teamPlayers) {
          final JSONPlayer searchedPlayer = JSONPlayer.getPlayer(teamPlayer);
          final int experience = searchedPlayer.getLeadDifferenceAt(start / 60_000, end / 60_000 + 1, TimelineStat.EXPERIENCE);
          xp += normalMode ? experience : experience * -1;

          final int goldEarned = searchedPlayer.getLeadDifferenceAt(start / 60_000, end / 60_000 + 1, TimelineStat.TOTAL_GOLD);
          gold += normalMode ? goldEarned : goldEarned * -1;

          final int csEarned = searchedPlayer.getLeadDifferenceAt(start / 60_000, end / 60_000 + 1, TimelineStat.CREEP_SCORE);
          cs += normalMode ? csEarned : csEarned * -1;

          roamSuccess += normalMode ? player.getLeadDifferenceAt(start / 60_000, end / 60_000 + 1, TimelineStat.LEAD) :
              (player.getEnemy().getLeadDifferenceAt(start / 60_000, end / 60_000 + 1, TimelineStat.LEAD) * -1);
        }

        for (JSONObject event : player.getTeam().getEvents(EventTypes.TURRET_PLATE_DESTROYED)) {
          final int timestamp = event.getInt("timestamp");
          if (timestamp < end && timestamp > start) {
            final JSONObject positionObject = event.getJSONObject("position");
            final Position position = new Position(positionObject.getInt("x"), positionObject.getInt("y"));
            if (Util.distance(gank.getFight().getLastPosition(), position) < Const.DISTANCE_BETWEEN_FIGHTS ||
                event.getString("laneType").equals(normalMode ? player.getLane().getType() : player.getEnemy().getLane().getType())) {
              plates += normalMode ? 1 : -1;
            }
          }
        }
      }
    }
    stats.setRoamObjectiveDamageAdvantage((short) (plates * 200));
    stats.setRoamGoldXpAdvantage((short) (xp + cs));
    stats.setRoamCreepScoreAdvantage((short) cs);
    stats.setRoamGoldAdvantage((short) gold);
    stats.setRoamSuccessScore((short) roamSuccess);
  }

  private static byte determineLevelups(Playerperformance playerperformance, JSONPlayer enemyPlayer, byte earlierLevelups) {
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
    return totalLevelups;
  }

  private static void handleFromBehind(JSONPlayer player, PlayerperformanceStats stats, int behindStart, int behindEnd,
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
    stats.setBehaviourFromBehindAhead((short) creepScoreFromBehind, (short) wardsFromBehind, (short) deathsFromBehind, (short) goldFromBehind,
        (short) xpFromBehind);
  }

  private static int searchForRechargeTimes(Playerperformance playerperformance, short firstTrinketSwap, List<Integer> yellowPlacementTimes) {
    int twoTrinkets = 0;
    val yellows = new ArrayList<>(yellowPlacementTimes);
    int lastWardCharged = 0;
    int chargedTime = 0;
    int currentAmount = 0;
    int currentMilli = 0;
    int currentLevel = 1;
    boolean wasNextLevel = false;
    boolean wasNextWard = false;
    boolean wasNextCharge = true;
    while (currentMilli < firstTrinketSwap * 1000) {
      if (wasNextCharge) {
        currentAmount++;
        lastWardCharged = currentMilli;
        wasNextCharge = false;

      } else if (wasNextLevel) {
        currentLevel++;
        if (chargedTime - lastWardCharged > getRechargeTimeAtLevel(currentLevel)) {
          chargedTime = lastWardCharged + getRechargeTimeAtLevel(currentLevel);
        }
        wasNextLevel = false;

      } else if (wasNextWard) {
        if (!yellows.isEmpty()) {
          yellows.remove(0);
        }
        currentAmount--;
        wasNextWard = false;
      }

      int nextLevel = getLevelupTime(playerperformance, currentLevel + 1);
      int nextWardPlaced = yellows.isEmpty() ? Integer.MAX_VALUE : yellows.get(0);
      if (nextLevel > nextWardPlaced && nextLevel > yellows.get(chargedTime)) {
        wasNextLevel = true;
      } else if (nextWardPlaced > nextLevel && nextWardPlaced > yellows.get(chargedTime)) {
        wasNextWard = true;
      } else if (currentAmount < 2) {
        wasNextCharge = true;
      }
      int min = Math.min(Math.min(nextLevel, nextWardPlaced), chargedTime);
      if (currentAmount == 2) {
        twoTrinkets += min - currentMilli;
      }
      currentMilli = min;
    }

    return twoTrinkets / 1000;
  }

  private static void getMidgameStats(JSONPlayer player, PlayerperformanceStats stats, int endMinute) {
    if (player.getLastMinute() > Const.EARLYGAME_UNTIL_MINUTE) {
      final int goldDifference = player.getLeadDifferenceAt(Const.EARLYGAME_UNTIL_MINUTE, Const.MIDGAME_UNTIL_MINUTE, TimelineStat.TOTAL_GOLD);
      final double goldPercentage = goldDifference * 1d / Const.MIDGAME_XP;
      stats.setMidgameGoldEfficiency(BigDecimal.valueOf(goldPercentage));

      final int xpDifference = player.getLeadDifferenceAt(Const.EARLYGAME_UNTIL_MINUTE, Const.MIDGAME_UNTIL_MINUTE, TimelineStat.EXPERIENCE);
      final double xpPercentage = xpDifference * 1d / Const.MIDGAME_XP;
      stats.setMidgameGoldXPEfficiency(BigDecimal.valueOf((xpPercentage + goldPercentage) / 2.0));
    }

    if (player.getLastMinute() > Const.MIDGAME_UNTIL_MINUTE) {
      final int leadDifference = player.getLeadDifferenceAt(27, endMinute, TimelineStat.LEAD);
      stats.setLategameLead((short) leadDifference);
    }
  }

  private static int getRechargeTimeAtLevel(int level) {
    final int levelProgress = (level - 1) / 17;
    final int rechargeDifference = Const.YELLOW_TRINKET_RECHARGE_TIME_START - Const.YELLOW_TRINKET_RECHARGE_TIME_END;
    return (1 - levelProgress) * rechargeDifference + Const.YELLOW_TRINKET_RECHARGE_TIME_END;
  }

  private static int getLevelupTime(Playerperformance playerperformance, int level) {
    return playerperformance.getLevelups().stream()
        .map(PlayerperformanceLevel::getTime)
        .filter(lvlLevel -> lvlLevel == level)
        .findFirst().orElse(0);
  }

  private static double determineAssistbountyFactor(int timestamp) {
    final int second = timestamp / 1000;
    if (second <= Const.ASSIST_FACTOR_INCREASE_SECOND) {
      return Const.ASSIST_FACTOR_START_VALUE;

    } else if (second >= Const.ASSIST_FACTOR_ENDING_SECOND) {
      return Const.ASSIST_FACTOR_END_VALUE;

    } else {
      final int currentSecond = second - Const.ASSIST_FACTOR_INCREASE_SECOND;
      final int timespanDifference = Const.ASSIST_FACTOR_ENDING_SECOND - Const.ASSIST_FACTOR_INCREASE_SECOND;
      final double progressToMaxValue = currentSecond * 1d / timespanDifference;
      final double valueDifference = Const.ASSIST_FACTOR_END_VALUE - Const.ASSIST_FACTOR_START_VALUE;
      return valueDifference * progressToMaxValue + Const.ASSIST_FACTOR_START_VALUE;
    }
  }

  /**
   * Bestimme das Startitem, dass ein Spieler gekauft hat
   *
   * @return Sekunden, wann es verkauft wurde
   */
  private static short determineStartItem(JSONPlayer player) {
    return player.getEvents(EventTypes.ITEM_SOLD).stream()
        .filter(event -> Item.find((short) event.getInt("itemId")).getType().equals(ItemType.STARTING))
        .map(event -> (short) (event.getInt("timestamp") / 1000))
        .findFirst().orElse((short) 0);
  }

  /**
   * Handle control of enemy Player
   *
   * @param player Spieler
   * @param minute Spielminute
   * @param stats eintragen hier
   */
  private static void handleControlled(JSONPlayer player, int minute, PlayerperformanceStats stats) {
    if (player.hasEnemy()) {
      final double statAt = player.getStatAt(minute, TimelineStat.ENEMY_CONTROLLED) / 1000.0;
      final double statAt1 = player.getEnemy().getStatAt(minute, TimelineStat.ENEMY_CONTROLLED) / 1000.0;
      stats.setEnemyControlAdvantage(statAt, statAt1);
    }

    if (player.hasEnemy()) {
      final double statAt = player.getStatAt(minute, TimelineStat.ENEMY_CONTROLLED) / 1000.0;
      final double statAt1 = player.getEnemy().getStatAt(15, TimelineStat.ENEMY_CONTROLLED) / 1000.0;
      stats.setEnemyControlAdvantageEarly(statAt, statAt1);
    }
  }

  private static byte determineAllObjectivesAmount(List<JSONTeam> jsonTeams) {
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

  private static byte determineStolenBarons(JSONTeam jsonTeam, JSONTeam enemyTeam) {
    byte stolenBarons = 0;
    for (JSONObject event : jsonTeam.getEvents(EventTypes.ELITE_MONSTER_KILL)) {
      val participatingIds = event.getJSONArray("assistingParticipantIds").toList()
          .stream().map(id -> (Integer) id - 1).collect(Collectors.toList());
      final long myTeam = participatingIds.stream().filter(jsonTeam::hasPlayer).count();
      final long enemies = participatingIds.stream().filter(enemyTeam::hasPlayer).count();
      if (enemies > myTeam) {
        stolenBarons++;
      }
    }
    return stolenBarons;
  }

  private static List<Short> searchForControlPurchase(JSONPlayer player) {
    return player.getEvents(EventTypes.ITEM_PURCHASED).stream()
        .filter(event -> Item.find((short) event.getInt("itemId")).getItemName().equals(Const.TRUESIGHT_WARD_NAME))
        .mapToInt(event -> event.getInt("timestamp"))
        .mapToObj(timestamp -> (short) (timestamp / 1000))
        .collect(Collectors.toCollection(ArrayList::new));
  }

  private static List<Short> searchForControlPlacements(JSONPlayer player) {
    return player.getEvents(EventTypes.WARD_PLACED).stream()
        .filter(event -> WardType.valueOf(event.getString("wardType")).equals(WardType.CONTROL_WARD))
        .mapToInt(event -> event.getInt("timestamp"))
        .mapToObj(timestamp -> (short) (timestamp / 1000))
        .collect(Collectors.toCollection(ArrayList::new));
  }

  private static short searchForTrinketSwap(JSONPlayer player) {
    return player.getEvents(EventTypes.ITEM_PURCHASED).stream()
        .filter(event -> Item.find((short) event.getInt("itemId")).getType().equals(ItemType.TRINKET))
        .filter(event -> !Item.find((short) event.getInt("itemId")).getItemName().equals(Const.DEFAULT_TRINKET_WARD_NAME))
        .mapToInt(event -> event.getInt("timestamp"))
        .mapToObj(timestamp -> (short) (timestamp / 1000))
        .findFirst().orElse((short) 0);
  }

  private static List<Integer> searchForTrinketPlacementsUntilSwap(JSONPlayer player, short second) {
    return player.getEvents(EventTypes.WARD_PLACED).stream()
        .filter(event -> event.getInt("timestamp") / 1000 <= second)
        .filter(event -> WardType.valueOf(event.getString("wardType")).equals(WardType.YELLOW_TRINKET))
        .mapToInt(event -> event.getInt("timestamp"))
        .boxed().collect(Collectors.toCollection(ArrayList::new));
  }

  private static short searchForFirstWardTime(JSONPlayer player) {
    return player.getEvents(EventTypes.WARD_PLACED).stream()
        .map(event -> (short) (event.getInt("timestamp") / 1000))
        .findFirst()
        .orElse((short) 0);
  }

  /**
   * Created by Lara on 09.04.2022 for web
   * <p>
   * TINYINT(3) : -/.///.///.128 → /.///.///.127 (/.///.///.255)
   * SMALLINT(5) : -/.///./32.768 → /.///./32.767 (/.///./65.535)
   * MEDIUMINT(7): -/.//8.388.608 → /.//8.388.607 (/./16.777.215)
   * INTEGER(10) : -2.147.483.648 → 2.147.483.647 (4.294.967.295)
   * <p>
   * Byte:
   * -TINYINT(3) : -128 → 127
   * Short:
   * -TINYINT(3) : (255)
   * -SMALLINT(5) : -32.768 → 32.767
   * Int:
   * -SMALLINT(5) : (/.///./65.535)
   * -MEDIUMINT(7): -/.//8.388.608 → /.//8.388.607 (/./16.777.215)
   * -INTEGER(10) : -2.147.483.648 → 2.147.483.647.
   */
  private static Playerperformance handlePerformance(JSONPlayer p, JSONPlayer e) {
    final int shiedling = p.getMedium(StoredStat.DAMAGE_HEALING_SHIELDING) != null ? p.getMedium(StoredStat.DAMAGE_HEALING_SHIELDING) :
        p.getMedium(StoredStat.DAMAGE_TEAM_HEAL) + p.getMedium(StoredStat.DAMAGE_TEAM_SHIELD);
    final byte stolen = (byte) (p.getTiny(StoredStat.OBJECTIVES_STOLEN) + p.getTiny(StoredStat.OBJECTIVES_STOLEN_TAKEDOWNS));
    final short creeps = (short) (p.getSmall(StoredStat.CREEP_SCORE_JUNGLE) + p.getSmall(StoredStat.CREEP_SCORE_LANE));
    final boolean firstBlood = p.getBool(StoredStat.FIRST_BLOOD) || p.getBool(StoredStat.FIRST_BLOOD_ASSIST);
    final Byte visionScore = e != null ? (byte) (p.getSmall(StoredStat.VISION_SCORE) - e.getSmall(StoredStat.VISION_SCORE)) :
        null;
    final byte controlWards = p.getTiny(StoredStat.CONTROL_WARDS_PLACED, StoredStat.CONTROL_WARDS_BOUGHT);
    final byte wardClear = p.getTiny(StoredStat.WARDS_TAKEDOWN, StoredStat.WARDS_CLEARED);
    val playerperformance = new Playerperformance(Lane.valueOf(p.get(StoredStat.LANE)),
        p.getSmall(StoredStat.Q_USAGE), p.getSmall(StoredStat.W_USAGE), p.getSmall(StoredStat.E_USAGE),
        p.getSmall(StoredStat.R_USAGE), p.getMedium(StoredStat.DAMAGE_MAGICAL), p.getMedium(StoredStat.DAMAGE_PHYSICAL),
        p.getMedium(StoredStat.DAMAGE_TOTAL), p.getMedium(StoredStat.DAMAGE_TAKEN), p.getMedium(StoredStat.DAMAGE_MITIGATED),
        p.getMedium(StoredStat.DAMAGE_HEALED), shiedling, p.getTiny(StoredStat.KILLS), p.getTiny(StoredStat.DEATHS),
        p.getTiny(StoredStat.ASSISTS), p.getTiny(StoredStat.KILLS_DOUBLE), p.getTiny(StoredStat.KILLS_TRIPLE),
        p.getTiny(StoredStat.KILLS_QUADRA), p.getTiny(StoredStat.KILLS_PENTA), p.getSmall(StoredStat.TIME_ALIVE),
        p.getSmall(StoredStat.TIME_DEAD), p.getSmall(StoredStat.WARDS_PLACED), stolen, p.getMedium(StoredStat.OBJECTIVES_DAMAGE),
        p.getTiny(StoredStat.BARON_KILLS), p.getMedium(StoredStat.GOLD_TOTAL), p.getMedium(StoredStat.EXPERIENCE_TOTAL), creeps,
        p.getSmall(StoredStat.ITEMS_BOUGHT), firstBlood, controlWards, wardClear, p.getSmall(StoredStat.VISION_SCORE),
        p.getTiny(StoredStat.TOWERS_TAKEDOWNS));
    if (visionScore != null) playerperformance.setVisionscoreAdvantage(visionScore);
    playerperformance.setSpellsHit(p.getSmall(StoredStat.SPELL_LANDED));
    playerperformance.setSpellsDodged(p.getSmall(StoredStat.SPELL_DODGE));
    playerperformance.setQuickDodged(p.getSmall(StoredStat.SPELL_DODGE_QUICK));
    playerperformance.setSoloKills(p.getTiny(StoredStat.SOLO_KILLS));
    playerperformance.setLevelUpAllin(p.getTiny(StoredStat.LEVELUP_TAKEDOWNS));
    playerperformance.setFlashAggressive(p.getTiny(StoredStat.AGGRESSIVE_FLASH));
    playerperformance.setTeleportKills(p.getTiny(StoredStat.TELEPORT_KILLS));
    playerperformance.setImmobilizations(p.getSmall(StoredStat.IMMOBILIZATIONS));
    if (p.getSmall(StoredStat.CONTROL_WARDS_UPTIME) != null) {
      playerperformance.setControlWardUptime((short) (p.getSmall(StoredStat.CONTROL_WARDS_UPTIME) * 60));
    }
    playerperformance.setWardsGuarded(p.getTiny(StoredStat.WARDS_GUARDED));
    if (p.getSmall(StoredStat.FIRST_TOWER_TIME) != null &&
        (p.getBool(StoredStat.FIRST_TOWER) || p.getBool(StoredStat.FIRST_TOWER_ASSIST))) {
      playerperformance.setFirstturretAdvantage(p.getSmall(StoredStat.FIRST_TOWER_TIME));
    } else if (e != null && e.getSmall(StoredStat.FIRST_TOWER_TIME) != null &&
        (e.getBool(StoredStat.FIRST_TOWER) || e.getBool(StoredStat.FIRST_TOWER_ASSIST))) {
      playerperformance.setFirstturretAdvantage((short) (e.getSmall(StoredStat.FIRST_TOWER_TIME) * -1));
    }
    if (e != null) playerperformance.setBaronExecutes(e.getTiny(StoredStat.BARON_EXECUTES));
    playerperformance.setBuffsStolen(p.getTiny(StoredStat.BUFFS_STOLEN));
    playerperformance.setInitialScuttles(p.getTiny(StoredStat.SCUTTLES_INITIAL));
    playerperformance.setTotalScuttles(p.getTiny(StoredStat.SCUTTLES_TOTAL));
    playerperformance.setSplitpushedTurrets(p.getTiny(StoredStat.TOWERS_SPLITPUSHED));
    playerperformance.setTeamInvading(p.getTiny(StoredStat.INVADING_KILLS));
    playerperformance.setGanksEarly(p.getTiny(StoredStat.LANER_ROAMS, StoredStat.JUNGLER_ROAMS));
    playerperformance.setDivesDone(handleDives(p, e, StoredStat.DIVES_DONE, StoredStat.DIVES_PROTECTED));
    playerperformance.setDivesSuccessful(p.getTiny(StoredStat.DIVES_DONE));
    playerperformance.setDivesGotten(handleDives(p, e, StoredStat.DIVES_PROTECTED, StoredStat.DIVES_DONE));
    playerperformance.setDivesProtected(p.getTiny(StoredStat.DIVES_PROTECTED));
    playerperformance.setBountyGold((short) (p.getSmall(StoredStat.BOUNTY_GOLD) -
        (e != null && e.getSmall(StoredStat.BOUNTY_GOLD) == null ? 0 : p.getSmall(StoredStat.BOUNTY_GOLD))));
    playerperformance.setCreepsEarly(p.getTiny(StoredStat.CREEP_SCORE_LANE_EARLY, StoredStat.CREEP_SCORE_JUNGLE_EARLY));
    playerperformance.setCreepsInvade(p.getSmall(StoredStat.CREEP_INVADED));
    playerperformance.setTurretplates(p.getTiny(StoredStat.TOWERS_PLATES));
    playerperformance.setFlamehorizonAdvantage(p.getTiny(StoredStat.CREEP_SCORE_ADVANTAGE));
    final short mejaisCompleted = (short) ((p.getSmall(StoredStat.MEJAIS_TIME) == null ? 0 : p.getSmall(StoredStat.MEJAIS_TIME))
        - (e != null && e.getSmall(StoredStat.MEJAIS_TIME) != null ? e.getSmall(StoredStat.MEJAIS_TIME) : 0));
    if (mejaisCompleted != 0) playerperformance.setMejaisCompleted(mejaisCompleted);
    playerperformance.setOutplayed(p.getTiny(StoredStat.OUTPLAYED));
    playerperformance.setDragonTakedowns(p.getTiny(StoredStat.DRAGON_TAKEDOWNS));
    playerperformance.setFastestLegendary(p.getSmall(StoredStat.LEGENDARY_FASTEST));
    playerperformance.setGankSetups(p.getTiny(StoredStat.GANK_SETUP));
    playerperformance.setInitialBuffs(p.getTiny(StoredStat.BUFFS_INITIAL));
    playerperformance.setEarlyKills(p.getTiny(StoredStat.KILLS_EARLY_JUNGLER, StoredStat.KILLS_EARLY_LANER));
    playerperformance.setJunglerKillsAtObjective(p.getTiny(StoredStat.OBJECTIVES_JUNGLERKILL));
    playerperformance.setAmbush(p.getTiny(StoredStat.AMBUSH));
    playerperformance.setEarlyTurrets(p.getTiny(StoredStat.TOWERS_EARLY));
    playerperformance.setLevelLead(p.getTiny(StoredStat.EXPERIENCE_ADVANTAGE));
    playerperformance.setPicksMade(p.getTiny(StoredStat.PICK_KILL));
    playerperformance.setAssassinated(p.getTiny(StoredStat.ASSASSINATION));
    playerperformance.setSavedAlly(p.getTiny(StoredStat.GUARD_ALLY));
    playerperformance.setSurvivedClose((byte) (p.getTiny(StoredStat.SURVIVED_CLOSE) +
        p.getTiny(StoredStat.SURVIVED_HIGH_DAMAGE) + p.getTiny(StoredStat.SURVIVED_HIGH_CROWDCONTROL)));
    return playerperformance;
  }

  private static byte handleDives(JSONPlayer player, JSONPlayer enemy, StoredStat divesDone, StoredStat divesProtected) {
    return (byte) ((player.getTiny(divesDone) == null ? 0 : player.getTiny(divesDone)) +
        ((enemy != null && enemy.getTiny(divesProtected) != null) ? enemy.getTiny(divesProtected) : 0));
  }

  private static void handleChampionsPicked(JSONPlayer player, JSONPlayer enemy, Playerperformance playerperformance) {
    val championOwnName = player.get(StoredStat.CHAMPION);
    val championOwn = Champion.find(championOwnName);
    championOwn.addPlayerperformance(playerperformance, true);
    if (enemy != null) {
      val championEnemyName = enemy.get(StoredStat.CHAMPION);
      val championEnemy = Champion.find(championEnemyName);
      championEnemy.addPlayerperformance(playerperformance, false);
    }
  }

  private static List<JSONPlayer> determinePlayers(QueueType queueType, JSONTeam jsonTeam) {
    val players = new ArrayList<JSONPlayer>();
    if (queueType.equals(QueueType.TOURNEY)) {
      players.addAll(jsonTeam.getAllPlayers());
    } else if (queueType.equals(QueueType.CLASH)) {
      players.addAll(jsonTeam.getListedPlayers());
    } else if (queueType.equals(QueueType.OTHER)) {
      players.addAll(jsonTeam.getListedPlayers());
    }
    return players;
  }

  private static List<JSONTeam> getJsonTeams(JSONArray participants, Map<Integer, JSONObject> playerInfo) {
    val jsonTeams = Arrays.asList(new JSONTeam(1), new JSONTeam(2));
    for (int i = 0; i < participants.length(); i++) {
      val participant = participants.getJSONObject(i);
      val puuid = participant.getString("puuid");
      final int teamId = participant.getInt("teamId");
      val jsonPlayer = new JSONPlayer(i, participant, puuid, teamId == 100);
      val team = JSONTeam.getTeam(teamId);
      for (int timestamp : playerInfo.keySet()) {
        int minute = timestamp / 60_000;
        val frame = playerInfo.get(timestamp);
        val infoStats = frame.getJSONObject(String.valueOf(jsonPlayer.getId() + 1));
        jsonPlayer.addInfo(infoStats, minute);
      }

      if (team != null) {
        team.addPlayer(jsonPlayer);
      }
    }

    for (JSONObject event : allEvents) {
      for (int pId : getPlayersOfEvent(event)) {
        final JSONPlayer player = JSONPlayer.getPlayer(pId - 1);
        if (player != null) {
          player.addEvent(event);
        }
      }

      final int tId = getTeamOfEvent(event);
      val team = JSONTeam.getTeam(tId);
      if (team != null) {
        team.addEvent(event);
      }
    }

    return jsonTeams;
  }

  @NotNull
  private static List<Integer> getPlayersOfEvent(JSONObject allEvent) {
    val partIds = new ArrayList<Integer>();
    if (allEvent.has("participantId")) {
      partIds.add(allEvent.getInt("participantId"));
    }
    if (allEvent.has("killerId")) {
      partIds.add(allEvent.getInt("killerId"));
    }
    if (allEvent.has("victimId")) {
      partIds.add(allEvent.getInt("victimId"));
    }
    if (allEvent.has("assistingParticipantIds")) {
      partIds.addAll(allEvent.getJSONArray("assistingParticipantIds").toList().stream()
          .map(id -> (Integer) id).collect(Collectors.toList()));
    }
    if (allEvent.has("creatorId")) {
      partIds.add(allEvent.getInt("creatorId"));
    }
    return partIds;
  }

  private static int getTeamOfEvent(JSONObject allEvent) {
    if (allEvent.has("teamId")) {
      return allEvent.getInt("teamId");

    } else if (allEvent.has("killerTeamId")) {
      return allEvent.getInt("killerTeamId");

    }
    return 0;
  }

  private static Teamperformance handleTeam(JSONTeam jsonTeam) {
    val jsonObject = jsonTeam.getTeamObject();
    val objectives = jsonObject.getJSONObject("objectives");
    val champion = objectives.getJSONObject("champion");
    val tower = objectives.getJSONObject("tower");
    val dragon = objectives.getJSONObject("dragon");
    val inhibitor = objectives.getJSONObject("inhibitor");
    val riftHerald = objectives.getJSONObject("riftHerald");
    val baron = objectives.getJSONObject("baron");
    final int teamId = jsonObject.getInt("teamId");
    final boolean win = jsonObject.getBoolean("win");

    final int totalDamage = jsonTeam.getAllPlayers().stream().mapToInt(player -> player.getMedium(StoredStat.DAMAGE_TOTAL)).sum();
    final int damageTaken = jsonTeam.getAllPlayers().stream().mapToInt(player -> player.getMedium(StoredStat.DAMAGE_TAKEN)).sum();
    final int totalGold = jsonTeam.getAllPlayers().stream().mapToInt(player -> player.getMedium(StoredStat.GOLD_TOTAL)).sum();
    final int totalCs = jsonTeam.getAllPlayers().stream()
        .mapToInt(player -> player.getMedium(StoredStat.CREEP_SCORE_LANE) + player.getMedium(StoredStat.CREEP_SCORE_JUNGLE)).sum();
    final short earliestDragon = (short) jsonTeam.getAllPlayers().stream().mapToInt(p -> p.getSmall(StoredStat.DRAGON_TIME)).min().orElse(-1);
    final byte atSpawn = (byte) jsonTeam.getAllPlayers().stream().mapToInt(player -> player.getSmall(StoredStat.OBJECTIVES_ON_SPAWN)).sum();
    final byte nearJgl = (byte) jsonTeam.getAllPlayers().stream().mapToInt(player -> player.getSmall(StoredStat.OBJECTIVES_50_50)).sum();
    final byte quest = (byte) jsonTeam.getAllPlayers().stream().filter(player -> player.getSmall(StoredStat.QUEST_FAST) == 1).count();
    final byte herald = (byte) jsonTeam.getAllPlayers().stream().mapToInt(player -> player.getTiny(StoredStat.RIFT_TURRETS_MULTI)).sum();
    final short acetime = (short) jsonTeam.getAllPlayers().stream().mapToInt(p -> p.getSmall(StoredStat.ACE_TIME)).min().orElse(-1);
    final byte killDeficit = (byte) jsonTeam.getAllPlayers().stream().mapToInt(p -> p.getTiny(StoredStat.KILLS_DISADVANTAGE)).sum();

    final Teamperformance teamperformance = new Teamperformance(teamId == 100, win, totalDamage, damageTaken,
        totalGold, totalCs, champion.getInt("kills"), tower.getInt("kills"), dragon.getInt("kills"), inhibitor.getInt("kills"),
        riftHerald.getInt("kills"), baron.getInt("kills"), tower.getBoolean("first"), dragon.getBoolean("first"));
    final JSONPlayer jsonPlayer = jsonTeam.getAllPlayers().get(0);
    if (jsonPlayer.getMedium(StoredStat.PERFECT_SOUL) != null)
      teamperformance.setPerfectSoul(jsonPlayer.getMedium(StoredStat.PERFECT_SOUL) == 1);
    teamperformance.setSurrendered(jsonPlayer.getMedium(StoredStat.SURRENDER) == 1);
    if (jsonPlayer.getSmall(StoredStat.RIFT_TURRETS) != null)
      teamperformance.setRiftTurrets(jsonPlayer.getSmall(StoredStat.RIFT_TURRETS) / 5d);
    if (jsonPlayer.getSmall(StoredStat.ELDER_TIME) != null) teamperformance.setElderTime(jsonPlayer.getSmall(StoredStat.ELDER_TIME));
    if (jsonPlayer.getSmall(StoredStat.BARON_POWERPLAY) != null)
      teamperformance.setElderTime(jsonPlayer.getSmall(StoredStat.BARON_POWERPLAY));
    teamperformance.setEarlyAces(jsonPlayer.getTiny(StoredStat.ACE_EARLY));
    teamperformance.setBaronTime(jsonPlayer.getTiny(StoredStat.BARON_TIME));
    if (earliestDragon != -1) teamperformance.setFirstDragonTime(earliestDragon);
    teamperformance.setObjectiveAtSpawn(atSpawn);
    teamperformance.setObjectiveContests(nearJgl);
    teamperformance.setQuestCompletedFirst(quest > 0);
    teamperformance.setInhibitorsTime(jsonPlayer.getSmall(StoredStat.INHIBITORS_TAKEN));
    teamperformance.setFlawlessAces(jsonPlayer.getTiny(StoredStat.ACE_FLAWLESS));
    teamperformance.setRiftOnMultipleTurrets(herald);
    if (acetime != -1) teamperformance.setFastestAcetime(acetime);
    if (killDeficit > 0) teamperformance.setKillDeficit(killDeficit);

    return teamperformance;
  }

  private static Game handleGame(JSONObject info, String gameId, Gametype gametype) {
    final long startMillis = info.getLong("gameStartTimestamp");
    val start = new Date(startMillis);
    final long endMillis = info.getLong("gameStartTimestamp");
    final short duration = (short) (endMillis - startMillis / 1000);
    return Game.get(new Game(gameId, start, duration), gametype);
  }

  private static HashMap<Integer, JSONObject> loadTimeline(JSONObject timeLineObject) {
    val playerInfo = new HashMap<Integer, JSONObject>();
    val timelineInfo = timeLineObject.getJSONObject("info");
    val timelineFrames = timelineInfo.getJSONArray("frames");
    for (int i = 0; i < timelineFrames.length(); i++) {
      val frameObject = timelineFrames.getJSONObject(i);
      val eventArray = frameObject.getJSONArray("events");
      val event = timelineFrames.getJSONObject(0);
      if (Arrays.stream(EventTypes.values()).anyMatch(type2 -> type2.name().equals(event.getString("type")))) {
        allEvents = IntStream.range(0, eventArray.length()).mapToObj(eventArray::getJSONObject).collect(Collectors.toList());
      }
      if (frameObject.has("participantFrames") && !frameObject.isNull("participantFrames")) {
        playerInfo.put(frameObject.getInt("timestamp"), frameObject.getJSONObject("participantFrames"));
      }
    }
    return playerInfo;
  }

  private static List<Fight> handleGameEvents(Game game) {
    for (JSONObject event : allEvents) {
      val type = EventTypes.valueOf(event.getString("type"));
      final int timestamp = event.getInt("timestamp");
      if (type.equals(EventTypes.PAUSE_START)) {
        handlePauseStart(timestamp, game);

      } else if (type.equals(EventTypes.PAUSE_END)) {
        handlePauseEnd(timestamp, game);

      }
    }
    val kills = allEvents.stream()
        .map(Kill::getKillFromEvent)
        .sorted(Comparator.comparingInt(kill1 -> kill1 != null ? kill1.getTimestamp() : 0))
        .collect(Collectors.toCollection(ArrayList::new));

    val fights = new ArrayList<Fight>();
    for (Kill kill : kills) {
      val validFight = fights.stream().filter(fight -> fight.getLastTimestamp() >= kill.getTimestamp() - Const.TIME_BETWEEN_FIGHTS * 60_000)
          .filter(fight -> Util.distance(fight.getLastPosition(), kill.getPosition()) <= Const.DISTANCE_BETWEEN_FIGHTS)
          .findFirst().orElse(null);
      if (validFight == null) {
        fights.add(new Fight(kill));
      } else {
        validFight.addKill(kill);
      }
    }

    val returnFights = new ArrayList<Fight>();
    for (Fight fight : fights) {
      if (fight.getFighttype().equals(Fighttype.DUEL)) {
        returnFights.add(fight.getDuel());
      } else if (fight.getFighttype().equals(Fighttype.PICK)) {
        returnFights.add(fight.getPick());
      } else if (fight.getFighttype().equals(Fighttype.SKIRMISH)) {
        returnFights.add(fight.getSkirmish());
      } else if (fight.getFighttype().equals(Fighttype.TEAMFIGHT)) {
        returnFights.add(fight.getTeamfight());
      }
    }

    return returnFights;
  }

  private static void handlePauseStart(int timestamp, Game game) {
    if (game.getNotOpened().isEmpty()) {
      game.addPause(new GamePause(timestamp, 0));
    } else {
      game.getNotOpened().get(0).setStart(timestamp);
    }
  }

  private static void handlePauseEnd(int timestamp, Game game) {
    if (game.getNotClosed().isEmpty()) {
      game.addPause(new GamePause(0, timestamp));
    } else {
      game.getNotClosed().get(0).setEnd(timestamp);
    }
  }

  private static void handleTeamEvents(Teamperformance teamperformance) {
    for (JSONObject event : allEvents) {
      val type = EventTypes.valueOf(event.getString("type"));
      final int timestamp = event.getInt("timestamp");
      if (type.equals(EventTypes.OBJECTIVE_BOUNTY_PRESTART)) {
        handleBountyStart(timestamp, teamperformance);
      } else if (type.equals(EventTypes.OBJECTIVE_BOUNTY_FINISH)) {
        handleBountyEnd(timestamp, teamperformance);
      } else if (type.equals(EventTypes.DRAGON_SOUL_GIVEN)) {
        teamperformance.setSoul(DragonSoul.valueOf(event.getString("name").toUpperCase()));
      }
    }
  }

  private static void handleBountyStart(int timestamp, Teamperformance teamperformance) {
    if (teamperformance.getNotOpened().isEmpty()) {
      teamperformance.addBounty(new TeamperformanceBounty(timestamp, 0));
    } else {
      teamperformance.getNotOpened().get(0).setStart(timestamp);
    }
  }

  private static void handleBountyEnd(int timestamp, Teamperformance teamperformance) {
    if (teamperformance.getNotClosed().isEmpty()) {
      teamperformance.addBounty(new TeamperformanceBounty(0, timestamp));
    } else {
      teamperformance.getNotClosed().get(0).setEnd(timestamp);
    }
  }

  private static void handlePlayerEvents(JSONPlayer player, Playerperformance playerperformance) {
    val items = IntStream.range(1, 8).mapToObj(i -> player.getMedium(StoredStat.valueOf("ITEM_" + i)))
        .filter(itemId -> itemId != null && itemId != 0).collect(Collectors.toList());
    for (JSONObject event : allEvents) {
      val type = EventTypes.valueOf(event.getString("type"));
      final int timestamp = event.getInt("timestamp");
      val role = handleEventOfPlayer(player, event);

      if (role != null) {
        if (type.equals(EventTypes.LEVEL_UP)) {
          playerperformance.addLevelup(new PlayerperformanceLevel((byte) event.getInt("level"), timestamp));
        } else if (type.equals(EventTypes.ITEM_PURCHASED)) {
          final int itemId = event.getInt("itemId");
          playerperformance.addItem(Item.find((short) itemId), items.contains(itemId), timestamp);
        } else if (type.equals(EventTypes.CHAMPION_KILL)) {
          playerperformance.addKill(handleChampionKills(playerperformance, event, timestamp, role));
        } else if (type.equals(EventTypes.TURRET_PLATE_DESTROYED) || type.equals(EventTypes.BUILDING_KILL) ||
            type.equals(EventTypes.ELITE_MONSTER_KILL)) {
          handleObjectives(playerperformance, event, type, timestamp, role);
        }
      }
    }

    for (JSONObject event : player.getEvents(EventTypes.CHAMPION_SPECIAL_KILL)) {
      val role = handleEventOfPlayer(player, event);
      if (role != null) {
        final int timestamp = event.getInt("timestamp");
        val kill = PlayerperformanceKill.find(playerperformance.getTeamperformance().getGame(), timestamp);
        kill.setType(KillType.valueOf(event.getString("killType").replace("KILL_", "")));
      }
    }
  }

  private static KillRole handleEventOfPlayer(JSONPlayer player, JSONObject event) {
    final int playerId = event.has("killerId") ? event.getInt("killerId") : event.getInt("participantId");
    val participatingIds = new ArrayList<Integer>();
    if (event.has("assistingParticipantIds")) {
      participatingIds.addAll(event.getJSONArray("assistingParticipantIds").toList()
          .stream().map(id -> (Integer) id).collect(Collectors.toList()));
    }
    final int victimId = event.has("victimId") ? event.getInt("victimId") : 0;
    if (playerId == player.getId() + 1) {
      return KillRole.KILLER;
    } else if (participatingIds.contains(playerId + 1)) {
      return KillRole.ASSIST;
    } else if (victimId == player.getId() + 1) {
      return KillRole.VICTIM;
    }
    return null;
  }

  private static PlayerperformanceKill handleChampionKills(Playerperformance playerperformance, JSONObject event, int timestamp,
                                                           KillRole role) {
    val positionObject = event.getJSONObject("position");
    final int xCoordinate = positionObject.getInt("x");
    final int yCoordinate = positionObject.getInt("y");
    val position = new Position((short) xCoordinate, (short) yCoordinate);

    val kill = playerperformance == null ? null : PlayerperformanceKill.find(playerperformance.getTeamperformance().getGame(), timestamp);
    final int killId = kill == null ? PlayerperformanceKill.lastId() + 1 : kill.getId();

    return new PlayerperformanceKill(killId, timestamp, position, (short) event.getInt("bounty"), role, KillType.NORMAL,
        (byte) event.getInt("killStreakLength"));
  }

  private static void handleObjectives(Playerperformance playerperformance, JSONObject event, EventTypes type, int timestamp, KillRole role) {
    val lane = event.has("laneType") ? Lane.findLane(event.getString("laneType")) : null;
    if (type.equals(EventTypes.TURRET_PLATE_DESTROYED)) {
      playerperformance.addObjective(new PlayerperformanceObjective(timestamp, ObjectiveSubtype.OUTER_TURRET, lane,
          (short) 160, role));
    } else {
      var query = event.has("monsterSubType") ? "monsterSubType" : "monsterType";
      if (type.equals(EventTypes.BUILDING_KILL)) {
        query = event.has("towerType") ? "towerType" : "buildingType";
      }
      val objectiveType = ObjectiveSubtype.valueOf(event.getString(query).replace("_BUILDING", ""));
      final short bounty = (short) event.getInt("bounty");
      playerperformance.addObjective(new PlayerperformanceObjective(timestamp, objectiveType, lane, bounty, role));
    }
  }

  private static void handlePlayerInfo(JSONPlayer player, Playerperformance playerperformance) {
    player.getInfos().stream()
        .filter(Objects::nonNull)
        .mapToInt(object -> player.getInfos().indexOf(object))
        .mapToObj(minute -> getPlayerperformanceInfo(player, minute))
        .forEach(playerperformance::addInfo);
  }

  private static PlayerperformanceInfo getPlayerperformanceInfo(JSONPlayer player, int minute) {
    final JSONObject infoStats = getEventObject(player, minute);
    if (infoStats == null) return null;

    val positionObject = infoStats.getJSONObject("position");
    val position = new Position(positionObject.getInt("x"), positionObject.getInt("y"));
    final int xp = player.getStatAt(minute, TimelineStat.EXPERIENCE);
    final int totalGold = player.getStatAt(minute, TimelineStat.TOTAL_GOLD);
    final int currentGold = player.getStatAt(minute, TimelineStat.CURRENT_GOLD);
    final double enemyControlled = player.getStatAt(minute, TimelineStat.ENEMY_CONTROLLED) / 1000.0;
    final int lead = player.getLeadAt(minute, TimelineStat.LEAD);
    final int creepScore = player.getStatAt(minute, TimelineStat.CREEP_SCORE);
    final int damageToChampions = player.getStatAt(minute, TimelineStat.DAMAGE);
    final int maxHealth = player.getStatAt(minute, TimelineStat.TOTAL_HEALTH);
    final int currentHealth = player.getStatAt(minute, TimelineStat.CURRENT_HEALTH);
    final int maxResource = player.getStatAt(minute, TimelineStat.TOTAL_RESOURCE);
    final int currentResource = player.getStatAt(minute, TimelineStat.CURRENT_RESOURCE);
    final int moveSpeed = player.getStatAt(minute, TimelineStat.MOVEMENT_SPEED);
    return new PlayerperformanceInfo((short) minute, totalGold, (short) currentGold, enemyControlled, position, xp, (short) lead,
        (short) creepScore, damageToChampions, (short) maxHealth, (short) currentHealth, (short) maxResource, (short) currentResource,
        (short) moveSpeed);
  }

  @Nullable
  private static JSONObject getEventObject(JSONPlayer player, int minute) {
    final JSONObject infoStats;
    if (player.getInfos().size() > minute) {
      infoStats = player.getInfos().get(minute);
    } else if (!player.getInfos().isEmpty()) {
      infoStats = player.getLastInfo();
    } else {
      return null;
    }
    return infoStats;
  }

  private static void handleSummonerspells(JSONPlayer player, Playerperformance performance) {
    performance.addSummonerspell(Summonerspell.find(player.getTiny(StoredStat.SUMMONER1_ID)), player.getTiny(StoredStat.SUMMONER1_AMOUNT));
    performance.addSummonerspell(Summonerspell.find(player.getTiny(StoredStat.SUMMONER2_ID)), player.getTiny(StoredStat.SUMMONER2_AMOUNT));
  }
}
