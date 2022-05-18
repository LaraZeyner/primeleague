package de.xeri.prm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.xeri.prm.manager.LoadupManager;
import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.league.Account;
import de.xeri.prm.models.league.TurnamentMatch;
import de.xeri.prm.models.match.ratings.DisplaystatSubtype;
import de.xeri.prm.models.match.ratings.Ratings;
import de.xeri.prm.models.match.playerperformance.Playerperformance;
import de.xeri.prm.models.match.playerperformance.Value;
import de.xeri.prm.servlet.datatables.table.PerformanceObject;

/**
 * Created by Lara on 09.05.2022 for web
 */
public class Test2 {
  public static void main(String[] args) {
    LoadupManager.init();
    /*EntityManagerFactory entityManagerFactory = Persistence
        .createEntityManagerFactory("Persistence");
    final EntityManager entityManager = entityManagerFactory
        .createEntityManager();*/
    init();
    System.out.println("hi");
  }


  private static final transient long serialVersionUID = -7441859998261775332L;
  private String championName;
  private String presence;
  private String gamesComp;
  private String games;
  private String wins;
  private String lead;
  private String goldEfficency;

  private static String competitiveGames;
  private static String playername;
  private static String kda;
  private static String vision;
  private static String roaming;
  private static String fighting;
  private static String income;
  private static String survival;
  private static String laning;
  private static List<PerformanceObject> performances;
  private static List<PerformanceObject> allPerformances;
  private static List<TurnamentMatch> matchesToUpdate;
  private static List<String> champions;

  public static void init() {
    /*allPerformances = new ArrayList<>();
    performances = new ArrayList<>();
    matchesToUpdate = new ArrayList<>();
    champions = Champion.get().stream().map(Champion::getName).collect(Collectors.toList());*/
    final Map<Lane, Map<String, Value>> values = Playerperformance.getValues();
    Account account = Account.findName("TRUE Xeri");
    final List<Playerperformance> playerperformances = new ArrayList<>(account.getPlayerperformances());

    /*competitiveGames = String.valueOf(playerperformances.stream()
        .filter(playerperformance -> playerperformance.getTeamperformance().getGame().isCompetitive())
        .count());*/

      /*final List<Champion> collect = Champion.get().stream()
          .collect(Collectors.toMap(champion -> champion, champion -> (int) playerperformances.stream()
              .filter(playerperformance -> playerperformance.getTeamperformance().getGame().isCompetitive())
              .filter(playerperformance -> playerperformance.wasPresent(champion)).count(), (a, b) -> b))
          .entrySet().stream()
          .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
          .map(Map.Entry::getKey)
          .collect(Collectors.toList()).subList(0, 10);*/

    final Ratings ratings = new Ratings(DisplaystatSubtype.BOTTOM,
        playerperformances.stream().filter(Playerperformance::isCompetitiveLike).collect(Collectors.toList()));

    final String fighting = ratings.getFighting().format();
    System.out.println("Fighting  : " + fighting);

    final String income = ratings.getIncome().format();
    System.out.println("Income    : " + income);

    final String laning = ratings.getLaning().format();
    System.out.println("Laning    : " + laning);
    
    final String objectives = ratings.getObjectives().format();
    System.out.println("Objective : " + objectives);

    final String roaming = ratings.getRoaming().format();
    System.out.println("Roaming   : " + roaming);

    final String survival = ratings.getSurvival().format();
    System.out.println("Survival  : " + survival);

    final String adaption = ratings.getAdaption().format();
    System.out.println("Adaption  : " + adaption);
    System.out.println("===================");

    final String all = ratings.format();
    System.out.println("Alles     : " + all);

      /*for (Champion champion : Champion.get()) {
        final List<Playerperformance> collect1 = playerperformances.stream()
            .filter(playerperformance -> playerperformance.getTeamperformance().getGame().isCompetitive())
            .filter(playerperformance -> playerperformance.wasPresent(champion)).collect(Collectors.toList());
        String competitive = String.valueOf(collect1.size());
        final int presenceInt = collect1.size() * 100 / Integer.parseInt(competitiveGames);
        String presence = String.valueOf(presenceInt);

        final List<Playerperformance> collect2 = playerperformances.stream()
            .filter(playerperformance -> playerperformance.wasPresent(champion)).collect(Collectors.toList());
        final Ratings ratings = new Ratings(collect2);
        final Stat stat = ratings.getLaning().getPlaystyle().getPositioning();
        stat.calculate();
        stat.format();
        String games = String.valueOf(collect2.size());
        //final String wins = ratings.winrate.format();
        //final String lead = ratings.laneLead.format();
        //final String efficiency = ratings.goldXpEfficiency.format();

        //allPerformances.add(new PerformanceObject(champion.getName(), presence, competitive, games, wins, lead, efficiency));

        for (Champion champion1 : collect) {
          performances.add(allPerformances.stream()
              .filter(pO -> pO.getChampionName().equals(champion1.getName()))
              .findFirst().orElse(null));
        }
      }*/
  }
}
