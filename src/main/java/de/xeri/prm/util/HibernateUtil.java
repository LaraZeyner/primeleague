package de.xeri.prm.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.persistence.NoResultException;
import javax.persistence.metamodel.EntityType;

import de.xeri.prm.manager.Data;
import de.xeri.prm.models.dynamic.Ability;
import de.xeri.prm.models.dynamic.Abilitystyle;
import de.xeri.prm.models.dynamic.Champion;
import de.xeri.prm.models.dynamic.Item;
import de.xeri.prm.models.dynamic.ItemStat;
import de.xeri.prm.models.dynamic.Item_Stat;
import de.xeri.prm.models.dynamic.Itemstyle;
import de.xeri.prm.models.dynamic.LeagueMap;
import de.xeri.prm.models.dynamic.Resource;
import de.xeri.prm.models.dynamic.Rune;
import de.xeri.prm.models.dynamic.Runetree;
import de.xeri.prm.models.dynamic.Summonerspell;
import de.xeri.prm.models.dynamic.Wincondition;
import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.io.Input;
import de.xeri.prm.models.league.Account;
import de.xeri.prm.models.league.League;
import de.xeri.prm.models.league.Matchday;
import de.xeri.prm.models.league.Matchlog;
import de.xeri.prm.models.league.Player;
import de.xeri.prm.models.league.Schedule;
import de.xeri.prm.models.league.Season;
import de.xeri.prm.models.league.SeasonElo;
import de.xeri.prm.models.league.Stage;
import de.xeri.prm.models.league.Team;
import de.xeri.prm.models.league.TurnamentMatch;
import de.xeri.prm.models.match.ChampionSelection;
import de.xeri.prm.models.match.Game;
import de.xeri.prm.models.match.GamePause;
import de.xeri.prm.models.match.Gametype;
import de.xeri.prm.models.match.ScheduledGame;
import de.xeri.prm.models.match.Teamperformance;
import de.xeri.prm.models.match.TeamperformanceBounty;
import de.xeri.prm.models.match.ratings.Rating;
import de.xeri.prm.models.match.playerperformance.JunglePath;
import de.xeri.prm.models.match.playerperformance.Playerperformance;
import de.xeri.prm.models.match.playerperformance.PlayerperformanceInfo;
import de.xeri.prm.models.match.playerperformance.PlayerperformanceItem;
import de.xeri.prm.models.match.playerperformance.PlayerperformanceKill;
import de.xeri.prm.models.match.playerperformance.PlayerperformanceLevel;
import de.xeri.prm.models.match.playerperformance.PlayerperformanceObjective;
import de.xeri.prm.models.match.playerperformance.PlayerperformanceSummonerspell;
import de.xeri.prm.models.others.ChampionRelationship;
import de.xeri.prm.models.others.Playstyle;
import de.xeri.prm.util.logger.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

/**
 * Created by Lara on 24.03.2022 for TRUES
 */
public final class HibernateUtil {

  private static final SessionFactory sessionFactory = buildSessionFactory();

  private static SessionFactory buildSessionFactory() {
    try {
      // Create the SessionFactory from hibernate.cfg.xml
      final Configuration configuration = addClasses(Arrays.asList(
          Ability.class,
          Abilitystyle.class,
          Account.class,
          Champion.class,
          ChampionRelationship.class,
          ChampionSelection.class,
          Rating.class,
          Game.class,
          GamePause.class,
          Gametype.class,
          Input.class,
          Item.class,
          Item_Stat.class,
          ItemStat.class,
          Itemstyle.class,
          JunglePath.class,
          League.class,
          LeagueMap.class,
          Matchday.class,
          Matchlog.class,
          Player.class,
          Playerperformance.class,
          PlayerperformanceInfo.class,
          PlayerperformanceItem.class,
          PlayerperformanceKill.class,
          PlayerperformanceLevel.class,
          PlayerperformanceObjective.class,
          PlayerperformanceSummonerspell.class,
          Playstyle.class,
          Resource.class,
          Rune.class,
          Runetree.class,
          Schedule.class,
          ScheduledGame.class,
          Season.class,
          SeasonElo.class,
          Stage.class,
          Summonerspell.class,
          Team.class,
          Teamperformance.class,
          TeamperformanceBounty.class,
          TurnamentMatch.class,
          Wincondition.class));
      final Properties properties = configuration.getProperties();
      final StandardServiceRegistryBuilder registry = new StandardServiceRegistryBuilder().applySettings(properties);
      return configuration.buildSessionFactory(registry.build());
    } catch (Throwable ex) {
      Logger.getLogger("Hibernate").severe("Initial SessionFactory creation failed." + ex);
      System.err.println();
      throw new ExceptionInInitializerError(ex);
    }
  }

  private static Configuration addClasses(List<Class> classes) {
    final Configuration configuration = new Configuration().configure();
    classes.forEach(configuration::addAnnotatedClass);
    return configuration;
  }

  public static <T> List<T> findList(Class<T> entityClass) {
    final Session session = Data.getInstance().getSession();
    final EntityType<T> storedEntity = session.getMetamodel().entity(entityClass);
    final String entityClassName = storedEntity.getName();

    try {
      final Query<Object[]> query = session.getNamedQuery(entityClassName + ".findAll");
      return query.list().stream().map(t -> (T) t[0]).collect(Collectors.toList());
    } catch (ClassCastException ex) {
      final Query<T> query = session.getNamedQuery(entityClassName + ".findAll");
      return query.list();
    }
  }

  public static <T> List<T> findList(Class<T> entityClass, String identifier, boolean bool) {
    if (bool) {
      final Session session = Data.getInstance().getSession();
      final EntityType<T> storedEntity = session.getMetamodel().entity(entityClass);
      final String entityClassName = storedEntity.getName();

      final Query<T> query = session.getNamedQuery(entityClassName + "." + identifier);
      return query.list();
    }
    return findList(entityClass, identifier);
  }

  public static <T> List<T> findList(Class<T> entityClass, long primaryKey) {
    return findList(entityClass, String.valueOf(primaryKey));
  }

  public static <T> List<T> findList(Class<T> entityClass, String primaryKey) {
    final Query<T> query = perform(entityClass, primaryKey);
    return query.list();
  }

  public static <T> List<T> findList(Class<T> entityClass, String[] params, Object[] values) {
    return findList(entityClass, params, values, "findBy");
  }

  public static <T> List<T> findList(Class<T> entityClass, String[] params, Object[] values, String subQuery) {
    final Query<T> query = performWithSubquery(entityClass, params, values, subQuery);
    return query.list();
  }

  public static <T> T find(Class<T> entityClass, long primaryKey) {
    return find(entityClass, String.valueOf(primaryKey));
  }

  public static <T> T find(Class<T> entityClass, String primaryKey) {
    try {
      return performSingle(entityClass, primaryKey);
    } catch (NoResultException exception) {
      return null;
    }
  }

  public static <T> T find(Class<T> entityClass, String[] params, Object[] values) {
    try {
      return performSingle(entityClass, params, values, "findBy");
    } catch (NoResultException exception) {
      return null;
    }
  }

  public static <T> T find(Class<T> entityClass, String[] params, Object[] values, String subQuery) {
    try {
      return performSingle(entityClass, params, values, subQuery);
    } catch (NoResultException exception) {
      return null;
    }
  }

  public static <T> boolean has(Class<T> entityClass, long primaryKey) {
    return has(entityClass, String.valueOf(primaryKey));
  }

  public static <T> boolean has(Class<T> entityClass, String primaryKey) {
    try {
      performSingle(entityClass, primaryKey);
      return true;
    } catch (NoResultException exception) {
      return false;
    }
  }

  public static <T> boolean has(Class<T> entityClass, String[] params, Object[] values) {
    try {
      performSingle(entityClass, params, values, "findBy");
      return true;
    } catch (NoResultException exception) {
      return false;
    }
  }

  public static <T> boolean has(Class<T> entityClass, String[] params, Object[] values, String subQuery) {
    try {
      performSingle(entityClass, params, values, subQuery);
      return true;
    } catch (NoResultException exception) {
      return false;
    }
  }

  private static <T> T performSingle(Class<T> entityClass, String primaryKey) {
    final Query<T> query = perform(entityClass, primaryKey);
    return query.setMaxResults(1).getSingleResult();
  }

  private static <T> Query<T> perform(Class<T> entityClass, String primaryKey) {
    final Session session = Data.getInstance().getSession();
    final EntityType<T> storedEntity = session.getMetamodel().entity(entityClass);
    final String entityClassName = storedEntity.getName();

    final Query<T> query = session.getNamedQuery(entityClassName + ".findById");
    final Map<Class<?>, Number> ret = new HashMap<>();

    try {
      ret.put(Long.class, Long.parseLong(primaryKey));
      ret.put(Integer.class, Integer.parseInt(primaryKey));
      ret.put(Short.class, Short.parseShort(primaryKey));
      ret.put(Byte.class, Byte.parseByte(primaryKey));

    } catch (NumberFormatException exception) {
      Logger.getLogger("Entity-Query").throwing(exception);
    }

    final Class<?> pk = query.getParameter("pk").getParameterType();
    query.setParameter("pk", pk.equals(String.class) ? primaryKey : ret.get(pk));
    return query;
  }

  private static <T> T performSingle(Class<T> entityClass, String[] params, Object[] values, String subquery) {
    try {
      final Query<T> query = performWithSubquery(entityClass, params, values, subquery);
      return query.setMaxResults(1).getSingleResult();
    } catch (ClassCastException ex) {
      ex.printStackTrace();
    }
    return null;
  }

  private static <T> Query<T> performWithSubquery(Class<T> entityClass, String[] params, Object[] values, String subquery) {
    final Session session = Data.getInstance().getSession();
    final EntityType<T> storedEntity = session.getMetamodel().entity(entityClass);
    final String entityClassName = storedEntity.getName();

    final Query<T> query = session.getNamedQuery(entityClassName + "." + subquery);

    for (int i = 0; i < params.length; i++) {
      final String param = params[i];
      final Object value = values[i];
      query.setParameter(param, value);
    }
    return query;
  }

  public static Object[] stats(String queryName, Lane lane) {
    List<Lane> lanes = lane.equals(Lane.UNKNOWN) ? Arrays.asList(Lane.TOP, Lane.JUNGLE, Lane.MIDDLE, Lane.BOTTOM, Lane.UTILITY) :
        Collections.singletonList(lane);
    final Session session = Data.getInstance().getSession();
    final Query<Object[]> query = session.getNamedQuery(queryName);
    query.setParameter("since", new Date(System.currentTimeMillis() - 15_552_000_000L));
    query.setParameter("lanes", lanes);
    return query.getSingleResult();
  }


  public static SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public static void shutdown() {
    getSessionFactory().close();
  }

}