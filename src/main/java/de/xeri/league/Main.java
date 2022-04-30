package de.xeri.league;

import java.util.Date;

import de.xeri.league.loader.AccountLoader;
import de.xeri.league.loader.ChampionLoader;
import de.xeri.league.loader.GameLoader;
import de.xeri.league.loader.GametypeLoader;
import de.xeri.league.loader.ItemLoader;
import de.xeri.league.loader.PlayerLoader;
import de.xeri.league.loader.RuneLoader;
import de.xeri.league.loader.ScheduleLoader;
import de.xeri.league.loader.SeasonLoader;
import de.xeri.league.loader.SpellLoader;
import de.xeri.league.loader.StatCatLoader;
import de.xeri.league.loader.TeamLoader;
import de.xeri.league.models.league.Team;
import de.xeri.league.util.Const;
import de.xeri.league.util.Data;
import de.xeri.league.util.logger.Logger;
import lombok.val;

/**
 * Created by Lara on 22.03.2022 for TRUES
 */
public final class Main {
  private static final Logger logger = Logger.getLogger("Main");

  public static void main(String[] args) {
    try {
      final Date date = new Date();
      final Data data = Data.getInstance();
      Data.getInstance().getSession().createSQLQuery("SET SQL_MODE='ALLOW_INVALID_DATES';").executeUpdate();
      logger.info("Datenbank geladen");

      val team = Team.findTid(Const.TEAMID);
      data.setCurrentGroup(team.getLastLeague());
      logger.info("Gruppe geladen");

      loadRiotObjects();
      data.commit();
      logger.info("Riots Daten geladen");

      loadPrimeLeague();
      data.commit();
      logger.info("Prime League geladen");

      GameLoader.load();
      data.commit();
      logger.info("Spiele geladen");

      data.getTransaction().commit();
      System.out.println((System.currentTimeMillis() - date.getTime())/1000.0 + " sec");
      System.out.println("TRUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUE");

    } catch (NullPointerException ex) {
      ex.printStackTrace();
    }
  }

  private static void loadPrimeLeague() {
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
  }

  private static void loadRiotObjects() {
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
  }
}
