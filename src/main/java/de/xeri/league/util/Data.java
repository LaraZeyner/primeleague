package de.xeri.league.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import de.xeri.league.models.dynamic.Champion;
import de.xeri.league.models.dynamic.Item;
import de.xeri.league.models.dynamic.Rune;
import de.xeri.league.models.dynamic.Summonerspell;
import de.xeri.league.models.league.Account;
import de.xeri.league.models.league.League;
import de.xeri.league.models.league.Matchday;
import de.xeri.league.models.league.Matchlog;
import de.xeri.league.models.league.Player;
import de.xeri.league.models.league.Schedule;
import de.xeri.league.models.league.Season;
import de.xeri.league.models.league.SeasonElo;
import de.xeri.league.models.league.Stage;
import de.xeri.league.models.league.Team;
import de.xeri.league.models.league.TurnamentMatch;
import de.xeri.league.models.match.ChampionSelection;
import de.xeri.league.models.match.Game;
import de.xeri.league.models.match.GamePause;
import de.xeri.league.models.match.Gametype;
import de.xeri.league.models.match.Playerperformance;
import de.xeri.league.models.match.PlayerperformanceInfo;
import de.xeri.league.models.match.PlayerperformanceItem;
import de.xeri.league.models.match.PlayerperformanceKill;
import de.xeri.league.models.match.PlayerperformanceLevel;
import de.xeri.league.models.match.PlayerperformanceObjective;
import de.xeri.league.models.match.PlayerperformanceSummonerspell;
import de.xeri.league.models.match.ScheduledGame;
import de.xeri.league.models.match.Teamperformance;
import de.xeri.league.models.match.TeamperformanceBounty;
import de.xeri.league.util.io.request.RequestManager;
import org.hibernate.Session;

/**
 * Created by Lara on 29.03.2022 for TRUES
 */
public class Data {
  private static Data data;

  public static Data getInstance() {
    if (data == null) data = new Data();
    return data;
  }

  private final RequestManager requester;
  private final Session session;
  private final EntityManagerFactory factory;
  private final EntityManager manager;
  private final int statLimit = 180;

  public Data() {
    this.requester = new RequestManager();
    this.session = HibernateUtil.getSessionFactory().openSession();
    this.factory = Persistence.createEntityManagerFactory("Persistence");
    this.manager = factory.createEntityManager();
  }

  public int getStatLimit() {
    return statLimit;
  }

  public void save() {
//    Ability.save,
//    Abilitystyle.save();
    Account.save();
    Champion.save();
//    ChampionRelationship.save();
    ChampionSelection.save();
    Game.save();
    GamePause.save();
    Gametype.save();
//    Input.save();
    Item.save();
//    Item_Stat.save();
//    ItemStat.save();
//    Itemstyle.save();
    League.save();
//    LeagueMap.save();
    Matchday.save();
    Matchlog.save();
    Player.save();
    Playerperformance.save();
    PlayerperformanceInfo.save();
    PlayerperformanceItem.save();
    PlayerperformanceKill.save();
    PlayerperformanceLevel.save();
    PlayerperformanceObjective.save();
    PlayerperformanceSummonerspell.save();
//    Playstyle.save();
//    Resource.save();
    Rune.save();
//    Runetree.save();
    Schedule.save();
    ScheduledGame.save();
    Season.save();
    SeasonElo.save();
    Stage.save();
    Summonerspell.save();
    Team.save();
    Teamperformance.save();
    TeamperformanceBounty.save();
    TurnamentMatch.save();
//    Wincondition.save()
  }

  //<editor-fold desc="getter and setter">
  public RequestManager getRequester() {
    return requester;
  }

  public Session getSession() {
    return session;
  }

  public EntityManagerFactory getFactory() {
    return factory;
  }

  public EntityManager getManager() {
    return manager;
  }
  // </editor-fold>

}
