package de.xeri.prm.servlet.loader.scouting.performance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.league.Player;
import de.xeri.prm.models.league.SeasonElo;
import de.xeri.prm.models.match.ratings.Ratings;
import de.xeri.prm.models.match.ratings.fighting.Fighting;
import de.xeri.prm.models.match.ratings.income.Income;
import de.xeri.prm.models.match.ratings.laning.Laning;
import de.xeri.prm.models.match.ratings.objectives.Objectives;
import de.xeri.prm.models.match.ratings.roaming.Roaming;
import de.xeri.prm.models.match.ratings.survival.Survival;
import de.xeri.prm.util.HibernateUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by Lara on 18.05.2022 for web
 */
@NoArgsConstructor
@Getter
public class PlayerView implements Serializable {
  private static final long serialVersionUID = -8701843635861320113L;

  private int games;
  private Player player;
  private String name;
  private String positionalIconUrl;
  private String rankedIconUrl;
  private SeasonElo seasonElo;
  private Ratings ratings;
  private String totalGames;
  private List<ChampionView> champions;
  private String kda;
  private String objectives;
  private List<String> onObjectives;
  private String roaming;
  private List<String> onRoaming;
  private String fighting;
  private List<String> onFighting;
  private String income;
  private List<String> onIncome;
  private String survival;
  private List<String> onSurvival;
  private String laning;
  private List<String> onLaning;
  private String lead;
  private String firstWard;
  private String firstObjective;
  private String firstKill;
  private String firstRecall;
  private String firstItem;
  private List<String> playerTags; //TODO (Abgie) 24.05.2022:

  public PlayerView(Player player, Lane lane) {
    this.player = player;
    this.name = player.getDisplayName();
    this.seasonElo = player.getCurrentElo();
    this.positionalIconUrl = player.getCurrentElo().getElo().getPositionalIconUrl(lane);
    this.rankedIconUrl = player.getCurrentElo().getElo().getRankedIconUrl();

    final Ratings ratings = Ratings.getRatings(player.getActiveAccount(), lane.getSubtype());
    this.ratings = ratings;
    this.games = (int) (double) this.ratings.getPlayerRatings().get("count");

    this.totalGames = "" + player.getAccounts().stream().mapToInt(account -> HibernateUtil.gamesOnLaneRecently(account, lane)).sum();
    this.champions = player.getChampionsPresence(lane);
    this.kda = ratings.getAdaption().getStats().getKDA().display();
    this.objectives = ratings.getObjectives().format();
    this.roaming = ratings.getRoaming().format();
    this.fighting = ratings.getFighting().format();
    this.income = ratings.getIncome().format();
    this.survival = ratings.getSurvival().format();
    this.laning = ratings.getLaning().format();
    this.lead = ratings.getLaning().getLaneBilance().getLead().display();
    this.firstWard = ratings.getObjectives().getWards().getFirstWardTime().display();
    this.firstObjective = ratings.getObjectives().getBotsideObjectives().getDragonTime().display();
    this.firstKill = ratings.getSurvival().getEarlySurvival().getFirstKillDeath().display();
    this.firstRecall = ratings.getLaning().getPostReset().getResetTime().display();
    this.firstItem = ratings.getIncome().getEarlyIncome().getFirstFullItem().display();

    final Objectives objectives = ratings.getObjectives();
    final List<String> objectiveKeys = objectives.subKeys();
    final List<String> objectiveOut = new ArrayList<>();
    for (int i = 0; i < objectiveKeys.size(); i++) {
      objectiveOut.add(objectiveKeys.get(i));
      objectiveOut.add(objectives.subValues().get(i));
    }
    this.onObjectives = objectiveOut;

    final Roaming roaming = ratings.getRoaming();
    final List<String> roamingKeys = roaming.subKeys();
    final List<String> roamingOut = new ArrayList<>();
    for (int i = 0; i < roamingKeys.size(); i++) {
      roamingOut.add(roamingKeys.get(i));
      roamingOut.add(roaming.subValues().get(i));
    }
    this.onRoaming = roamingOut;

    final Fighting fighting = ratings.getFighting();
    final List<String> fightingKeys = fighting.subKeys();
    final List<String> fightingOut = new ArrayList<>();
    for (int i = 0; i < fightingKeys.size(); i++) {
      fightingOut.add(fightingKeys.get(i));
      fightingOut.add(fighting.subValues().get(i));
    }
    this.onFighting = fightingOut;

    final Income income = ratings.getIncome();
    final List<String> incomeKeys = income.subKeys();
    final List<String> incomeOut = new ArrayList<>();
    for (int i = 0; i < incomeKeys.size(); i++) {
      incomeOut.add(incomeKeys.get(i));
      incomeOut.add(income.subValues().get(i));
    }
    this.onIncome = incomeOut;

    final Survival survival = ratings.getSurvival();
    final List<String> survivalKeys = survival.subKeys();
    final List<String> survivalOut = new ArrayList<>();
    for (int i = 0; i < survivalKeys.size(); i++) {
      survivalOut.add(survivalKeys.get(i));
      survivalOut.add(survival.subValues().get(i));
    }
    this.onSurvival = survivalOut;

    final Laning laning = ratings.getLaning();
    final List<String> laningKeys = laning.subKeys();
    final List<String> laningOut = new ArrayList<>();
    for (int i = 0; i < laningKeys.size(); i++) {
      laningOut.add(laningKeys.get(i));
      laningOut.add(laning.subValues().get(i));
    }
    this.onLaning = laningOut;
  }
}
