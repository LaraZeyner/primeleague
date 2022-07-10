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
  private List<ChampionView> recommended;
  private ChampionView selectedChampion;
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

  private List<Tag> extremeValueTagsPositive;
  private List<Tag> highValueTagsPositive;
  private List<Tag> strongValueTagsPositive;
  private List<Tag> extremeValueTagsNegative;
  private List<Tag> highValueTagsNegative;
  private List<Tag> strongValueTagsNegative;

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
    final List<ChampionView> championsPresence = player.getChampionsPresence(lane);
    this.champions = championsPresence;
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
    this.selectedChampion = championsPresence.get(0);
    this.recommended = new ArrayList<>();


    this.extremeValueTagsPositive = new ArrayList<>();
    this.highValueTagsPositive = new ArrayList<>();
    this.strongValueTagsPositive = new ArrayList<>();
    this.extremeValueTagsNegative = new ArrayList<>();
    this.highValueTagsNegative = new ArrayList<>();
    this.strongValueTagsNegative = new ArrayList<>();

    final Objectives objectives = ratings.getObjectives();
    final List<String> objectiveKeys = objectives.subKeys();
    final List<String> objectiveOut = new ArrayList<>();
    for (int i = 0; i < objectiveKeys.size(); i++) {
      final String objectiveKey = objectiveKeys.get(i);
      objectiveOut.add(objectiveKey);
      final String objectiveValueString = objectives.subValues().get(i);
      objectiveOut.add(objectiveValueString);
      int objectiveValue = Integer.parseInt(objectiveValueString);
      if (objectiveValue > 66) {
        extremeValueTagsPositive.add(new Tag(objectiveKey, objectives.getSubcategoryStats(i)));
      } else if (objectiveValue > 49) {
        highValueTagsPositive.add(new Tag(objectiveKey, objectives.getSubcategoryStats(i)));
      } else if (objectiveValue > 33) {
        strongValueTagsPositive.add(new Tag(objectiveKey, objectives.getSubcategoryStats(i)));
      } else if (objectiveValue < -66) {
        extremeValueTagsNegative.add(new Tag(objectiveKey, objectives.getSubcategoryStats(i)));
      } else if (objectiveValue < -49) {
        highValueTagsNegative.add(new Tag(objectiveKey, objectives.getSubcategoryStats(i)));
      } else if (objectiveValue < -33) {
        strongValueTagsNegative.add(new Tag(objectiveKey, objectives.getSubcategoryStats(i)));
      }
    }
    this.onObjectives = objectiveOut;

    final Roaming roaming = ratings.getRoaming();
    final List<String> roamingKeys = roaming.subKeys();
    final List<String> roamingOut = new ArrayList<>();
    for (int i = 0; i < roamingKeys.size(); i++) {
      final String roamingKey = roamingKeys.get(i);
      roamingOut.add(roamingKey);
      final String roamingValueString = roaming.subValues().get(i);
      roamingOut.add(roamingValueString);
      int roamingValue = Integer.parseInt(roamingValueString);
      if (roamingValue > 66) {
        extremeValueTagsPositive.add(new Tag(roamingKey, roaming.getSubcategoryStats(i)));
      } else if (roamingValue > 49) {
        highValueTagsPositive.add(new Tag(roamingKey, roaming.getSubcategoryStats(i)));
      } else if (roamingValue > 33) {
        strongValueTagsPositive.add(new Tag(roamingKey, roaming.getSubcategoryStats(i)));
      } else if (roamingValue < -66) {
        extremeValueTagsNegative.add(new Tag(roamingKey, roaming.getSubcategoryStats(i)));
      } else if (roamingValue < -49) {
        highValueTagsNegative.add(new Tag(roamingKey, roaming.getSubcategoryStats(i)));
      } else if (roamingValue < -33) {
        strongValueTagsNegative.add(new Tag(roamingKey, roaming.getSubcategoryStats(i)));
      }
    }
    this.onRoaming = roamingOut;

    final Fighting fighting = ratings.getFighting();
    final List<String> fightingKeys = fighting.subKeys();
    final List<String> fightingOut = new ArrayList<>();
    for (int i = 0; i < fightingKeys.size(); i++) {
      final String fightingKey = fightingKeys.get(i);
      fightingOut.add(fightingKey);
      final String fightingValueString = fighting.subValues().get(i);
      fightingOut.add(fightingValueString);
      int fightingValue = Integer.parseInt(fightingValueString);
      if (fightingValue > 66) {
        extremeValueTagsPositive.add(new Tag(fightingKey, fighting.getSubcategoryStats(i)));
      } else if (fightingValue > 49) {
        highValueTagsPositive.add(new Tag(fightingKey, fighting.getSubcategoryStats(i)));
      } else if (fightingValue > 33) {
        strongValueTagsPositive.add(new Tag(fightingKey, fighting.getSubcategoryStats(i)));
      } else if (fightingValue < -66) {
        extremeValueTagsNegative.add(new Tag(fightingKey, fighting.getSubcategoryStats(i)));
      } else if (fightingValue < -49) {
        highValueTagsNegative.add(new Tag(fightingKey, fighting.getSubcategoryStats(i)));
      } else if (fightingValue < -33) {
        strongValueTagsNegative.add(new Tag(fightingKey, fighting.getSubcategoryStats(i)));
      }
    }
    this.onFighting = fightingOut;

    final Income income = ratings.getIncome();
    final List<String> incomeKeys = income.subKeys();
    final List<String> incomeOut = new ArrayList<>();
    for (int i = 0; i < incomeKeys.size(); i++) {
      final String incomeKey = incomeKeys.get(i);
      incomeOut.add(incomeKey);
      final String incomeValueString = income.subValues().get(i);
      incomeOut.add(incomeValueString);
      int incomeValue = Integer.parseInt(incomeValueString);
      if (incomeValue > 66) {
        extremeValueTagsPositive.add(new Tag(incomeKey, income.getSubcategoryStats(i)));
      } else if (incomeValue > 49) {
        highValueTagsPositive.add(new Tag(incomeKey, income.getSubcategoryStats(i)));
      } else if (incomeValue > 33) {
        strongValueTagsPositive.add(new Tag(incomeKey, income.getSubcategoryStats(i)));
      } else if (incomeValue < -66) {
        extremeValueTagsNegative.add(new Tag(incomeKey, income.getSubcategoryStats(i)));
      } else if (incomeValue < -49) {
        highValueTagsNegative.add(new Tag(incomeKey, income.getSubcategoryStats(i)));
      } else if (incomeValue < -33) {
        strongValueTagsNegative.add(new Tag(incomeKey, income.getSubcategoryStats(i)));
      }
    }
    this.onIncome = incomeOut;

    final Survival survival = ratings.getSurvival();
    final List<String> survivalKeys = survival.subKeys();
    final List<String> survivalOut = new ArrayList<>();
    for (int i = 0; i < survivalKeys.size(); i++) {
      final String survivalKey = survivalKeys.get(i);
      survivalOut.add(survivalKey);
      final String survivalValueString = survival.subValues().get(i);
      survivalOut.add(survivalValueString);
      int survivalValue = Integer.parseInt(survivalValueString);
      if (survivalValue > 66) {
        extremeValueTagsPositive.add(new Tag(survivalKey, survival.getSubcategoryStats(i)));
      } else if (survivalValue > 49) {
        highValueTagsPositive.add(new Tag(survivalKey, survival.getSubcategoryStats(i)));
      } else if (survivalValue > 33) {
        strongValueTagsPositive.add(new Tag(survivalKey, survival.getSubcategoryStats(i)));
      } else if (survivalValue < -66) {
        extremeValueTagsNegative.add(new Tag(survivalKey, survival.getSubcategoryStats(i)));
      } else if (survivalValue < -49) {
        highValueTagsNegative.add(new Tag(survivalKey, survival.getSubcategoryStats(i)));
      } else if (survivalValue < -33) {
        strongValueTagsNegative.add(new Tag(survivalKey, survival.getSubcategoryStats(i)));
      }
    }
    this.onSurvival = survivalOut;

    final Laning laning = ratings.getLaning();
    final List<String> laningKeys = laning.subKeys();
    final List<String> laningOut = new ArrayList<>();
    for (int i = 0; i < laningKeys.size(); i++) {
      final String laningKey = laningKeys.get(i);
      laningOut.add(laningKey);
      final String laningValueString = laning.subValues().get(i);
      laningOut.add(laningValueString);
      int laningValue = Integer.parseInt(laningValueString);
      if (laningValue > 66) {
        extremeValueTagsPositive.add(new Tag(laningKey, laning.getSubcategoryStats(i)));
      } else if (laningValue > 49) {
        highValueTagsPositive.add(new Tag(laningKey, laning.getSubcategoryStats(i)));
      } else if (laningValue > 33) {
        strongValueTagsPositive.add(new Tag(laningKey, laning.getSubcategoryStats(i)));
      } else if (laningValue < -66) {
        extremeValueTagsNegative.add(new Tag(laningKey, laning.getSubcategoryStats(i)));
      } else if (laningValue < -49) {
        highValueTagsNegative.add(new Tag(laningKey, laning.getSubcategoryStats(i)));
      } else if (laningValue < -33) {
        strongValueTagsNegative.add(new Tag(laningKey, laning.getSubcategoryStats(i)));
      }
    }
    this.onLaning = laningOut;
  }

  public void setSelectedChampion(ChampionView selectedChampion) {
    this.selectedChampion = selectedChampion;
    selectedChampion.setSelected(true);
  }

}
