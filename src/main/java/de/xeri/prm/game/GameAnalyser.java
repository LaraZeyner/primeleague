package de.xeri.prm.game;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.xeri.prm.game.events.fight.Fight;
import de.xeri.prm.game.events.fight.Kill;
import de.xeri.prm.game.events.fight.enums.Fighttype;
import de.xeri.prm.game.events.location.Position;
import de.xeri.prm.game.models.JSONPlayer;
import de.xeri.prm.game.models.JSONTeam;
import de.xeri.prm.game.models.TimelineStat;
import de.xeri.prm.models.dynamic.Champion;
import de.xeri.prm.models.dynamic.Item;
import de.xeri.prm.models.dynamic.Rune;
import de.xeri.prm.models.dynamic.Summonerspell;
import de.xeri.prm.models.enums.DragonSoul;
import de.xeri.prm.models.enums.EventTypes;
import de.xeri.prm.models.enums.KillRole;
import de.xeri.prm.models.enums.KillType;
import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.enums.ObjectiveSubtype;
import de.xeri.prm.models.enums.QueueType;
import de.xeri.prm.models.enums.SelectionType;
import de.xeri.prm.models.enums.StoredStat;
import de.xeri.prm.models.league.Account;
import de.xeri.prm.models.league.Team;
import de.xeri.prm.models.league.TurnamentMatch;
import de.xeri.prm.models.match.ChampionSelection;
import de.xeri.prm.models.match.Game;
import de.xeri.prm.models.match.GamePause;
import de.xeri.prm.models.match.Gametype;
import de.xeri.prm.models.match.Teamperformance;
import de.xeri.prm.models.match.TeamperformanceBounty;
import de.xeri.prm.models.match.playerperformance.Playerperformance;
import de.xeri.prm.models.match.playerperformance.PlayerperformanceInfo;
import de.xeri.prm.models.match.playerperformance.PlayerperformanceKill;
import de.xeri.prm.models.match.playerperformance.PlayerperformanceLevel;
import de.xeri.prm.models.match.playerperformance.PlayerperformanceObjective;
import de.xeri.prm.util.Const;
import de.xeri.prm.util.Util;
import de.xeri.prm.util.io.exception.ConstraintException;
import de.xeri.prm.util.io.json.JSON;
import de.xeri.prm.util.io.riot.RiotAccountURLGenerator;
import de.xeri.prm.util.logger.Logger;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Lara on 08.04.2022 for web
 */
@Getter
public final class GameAnalyser {
  private static final Logger logger = Logger.getLogger("Spielanalyse");

  public static List<JSONTeam> jsonTeams;
  private static Set<Champion> champions;
  private static Set<Summonerspell> summonerSpells;
  public static Map<Short, Item> items;
  public static boolean noChallengeWarned;

  private final List<JSONObject> allEvents = new ArrayList<>();
  private final List<JSONObject> gameEvents = new ArrayList<>();

  private int highestMinute;
  private final Set<PlayerperformanceKill> kills;
  public int lastKillId;
  private int unknownLaneCount;

  public GameAnalyser() {
    this.highestMinute = 0;
    this.kills = new HashSet<>();
    this.lastKillId = PlayerperformanceKill.lastId();
    this.unknownLaneCount = 0;
    noChallengeWarned = false;
  }

  public boolean validate(JSON gameJson, JSON timelineJson, QueueType queueType) {
    if (champions == null) {
      champions = Champion.get();
      summonerSpells = Summonerspell.get();
      items = new HashMap<>();
      Item.get().forEach(item -> items.put(item.getId(), item));

    }

    val gameData = gameJson.getJSONObject();
    val info = gameData.getJSONObject("info");
    final int queueId = info.getInt("queueId");
    val participants = info.getJSONArray("participants");
    if ((queueId == 0 || queueId == 400 || queueId == 410 || queueId == 420 || queueId == 430 || queueId == 700) && participants.length() == 10) {
      long millis = System.currentTimeMillis();

      val metadata = gameData.getJSONObject("metadata");
      val gameId = metadata.getString("matchId");
      Map<Integer, JSONObject> playerInfo = new HashMap<>();
      if (timelineJson != null) {
        final JSONObject timelineObject = timelineJson.getJSONObject();
        playerInfo = timelineObject != null ? loadTimeline(timelineObject) : new HashMap<>();
      }
      final String tCode = info.getString("tournamentCode");
      val gametype = Gametype.find((short) (tCode.equals("") ? queueId : -1));
      try {
        val game = handleGame(info, gameId, gametype);
        gametype.addGame(game, gametype);

        createJsonTeams(participants, playerInfo);

        val fights = handleGameEvents(game);
        handleEventsForTeams();
        for (int i = 0; i < jsonTeams.size(); i++) {
          val jsonTeam = jsonTeams.get(i);
          val teams = info.getJSONArray("teams");
          jsonTeam.setTeamObject(teams.getJSONObject(i));
          determineBansAndPicks(teams.getJSONObject(i), i, game, participants);
        }

        for (final JSONTeam jsonTeam : jsonTeams) {
          if (jsonTeam.doesExist()) {
            val teamperformance = handleTeam(jsonTeam);
            val team = jsonTeam.getMostUsedTeam(queueType);
            game.addTeamperformance(teamperformance, team);
            handleTeamEvents(teamperformance);

            val players = determinePlayers(queueType, jsonTeam);
            players.forEach(player -> handlePlayer(player, teamperformance, fights));
          }
        }
        final List<Team> teams = game.getTeamperformances().stream()
            .map(Teamperformance::getTeam)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        if (teams.size() == 2 && game.getGametype().getId() < 1) {
          List<TurnamentMatch> turnamentMatches = new ArrayList<>(teams.get(0).getMatchesHome());
          turnamentMatches.addAll(teams.get(0).getMatchesGuest());
          final List<TurnamentMatch> collect = turnamentMatches.stream()
              .filter(TurnamentMatch::isOpen)
              .filter(match -> match.getMatchday().getStage().isInSeason(game.getGameStart()))
              .filter(match -> match.hasTeam(teams.get(1)))
              .collect(Collectors.toList());

          for (TurnamentMatch turnamentMatch : collect) {
            turnamentMatch.addGame(game);
          }
        }
        logger.info("Match " + gameId  + " vom " + new SimpleDateFormat("dd.MM.yyyy HH:mm")
            .format(game.getGameStart()) + " geladen in " + (System.currentTimeMillis() - millis) / 1000 + "s");
        return true;

      } catch (ConstraintException exception) {
        logger.warning(exception.getMessage());
      }
    }
    logger.attention("Match entfernt");
    return false;
  }

  private void createJsonTeams(JSONArray participants, Map<Integer, JSONObject> playerInfo) {
    jsonTeams = Arrays.asList(new JSONTeam(1), new JSONTeam(2));
    for (int i = 0; i < participants.length(); i++) {
      val participant = participants.getJSONObject(i);
      val puuid = participant.getString("puuid");
      final int teamId = participant.getInt("teamId");
      val jsonPlayer = new JSONPlayer(i, participant, puuid, teamId == 100, highestMinute);
      if (playerInfo != null) {
        for (int timestamp : playerInfo.keySet()) {
          int minute = timestamp / 60_000;
          val frame = playerInfo.get(timestamp);
          val infoStats = frame.getJSONObject(String.valueOf(jsonPlayer.getId() + 1));
          jsonPlayer.addInfo(infoStats, minute);
        }
      }
      val team = getTeam(teamId);
      if (team != null) {
        team.addPlayer(jsonPlayer);
      }

    }
  }

  /**
   * Verteile Events auf Spieler, Teams und Game
   */
  private void handleEventsForTeams() {
    for (JSONObject event : allEvents) {
      if (isOtherEvent(event)) {
        gameEvents.add(event);

      } else {
        if (isPlayerEvent(event)) {
          for (int pId : getPlayersOfEvent(event)) {
            val player = getPlayer(pId - 1);
            if (player != null) {
              player.addEvent(event);
            }
          }
        }

        if (isTeamEvent(event)) {
          final Integer tId = getTeamOfEvent(event);
          if (tId != null) {
            val team = getTeam(tId);
            if (team != null) {
              team.addEvent(event);
            }
          }
        }
      }
    }
  }

  private boolean isTeamEvent(JSONObject event) {
    return event.has("teamId") || event.has("killerTeamId");
  }

  private boolean isPlayerEvent(JSONObject event) {
    return event.has("participantId") || event.has("killerId") || event.has("victimId") ||
        event.has("assistingParticipantIds") || event.has("creatorId");
  }

  private boolean isOtherEvent(JSONObject event) {
    return !isPlayerEvent(event) && !isTeamEvent(event);
  }

  private void determineBansAndPicks(JSONObject jsonObject, int id, Game game, JSONArray participants) {

    val bans = jsonObject.getJSONArray("bans");
    val pickTurns = new HashMap<Integer, Integer>();
    for (int j = 0; j < bans.length(); j++) {

      val selectionObject = bans.getJSONObject(j);
      final int championId = selectionObject.getInt("championId");
      val champion = champions.stream().filter(champ -> champ.getId() == championId).findFirst().orElse(null);
      if (champion != null) {
        final byte index = (byte) (j + 1 + id * 5);
        final ChampionSelection selection = new ChampionSelection(SelectionType.BAN, index);
        game.addChampionSelection(selection, champion, false);
      }
      final int pickTurn = selectionObject.getInt("pickTurn");
      pickTurns.put(j, pickTurn);
    }

    val ints = pickTurns.entrySet().stream()
        .sorted(Map.Entry.comparingByValue())
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());
    for (int i : ints) {
      final int index = i + id * 5;
      final int championId = participants.getJSONObject(index).getInt("championId");
      champions.stream()
          .filter(champ -> champ.getId() == championId).findFirst()
          .ifPresent(champion -> game.addChampionSelection(new ChampionSelection(SelectionType.PICK, (byte) (index + 1)), champion, false));
    }
  }

  private void handlePlayer(JSONPlayer player, Teamperformance teamperformance, List<Fight> fights) {
    val enemyPlayer = player.getEnemy();
    val performance = handlePerformance(player, enemyPlayer);
    val account = (player.isListed()) ? player.getAccount() : Account.get(RiotAccountURLGenerator.fromPuuid(player.get(StoredStat.PUUID)));
    if (account != null) {
      handleChampionsPicked(player, enemyPlayer, performance);
      val playerperformance = teamperformance.addPlayerperformance(performance, account);
      handleSummonerspells(player, performance);
      val styles = player.object(StoredStat.RUNES).getJSONArray("styles");
      for (int i = 0; i < styles.length(); i++) {
        val substyle = styles.getJSONObject(i);
        val runes = substyle.getJSONArray("selections");
        for (int j = 0; j < runes.length(); j++) {
          val runeObject = runes.getJSONObject(j);
          final short perkId = (short) runeObject.getInt("perk");
          if (Rune.has(perkId)) {
            val perk = Rune.find(perkId);
            playerperformance.addRune(perk);
          }
        }
      }

      handlePlayerEvents(player, playerperformance);
      handlePlayerInfo(player, playerperformance);

      new StatAnalyser(this).handlePlayerStats(playerperformance, teamperformance, jsonTeams, player, fights);
    }
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
  private Playerperformance handlePerformance(JSONPlayer p, JSONPlayer e) {
    final Integer healingShielding = p.getMedium(StoredStat.DAMAGE_HEALING_SHIELDING);
    final int shiedling = healingShielding != null ?
        healingShielding : p.getMedium(StoredStat.DAMAGE_TEAM_HEAL) + p.getMedium(StoredStat.DAMAGE_TEAM_SHIELD);
    final byte stolen = (byte) (p.getTiny(StoredStat.OBJECTIVES_STOLEN) + p.getTiny(StoredStat.OBJECTIVES_STOLEN_TAKEDOWNS));
    final short creeps = (short) (p.getSmall(StoredStat.CREEP_SCORE_JUNGLE) + p.getSmall(StoredStat.CREEP_SCORE_LANE));
    final boolean firstBlood = p.getBool(StoredStat.FIRST_BLOOD) || p.getBool(StoredStat.FIRST_BLOOD_ASSIST);
    final byte controlWards = p.getTiny(StoredStat.CONTROL_WARDS_PLACED, StoredStat.CONTROL_WARDS_BOUGHT);
    final byte wardClear = p.getTiny(StoredStat.WARDS_TAKEDOWN, StoredStat.WARDS_CLEARED);
    val laneString = p.get(StoredStat.LANE);
    Lane lane;
    if (laneString.equals("")) {
      lane = unknownLaneCount == 0 ? Lane.UNKNOWN : Lane.valueOf("UNKNOWN" + unknownLaneCount);
      unknownLaneCount++;
    } else {
      lane = Lane.valueOf(laneString);
    }
    val playerperformance = new Playerperformance(lane, p.getSmall(StoredStat.Q_USAGE), p.getSmall(StoredStat.W_USAGE),
        p.getSmall(StoredStat.E_USAGE), p.getSmall(StoredStat.R_USAGE), p.getMedium(StoredStat.DAMAGE_MAGICAL),
        p.getMedium(StoredStat.DAMAGE_PHYSICAL), p.getMedium(StoredStat.DAMAGE_TOTAL), p.getMedium(StoredStat.DAMAGE_TAKEN),
        p.getMedium(StoredStat.DAMAGE_MITIGATED), p.getMedium(StoredStat.DAMAGE_HEALED), shiedling, p.getTiny(StoredStat.KILLS),
        p.getTiny(StoredStat.DEATHS), p.getTiny(StoredStat.ASSISTS), p.getTiny(StoredStat.KILLS_DOUBLE), p.getTiny(StoredStat.KILLS_TRIPLE),
        p.getTiny(StoredStat.KILLS_QUADRA), p.getTiny(StoredStat.KILLS_PENTA), p.getSmall(StoredStat.TIME_ALIVE),
        p.getSmall(StoredStat.TIME_DEAD), p.getSmall(StoredStat.WARDS_PLACED), stolen, p.getMedium(StoredStat.OBJECTIVES_DAMAGE),
        p.getTiny(StoredStat.BARON_KILLS), p.getMedium(StoredStat.GOLD_TOTAL), p.getMedium(StoredStat.EXPERIENCE_TOTAL), creeps,
        p.getSmall(StoredStat.ITEMS_BOUGHT), firstBlood, controlWards, wardClear, p.getSmall(StoredStat.VISION_SCORE),
        p.getTiny(StoredStat.TOWERS_TAKEDOWNS));
    final Byte visionScore = e != null ? (byte) (p.getSmall(StoredStat.VISION_SCORE) - e.getSmall(StoredStat.VISION_SCORE)) :
        null;
    if (visionScore != null) playerperformance.setVisionscoreAdvantage(visionScore);

    final Short spellsLanded = p.getSmall(StoredStat.SPELL_LANDED);
    if (spellsLanded != null) {
      playerperformance.setSpellsHit(spellsLanded);
      playerperformance.setSpellsDodged(p.getSmall(StoredStat.SPELL_DODGE));
      if (p.has(StoredStat.SPELL_DODGE_QUICK)) {
        playerperformance.setQuickDodged(p.getSmall(StoredStat.SPELL_DODGE_QUICK));
      }
    }

    final Byte soloKills = p.getTiny(StoredStat.SOLO_KILLS);
    if (soloKills != null) {
      playerperformance.setSoloKills(soloKills);
    }

    try {
      final Byte levelUpAllin = handleLevelup(p);
      if (levelUpAllin != null) {
        playerperformance.setLevelUpAllin(levelUpAllin);
      }
    } catch (ConstraintException exception) {
      logger.config(exception.getMessage(), exception);
    }

    final Byte aggressiveFlash = p.getTiny(StoredStat.AGGRESSIVE_FLASH);
    if (aggressiveFlash != null) {
      playerperformance.setAggressiveFlash(aggressiveFlash);
    }

    final Byte teleportKills = p.getTiny(StoredStat.TELEPORT_KILLS);
    if (teleportKills != null) {
      playerperformance.setTeleportKills(teleportKills);
    }

    final Short immobilizations = p.getSmall(StoredStat.IMMOBILIZATIONS);
    if (immobilizations != null) {
      playerperformance.setImmobilizations(immobilizations);
    }

    final Short controlWardUptime = p.getSmall(StoredStat.CONTROL_WARDS_UPTIME);
    if (controlWardUptime != null) {
      playerperformance.setControlWardUptime((short) (controlWardUptime * 60));
    }

    final Byte guardedWards = p.getTiny(StoredStat.WARDS_GUARDED);
    if (guardedWards != null) {
      playerperformance.setGuardedWards(guardedWards);
    }

    final Short playerFirstTowerTime = p.getSmall(StoredStat.FIRST_TOWER_TIME);
    if (playerFirstTowerTime != null && (p.getBool(StoredStat.FIRST_TOWER) || p.getBool(StoredStat.FIRST_TOWER_ASSIST))) {
      playerperformance.setFirstturretAdvantage(playerFirstTowerTime);

    } else if (e != null) {
      final Short enemyFirstTowerTime = e.getSmall(StoredStat.FIRST_TOWER_TIME);
      if (enemyFirstTowerTime != null && (e.getBool(StoredStat.FIRST_TOWER) || e.getBool(StoredStat.FIRST_TOWER_ASSIST))) {
        playerperformance.setFirstturretAdvantage((short) (enemyFirstTowerTime * -1));
      }
    }

    final Byte baronExecutes = e == null ? null : e.getTiny(StoredStat.BARON_EXECUTES);
    if (baronExecutes != null) {
      playerperformance.setBaronExecutes(baronExecutes);
    }

    final Byte buffsStolen = p.getTiny(StoredStat.BUFFS_STOLEN);
    if (buffsStolen != null) {
      playerperformance.setBuffsStolen(buffsStolen);
    }

    final Byte initialScuttles = p.getTiny(StoredStat.SCUTTLES_INITIAL);
    if (initialScuttles != null) {
      playerperformance.setInitialScuttles(initialScuttles);
      playerperformance.setTotalScuttles(p.getTiny(StoredStat.SCUTTLES_TOTAL));
    }

    final Byte splitpushedTurrets = p.getTiny(StoredStat.TOWERS_SPLITPUSHED);
    if (splitpushedTurrets != null) {
      playerperformance.setSplitpushedTurrets(splitpushedTurrets);
    }

    final Byte teamInvading = p.getTiny(StoredStat.INVADING_KILLS);
    if (teamInvading != null) {
      playerperformance.setTeamInvading(teamInvading);
    }

    final Byte ganksEarly = p.getTiny(StoredStat.LANER_ROAMS, StoredStat.JUNGLER_ROAMS);
    if (ganksEarly != null) {
      playerperformance.setGanksEarly(ganksEarly);
    }

    final Byte dives = p.getTiny(StoredStat.DIVES_DONE);
    if (dives != null) {
      playerperformance.setDivesSuccessful(dives);
    }
    final Byte protect = p.getTiny(StoredStat.DIVES_PROTECTED);
    if (protect != null) {
      playerperformance.setDivesProtected(protect);
    }

    if (e != null) {
      final Byte enemyProtect = e.getTiny(StoredStat.DIVES_PROTECTED);
      final Byte divesDone = handleDives(dives, enemyProtect);
      if (divesDone != null) {
        playerperformance.setDivesDone(divesDone);
      }


      final Byte enemyDives = e.getTiny(StoredStat.DIVES_DONE);
      final Byte divesGotten = handleDives(protect, enemyDives);
      if (divesGotten != null) {
        playerperformance.setDivesGotten(divesGotten);
      }
    }

    final Short bounty = p.getSmall(StoredStat.BOUNTY_GOLD);
    if (bounty != null) {
      playerperformance.setBountyGold((short) (bounty - (e != null && e.getSmall(StoredStat.BOUNTY_GOLD) == null ? 0 : bounty)));
    }

    final Byte earlyCreeps = p.getTiny(StoredStat.CREEP_SCORE_LANE_EARLY, StoredStat.CREEP_SCORE_JUNGLE_EARLY);
    if (earlyCreeps != null) {
      playerperformance.setEarlyCreeps(earlyCreeps);
    }

    final Short invadedCreeps = p.getSmall(StoredStat.CREEP_INVADED);
    if (invadedCreeps != null) {
      playerperformance.setInvadedCreeps(invadedCreeps);
    }

    final Byte turretplates = p.getTiny(StoredStat.TOWERS_PLATES);
    if (turretplates != null) {
      playerperformance.setTurretplates(turretplates);
    }

    final Short csAdvantage = p.getSmall(StoredStat.CREEP_SCORE_ADVANTAGE);
    if (csAdvantage != null) {
      playerperformance.setCreepScoreAdvantage(csAdvantage);
    }

    final Short mejaisTime = p.getSmall(StoredStat.MEJAIS_TIME);
    final Short enemyMejaisTime = e == null ? null : e.getSmall(StoredStat.MEJAIS_TIME);
    if (mejaisTime != null || enemyMejaisTime != null) {
      final short mejaisCompleted = (short) ((mejaisTime == null ? 0 : mejaisTime) - (enemyMejaisTime == null ? 0 : enemyMejaisTime));
      playerperformance.setMejaisCompleted(mejaisCompleted);
    }

    final Byte outplayed = p.getTiny(StoredStat.OUTPLAYED);
    if (outplayed != null) {
      playerperformance.setOutplayed(outplayed);
    }

    final Byte dragonTakedowns = p.getTiny(StoredStat.DRAGON_TAKEDOWNS);
    if (dragonTakedowns != null) {
      playerperformance.setDragonTakedowns(dragonTakedowns);
    }

    final Short fastestLegendary = p.getSmall(StoredStat.LEGENDARY_FASTEST);
    if (fastestLegendary != null) {
      playerperformance.setFastestLegendary(fastestLegendary);
    }

    final Byte gankSetups = p.getTiny(StoredStat.GANK_SETUP);
    if (gankSetups != null) {
      playerperformance.setGankSetups(gankSetups);
    }

    final Byte initialBuffs = p.getTiny(StoredStat.BUFFS_INITIAL);
    if (initialBuffs != null) {
      playerperformance.setInitialBuffs(initialBuffs);
    }

    final Byte earlyKills = p.getTiny(StoredStat.KILLS_EARLY_JUNGLER, StoredStat.KILLS_EARLY_LANER);
    if (earlyKills != null) {
      playerperformance.setEarlyKills(earlyKills);
    }

    final Byte junglerKills = p.getTiny(StoredStat.OBJECTIVES_JUNGLERKILL);
    if (junglerKills != null) {
      playerperformance.setJunglerKillsAtObjective(junglerKills);
    }

    final Byte ambush = p.getTiny(StoredStat.AMBUSH);
    if (ambush != null) {
      playerperformance.setAmbush(ambush);
    }

    final Byte earlyTurrets = p.getTiny(StoredStat.TOWERS_EARLY);
    if (earlyTurrets != null) {
      playerperformance.setEarlyTurrets(earlyTurrets);
    }

    final Byte xpLead = p.getTiny(StoredStat.EXPERIENCE_ADVANTAGE);
    if (xpLead != null) {
      playerperformance.setLevelLead(xpLead);
    }

    final Byte picksMade = p.getTiny(StoredStat.PICK_KILL);
    if (picksMade != null) {
      playerperformance.setPicksMade(picksMade);
    }

    final Byte assassinated = p.getTiny(StoredStat.ASSASSINATION);
    if (assassinated != null) {
      playerperformance.setAssassinated(assassinated);
    }

    final Byte savedAlly = p.getTiny(StoredStat.GUARD_ALLY);
    if (savedAlly != null) {
      playerperformance.setSavedAlly(savedAlly);
    }

    byte survived = 0;
    final Byte survivedClose = p.getTiny(StoredStat.SURVIVED_CLOSE);
    final Byte survivedDamage = p.getTiny(StoredStat.SURVIVED_HIGH_DAMAGE);
    final Byte survivedCC = p.getTiny(StoredStat.SURVIVED_HIGH_CROWDCONTROL);

    if (survivedClose != null || survivedDamage != null || survivedCC != null) {
      survived += survivedClose != null ? survivedClose : 0;
      survived += survivedDamage != null ? survivedDamage : 0;
      survived += survivedCC != null ? survivedCC : 0;
      playerperformance.setSurvivedClose(survived);
    }

    return playerperformance;
  }

  private Byte handleLevelup(JSONPlayer player) throws ConstraintException {
    final Byte levelUpAllin = player.getTiny(StoredStat.LEVELUP_TAKEDOWNS);
    if (levelUpAllin > 17) {
      throw new ConstraintException("Mehr als 17 Levelups");
    }
    return levelUpAllin;
  }

  private Byte handleDives(Byte divesDone, Byte divesProtected) {
    return (divesDone != null && divesProtected != null) ? (byte) (divesDone + divesProtected) : null;
  }

  private void handleChampionsPicked(JSONPlayer player, JSONPlayer enemy, Playerperformance playerperformance) {
    final int championOwnId = player.getMedium(StoredStat.CHAMPION);
    champions.stream()
        .filter(champ -> champ.getId() == championOwnId).findFirst()
        .ifPresent(championOwn -> championOwn.addPlayerperformance(playerperformance, true));

    if (enemy != null) {
      final int championEnemyId = enemy.getMedium(StoredStat.CHAMPION);
      champions.stream()
          .filter(champ -> champ.getId() == championEnemyId).findFirst()
          .ifPresent(championEnemy -> championEnemy.addPlayerperformance(playerperformance, false));
    }
  }

  private List<JSONPlayer> determinePlayers(QueueType queueType, JSONTeam jsonTeam) {
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

  @NotNull
  private List<Integer> getPlayersOfEvent(JSONObject allEvent) {
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

  private Integer getTeamOfEvent(JSONObject allEvent) {
    if (allEvent.has("teamId")) {
      return allEvent.getInt("teamId");

    } else if (allEvent.has("killerTeamId")) {
      return allEvent.getInt("killerTeamId");

    }
    return null;
  }

  private Teamperformance handleTeam(JSONTeam jsonTeam) {
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

    final int totalDamage = jsonTeam.getSum(StoredStat.DAMAGE_TOTAL);
    final int damageTaken = jsonTeam.getSum(StoredStat.DAMAGE_TAKEN);
    final int totalGold = jsonTeam.getSum(StoredStat.GOLD_TOTAL);
    final int totalCs = jsonTeam.getSum(StoredStat.CREEP_SCORE_LANE) + jsonTeam.getSum(StoredStat.CREEP_SCORE_JUNGLE);
    final Teamperformance teamperformance = new Teamperformance(teamId == 100, win, totalDamage, damageTaken, totalGold, totalCs,
        champion.getInt("kills"), tower.getInt("kills"), dragon.getInt("kills"), inhibitor.getInt("kills"),
        riftHerald.getInt("kills"), baron.getInt("kills"), tower.getBoolean("first"), dragon.getBoolean("first"));
    final JSONPlayer jsonPlayer = jsonTeam.getAllPlayers().get(0);

    final Integer perfectSoul = jsonPlayer.getMedium(StoredStat.PERFECT_SOUL);
    if (perfectSoul != null) {
      teamperformance.setPerfectSoul(perfectSoul == 1);
    }

    final Boolean surrendered = jsonPlayer.getBool(StoredStat.SURRENDER);
    if (surrendered != null) {
      teamperformance.setSurrendered(surrendered);
    }

    final Short riftTurrets = jsonPlayer.getSmall(StoredStat.RIFT_TURRETS);
    if (riftTurrets != null) {
      teamperformance.setRiftTurrets(riftTurrets / 5.0);
    }

    final Short elderTime = jsonPlayer.getSmall(StoredStat.ELDER_TIME);
    if (elderTime != null) {
      teamperformance.setElderTime(elderTime);
    }

    final Short baronPowerplay = jsonPlayer.getSmall(StoredStat.BARON_POWERPLAY);
    if (baronPowerplay != null) {
      teamperformance.setElderTime(baronPowerplay);
    }

    final Byte earlyAces = jsonPlayer.getTiny(StoredStat.ACE_EARLY);
    if (earlyAces != null) {
      teamperformance.setEarlyAces(earlyAces);
    }

    final Short baronTime = jsonPlayer.getSmall(StoredStat.BARON_TIME);
    if (baronTime != null) {
      teamperformance.setBaronTime(baronTime);
    }

    final Integer earliestDragon = jsonTeam.getSum(StoredStat.DRAGON_TIME);
    if (earliestDragon != null) teamperformance.setFirstDragonTime((short) (int) earliestDragon);

    final Integer atSpawn = jsonTeam.getSum(StoredStat.OBJECTIVES_ON_SPAWN);
    if (atSpawn != null) teamperformance.setObjectiveAtSpawn((byte) (int) atSpawn);

    final Integer nearJgl = jsonTeam.getSum(StoredStat.OBJECTIVES_50_50);
    if (nearJgl != null) teamperformance.setObjectiveContests((byte) (int) nearJgl);

    final Integer quest = jsonTeam.getSum(StoredStat.QUEST_FAST);
    if (quest != null) teamperformance.setQuestCompletedFirst(quest > 0);

    final Short inhibitorsTime = jsonPlayer.getSmall(StoredStat.INHIBITORS_TAKEN);
    if (inhibitorsTime != null) {
      teamperformance.setInhibitorsTime(inhibitorsTime);
    }

    final Byte flawlessAces = jsonPlayer.getTiny(StoredStat.ACE_FLAWLESS);
    if (flawlessAces != null) {
      teamperformance.setFlawlessAces(flawlessAces);
    }

    final Integer herald = jsonTeam.getSum(StoredStat.RIFT_TURRETS_MULTI);
    if (herald != null) teamperformance.setRiftOnMultipleTurrets((byte) (int) herald);

    final Integer acetime = jsonTeam.getMin(StoredStat.ACE_TIME);
    if (acetime != null) teamperformance.setFastestAcetime((short) (int) acetime);

    final Integer killDeficit = jsonTeam.getSum(StoredStat.KILLS_DISADVANTAGE);
    if (killDeficit != null) teamperformance.setKillDeficit((byte) (int) killDeficit);

    final int vision = jsonTeam.getSum(StoredStat.VISION_SCORE);
    teamperformance.setVision((short) vision);

    final Integer immobilizations = jsonTeam.getSum(StoredStat.IMMOBILIZATIONS);
    if (immobilizations != null) teamperformance.setImmobilizations((short) (int) immobilizations);

    final int damageMitigated = jsonTeam.getSum(StoredStat.DAMAGE_MITIGATED);
    teamperformance.setDamageMitigated(damageMitigated);

    return teamperformance;
  }

  private Game handleGame(JSONObject info, String gameId, Gametype gametype) throws ConstraintException {
    final long startMillis = info.getLong("gameStartTimestamp");
    val start = new Date(startMillis);
    int end = info.getInt("gameDuration");
    if (end > 100_000) {
      end = end / 1000;
    }
    final short duration = (short) end;
    if (duration > 100) {
      return Game.get(new Game(gameId, start, duration), gametype);
    }
    throw new ConstraintException("Spieldauer zu kurz");
  }

  private HashMap<Integer, JSONObject> loadTimeline(JSONObject timeLineObject) {
    val playerInfo = new HashMap<Integer, JSONObject>();
    val timelineInfo = timeLineObject.getJSONObject("info");
    val timelineContent = timelineInfo.getJSONArray("frames");
    // Each minute
    for (int i = 0; i < timelineContent.length(); i++) {
      val frameObject = timelineContent.getJSONObject(i);
      val eventArray = frameObject.getJSONArray("events");
      for (int j = 0; j < eventArray.length(); j++) {
        val event = eventArray.getJSONObject(j);
        final String typeString = event.getString("type");
        if (Arrays.stream(EventTypes.values()).anyMatch(type2 -> type2.name().equals(typeString))) {
          allEvents.add(event);

        }
      }

      if (frameObject.has("participantFrames") && !frameObject.isNull("participantFrames")) {
        final int timestamp = frameObject.getInt("timestamp");
        playerInfo.put(timestamp, frameObject.getJSONObject("participantFrames"));
        final int minute = timestamp / 60_000;
        if (highestMinute < minute) {
          highestMinute = minute;
        }
      }
    }

    return playerInfo;
  }

  private List<Fight> handleGameEvents(Game game) {
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
        .filter(Objects::nonNull)
        .sorted(Comparator.comparingInt(Kill::getTimestamp))
        .collect(Collectors.toCollection(ArrayList::new));

    val fights = new ArrayList<Fight>();
    for (Kill kill : kills) {
      if (kill.getKiller() != 0) {
        val validFight = fights.stream().filter(fight -> fight.getLastTimestamp() >= kill.getTimestamp() - Const.SECONDS_BETWEEN_FIGHTS * 1000)
            .filter(fight -> Util.distance(fight.getLastPosition(), kill.getPosition()) <= Const.DISTANCE_BETWEEN_FIGHTS)
            .findFirst().orElse(null);
        if (validFight == null) {
          fights.add(new Fight(kill));
        } else {
          validFight.addKill(kill);
        }
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

  private void handlePauseStart(int timestamp, Game game) {
    if (game.getNotOpened().isEmpty()) {
      game.addPause(new GamePause(timestamp, 0));
    } else {
      game.getNotOpened().get(0).setStart(timestamp);
    }
  }

  private void handlePauseEnd(int timestamp, Game game) {
    if (timestamp > 0) {
      if (game.getNotClosed().isEmpty()) {
        game.addPause(new GamePause(0, timestamp));
      } else {
        game.getNotClosed().get(0).setEnd(timestamp);
      }
    }
  }

  private void handleTeamEvents(Teamperformance teamperformance) {
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

  private void handleBountyStart(int timestamp, Teamperformance teamperformance) {
    if (teamperformance.getNotOpened().isEmpty()) {
      teamperformance.addBounty(new TeamperformanceBounty(timestamp, 0));
    } else {
      teamperformance.getNotOpened().get(0).setStart(timestamp);
    }
  }

  private void handleBountyEnd(int timestamp, Teamperformance teamperformance) {
    if (teamperformance.getNotClosed().isEmpty()) {
      teamperformance.addBounty(new TeamperformanceBounty(0, timestamp));
    } else {
      teamperformance.getNotClosed().get(0).setEnd(timestamp);
    }
  }

  private void handlePlayerEvents(@NonNull JSONPlayer player, @NonNull Playerperformance playerperformance) {
    val items = IntStream.range(1, 8).mapToObj(i -> player.getMedium(StoredStat.valueOf("ITEM_" + i)))
        .filter(itemId -> itemId != null && itemId != 0).collect(Collectors.toList());

    for (JSONObject event : player.getEvents(EventTypes.LEVEL_UP)) {
      final int timestamp = event.getInt("timestamp");
      final PlayerperformanceLevel level = new PlayerperformanceLevel((byte) event.getInt("level"), timestamp);
      playerperformance.addLevelup(level);
    }

    for (JSONObject event : player.getEvents(EventTypes.ITEM_PURCHASED)) {
      final int itemId = event.getInt("itemId");
      final int timestamp = event.getInt("timestamp");
      final Item item = GameAnalyser.items.get((short) itemId);
      if (item != null) {
        playerperformance.addItem(item, items.contains(itemId), timestamp);
      }
    }

    List<PlayerperformanceKill> killList = new ArrayList<>();
    for (JSONObject event : player.getEvents(EventTypes.CHAMPION_KILL)) {
      final int timestamp = event.getInt("timestamp");
      val role = handleEventOfPlayer(player, event);
      final PlayerperformanceKill kill = handleChampionKills(event, timestamp, role);
      playerperformance.addKill(kill);
      if (kill.getPlayerperformance() != null) {
        killList.add(kill);
      }

    }

    for (JSONObject event : player.getEvents(EventTypes.CHAMPION_SPECIAL_KILL)) {
      final int timestamp = event.getInt("timestamp");
      killList.stream().filter(k -> k.getTime() == timestamp).findFirst()
          .ifPresent(kill -> kill.setType(KillType.valueOf(event.getString("killType").replace("KILL_", ""))));
    }

    for (JSONObject event : player.getEvents(EventTypes.TURRET_PLATE_DESTROYED)) {
      final int timestamp = event.getInt("timestamp");
      val lane = event.has("laneType") ? Lane.findLane(event.getString("laneType")) : null;
      val role = handleEventOfPlayer(player, event);
      playerperformance.addObjective(new PlayerperformanceObjective(timestamp, ObjectiveSubtype.OUTER_TURRET, lane, (short) 160, role));
    }

    for (JSONObject event : player.getEvents(EventTypes.BUILDING_KILL, EventTypes.ELITE_MONSTER_KILL)) {
      final int timestamp = event.getInt("timestamp");
      val role = handleEventOfPlayer(player, event);
      handleObjectives(playerperformance, event, timestamp, role);
    }

    if(!killList.isEmpty()) {
      kills.addAll(killList);
    }
  }

  @NonNull
  private KillRole handleEventOfPlayer(JSONPlayer player, JSONObject event) {
    int killerOrPId = determinePlayerId(event);

    if (killerOrPId == player.getPId()) {
      return KillRole.KILLER;
    } else if (event.has("assistingParticipantIds")) {
      val participatingIds = event.getJSONArray("assistingParticipantIds").toList().stream()
          .map(id -> (Integer) id)
          .collect(Collectors.toList());
      if (participatingIds.contains(player.getPId())) {
        return KillRole.ASSIST;
      }
    }
    final int victimId = event.has("victimId") ? event.getInt("victimId") : 0;
    if (victimId == player.getPId()) {
      return KillRole.VICTIM;
    }
    throw new NullPointerException("Spieler nicht Teil des Events");
  }

  @NonNull
  private Integer determinePlayerId(JSONObject event) {
    if (event.has("killerId")) return event.getInt("killerId");
    else if (event.has("creatorId")) return event.getInt("creatorId");
    else if (event.has("participantId")) return event.getInt("participantId");
    throw new IllegalArgumentException("SpielerId zu Event nicht gefunden");
  }

  private PlayerperformanceKill handleChampionKills(@NonNull JSONObject event, int timestamp, @NonNull KillRole role) {
    val positionObject = event.getJSONObject("position");
    final int xCoordinate = positionObject.getInt("x");
    final int yCoordinate = positionObject.getInt("y");
    val position = new Position((short) xCoordinate, (short) yCoordinate);

    val kill = kills.stream().filter(k -> k.getTime() == timestamp).findFirst().orElse(null);
    final int killId;
    if (kill == null) {
      lastKillId++;
      killId = lastKillId;
    } else {
      killId = kill.getId();
    }


    return new PlayerperformanceKill(killId, timestamp, position, (short) event.getInt("bounty"), role, KillType.NORMAL,
        (byte) event.getInt("killStreakLength"));
  }

  private void handleObjectives(Playerperformance playerperformance, JSONObject event, int timestamp, KillRole role) {
    val type = EventTypes.valueOf(event.getString("type"));
    val lane = event.has("laneType") ? Lane.findLane(event.getString("laneType")) : null;
    var query = event.has("monsterSubType") ? "monsterSubType" : "monsterType";
    if (type.equals(EventTypes.BUILDING_KILL)) {
      query = event.has("towerType") ? "towerType" : "buildingType";
    }
    val objectiveType = ObjectiveSubtype.valueOf(event.getString(query).replace("_BUILDING", ""));
    short bounty = (short) objectiveType.getBounty();
    if (event.has("bounty")) {
      bounty += event.getInt("bounty");
    }
    playerperformance.addObjective(new PlayerperformanceObjective(timestamp, objectiveType, lane, bounty, role));
  }

  private void handlePlayerInfo(JSONPlayer player, Playerperformance playerperformance) {
    player.getInfos().stream()
        .filter(Objects::nonNull)
        .mapToInt(object -> player.getInfos().indexOf(object))
        .mapToObj(minute -> getPlayerperformanceInfo(player, minute))
        .forEach(playerperformance::addInfo);
  }

  private PlayerperformanceInfo getPlayerperformanceInfo(JSONPlayer player, int minute) {
    final JSONObject infoStats = getEventObject(player, minute);
    if (infoStats == null) return null;

    val positionObject = infoStats.getJSONObject("position");
    val position = new Position(positionObject.getInt("x"), positionObject.getInt("y"));
    final int xp = player.getStatAt(minute, TimelineStat.EXPERIENCE);
    final int totalGold = player.getStatAt(minute, TimelineStat.TOTAL_GOLD);
    final int cGold = player.getStatAt(minute, TimelineStat.CURRENT_GOLD);
    final int currentGold = Math.max(cGold, 0);
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
  private JSONObject getEventObject(JSONPlayer player, int minute) {
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

  private void handleSummonerspells(JSONPlayer player, Playerperformance performance) {
    summonerSpells.stream()
        .filter(summonerspell -> summonerspell.getId() == player.getTiny(StoredStat.SUMMONER1_ID)).findFirst()
        .ifPresent(summonerspell -> performance.addSummonerspell(summonerspell, player.getTiny(StoredStat.SUMMONER1_AMOUNT)));

    summonerSpells.stream()
        .filter(summonerspell -> summonerspell.getId() == player.getTiny(StoredStat.SUMMONER2_ID)).findFirst()
        .ifPresent(summonerspell -> performance.addSummonerspell(summonerspell, player.getTiny(StoredStat.SUMMONER2_AMOUNT)));
  }

  @Nullable
  public JSONTeam getTeam(int id) {
    if (id == 100) {
      return jsonTeams.get(0);

    } else if (id == 200) {
      return jsonTeams.get(1);
    }

    return null;
  }

  public JSONPlayer getPlayer(int id) {
    return jsonTeams.stream()
        .flatMap(team -> team.getAllPlayers().stream())
        .filter(jsonPlayer -> jsonPlayer.getId() == id)
        .findFirst().orElse(null);
  }

}
