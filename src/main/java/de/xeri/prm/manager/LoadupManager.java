package de.xeri.prm.manager;

import java.util.Date;

import de.xeri.prm.game.RiotGameRequester;
import de.xeri.prm.loader.AccountLoader;
import de.xeri.prm.loader.ChampionLoader;
import de.xeri.prm.loader.GametypeLoader;
import de.xeri.prm.loader.ItemLoader;
import de.xeri.prm.loader.PlayerLoader;
import de.xeri.prm.loader.RuneLoader;
import de.xeri.prm.loader.ScheduleLoader;
import de.xeri.prm.loader.SeasonLoader;
import de.xeri.prm.loader.SpellLoader;
import de.xeri.prm.loader.StatCatLoader;
import de.xeri.prm.loader.TeamLoader;
import de.xeri.prm.models.enums.QueueType;
import de.xeri.prm.models.match.ScheduledGame;
import de.xeri.prm.util.Const;
import de.xeri.prm.util.logger.Logger;
import lombok.val;

/**
 * Created by Lara on 09.05.2022 for web
 */
public class LoadupManager {

  public static Date init() {
    if (Const.check()) {
      final Date date = new Date();
      final Data data = Data.getInstance();
      data.init();
      return date;
    }
    System.exit(1);
    return new Date();
  }


  public static boolean loadRiotData() {
    val logger = Logger.getLogger("Riot-Data");

    GametypeLoader.createTypes();
    logger.info("Spielarten geladen");

    RuneLoader.createItems();
    logger.info("Runen geladen");

    ItemLoader.createItems();
    logger.info("Items geladen");

    SpellLoader.createItems();
    logger.info("Summoner Spells geladen");

    ChampionLoader.createChampions();
    logger.info("Champions geladen");

    StatCatLoader.load();
    logger.info("Statkategorien geladen");

    Data.getInstance().commit();
    return true;
  }

  public static boolean loadPrimeLeague() {
    val logger = Logger.getLogger("Prime-League");
    SeasonLoader.load();
    Data.getInstance().commit();
    logger.info("Season geladen");

    PlayerLoader.load();
    Data.getInstance().commit();
    logger.info("Spielerdaten & Spiele geladen");
    // Search for players of Team without account
    AccountLoader.load();
    Data.getInstance().commit();
    // Search for valueable Teams
    AccountLoader.updateTeams();
    Data.getInstance().commit();

    TeamLoader.handleTeam(Const.TEAMID);
    ScheduleLoader.load();
    Data.getInstance().commit();
    return true;
  }

  public static boolean loadGames() {
    ScheduledGame.findMode(QueueType.TOURNEY).forEach(RiotGameRequester::loadCompetitive);

    ScheduledGame.findMode(QueueType.CLASH).forEach(RiotGameRequester::loadClashGame);
    ScheduledGame.findMode(QueueType.OTHER).forEach(RiotGameRequester::loadMatchmade);

    return true;
  }
}
