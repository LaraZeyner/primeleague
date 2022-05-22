package de.xeri.prm.manager;

import de.xeri.prm.models.league.League;
import de.xeri.prm.models.league.Season;
import de.xeri.prm.models.league.Team;
import de.xeri.prm.util.Const;
import de.xeri.prm.util.HibernateUtil;
import de.xeri.prm.util.io.request.RequestManager;
import de.xeri.prm.util.logger.Logger;
import lombok.val;
import org.hibernate.Session;
import org.hibernate.Transaction;

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
  /*private final EntityManagerFactory factory;
  private final EntityManager manager;*/
  private final Transaction transaction;
  private final int statLimit = 180;
  private League currentGroup;
  private Season currentSeason;

  public Data() {
    this.requester = new RequestManager();
    this.session = HibernateUtil.getSessionFactory().openSession();
    this.transaction = session.beginTransaction();
    /*this.factory = Persistence.createEntityManagerFactory("Persistence");
    this.manager = factory.createEntityManager();*/
  }

  public void init() {
    if (currentGroup == null) {
      Logger logger = Logger.getLogger("Init");
      val team = Team.findTid(Const.TEAMID);
      data.setCurrentGroup(team.getLastLeague());
      logger.info("Gruppe geladen");

      val season = Season.current();
      data.setCurrentSeason(season);
      logger.info("Season geladen");
      logger.info("Datenbank geladen");
    }
  }

  public int getStatLimit() {
    return statLimit;
  }

  //<editor-fold desc="getter and setter">
  public League getCurrentGroup() {
    if (currentGroup == null) {
      data.init();
    }
    return currentGroup;
  }

  public void setCurrentGroup(League currentGroup) {
    this.currentGroup = currentGroup;
  }

  public Season getCurrentSeason() {
    if (currentSeason == null) {
      data.init();
    }
    return currentSeason;
  }

  public void setCurrentSeason(Season currentSeason) {
    this.currentSeason = currentSeason;
  }

  public RequestManager getRequester() {
    return requester;
  }

  public Session getSession() {
    return session;
  }

  /*public EntityManagerFactory getFactory() {
    return factory;
  }

  public EntityManager getManager() {
    return manager;
  }*/

  public Transaction getTransaction() {
    return transaction;
  }

  public void commit() {
    transaction.commit();
    transaction.begin();
  }

  public void save(Object object) {
    session.saveOrUpdate(object);
  }

  public void remove(Object object) {
    session.remove(object);
  }

  public void flush() {
    session.flush();
  }

  // </editor-fold>

}
