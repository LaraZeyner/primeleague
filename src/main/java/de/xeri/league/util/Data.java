package de.xeri.league.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import de.xeri.league.models.league.League;
import de.xeri.league.util.io.request.RequestManager;
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
  private final EntityManagerFactory factory;
  private final EntityManager manager;
  private final Transaction transaction;
  private final int statLimit = 180;
  private League currentGroup;

  public Data() {
    this.requester = new RequestManager();
    this.session = HibernateUtil.getSessionFactory().openSession();
    this.transaction = session.beginTransaction();
    this.factory = Persistence.createEntityManagerFactory("Persistence");
    this.manager = factory.createEntityManager();
  }

  public int getStatLimit() {
    return statLimit;
  }

  //<editor-fold desc="getter and setter">


  public League getCurrentGroup() {
    return currentGroup;
  }

  public void setCurrentGroup(League currentGroup) {
    this.currentGroup = currentGroup;
  }

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

  public void flush() {
    session.flush();
  }

  // </editor-fold>

}
