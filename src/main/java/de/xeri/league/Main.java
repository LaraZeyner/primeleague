package de.xeri.league;

import java.util.Date;
import java.util.logging.Logger;

import de.xeri.league.loader.ChampionLoader;
import de.xeri.league.loader.GameLoader;
import de.xeri.league.loader.GametypeLoader;
import de.xeri.league.loader.ItemLoader;
import de.xeri.league.loader.RuneLoader;
import de.xeri.league.loader.ScheduleLoader;
import de.xeri.league.loader.SeasonLoader;
import de.xeri.league.loader.SpellLoader;
import de.xeri.league.loader.TeamLoader;
import de.xeri.league.util.Const;
import de.xeri.league.util.Data;
import org.hibernate.Transaction;

/**
 * Created by Lara on 22.03.2022 for TRUES
 */
public final class Main {

  public static void main(String[] args) {
    final Date date = new Date();
    final Data data = Data.getInstance();
    final Transaction transaction = data.getSession().beginTransaction();
    Logger.getLogger("Main").info("Database loaded");
    loadRiotObjects();
    loadPrimeLeague();
    loadGames();
    data.save();
    transaction.commit();
    System.out.println((System.currentTimeMillis() - date.getTime())/1000.0 + " sec");
  }

  private static void loadGames() {
    GameLoader.load();
  }

  private static void loadPrimeLeague() {
    SeasonLoader.load();
    TeamLoader.handleTeam(Const.TEAMID);
    ScheduleLoader.load();
  }

  private static void loadRiotObjects() {
    GametypeLoader.createTypes();
    RuneLoader.createItems();
    ItemLoader.createItems();
    SpellLoader.createItems();
    ChampionLoader.createChampions();
  }
}
