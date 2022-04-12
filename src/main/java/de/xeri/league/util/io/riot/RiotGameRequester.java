package de.xeri.league.util.io.riot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.xeri.league.models.dynamic.Champion;
import de.xeri.league.models.dynamic.Item;
import de.xeri.league.models.dynamic.Rune;
import de.xeri.league.models.dynamic.Summonerspell;
import de.xeri.league.models.enums.Lane;
import de.xeri.league.models.enums.QueueType;
import de.xeri.league.models.enums.SelectionType;
import de.xeri.league.models.enums.StoredStat;
import de.xeri.league.models.league.Account;
import de.xeri.league.models.league.Team;
import de.xeri.league.models.match.ChampionSelection;
import de.xeri.league.models.match.Game;
import de.xeri.league.models.match.Gametype;
import de.xeri.league.models.match.Playerperformance;
import de.xeri.league.models.match.PlayerperformanceSummonerspell;
import de.xeri.league.models.match.ScheduledGame;
import de.xeri.league.models.match.Teamperformance;
import de.xeri.league.util.Data;
import de.xeri.league.util.io.json.JSON;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Lara on 08.04.2022 for web
 */
public final class RiotGameRequester {

  private static List<JSONTeam> jsonTeams;

  private static List<JSONPlayer> getJSONPlayers() {
    return jsonTeams.stream().flatMap(jsonTeam -> jsonTeam.getAllPlayers().stream()).collect(Collectors.toList());
  }

  private static List<JSONPlayer> findJSONPlayersExcept(StoredStat storedStat, String value, JSONPlayer jsonPlayer) {
    return findJSONPlayersWith(storedStat, value).stream().filter(player -> player.getId() != jsonPlayer.getId())
        .collect(Collectors.toList());
  }

  private static List<JSONPlayer> findJSONPlayersWith(StoredStat storedStat, String value) {
    return getJSONPlayers().stream().filter(player -> player.string(storedStat).equals(value))
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
    final JSON game = RiotURLGenerator.getMatch().getMatch(scheduledGame.getId());
    if (game != null && isValidGame(game, queueType) && queueType != QueueType.OTHER) {
      final JSON timeline = RiotURLGenerator.getMatch().getTimeline(scheduledGame.getId());
      handleTimeline(timeline, queueType);
    }
    ScheduledGame.get().remove(scheduledGame);
    Data.getInstance().getSession().remove(scheduledGame);
  }

  private static boolean isValidGame(JSON gameJson, QueueType queueType) {
    final JSONObject gameData = gameJson.getJSONObject();
    final JSONObject metadata = gameData.getJSONObject("metadata");
    final String gameId = metadata.getString("matchId");
    final JSONObject info = gameData.getJSONObject("info");
    final int queueId = info.getInt("queueId");
    final JSONArray participants = info.getJSONArray("participants");
    if ((queueId == 0 || queueId == 400 || queueId == 410 || queueId == 420 || queueId == 430 || queueId == 700) && participants.length() == 10) {
      final Game game = handleGame(info, gameId);
      final Gametype gametype = Gametype.find(queueId);
      gametype.addGame(game);

      jsonTeams = getJsonTeams(participants);

      for (int i = 0; i < jsonTeams.size(); i++) {
        final JSONTeam jsonTeam = jsonTeams.get(i);
        final JSONArray teams = info.getJSONArray("teams");
        if (jsonTeam.doesExist()) {
          final Teamperformance teamperformance = handleTeam(jsonTeam, teams.getJSONObject(i), game);
          final Team team = jsonTeam.getMostUsedTeam(queueType);
          team.addTeamperformance(teamperformance);
          game.addTeamperformance(teamperformance);

          final List<JSONPlayer> players = determinePlayers(queueType, jsonTeam);
          final List<Playerperformance> playerperformances = handlePlayers(players);

          playerperformances.forEach(teamperformance::addPlayerperformance);
        }
        determineBansAndPicks(teams.getJSONObject(i), i, game, participants);
      }
      // TODO: 08.04.2022 change gametype
      return true;
    }
    return false;
  }

  private static void determineBansAndPicks(JSONObject jsonObject, int id, Game game, JSONArray participants) {
    final JSONArray bans = jsonObject.getJSONArray("bans");
    final List<Integer> pickTurns = new ArrayList<>();
    for (int j = 0; j < bans.length(); j++) {
      final JSONObject selectionObject = bans.getJSONObject(j);
      final int championId = selectionObject.getInt("championId");
      final Champion champion = Champion.find(championId);
      final int pickTurn = selectionObject.getInt("pickTurn");
      pickTurns.add(pickTurn);
      final ChampionSelection ban = ChampionSelection.get(new ChampionSelection(SelectionType.BAN, (byte) (j + 1 + id * 5)), game);
      game.addChampionSelection(ban);
      champion.addChampionSelection(ban);
    }

    final List<Integer> indexes = new ArrayList<>();
    int iterator = 1;
    while (!pickTurns.isEmpty()) {
      final int lowestIndex = pickTurns.indexOf(Collections.min(pickTurns));
      indexes.set(lowestIndex, iterator);
      pickTurns.remove(lowestIndex);
      iterator++;
    }
    for (int i : indexes) {
      final ChampionSelection pick = ChampionSelection.get(new ChampionSelection(SelectionType.PICK, (byte) (i + id * 5)), game);
      game.addChampionSelection(pick);
      final String championName = participants.getJSONObject(i).getString("championName");
      final Champion champion = Champion.find(championName);
      champion.addChampionSelection(pick);
    }
  }

  private static List<Playerperformance> handlePlayers(List<JSONPlayer> players) {
    return players.stream().map(RiotGameRequester::handlePlayer).collect(Collectors.toList());
  }

  private static Playerperformance handlePlayer(JSONPlayer player) {
    final JSONPlayer enemy = getEnemyPlayer(player);
    final Playerperformance playerperformance = handlePerformance(player, enemy);
    player.getAccount().addPlayerperformance(playerperformance);


    handleSummonerspells(player, playerperformance);

    handleChampionsPicked(player, enemy, playerperformance);

    IntStream.range(1, 8).mapToObj(i -> player.Int(StoredStat.valueOf("ITEM_" + i)))
        .filter(itemId -> itemId != null && itemId != 0)
        .map(Item::find)
        .filter(Objects::nonNull)
        .forEach(playerperformance::addItem);

    final JSONArray styles = player.object(StoredStat.RUNES).getJSONArray("styles");
    IntStream.range(0, styles.length()).mapToObj(styles::getJSONObject)
        .map(substyle -> substyle.getJSONArray("selections"))
        .forEach(runes -> IntStream.range(0, runes.length()).mapToObj(runes::getJSONObject)
            .map(runeObject -> Rune.find((short) runeObject.getInt("perk")))
            .forEach(playerperformance::addRune)
        );

    return playerperformance;
  }

  private static void handleSummonerspells(JSONPlayer player, Playerperformance playerperformance) {
    final Summonerspell summonerspell1 = Summonerspell.find(player.Int(StoredStat.SUMMONER1_ID));
    PlayerperformanceSummonerspell.get(new PlayerperformanceSummonerspell(playerperformance, summonerspell1, player.Byte(StoredStat.SUMMONER1_AMOUNT)));
    final Summonerspell summonerspell2 = Summonerspell.find(player.Int(StoredStat.SUMMONER2_ID));
    PlayerperformanceSummonerspell.get(new PlayerperformanceSummonerspell(playerperformance, summonerspell2,
        player.Byte(StoredStat.SUMMONER2_AMOUNT)));
  }

  /**
   *   private byte ganksTotal;
   *   private byte ganksTop;
   *   private byte ganksMid;
   *   private byte ganksBot;
   *
   *   private short wardsCleared;
   *   private byte wardsControl;
   *
   *   private short immobilizations;
   *   private byte flashAggressive;
   *   private short spellsHit;
   *   private short spellsDodged;
   *   private short quickDodged;
   *   private BigDecimal damageTeamDealed;
   *   private BigDecimal damageTeamTaken;
   *   private byte killsSolo;
   *   private byte allinLevelup;
   *   private byte killsTeleport;
   *   private short wardsControlCoverage;
   *   private byte wardsGuarded;
   *   private short firstturretAdvantage;
   *   private byte baronExecutes;
   *   private byte buffsStolen;
   *   private byte scuttlesInitial;
   *   private byte scuttlesTotal;
   *   private byte turretsSplitpushed;
   *   private byte teamInvading;
   *   private byte ganksEarly;
   *   private byte divesDone;
   *   private byte divesProtected;
   *   private short goldBounty;
   *   private short creepsEarly;
   *   private short creepsInvade;
   *   private short laneLead;
   *   private byte turretplates;
   *   private short flamehorizonAdvantage;
   *   private short mejaisCompleted;
   * @param player
   * @param enemy
   * @return
   */
  private static Playerperformance handlePerformance(JSONPlayer player, JSONPlayer enemy) {
    final int shiedling = player.Int(StoredStat.DAMAGE_TEAM_HEAL) + player.Int(StoredStat.DAMAGE_TEAM_SHIELD);
    final byte stolen = (byte) (player.Byte(StoredStat.OBJECTIVES_STOLEN) + player.Byte(StoredStat.OBJECTIVES_STOLEN_TAKEDOWNS));
    final short creeps = (short) (player.Short(StoredStat.CREEP_SCORE_JUNGLE) + player.Short(StoredStat.CREEP_SCORE_MINIONS));
    final boolean firstBlood = player.Int(StoredStat.FIRST_BLOOD) == 1 || player.Int(StoredStat.FIRST_BLOOD_ASSIST) == 1;
    final byte visionScore = (byte) (player.Int(StoredStat.VISION_SCORE) - enemy.Int(StoredStat.VISION_SCORE));
    final Playerperformance playerperformance = new Playerperformance(Lane.valueOf(player.string(StoredStat.LANE)),
        player.Short(StoredStat.Q_USAGE), player.Short(StoredStat.W_USAGE), player.Short(StoredStat.E_USAGE),
        player.Short(StoredStat.R_USAGE), player.Int(StoredStat.DAMAGE_MAGICAL), player.Int(StoredStat.DAMAGE_PHYSICAL),
        player.Int(StoredStat.DAMAGE_TOTAL), player.Int(StoredStat.DAMAGE_TAKEN), player.Int(StoredStat.DAMAGE_MITIGATED),
        player.Int(StoredStat.DAMAGE_HEALED), shiedling, player.Byte(StoredStat.KILLS), player.Byte(StoredStat.DEATHS),
        player.Byte(StoredStat.ASSISTS), player.Byte(StoredStat.KILLS_DOUBLE), player.Byte(StoredStat.KILLS_TRIPLE),
        player.Byte(StoredStat.KILLS_QUADRA), player.Byte(StoredStat.KILLS_PENTA), player.Short(StoredStat.TIME_ALIVE),
        player.Short(StoredStat.TIME_DEAD), player.Short(StoredStat.WARDS_PLACED), stolen, player.Int(StoredStat.OBJECTIVE_DAMAGE),
        player.Byte(StoredStat.BARON_KILLS), player.Short(StoredStat.GOLD_TOTAL), player.Int(StoredStat.EXPERIENCE_TOTAL), creeps,
        player.Short(StoredStat.ITEMS_BOUGHT), firstBlood, player.Byte(StoredStat.CONTROL_WARDS_BOUGHT),
        player.Byte(StoredStat.WARDS_CLEARED), visionScore);
    return playerperformance;
  }

  private static void handleChampionsPicked(JSONPlayer player, JSONPlayer enemy, Playerperformance playerperformance) {
    final String championOwnName = player.string(StoredStat.CHAMPION);
    final Champion championOwn = Champion.find(championOwnName);
    championOwn.addPlayerperformance(playerperformance, true);
    if (enemy != null) {
      final String championEnemyName = enemy.string(StoredStat.CHAMPION);
      final Champion championEnemy = Champion.find(championEnemyName);
      championEnemy.addPlayerperformance(playerperformance, false);
    }
  }

  private static JSONPlayer getEnemyPlayer(JSONPlayer player) {
    final List<JSONPlayer> jsonPlayers = findJSONPlayersExcept(StoredStat.LANE, player.string(StoredStat.LANE), player);
    return jsonPlayers.isEmpty() ? null : jsonPlayers.get(0);
  }

  private static List<JSONPlayer> determinePlayers(QueueType queueType, JSONTeam jsonTeam) {
    final List<JSONPlayer> players = new ArrayList<>();
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
    final List<JSONTeam> jsonTeams = Arrays.asList(new JSONTeam(1), new JSONTeam(2));
    for (int i = 0; i < participants.length(); i++) {
      final JSONObject participant = participants.getJSONObject(i);
      final Account account = Account.find(participant.getString("puuid"));
      final JSONPlayer jsonPlayer = new JSONPlayer(i, participant, account);
      if (participant.getInt("teamid") == 100) {
        jsonTeams.get(0).addPlayer(jsonPlayer);
      } else {
        jsonTeams.get(1).addPlayer(jsonPlayer);
      }
    }
    return jsonTeams;
  }

  private static Teamperformance handleTeam(JSONTeam jsonTeam, JSONObject jsonObject, Game game) {
    final JSONObject objectives = jsonObject.getJSONObject("objectives");
    final JSONObject champion = objectives.getJSONObject("champion");
    final JSONObject tower = objectives.getJSONObject("tower");
    final JSONObject dragon = objectives.getJSONObject("dragon");
    final JSONObject inhibitor = objectives.getJSONObject("inhibitor");
    final JSONObject riftHerald = objectives.getJSONObject("riftHerald");
    final JSONObject baron = objectives.getJSONObject("baron");
    final int teamId = jsonObject.getInt("teamId");
    final boolean win = jsonObject.getBoolean("win");

    final int totalGold = jsonTeam.getAllPlayers().stream().mapToInt(player -> player.getJson().getInt("goldEarned")).sum();
    final int totalCs = jsonTeam.getAllPlayers().stream().mapToInt(player -> player.getJson().getInt("totalMinionsKilled")).sum() +
        jsonTeam.getAllPlayers().stream().mapToInt(player -> player.getJson().getInt("neutralMinionsKilled")).sum();

    final Teamperformance teamperformance = Teamperformance.get(new Teamperformance(teamId == 100, win, totalGold, totalCs,
        champion.getInt("kills"), tower.getInt("kills"), dragon.getInt("kills"), inhibitor.getInt("kills"),
        riftHerald.getInt("kills"), baron.getInt("kills"), tower.getBoolean("first"), dragon.getBoolean("first")), game);
    final JSONPlayer jsonPlayer = jsonTeam.getAllPlayers().get(0);
    if (jsonPlayer.Int(StoredStat.PERFECT_SOUL) != null)
      teamperformance.setPerfectSoul(jsonPlayer.Int(StoredStat.PERFECT_SOUL) == 1);
      teamperformance.setSurrendered(jsonPlayer.Int(StoredStat.SURRENDER) == 1);
    if (jsonPlayer.Short(StoredStat.RIFT_TURRETS) != null)
      teamperformance.setRiftTurrets(jsonPlayer.Short(StoredStat.RIFT_TURRETS) / 5d);
    if (jsonPlayer.Short(StoredStat.ELDER_TIME) != null) teamperformance.setElderTime(jsonPlayer.Short(StoredStat.ELDER_TIME));
    if (jsonPlayer.Short(StoredStat.BARON_POWERPLAY) != null)
      teamperformance.setElderTime(jsonPlayer.Short(StoredStat.BARON_POWERPLAY));

    return teamperformance;
  }

  private static Game handleGame(JSONObject info, String gameId) {
    final long startMillis = info.getLong("gameStartTimestamp");
    final Date start = new Date(startMillis);
    final long endMillis = info.getLong("gameStartTimestamp");
    final short duration = (short) (endMillis - startMillis / 1000);
    return Game.get(new Game(gameId, start, duration));
  }

  private static void handleTimeline(JSON timeline, QueueType queueType) {
    if (timeline != null && !queueType.equals(QueueType.OTHER)) {
    // TODO: 11.04.2022 POSITION
    }
  }
}
