package de.xeri.prm.manager;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
import de.xeri.prm.models.league.Schedule;
import de.xeri.prm.models.league.Team;
import de.xeri.prm.models.match.ScheduledGame;
import de.xeri.prm.util.Const;
import de.xeri.prm.util.logger.Logger;
import lombok.val;
import org.hibernate.query.Query;

/**
 * Created by Lara on 09.05.2022 for web
 */
public class LoadupManager {

  public static Date init() {
    if (Const.check()) {
      final Date date = new Date();
      final PrimeData primeData = PrimeData.getInstance();
      primeData.init();
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

    PrimeData.getInstance().commit();
    return true;
  }

  public static boolean loadPrimeLeague() {
    val logger = Logger.getLogger("Prime-League");
    SeasonLoader.load();
    PrimeData.getInstance().commit();
    logger.info("Season geladen");

    PlayerLoader.loadAll();
    PrimeData.getInstance().commit();
    logger.info("Spielerdaten & Spiele geladen");
    // Search for players of Team without account
    AccountLoader.load();
    PrimeData.getInstance().commit();
    // Search for valueable Teams
    AccountLoader.updateTeams();
    PrimeData.getInstance().commit();

    TeamLoader.handleTeam(Const.TEAMID);
    ScheduleLoader.load();
    PrimeData.getInstance().commit();
    return true;
  }

  /**
   * CompGames der Ligateams
   * Games der nächsten Gegner + TRUES mit 3
   * Clash der nächsten Gegner + TRUES
   * Games der nächsten Gegner + TRUES
   * Alle Tourney
   * Alle Games mit 3
   * Alle Clash
   * Alle Games
   */
  public static boolean loadGames() {
    val logger = Logger.getLogger("Spielanalyse");
    final Set<Team> teams = PrimeData.getInstance().getCurrentGroup().getTeams();
    final List<ScheduledGame> tourneys = ScheduledGame.findMode(QueueType.TOURNEY);
    // CompGames der Ligateams
    tourneys.stream().filter(scheduledGame -> scheduledGame.getTeamsMap().keySet().stream().anyMatch(teams::contains)).forEach(RiotGameRequester::loadCompetitive);
    logger.info("CompGames der Ligateams geladen");


    // Games der nächsten Gegner + TRUES mit 3
    List<Team> teams1 = Arrays.asList(Team.findTid(Const.TEAMID), Schedule.nextOrLast().getEnemyTeam());
    final Query<ScheduledGame> query = PrimeData.getInstance().getSession().createQuery("FROM ScheduledGame s " +
        "WHERE teams LIKE :t1 OR teams LIKE :t2");
    query.setParameter("t1", "%" + teams1.get(0).getId() + "%");
    query.setParameter("t2", teams1.size() > 1 ? ("%" + teams1.get(1).getId() + "%") : ("%" + teams1.get(0).getId() + "%"));
    final List<ScheduledGame> list = query.list();
    list.stream().filter(scheduledGame -> scheduledGame.getQueueType().equals(QueueType.CLASH))
        .filter(scheduledGame -> scheduledGame.getTeamsMap().keySet().stream()
        .anyMatch(team -> teams1.contains(team) && scheduledGame.getTeamsMap().get(team) > 2))
        .forEach(RiotGameRequester::loadClashGame);
    list.stream().filter(scheduledGame -> scheduledGame.getQueueType().equals(QueueType.OTHER))
        .filter(scheduledGame -> scheduledGame.getTeamsMap().keySet().stream()
            .anyMatch(team -> teams1.contains(team) && scheduledGame.getTeamsMap().get(team) > 2))
        .forEach(RiotGameRequester::loadMatchmade);
    logger.info("Games der nächsten Gegner + TRUES mit 3 geladen");

    // Clash der nächsten Gegner + TRUES
    ScheduledGame.findMode(QueueType.CLASH).stream().filter(scheduledGame -> scheduledGame.getTeamsMap().keySet().stream()
        .anyMatch(teams1::contains)).forEach(RiotGameRequester::loadClashGame);
    logger.info("Clash der nächsten Gegner + TRUES geladen");

    // Games der nächsten Gegner + TRUE
    final Query<ScheduledGame> query2 = PrimeData.getInstance().getSession().createQuery("FROM ScheduledGame s " +
        "WHERE teams LIKE :t1 OR teams LIKE :t2");
    query2.setParameter("t1", "%" + teams1.get(0).getId() + "%");
    query2.setParameter("t2", teams1.size() > 1 ? ("%" + teams1.get(1).getId() + "%") : ("%" + teams1.get(0).getId() + "%"));
    final List<ScheduledGame> list2 = query2.list();
    list2.stream().filter(scheduledGame -> scheduledGame.getQueueType().equals(QueueType.OTHER)).forEach(RiotGameRequester::loadMatchmade);

    logger.info("Games der nächsten Gegner + TRUES geladen");

    // Alle Tourney
    ScheduledGame.findMode(QueueType.TOURNEY).forEach(RiotGameRequester::loadCompetitive);
    logger.info("Tourney geladen");

    // Alle Games mit 3
    // TODO Lange Ladezeit
    ScheduledGame.findMode(QueueType.CLASH).stream().filter(scheduledGame -> scheduledGame.getTeamsMap().keySet().stream()
        .anyMatch(team -> scheduledGame.getTeamsMap().get(team) > 2)).forEach(RiotGameRequester::loadClashGame);
    ScheduledGame.findMode(QueueType.OTHER).stream().filter(scheduledGame -> scheduledGame.getTeamsMap().keySet().stream()
        .anyMatch(team -> scheduledGame.getTeamsMap().get(team) > 2)).forEach(RiotGameRequester::loadMatchmade);
    logger.info("Games mit 3 geladen");

    // Alle Clash
    ScheduledGame.findMode(QueueType.CLASH).forEach(RiotGameRequester::loadClashGame);
    logger.info("Clash geladen");

    // Alle Games
    ScheduledGame.findMode(QueueType.OTHER).forEach(RiotGameRequester::loadMatchmade);

    return true;
  }
}
