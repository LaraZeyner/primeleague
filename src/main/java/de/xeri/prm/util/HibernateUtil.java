package de.xeri.prm.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import de.xeri.prm.models.dynamic.Matchup;
import de.xeri.prm.models.dynamic.Resource;
import de.xeri.prm.models.dynamic.Rune;
import de.xeri.prm.models.dynamic.Runetree;
import de.xeri.prm.models.dynamic.Summonerspell;
import de.xeri.prm.models.enums.ChampionPlaystyle;
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
import de.xeri.prm.models.match.playerperformance.JunglePath;
import de.xeri.prm.models.match.playerperformance.Playerperformance;
import de.xeri.prm.models.match.playerperformance.PlayerperformanceInfo;
import de.xeri.prm.models.match.playerperformance.PlayerperformanceItem;
import de.xeri.prm.models.match.playerperformance.PlayerperformanceKill;
import de.xeri.prm.models.match.playerperformance.PlayerperformanceLevel;
import de.xeri.prm.models.match.playerperformance.PlayerperformanceObjective;
import de.xeri.prm.models.match.playerperformance.PlayerperformanceSummonerspell;
import de.xeri.prm.models.match.ratings.Rating;
import de.xeri.prm.models.match.ratings.StatScope;
import de.xeri.prm.models.others.ChampionRelationship;
import de.xeri.prm.servlet.datatables.scouting.draft.CompositionAttribute;
import de.xeri.prm.util.logger.Logger;
import lombok.val;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Lara on 24.03.2022 for TRUES
 */
public final class HibernateUtil {

  private static final SessionFactory sessionFactory = buildSessionFactory();

  private static Map<Champion, Map<CompositionAttribute, Double>> championData;
  private static Map<CompositionAttribute, Double> averageChampionData;

  private static SessionFactory buildSessionFactory() {
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
        TurnamentMatch.class));
    final Properties properties = configuration.getProperties();
    final StandardServiceRegistryBuilder registry = new StandardServiceRegistryBuilder().applySettings(properties);
    return configuration.buildSessionFactory(registry.build());
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

  public static Object[] gamesOnAllLanes(Account account) {
    final Session session = Data.getInstance().getSession();
    final Query<Object[]> query = session.getNamedQuery("Playerperformance.gamesOnLane");
    query.setParameter("since", new Date(System.currentTimeMillis() - 180 * Const.MILLIS_PER_DAY));
    query.setParameter("account", account);
    return query.getSingleResult();
  }

  public static int gamesOnLaneRecently(Account account, Lane lane) {
    final Session session = Data.getInstance().getSession();
    final Query<Object> query = session.getNamedQuery("Playerperformance.gamesOnLaneRecently");
    query.setParameter("since", new Date(System.currentTimeMillis() - 180 * Const.MILLIS_PER_DAY));
    query.setParameter("account", account);
    query.setParameter("lane", lane);
    return (int) (((Long) query.getSingleResult()).longValue());
  }

  public static LinkedHashMap<Short, Integer> getChampionIdsPickedOn(Account account, Lane lane, StatScope scope) {
    final Session session = Data.getInstance().getSession();
    final Query<Short> query = session.getNamedQuery(scope.equals(StatScope.COMPETITIVELIKE) ? "Playerperformance.championsPickedCompetitive" :
        (scope.equals(StatScope.COMPETITIVE) ? "Playerperformance.championsPickedCompet" : "Playerperformance.championsPickedOther"));
    return determineChampionIdsSorted(account, lane, scope, query);
  }

  public static LinkedHashMap<Short, Integer> getChampionIdsPresentOn(Account account, Lane lane, StatScope scope) {
    final Session session = Data.getInstance().getSession();
    final Query<Short> query = session.getNamedQuery(scope.equals(StatScope.COMPETITIVELIKE) ?
        "ChampionSelection.championsPresenceCompetitive" : "ChampionSelection.championsPresenceOther");
    return determineChampionIdsSorted(account, lane, scope, query);
  }

  public static Map<CompositionAttribute, Double> getAverageChampionStats() {
    if (averageChampionData == null) {
      final Session session = Data.getInstance().getSession();
      final Query<Object[]> query = session.getNamedQuery("Playerperformance.averageChampionValues");

      Object[] objects = query.getSingleResult();
      final Map<CompositionAttribute, Double> championMap = new HashMap<>();
      championMap.put(CompositionAttribute.WINCONDITION_TRADE, getChampionStats().keySet().stream()
          .mapToDouble(champion -> getChampionStats().get(champion).get(CompositionAttribute.WINCONDITION_TRADE)).average().orElse(0));
      championMap.put(CompositionAttribute.WINCONDITION_ALLIN, getChampionStats().keySet().stream()
          .mapToDouble(champion -> getChampionStats().get(champion).get(CompositionAttribute.WINCONDITION_ALLIN)).average().orElse(0));
      championMap.put(CompositionAttribute.WINCONDITION_SUSTAIN, getChampionStats().keySet().stream()
          .mapToDouble(champion -> getChampionStats().get(champion).get(CompositionAttribute.WINCONDITION_SUSTAIN)).average().orElse(0));

      championMap.put(CompositionAttribute.STATISTIC_CROWDCONTROL, objects[3] == null ? 0 : Double.parseDouble(objects[3].toString()));
      championMap.put(CompositionAttribute.STATISTIC_DAMAGE_PHYSICAL, objects[0] == null ? 0 : Double.parseDouble(objects[0].toString()));
      championMap.put(CompositionAttribute.STATISTIC_DAMAGE_MAGICAL, objects[1] == null ? 0 : Double.parseDouble(objects[1].toString()));
      championMap.put(CompositionAttribute.STATISTIC_DAMAGE_TOTAL, objects[2] == null ? 0 : Double.parseDouble(objects[2].toString()));
      championMap.put(CompositionAttribute.STATISTIC_DAMAGE_TRUE, championMap.get(CompositionAttribute.STATISTIC_DAMAGE_TOTAL) -
          championMap.get(CompositionAttribute.STATISTIC_DAMAGE_PHYSICAL) - championMap.get(CompositionAttribute.STATISTIC_DAMAGE_MAGICAL));

      championMap.put(CompositionAttribute.PLAYSTYLE_SIEGE, getChampionStats().keySet().stream()
          .mapToDouble(champion -> getChampionStats().get(champion).get(CompositionAttribute.PLAYSTYLE_SIEGE)).average().orElse(0));
      championMap.put(CompositionAttribute.PLAYSTYLE_SPLITPUSH, getChampionStats().keySet().stream()
          .mapToDouble(champion -> getChampionStats().get(champion).get(CompositionAttribute.PLAYSTYLE_SPLITPUSH)).average().orElse(0));
      championMap.put(CompositionAttribute.PLAYSTYLE_TEAMFIGHT, getChampionStats().keySet().stream()
          .mapToDouble(champion -> getChampionStats().get(champion).get(CompositionAttribute.PLAYSTYLE_TEAMFIGHT)).average().orElse(0));
      championMap.put(CompositionAttribute.TYPE_DIVING, getChampionStats().keySet().stream()
          .mapToDouble(champion -> getChampionStats().get(champion).get(CompositionAttribute.TYPE_DIVING)).average().orElse(0));

      championMap.put(CompositionAttribute.TYPE_AGGRESSION_ENGAGE, 10 - championMap.getOrDefault(CompositionAttribute.WINCONDITION_SUSTAIN, (double) 0));
      championMap.put(CompositionAttribute.TYPE_AGGRESSION_DISENGAGE, championMap.getOrDefault(CompositionAttribute.WINCONDITION_SUSTAIN, (double) 0));

      championMap.put(CompositionAttribute.TYPE_DAMAGE_BURST, getChampionStats().keySet().stream()
          .mapToDouble(champion -> getChampionStats().get(champion).get(CompositionAttribute.TYPE_DAMAGE_BURST)).average().orElse(0));
      championMap.put(CompositionAttribute.TYPE_DAMAGE_DPS, getChampionStats().keySet().stream()
          .mapToDouble(champion -> getChampionStats().get(champion).get(CompositionAttribute.TYPE_DAMAGE_DPS)).average().orElse(0));
      championMap.put(CompositionAttribute.TYPE_DURABILITY_FRONTLINE, getChampionStats().keySet().stream()
          .mapToDouble(champion -> getChampionStats().get(champion).get(CompositionAttribute.TYPE_DURABILITY_FRONTLINE)).average().orElse(0));
      championMap.put(CompositionAttribute.TYPE_DURABILITY_PEEL, getChampionStats().keySet().stream()
          .mapToDouble(champion -> getChampionStats().get(champion).get(CompositionAttribute.TYPE_DURABILITY_PEEL)).average().orElse(0));
      championMap.put(CompositionAttribute.TYPE_DURABILITY_RANGE, getChampionStats().keySet().stream()
          .mapToDouble(champion -> getChampionStats().get(champion).get(CompositionAttribute.TYPE_DURABILITY_RANGE)).average().orElse(0));
      championMap.put(CompositionAttribute.TYPE_GAMEPHASE_EARLYGAME, .5);
      championMap.put(CompositionAttribute.TYPE_GAMEPHASE_MIDGAME, .5);
      championMap.put(CompositionAttribute.TYPE_GAMEPHASE_LATEGAME, .5);
      championMap.put(CompositionAttribute.TYPE_GANKSETUPS, getChampionStats().keySet().stream()
          .mapToDouble(champion -> getChampionStats().get(champion).get(CompositionAttribute.TYPE_GANKSETUPS)).average().orElse(0));
      championMap.put(CompositionAttribute.TYPE_WAVECLEAR, getChampionStats().keySet().stream()
          .mapToDouble(champion -> getChampionStats().get(champion).get(CompositionAttribute.TYPE_WAVECLEAR)).average().orElse(0));
      averageChampionData = championMap;
    }
    return averageChampionData;
  }

  public static Map<Champion, Map<CompositionAttribute, Double>> getChampionStats() {
    if (championData == null) {
      Map<Champion, Map<CompositionAttribute, Double>> map = new HashMap<>();
      final Session session = Data.getInstance().getSession();
      final Query<Object[]> query = session.getNamedQuery("Playerperformance.championValues");
      final List<Object[]> list = query.list();

      final Map<Champion, List<Double>> wins = getWins();

      for (Object[] objects : list) {
        final Champion champion = (Champion) objects[0];
        final Map<CompositionAttribute, Double> championMap = new HashMap<>();
        championMap.put(CompositionAttribute.WINCONDITION_TRADE, (double) champion.getTrade());
        championMap.put(CompositionAttribute.WINCONDITION_ALLIN, (double) champion.getAllin());
        championMap.put(CompositionAttribute.WINCONDITION_SUSTAIN, (double) champion.getSustain());

        championMap.put(CompositionAttribute.STATISTIC_CROWDCONTROL, objects[4] == null ? 0 : Double.parseDouble(objects[4].toString()));
        championMap.put(CompositionAttribute.STATISTIC_DAMAGE_PHYSICAL, objects[1] == null ? 0 : Double.parseDouble(objects[1].toString()));
        championMap.put(CompositionAttribute.STATISTIC_DAMAGE_MAGICAL, objects[2] == null ? 0 : Double.parseDouble(objects[2].toString()));
        championMap.put(CompositionAttribute.STATISTIC_DAMAGE_TOTAL, objects[3] == null ? 0 : Double.parseDouble(objects[3].toString()));
        championMap.put(CompositionAttribute.STATISTIC_DAMAGE_TRUE, championMap.get(CompositionAttribute.STATISTIC_DAMAGE_TOTAL) -
            championMap.get(CompositionAttribute.STATISTIC_DAMAGE_PHYSICAL) - championMap.get(CompositionAttribute.STATISTIC_DAMAGE_MAGICAL));

        championMap.put(CompositionAttribute.PLAYSTYLE_SIEGE,
            (double) champion.getPlaystyles().stream().filter(Objects::nonNull).filter(playstyle -> playstyle.equals(ChampionPlaystyle.POKE)).count());
        championMap.put(CompositionAttribute.PLAYSTYLE_SPLITPUSH,
            (double) champion.getPlaystyles().stream().filter(Objects::nonNull).filter(playstyle -> playstyle.equals(ChampionPlaystyle.SPLITPUSH)).count());
        championMap.put(CompositionAttribute.PLAYSTYLE_TEAMFIGHT,
            (double) champion.getPlaystyles().stream().filter(Objects::nonNull).filter(playstyle -> playstyle.equals(ChampionPlaystyle.TEAMFIGHT)).count());
        championMap.put(CompositionAttribute.TYPE_DIVING,
            (double) champion.getPlaystyles().stream().filter(Objects::nonNull).filter(playstyle -> playstyle.equals(ChampionPlaystyle.DIVING)).count());


        championMap.put(CompositionAttribute.TYPE_AGGRESSION_ENGAGE, championMap.getOrDefault(CompositionAttribute.WINCONDITION_ALLIN, (double) 0));
        championMap.put(CompositionAttribute.TYPE_AGGRESSION_DISENGAGE, 10 - championMap.getOrDefault(CompositionAttribute.WINCONDITION_ALLIN, (double) 0));

        championMap.put(CompositionAttribute.TYPE_DAMAGE_BURST, champion.getBurstValue());
        championMap.put(CompositionAttribute.TYPE_DAMAGE_DPS, 1 - champion.getBurstValue());

        championMap.put(CompositionAttribute.TYPE_DURABILITY_FRONTLINE, champion.getDurability());
        championMap.put(CompositionAttribute.TYPE_DURABILITY_PEEL, 6000 - champion.getRange() - champion.getDurability());
        championMap.put(CompositionAttribute.TYPE_DURABILITY_RANGE, champion.getRange());

        championMap.put(CompositionAttribute.TYPE_GAMEPHASE_EARLYGAME, wins.get(champion).get(0));
        championMap.put(CompositionAttribute.TYPE_GAMEPHASE_MIDGAME, wins.get(champion).get(1));
        championMap.put(CompositionAttribute.TYPE_GAMEPHASE_LATEGAME, wins.get(champion).get(2));

        championMap.put(CompositionAttribute.TYPE_GANKSETUPS, objects[5] == null ? 0 : Double.parseDouble(objects[5].toString()));
        championMap.put(CompositionAttribute.TYPE_WAVECLEAR, (double) (champion.getWaveClear() != null ? champion.getWaveClear() : 0));
        map.put(champion, championMap);
        championData = map;
      }
    }
    return championData;
  }

  public static List<Integer> getWins(Account account, Lane lane, short championId) {
    final Session session = Data.getInstance().getSession();
    final Query<Object[]> query = session.getNamedQuery("Playerperformance.championWins");
    query.setParameter("since", new Date(System.currentTimeMillis() - 180 * Const.MILLIS_PER_DAY));
    query.setParameter("account", account);
    query.setParameter("lane", lane);
    query.setParameter("championId", championId);
    final Object[] singleResult = query.getSingleResult();
    return Arrays.asList((int) (((Long) singleResult[0]).longValue()), (int) (((Long) singleResult[1]).longValue()));
  }

  private static Map<Champion, List<Double>> getWins() {
    final Session session = Data.getInstance().getSession();
    final Query<Object[]> query = session.getNamedQuery("ChampionSelection.championStrongPhase");
    final List<Object[]> list = query.list();
    Map<Champion, List<Double>> map = new HashMap<>();
    for (Object[] objects : list) {
      Champion champion = (Champion) objects[0];
      Double early = (Double) objects[1];
      Double mid = (Double) objects[2];
      Double late = (Double) objects[3];
      map.put(champion, Arrays.asList(early, mid, late));
    }

    return map;
  }

  @NotNull
  private static LinkedHashMap<Short, Integer> determineChampionIdsSorted(Account account, Lane lane, StatScope
      scope, Query<Short> query) {
    query.setParameter("since", new Date(System.currentTimeMillis() - (scope.equals(StatScope.RECENT) ? 30 : 180) * Const.MILLIS_PER_DAY));
    query.setParameter("account", account);
    query.setParameter("lane", lane);
    final List<Short> list = query.list();

    val map = new HashMap<Short, Integer>();
    list.forEach(id -> map.put(id, map.containsKey(id) ? map.get(id) + 1 : 1));
    return map.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
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

  public static Object[] stats(Lane lane, Account account) {
    List<Lane> lanes = lane.equals(Lane.UNKNOWN) ? Arrays.asList(Lane.TOP, Lane.JUNGLE, Lane.MIDDLE, Lane.BOTTOM, Lane.UTILITY) :
        Collections.singletonList(lane);
    final Session session = Data.getInstance().getSession();
    final Query<Object[]> query = session.getNamedQuery("Playerperformance.findStatAvg");
    query.setParameter("since", new Date(System.currentTimeMillis() - 15_552_000_000L));
    query.setParameter("lanes", lanes);
    query.setParameter("account", account);
    return query.getSingleResult();
  }

  public static List<Matchup> determineMatchups(Champion champion) {
    final Session session = Data.getInstance().getSession();
    final Query<Object[]> query = session.getNamedQuery("Playerperformance.matchupOwn");
    query.setParameter("since", new Date(System.currentTimeMillis() - 15_552_000_000L));
    query.setParameter("picked", champion);
    final List<Matchup> collect = query.list().stream().map(Matchup::fromObjects).collect(Collectors.toList());

    final Query<Object[]> query2 = session.getNamedQuery("Playerperformance.matchupEnemy");
    query2.setParameter("since", new Date(System.currentTimeMillis() - 15_552_000_000L));
    query2.setParameter("picked", champion);
    final List<Matchup> collect2 = query2.list().stream().map(Matchup::fromObjects).collect(Collectors.toList());
    collect.forEach(matchup -> matchup.merge(collect2));
    return collect2;
  }

  public static Matchup determineMatchup(Champion champion, Champion versus) {
    return determineMatchups(champion).stream()
        .filter(matchup -> matchup.getChampion().equals(versus))
        .findFirst().orElse(new Matchup(versus, 0, 0));
  }

  public static List<Matchup> determineMatchups(Player player, Champion champion) {
    final Session session = Data.getInstance().getSession();
    final Query<Object[]> query = session.getNamedQuery("Playerperformance.matchupPlayer");
    query.setParameter("since", new Date(System.currentTimeMillis() - 15_552_000_000L));
    query.setParameter("picked", champion);
    query.setParameter("accounts", new ArrayList<>(player.getAccounts()));
    return query.list().stream().map(Matchup::fromObjects).collect(Collectors.toList());
  }

  public static Matchup determineMatchup(Player player, Champion champion, Champion versus) {
    return determineMatchups(player, champion).stream()
        .filter(matchup -> matchup.getChampion().equals(versus))
        .findFirst().orElse(new Matchup(versus, 0, 0));
  }


  public static SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public static void shutdown() {
    getSessionFactory().close();
  }


}