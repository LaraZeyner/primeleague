package de.xeri.prm.servlet.datatables.draft;

import java.io.Serializable;
import java.util.List;

import de.xeri.prm.models.enums.Lane;
import de.xeri.prm.models.league.Player;
import de.xeri.prm.models.league.SeasonElo;
import de.xeri.prm.models.match.ratings.Ratings;
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
  private String roaming;
  private String fighting;
  private String income;
  private String survival;
  private String laning;
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
  }

  /*public void onCellEdit(CellEditEvent event) {
    Object oldValue = event.getOldValue();
    Object newValue = event.getNewValue();

    if (newValue != null && !newValue.equals(oldValue)) {
      String neww = String.valueOf(newValue);
      final PerformanceObject newPerf = allPerformances.stream().filter(pO -> pO.getChampionName().equals(neww)).findFirst().orElse(null);
      IntStream.range(0, performances.size()).filter(i -> performances.get(i).getChampionName().equals(neww)).forEach(i -> performances.set(i, newPerf));
      FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Champion geändert", "");
      FacesContext.getCurrentInstance().addMessage(null, msg);
    }
  }*/
}
