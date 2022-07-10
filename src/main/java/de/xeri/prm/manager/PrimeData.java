package de.xeri.prm.manager;

import de.xeri.prm.models.league.League;
import de.xeri.prm.models.league.Season;
import de.xeri.prm.models.league.Team;
import de.xeri.prm.util.Const;
import de.xeri.prm.util.HibernateUtil;
import de.xeri.prm.util.io.json.JSON;
import de.xeri.prm.util.io.request.RequestManager;
import de.xeri.prm.util.logger.Logger;
import lombok.val;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Created by Lara on 29.03.2022 for TRUES
 */
public class PrimeData {
  private static PrimeData primeData;

  public static PrimeData getInstance() {
    if (primeData == null) {
      primeData = new PrimeData();
      primeData.init();
    }
    return primeData;
  }

  private final RequestManager requester;
  private final Session session;
  private final Transaction transaction;
  private final int statLimit = 180;
  private League currentGroup;
  private Season currentSeason;
  private String currentVersion;
  private Team trueTeam;

  public PrimeData() {
    this.requester = new RequestManager();
    this.session = HibernateUtil.getSessionFactory().openSession();
    this.transaction = session.beginTransaction();
  }

  private void init() {
    if (currentGroup == null) {
      Logger logger = Logger.getLogger("Init");
      val team = Team.findTid(Const.TEAMID);
      primeData.setCurrentGroup(team.getLastLeague());
      logger.info("Gruppe geladen");

      val season = Season.current();
      primeData.setCurrentSeason(season);
      primeData.setTrueTeam(team);
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
      primeData.init();
    }
    return currentGroup;
  }

  public void setCurrentGroup(League currentGroup) {
    this.currentGroup = currentGroup;
  }

  public Season getCurrentSeason() {
    if (currentSeason == null) {
      primeData.init();
    }
    return currentSeason;
  }

  public Team getTrueTeam() {
    if (trueTeam == null) {
      primeData.init();
    }
    return trueTeam;
  }

  public String getCurrentVersion() {
    if (currentVersion == null) {
      final JSON json = getInstance().getRequester().requestJSON("https://ddragon.leagueoflegends.com/api/versions.json");
      final Object versionObject = json.getJSONArray().get(0);
      currentVersion = String.valueOf(versionObject);
    }
    return currentVersion;
  }

  public void setCurrentSeason(Season currentSeason) {
    this.currentSeason = currentSeason;
  }

  public void setTrueTeam(Team trueTeam) {
    this.trueTeam = trueTeam;
  }

  public RequestManager getRequester() {
    return requester;
  }

  public Session getSession() {
    return session;
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
