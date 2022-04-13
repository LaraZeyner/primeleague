package de.xeri.league.util;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

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
import de.xeri.league.models.match.Gametype;
import de.xeri.league.models.match.Playerperformance;
import de.xeri.league.models.match.PlayerperformanceSummonerspell;
import de.xeri.league.models.match.ScheduledGame;
import de.xeri.league.models.match.Teamperformance;
import de.xeri.league.models.others.ChampionRelationship;
import de.xeri.league.models.others.Playstyle;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

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
          Game.class,
          Gametype.class,
          Input.class,
          Item.class,
          Item_Stat.class,
          ItemStat.class,
          Itemstyle.class,
          League.class,
          LeagueMap.class,
          Matchday.class,
          Matchlog.class,
          Player.class,
          Playerperformance.class,
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

  public static SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public static void shutdown() {
    getSessionFactory().close();
  }

}