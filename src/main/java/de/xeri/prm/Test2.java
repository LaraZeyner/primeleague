package de.xeri.prm;

import java.util.Map;

import de.xeri.prm.manager.LoadupManager;
import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.league.Account;
import de.xeri.prm.models.match.playerperformance.Playerperformance;
import de.xeri.prm.models.match.playerperformance.Value;
import de.xeri.prm.models.match.ratings.DisplaystatSubtype;
import de.xeri.prm.models.match.ratings.Ratings;
import de.xeri.prm.servlet.loader.league.LoadLeague;
import de.xeri.prm.servlet.loader.scouting.LoadPlayers;

/**
 * Created by Lara on 09.05.2022 for web
 */
public class Test2 {
  public static void main(String[] args) {
    LoadupManager.init();

    /*final Query<Playerperformance> namedQuery = PrimeData.getInstance().getSession().getNamedQuery("Playerperformance.forChampion");
    namedQuery.setParameter("champion", Champion.find("Ahri"));
    namedQuery.setMaxResults(28);
    final List<Playerperformance> list = namedQuery.list();

    AllEventsTimelineView view = new AllEventsTimelineView();
    view.init();
    view.getPlayers().forEach(Player::getCurrentEloString);*/

    final LoadLeague loadLeague = new LoadLeague();
    loadLeague.init();
    loadLeague.updateAll();
    loadLeague.updateElosAndGames();

    final LoadPlayers loadPlayers = new LoadPlayers();
    loadPlayers.init();
    /*init();
    long time = System.currentTimeMillis();
    final LoadPlayers loadPlayers = new LoadPlayers();
    loadPlayers.init();
    System.out.println("hi | " + (System.currentTimeMillis() - time) / 1000);*/
    System.out.println("hi");
  }

  public static void init() {
    final Map<Lane, Map<String, Value>> values = Playerperformance.getValues();
    final Account account = Account.findName("TRUE Whitelizard");
    final Ratings ratings = Ratings.getRatings(account, DisplaystatSubtype.SUPPORT);

    if (ratings != null) {
      final Double count = ratings.getPlayerRatings().get("count");
      System.out.println("Spiele : " + count);

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
    }
  }
}
