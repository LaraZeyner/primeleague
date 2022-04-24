package de.xeri.league;

import java.util.Date;
import java.util.logging.Logger;

import de.xeri.league.loader.AccountLoader;
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

/**
 * Created by Lara on 22.03.2022 for TRUES
 */
public final class Main {

  public static void main(String[] args) {
    final Date date = new Date();
    final Data data = Data.getInstance();
    Logger.getLogger("Main").info("Database loaded");

    loadRiotObjects();
    data.commit();

    loadPrimeLeague();
    data.commit();

    loadGames();
    data.commit();

    data.getTransaction().commit();
    System.out.println((System.currentTimeMillis() - date.getTime())/1000.0 + " sec");
    System.out.println("TRUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUE");
  }

  private static void loadGames() {
    GameLoader.load();
    System.out.println("Games loaded");
  }

  private static void loadPrimeLeague() {
    // Load Season
    SeasonLoader.load();
    // Search for players of Team without account
    AccountLoader.load();
    // Search for valueable Teams
    AccountLoader.updateTeams();

    TeamLoader.handleTeam(Const.TEAMID);
    ScheduleLoader.load();
    System.out.println("PRM LOADED");
  }

  private static void loadRiotObjects() {
    GametypeLoader.createTypes();
    RuneLoader.createItems();
    ItemLoader.createItems();
    SpellLoader.createItems();
    ChampionLoader.createChampions();
    Data.getInstance().commit();
    System.out.println("RIOT LOADED");
  }
}
