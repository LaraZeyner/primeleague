package de.xeri.league.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.persistence.NoResultException;
import javax.persistence.metamodel.EntityType;

import de.xeri.league.manager.Data;
import de.xeri.league.models.dynamic.Ability;
import de.xeri.league.models.dynamic.Abilitystyle;
import de.xeri.league.models.dynamic.Champion;
import de.xeri.league.models.dynamic.Item;
import de.xeri.league.models.dynamic.ItemStat;
import de.xeri.league.models.dynamic.Item_Stat;
import de.xeri.league.models.dynamic.Itemstyle;
import de.xeri.league.models.dynamic.LeagueMap;
import de.xeri.league.models.dynamic.Resource;
import de.xeri.league.models.dynamic.Rune;
import de.xeri.league.models.dynamic.Runetree;
import de.xeri.league.models.dynamic.Summonerspell;
import de.xeri.league.models.dynamic.Wincondition;
import de.xeri.league.models.io.Input;
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
import de.xeri.league.models.match.ScheduledGame;
import de.xeri.league.models.match.Teamperformance;
import de.xeri.league.models.match.TeamperformanceBounty;
import de.xeri.league.models.match.neu.Rating;
import de.xeri.league.models.match.playerperformance.JunglePath;
import de.xeri.league.models.match.playerperformance.Playerperformance;
import de.xeri.league.models.match.playerperformance.PlayerperformanceInfo;
import de.xeri.league.models.match.playerperformance.PlayerperformanceItem;
import de.xeri.league.models.match.playerperformance.PlayerperformanceKill;
import de.xeri.league.models.match.playerperformance.PlayerperformanceLevel;
import de.xeri.league.models.match.playerperformance.PlayerperformanceObjective;
import de.xeri.league.models.match.playerperformance.PlayerperformanceSummonerspell;
import de.xeri.league.models.others.ChampionRelationship;
import de.xeri.league.models.others.Playstyle;
import de.xeri.league.util.logger.Logger;
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
    final List<T> list = performList(entityClass, primaryKey);
    if (list.isEmpty()) {
      Logger.getLogger("Entity-Query").attention("keine Einträge in der Collection");
    }
    return list;
  }

  public static <T> List<T> findList(Class<T> entityClass, String[] params, Object[] values) {
    final List<T> list = performList(entityClass, params, values, "findBy");
    if (list.isEmpty()) {
      Logger.getLogger("Entity-Query").attention("keine Einträge in der Collection");
    }
    return list;
  }

  public static <T> List<T> findList(Class<T> entityClass, String[] params, Object[] values, String subQuery) {
    final List<T> list = performList(entityClass, params, values, subQuery);
    if (list.isEmpty()) {
      Logger.getLogger("Entity-Query").attention("keine Einträge in der Collection");
    }
    return list;
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

  private static <T> List<T> performList(Class<T> entityClass, String primaryKey) {
    final Query<T> query = perform(entityClass, primaryKey);
    return query.list();
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

  private static <T> List<T> performList(Class<T> entityClass, String[] params, Object[] values, String subquery) {
    final Query<T> query = performWithSubquery(entityClass, params, values, subquery);
    return query.list();
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


  public static SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public static void shutdown() {
    getSessionFactory().close();
  }

}