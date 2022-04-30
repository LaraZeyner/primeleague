package de.xeri.league.util.io.riot;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
import de.xeri.league.models.match.Playerperformance;
import de.xeri.league.models.match.PlayerperformanceInfo;
import de.xeri.league.models.match.PlayerperformanceKill;
import de.xeri.league.models.match.PlayerperformanceLevel;
import de.xeri.league.models.match.PlayerperformanceObjective;
import de.xeri.league.models.match.PlayerperformanceStats;
import de.xeri.league.models.match.Position;
import de.xeri.league.models.match.ScheduledGame;
import de.xeri.league.models.match.Teamperformance;
import de.xeri.league.models.match.TeamperformanceBounty;
import de.xeri.league.util.Const;
import de.xeri.league.util.Data;
import de.xeri.league.util.io.json.JSON;
import lombok.val;
import lombok.var;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Lara on 08.04.2022 for web
 */
public final class RiotGameRequester {
  private static List<JSONTeam> jsonTeams;

  private static List<JSONPlayer> getJSONPlayers() {
    return jsonTeams.stream()
        .flatMap(jsonTeam -> jsonTeam.getAllPlayers().stream())
        .collect(Collectors.toList());
  }

  private static List<JSONPlayer> findJSONPlayersExcept(String value, JSONPlayer jsonPlayer) {
    return findJSONPlayersWith(value).stream()
        .filter(player -> player.getId() != jsonPlayer.getId())
        .collect(Collectors.toList());
  }

  private static List<JSONPlayer> findJSONPlayersWith(String value) {
    return getJSONPlayers().stream()
        .filter(player -> player.get(StoredStat.LANE).equals(value))
        .collect(Collectors.toList());
  }

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
  }

  private static boolean isValidGame(JSON gameJson, JSON timelineJson, QueueType queueType) {
    val gameData = gameJson.getJSONObject();
    val info = gameData.getJSONObject("info");
    final int queueId = info.getInt("queueId");
    val participants = info.getJSONArray("participants");
    if ((queueId == 0 || queueId == 400 || queueId == 410 || queueId == 420 || queueId == 430 || queueId == 700) && participants.length() == 10) {

      val metadata = gameData.getJSONObject("metadata");
      val gameId = metadata.getString("matchId");
      val events = new ArrayList<JSONObject>();
      val playerInfo = new HashMap<Integer, JSONObject>();
      if (timelineJson.getJSONObject() != null) {
        loadTimeline(events, playerInfo, timelineJson.getJSONObject());
      }

      val gametype = Gametype.find((info.has("tournamentCode") && !info.isNull("tournamentCode")) ? (short) -1 : (short) queueId);
      val game = handleGame(info, gameId, gametype);
      gametype.addGame(game, gametype);
      handleGameEvents(events, game);

      jsonTeams = getJsonTeams(participants);
      for (int i = 0; i < jsonTeams.size(); i++) {
        val jsonTeam = jsonTeams.get(i);
        val teams = info.getJSONArray("teams");
        jsonTeam.setTeamObject(teams.getJSONObject(i));
        if (jsonTeam.doesExist()) {
          val teamperformance = handleTeam(jsonTeam);
          val team = jsonTeam.getMostUsedTeam(queueType);
          game.addTeamperformance(teamperformance, team);
          handleTeamEvents(events, teamperformance);

          val players = determinePlayers(queueType, jsonTeam);
          players.forEach(player -> handlePlayer(player, teamperformance, events, playerInfo));
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

  private static void handlePlayer(JSONPlayer player, Teamperformance teamperformance, List<JSONObject> events,
                                   Map<Integer, JSONObject> playerInfo) {
    val enemyPlayer = getEnemyPlayer(player);
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

      handlePlayerEvents(events, player, playerperformance);
      handlePlayerInfo(playerInfo, player, playerperformance);

      handlePlayerStats(playerperformance, teamperformance, jsonTeams, events, playerInfo, player);
    }
  }

  private static void handlePlayerStats(Playerperformance playerperformance, Teamperformance teamperformance, List<JSONTeam> jsonTeams,
                                        List<JSONObject> events, Map<Integer, JSONObject> playerInfo, JSONPlayer player) {
    final int pId = player.getId() + 1;
    val stats = new PlayerperformanceStats(playerperformance);
    final JSONPlayer enemyPlayer = getEnemyPlayer(player);

    val jsonTeam = jsonTeams.get(teamperformance.isFirstPick() ? 0 : 1);
    val enemyTeam = jsonTeams.get(teamperformance.isFirstPick() ? 1 : 0);
    byte allObjectivesAmount = determineAllObjectivesAmount(jsonTeams);
    stats.setObjectivesStolenAndContested(playerperformance, allObjectivesAmount);
    stats.setObjectivesKilledJunglerBefore(playerperformance, allObjectivesAmount);
    byte stolenBarons = determineStolenBarons(events, jsonTeam, enemyTeam);
    stats.setBaronTakedownsAttempts(playerperformance, stolenBarons);

    short firstControlWardTime = 0;
    final short firstWardTime = searchForFirstWardTime(events, pId, firstControlWardTime);
    stats.setFirstWardTime(firstWardTime);
    stats.setFirstControlwardTime(firstControlWardTime);

    final short firstTrinketSwap = searchForTrinketSwap(events, pId);
    stats.setFirstTrinketSwap(firstTrinketSwap);

    val purchases = searchForControlPurchase(events, pId);
    val placements = searchForControlPlacements(events, pId);
    final short averageControlTime = (short) IntStream.range(0, placements.size())
        .filter(i -> purchases.size() > i)
        .map(i -> placements.get(i) - purchases.get(i))
        .boxed().collect(Collectors.toCollection(ArrayList::new))
        .stream().mapToInt(Integer::intValue).average()
        .orElse(0);
    stats.setControlWardInventoryTime(averageControlTime);

    final int totalMitigation = jsonTeam.getAllPlayers().stream()
        .mapToInt(p -> p.getMedium(StoredStat.DAMAGE_MITIGATED))
        .sum();
    stats.setTeamDamageMitigated(playerperformance, totalMitigation);

    int bountyDrop = 0;
    int duelsWon = 0;
    int duelsLost = 0;
    int deathsEarly = 0;
    int firstKillTime = 0;
    int firstDeathTime = 0;
    for (JSONObject event : events) {
      if (event.has("victimId")) {
        final int victimId = event.getInt("victimId");
        val type = EventTypes.valueOf(event.getString("type"));
        if (victimId == pId && type.equals(EventTypes.CHAMPION_KILL)) {
          if (firstKillTime == 0) {
            int timestamp = event.getInt("timestamp");
            firstKillTime = timestamp / 1000;
          }
          final int killerId = event.getInt("killerId");
          if (killerId != 0) {
            int timestamp = event.getInt("timestamp");
            if (timestamp / 60_000 <= 14) {
              deathsEarly++;
            }
            bountyDrop += event.getInt("shutdownBounty");
          }
          if (!event.has("assistingParticipantIds")) {
            duelsLost++;
          }
        }
      }
      if (event.has("killerId")) {
        final int killerId = event.getInt("killerId");
        val type = EventTypes.valueOf(event.getString("type"));

        if (killerId == pId && type.equals(EventTypes.CHAMPION_KILL)) {
          if (firstDeathTime == 0) {
            int timestamp = event.getInt("timestamp");
            firstDeathTime = timestamp / 1000;
          }
          if (!event.has("assistingParticipantIds")) {
            duelsWon++;
          }
        }
      }
    }
    if (firstDeathTime < 300) {
      int deathMinute = firstDeathTime / 60;
      final int leadAt = getLeadAt(playerInfo, player, deathMinute);
      stats.setLeadDifferenceAfterDiedEarly((short) (getLeadAt(playerInfo, player, 15) - leadAt));
    }

    stats.setFirstKillTime((short) firstKillTime, (short) firstDeathTime);

    short startItemSold = determineStartItem(events);
    stats.setStartItemSold(startItemSold);

    boolean wasAhead = false;
    boolean wasBehind = false;
    boolean comeback = false;
    int xpLead = 0;
    for (int i = 0; i < playerInfo.size(); i++) {
      final int integer = new ArrayList<>(playerInfo.keySet()).get(i);
      final int minute = integer / 60000;
      if (minute <= 15) {
        if (getLeadAt(playerInfo, player, minute) >= Const.AHEAD_XPGOLD && !wasAhead) {
          wasAhead = true;
        } else if (getLeadAt(playerInfo, player, minute) <= (Const.AHEAD_XPGOLD * -1) && !wasBehind) {
          wasBehind = true;
        }
      }
      if (wasAhead && getLeadAt(playerInfo, player, minute) < 0 ||
          wasBehind && getLeadAt(playerInfo, player, minute) > 0) {
        comeback = true;
      }

      xpLead = getXPLeadAt(playerInfo, player, minute);
    }
    stats.setAhead(wasAhead);
    stats.setBehind(wasBehind);
    stats.setComeback(comeback);
    stats.setXpLead((short) xpLead);

    byte objectivesEarlyWe = 0;
    byte objectivesEarlyEnemy = 0;
    for (JSONObject event : events) {
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
    for (JSONObject event : events) {
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


    val map = new TreeMap<>(playerInfo);
    val lastEntry = map.lastEntry();
    final int minute = lastEntry.getKey() / 60000;
    final int leadAt = getLeadAt(playerInfo, player, minute - 3);
    final boolean leadExtend = wasAhead && (leadAt - Const.AHEAD_XPGOLD > 1000);
    stats.setExtendingLead(leadExtend);
    stats.setDeathsEarly(playerperformance, (byte) deathsEarly);
    stats.setDuels(playerperformance, duelsWon, duelsLost);
    stats.setBountyDifference(playerperformance, (short) bountyDrop);


    final double csAt = getCSAt(playerInfo, player, 14) * 1d / 158;
    stats.setEarlyFarmEfficiency(BigDecimal.valueOf(csAt));
    stats.setEarlyGoldAdvantage((short) getGoldLeadAt(playerInfo, player, 10));


    handleControlledAt(playerInfo, player, minute, stats);

    stats.setSpellDodge(playerperformance, enemyPlayer.getSmall(StoredStat.SPELL_LANDED), enemyPlayer.getSmall(StoredStat.SPELL_DODGE),
        enemyPlayer.getSmall(StoredStat.SPELL_DODGE_QUICK));

    playerperformance.setLaneLead((short) getLeadAt(playerInfo, player, 15));
    playerperformance.setEarlyLaneLead((short) getLeadAt(playerInfo, player, 10));


    playerperformance.setStats(stats);
  }

  private static short determineStartItem(List<JSONObject> events) {
    for (JSONObject event : events) {
      val type = EventTypes.valueOf(event.getString("type"));
      if (type.equals(EventTypes.ITEM_SOLD)) {
        val itemId = event.getInt("itemId");
        val item = Item.find((short) itemId);
        if (item.getType().equals(ItemType.STARTING)) {
          return (short) (event.getInt("timestamp") / 1000);
        }
      }
    }
    return 0;
  }

  private static int getCSAt(Map<Integer, JSONObject> playerInfo, JSONPlayer player, int minute) {
      val playerperformanceInfoMeAt15 = getPlayerperformanceInfo(playerInfo, player, minute);
      if (playerperformanceInfoMeAt15 != null) {
        return playerperformanceInfoMeAt15.getCreepScore();
    }
    return 0;
  }

  private static int getGoldLeadAt(Map<Integer, JSONObject> playerInfo, JSONPlayer player, int minute) {
    val enemyPlayer = getEnemyPlayer(player);
    if (enemyPlayer != null) {

      val playerperformanceInfoMe = getPlayerperformanceInfo(playerInfo, player, minute);
      if (playerperformanceInfoMe != null) {

        val playerperformanceInfoEnemy = getPlayerperformanceInfo(playerInfo, enemyPlayer, minute);
        if (playerperformanceInfoEnemy != null) {

          return playerperformanceInfoMe.getTotalGold() - playerperformanceInfoEnemy.getTotalGold();
        }
      }
    }
    return 0;
  }

  private static void handleControlledAt(Map<Integer, JSONObject> playerInfo, JSONPlayer player, int minute, PlayerperformanceStats stats) {
    val enemyPlayer = getEnemyPlayer(player);
    if (enemyPlayer != null) {

      val playerperformanceInfoMe = getPlayerperformanceInfo(playerInfo, player, minute);
      if (playerperformanceInfoMe != null) {
        final double enemyControlled = playerperformanceInfoMe.getEnemyControlled();

        val playerperformanceInfoEnemy = getPlayerperformanceInfo(playerInfo, enemyPlayer, minute);
        if (playerperformanceInfoEnemy != null) {
          final double underControl = playerperformanceInfoEnemy.getEnemyControlled();

          stats.setEnemyControlAdvantage(enemyControlled, underControl);
        }
      }
    }
  }

  private static int getLeadAt(Map<Integer, JSONObject> playerInfo, JSONPlayer player, int minute) {
    val enemyPlayer = getEnemyPlayer(player);
    if (enemyPlayer != null) {

      val playerperformanceInfoMeAt15 = getPlayerperformanceInfo(playerInfo, player, minute);
      if (playerperformanceInfoMeAt15 != null) {
        final int leadMe = playerperformanceInfoMeAt15.getExperience() + playerperformanceInfoMeAt15.getTotalGold();

        val playerperformanceInfoEnemyAt15 = getPlayerperformanceInfo(playerInfo, enemyPlayer, minute);
        if (playerperformanceInfoEnemyAt15 != null) {
          final int leadEnemy = playerperformanceInfoEnemyAt15.getExperience() + playerperformanceInfoEnemyAt15.getTotalGold();

          return leadMe - leadEnemy;
        }
      }
    }
    return 0;
  }

  private static int getXPLeadAt(Map<Integer, JSONObject> playerInfo, JSONPlayer player, int minute) {
    val enemyPlayer = getEnemyPlayer(player);
    if (enemyPlayer != null) {

      val playerperformanceInfoMeAt15 = getPlayerperformanceInfo(playerInfo, player, minute);
      if (playerperformanceInfoMeAt15 != null) {

        val playerperformanceInfoEnemyAt15 = getPlayerperformanceInfo(playerInfo, enemyPlayer, minute);
        if (playerperformanceInfoEnemyAt15 != null) {

          return playerperformanceInfoMeAt15.getExperience() - playerperformanceInfoEnemyAt15.getExperience();
        }
      }
    }
    return 0;
  }

  private static PlayerperformanceInfo getPlayerperformanceInfo(Map<Integer, JSONObject> playerInfo, JSONPlayer player, int minute) {
    for (int timestamp : playerInfo.keySet()) {
      if (timestamp / 60000 == minute) {
        val frame = playerInfo.get(timestamp);
        val infoStats = frame.getJSONObject(String.valueOf(player.getId() + 1));
        val positionObject = infoStats.getJSONObject("position");
        val position = new Position((short) positionObject.getInt("x"), (short) positionObject.getInt("y"));
        final int xp = infoStats.getInt("xp");
        final int totalGold = infoStats.getInt("totalGold");
        final double enemyControlled = infoStats.getInt("timeEnemySpentControlled") * 1d / 1000;
        final int lead = getLeadAt(playerInfo, player, minute);
        final short creepScore = (short) (infoStats.getInt("minionsKilled") + infoStats.getInt("jungleMinionsKilled"));
        return new PlayerperformanceInfo((short) minute, totalGold, (short) infoStats.getInt("currentGold"), enemyControlled, position,
            xp, (short) lead, creepScore, infoStats.getJSONObject("damageStats").getInt("totalDamageDoneToChampions"));
      }
    }
    return null;
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

  private static byte determineStolenBarons(List<JSONObject> events, JSONTeam jsonTeam, JSONTeam enemyTeam) {
    byte stolenBarons = 0;
    for (JSONObject event : events) {
      val type = EventTypes.valueOf(event.getString("type"));
      if (type.equals(EventTypes.ELITE_MONSTER_KILL) && event.getString("monsterType").equals("BARON_NASHOR")) {
        int t1 = 0;
        int t2 = 0;
        val participatingIds = new ArrayList<Integer>();
        if (event.has("assistingParticipantIds")) {
          participatingIds.addAll(event.getJSONArray("assistingParticipantIds").toList()
              .stream().map(id -> (Integer) id - 1).collect(Collectors.toList()));
        }

        for (int i : participatingIds) {
          if (jsonTeam.hasPlayer(i)) {
            t1++;
          } else if (enemyTeam.hasPlayer(i)) {
            t2++;
          }
        }
        if (t2 < t1) {
          int killerId = event.getInt("killerId");
          if (enemyTeam.hasPlayer(killerId - 1)) {
            stolenBarons++;
          }
        }

      }
    }
    return stolenBarons;
  }

  private static List<Short> searchForControlPurchase(List<JSONObject> events, int pId) {
    val returnList = new ArrayList<Short>();
    for (JSONObject event : events) {
      if (event.has("participantId")) {
        final int participantId = event.getInt("participantId");
        val type = EventTypes.valueOf(event.getString("type"));

        if (participantId == pId && type.equals(EventTypes.ITEM_PURCHASED)) {
          val itemId = event.getInt("itemId");
          val item = Item.find((short) itemId);
          if (item.getItemName().equals("Control Ward")) {
            final int timestamp = event.getInt("timestamp");
            returnList.add((short) (timestamp / 1000));
          }
        }
      }
    }

    return returnList;
  }

  private static List<Short> searchForControlPlacements(List<JSONObject> events, int pId) {
    val returnList = new ArrayList<Short>();
    for (JSONObject event : events) {
      if (event.has("participantId")) {
        final int participantId = event.getInt("participantId");
        val type = EventTypes.valueOf(event.getString("type"));

        if (participantId == pId && type.equals(EventTypes.WARD_PLACED)) {
          val wardTypeString = event.getString("type");
          val wardType = WardType.valueOf(wardTypeString);

          if (wardType.equals(WardType.CONTROL_WARD)) {
            final int timestamp = event.getInt("timestamp");
            returnList.add((short) (timestamp / 1000));
          }
        }
      }
    }

    return returnList;
  }

  private static short searchForTrinketSwap(List<JSONObject> events, int pId) {
    for (JSONObject event : events) {
      if (event.has("participantId")) {
        final int participantId = event.getInt("participantId");
        val type = EventTypes.valueOf(event.getString("type"));

        if (participantId == pId && type.equals(EventTypes.ITEM_PURCHASED)) {
          val itemId = event.getInt("itemId");
          val item = Item.find((short) itemId);
          if (item.getType().equals(ItemType.TRINKET) && !item.getItemName().equals("Stealth Ward")) {
            return (short) (event.getInt("timestamp") / 1000);
          }
        }
      }
    }

    return 0;
  }

  private static short searchForFirstWardTime(List<JSONObject> events, int pId, short controlTime) {
    short wardTime = 0;
    for (JSONObject event : events) {
      if (event.has("participantId")) {
        final int participantId = event.getInt("participantId");
        val type = EventTypes.valueOf(event.getString("type"));

        if (participantId == pId && type.equals(EventTypes.WARD_PLACED)) {
          val wardTypeString = event.getString("type");
          val wardType = WardType.valueOf(wardTypeString);
          final short timestamp = (short) (event.getInt("timestamp") / 1000);

          if (wardType.equals(WardType.CONTROL_WARD) && controlTime == 0) {
            controlTime = timestamp;
          } else {
            wardTime = timestamp;
          }
        }

        if (wardTime != 0 && controlTime != 0) {
          return wardTime;
        }
      }
    }

    return 0;
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
    val visionScore = e != null ? (byte) (p.getSmall(StoredStat.VISION_SCORE) - e.getSmall(StoredStat.VISION_SCORE)) :
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
    playerperformance.setScuttlesInitial(p.getTiny(StoredStat.SCUTTLES_INITIAL));
    playerperformance.setScuttlesTotal(p.getTiny(StoredStat.SCUTTLES_TOTAL));
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

  private static JSONPlayer getEnemyPlayer(JSONPlayer player) {
    val jsonPlayers = findJSONPlayersExcept(player.get(StoredStat.LANE), player);
    return jsonPlayers.isEmpty() ? null : jsonPlayers.get(0);
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

  private static List<JSONTeam> getJsonTeams(JSONArray participants) {
    val jsonTeams = Arrays.asList(new JSONTeam(1), new JSONTeam(2));
    for (int i = 0; i < participants.length(); i++) {
      val participant = participants.getJSONObject(i);
      val puuid = participant.getString("puuid");
      val jsonPlayer = new JSONPlayer(i, participant, puuid);
      if (participant.getInt("teamid") == 100) {
        jsonTeams.get(0).addPlayer(jsonPlayer);
      } else {
        jsonTeams.get(1).addPlayer(jsonPlayer);
      }
    }
    return jsonTeams;
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

  private static void loadTimeline(List<JSONObject> events, Map<Integer, JSONObject> playerInfo, JSONObject timeLineObject) {
    val timelineInfo = timeLineObject.getJSONObject("info");
    val timelineFrames = timelineInfo.getJSONArray("frames");
    for (int i = 0; i < timelineFrames.length(); i++) {
      val frameObject = timelineFrames.getJSONObject(i);
      final JSONArray eventArray = frameObject.getJSONArray("events");
      val event = timelineFrames.getJSONObject(0);
      if (Arrays.stream(EventTypes.values()).anyMatch(type2 -> type2.name().equals(event.getString("type")))) {
        IntStream.range(0, eventArray.length()).mapToObj(eventArray::getJSONObject).forEach(events::add);
      }
      if (frameObject.has("participantFrames") && !frameObject.isNull("participantFrames")) {
        playerInfo.put(frameObject.getInt("timestamp"), frameObject.getJSONObject("participantFrames"));
      }
    }
  }

  private static void handleGameEvents(List<JSONObject> events, Game game) {
    for (JSONObject event : events) {
      val type = EventTypes.valueOf(event.getString("type"));
      final int timestamp = event.getInt("timestamp");
      if (type.equals(EventTypes.PAUSE_START)) {
        handlePauseStart(timestamp, game);
      } else if (type.equals(EventTypes.PAUSE_END)) {
        handlePauseEnd(timestamp, game);
      }
    }
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

  private static void handleTeamEvents(List<JSONObject> events, Teamperformance teamperformance) {
    for (JSONObject event : events) {
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

  private static void handlePlayerEvents(List<JSONObject> events, JSONPlayer player, Playerperformance playerperformance) {
    val items = IntStream.range(1, 8).mapToObj(i -> player.getMedium(StoredStat.valueOf("ITEM_" + i)))
        .filter(itemId -> itemId != null && itemId != 0).collect(Collectors.toList());
    for (JSONObject event : events) {
      val type = EventTypes.valueOf(event.getString("type"));
      final int timestamp = event.getInt("timestamp");
      final int playerId = event.has("killerId") ? event.getInt("killerId") : event.getInt("participantId");
      val participatingIds = new ArrayList<Integer>();
      if (event.has("assistingParticipantIds")) {
        participatingIds.addAll(event.getJSONArray("assistingParticipantIds").toList()
            .stream().map(id -> (Integer) id - 1).collect(Collectors.toList()));
      }
      final int victimId = event.has("victimId") ? event.getInt("victimId") : 0;
      KillRole role = null;
      if (playerId == player.getId() + 1) {
        role = KillRole.KILL;
      } else if (participatingIds.contains(playerId + 1)) {
        role = KillRole.ASSIST;
      } else if (victimId == player.getId() + 1) {
        role = KillRole.VICTIM;
      }

      if (role != null) {
        if (type.equals(EventTypes.LEVEL_UP)) {
          playerperformance.addLevelup(new PlayerperformanceLevel((byte) event.getInt("level"), timestamp));
        } else if (type.equals(EventTypes.ITEM_PURCHASED)) {
          final int itemId = event.getInt("itemId");
          playerperformance.addItem(Item.find((short) itemId), items.contains(itemId), timestamp);
        } else if (type.equals(EventTypes.CHAMPION_SPECIAL_KILL) || type.equals(EventTypes.CHAMPION_KILL)) {
          handleChampionKills(playerperformance, event, type, timestamp, role);
        } else if (type.equals(EventTypes.TURRET_PLATE_DESTROYED) || type.equals(EventTypes.BUILDING_KILL) ||
            type.equals(EventTypes.ELITE_MONSTER_KILL)) {
          handleObjectives(playerperformance, event, type, timestamp, role);
        }
      }
    }
  }

  private static void handleChampionKills(Playerperformance playerperformance, JSONObject event, EventTypes type, int timestamp, KillRole role) {
    if (type.equals(EventTypes.CHAMPION_SPECIAL_KILL)) {
      val kill = PlayerperformanceKill.find(playerperformance.getTeamperformance().getGame(), timestamp);
      kill.setType(KillType.valueOf(event.getString("killType").replace("KILL_", "")));
    } else {
      val positionObject = event.getJSONObject("position");
      val position = new Position((short) positionObject.getInt("x"), (short) positionObject.getInt("y"));
      val kill = PlayerperformanceKill.find(playerperformance.getTeamperformance().getGame(), timestamp);
      final int killId = kill == null ? PlayerperformanceKill.lastId() + 1 : kill.getId();
      playerperformance.addKill(new PlayerperformanceKill(killId, timestamp, position, (short) event.getInt("bounty"), role,
          KillType.NORMAL, (byte) event.getInt("killStreakLength")));
    }
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

  private static void handlePlayerInfo(Map<Integer, JSONObject> infos, JSONPlayer player, Playerperformance playerperformance) {
    for (int timestamp : infos.keySet()) {
      final JSONObject frame = infos.get(timestamp);
      final JSONObject stats = frame.getJSONObject(String.valueOf(player.getId() + 1));
      final JSONObject positionObject = stats.getJSONObject("position");
      final Position position = new Position((short) positionObject.getInt("x"), (short) positionObject.getInt("y"));
      final short creepScore = (short) (stats.getInt("minionsKilled") + stats.getInt("jungleMinionsKilled"));
      final int minute = timestamp / 60000;
      final int xp = stats.getInt("xp");
      final int totalGold = stats.getInt("totalGold");
      final double enemyControlled = stats.getInt("timeEnemySpentControlled") * 1d / 1000;
      final int lead = getLeadAt(infos, player, minute);
      final PlayerperformanceInfo info = new PlayerperformanceInfo((short) minute, totalGold, (short) stats.getInt("currentGold"),
          enemyControlled, position, xp, (short) lead, creepScore,
          stats.getJSONObject("damageStats").getInt("totalDamageDoneToChampions"));
      playerperformance.addInfo(info);
    }
  }

  private static void handleSummonerspells(JSONPlayer player, Playerperformance performance) {
    performance.addSummonerspell(Summonerspell.find(player.getTiny(StoredStat.SUMMONER1_ID)), player.getTiny(StoredStat.SUMMONER1_AMOUNT));
    performance.addSummonerspell(Summonerspell.find(player.getTiny(StoredStat.SUMMONER2_ID)), player.getTiny(StoredStat.SUMMONER2_AMOUNT));
  }
}
