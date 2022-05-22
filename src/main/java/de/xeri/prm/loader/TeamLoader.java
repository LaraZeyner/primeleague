package de.xeri.prm.loader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.xeri.prm.models.enums.StageType;
import de.xeri.prm.models.enums.Teamrole;
import de.xeri.prm.models.league.Account;
import de.xeri.prm.models.league.League;
import de.xeri.prm.models.league.Matchday;
import de.xeri.prm.models.league.Player;
import de.xeri.prm.models.league.Season;
import de.xeri.prm.models.league.Stage;
import de.xeri.prm.models.league.Team;
import de.xeri.prm.manager.Data;
import de.xeri.prm.util.Util;
import de.xeri.prm.util.io.json.HTML;
import de.xeri.prm.util.io.riot.RiotAccountURLGenerator;
import de.xeri.prm.util.logger.Logger;
import lombok.val;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by Lara on 04.04.2022 for web
 */
public class TeamLoader {
  private static int amount = 0;
  public static final List<Integer> notFoundTeams = new ArrayList<>();
  private static final Map<Team, Long> updated = new HashMap<>();

  public static Team handleTeam(int turnamentId) {
    return handleTeam(turnamentId, Data.getInstance().getCurrentSeason(), true, null);
  }

  public static Team handleTeam(int turnamentId, Season season, boolean updateMatches, String name) {
    final Logger logger = Logger.getLogger("Team-Erstellung");
    try {
      final HTML html = Data.getInstance().getRequester().requestHTML("https://www.primeleague.gg/leagues/teams/" + turnamentId);
      if (!html.toString().contains("webmaster has already been notified")) {
        final Document doc = Jsoup.parse(html.toString());
        final Team team = handleTeamCore(doc, turnamentId);
        handleMembers(doc, team);
        handleSeason(season, doc, team);
        if (updateMatches) {
          loadMatchesOfTeam(doc, season);
          logger.info("Team " + team.getId() + ": " + team.getTeamName() + " aktualisiert");
        } else {
          amount++;
          if (amount % 10 == 0) {
            logger.info(amount + " Teams hinzugefügt");
          }
        }
        return team;
      }
      handleTeampageEmpty(turnamentId, name, logger);
    } catch (FileNotFoundException exception) {
      handleTeampageEmpty(turnamentId, name, logger);
    } catch (IOException exception) {
      logger.severe(exception.getMessage());
    }
    return null;
  }

  private static void handleTeampageEmpty(int turnamentId, String name, Logger logger) {
    if (name != null) {
      final Team team = Team.get(new Team(turnamentId, name, name.length() >= 3 ? name.substring(0, 3) : name));
      team.setTeamResult("GELÖSCHT");
    } else {
      notFoundTeams.add(turnamentId);
      logger.warning("Team konnte nicht gefunden werden");
    }
  }

  static Team handleTeamCore(Document doc, int turnamentId) {
    final String title = doc.select("div.page-title").text();
    final Elements members = doc.select("section.league-team-members").select("div.section-content").select("li");
    if (title.startsWith("[DELETED]") && members.size() < 5) {
      return null;
    }
    try {
      final String teamName = title.split(" \\(")[0];
      final String teamAbbr = title.split(" \\(")[1].split("\\)")[0];
      final Team team = Team.get(new Team(turnamentId, teamName, teamAbbr));
      final String resultString = doc.select("ul.content-icon-info").select("li").get(1).text();
      if (resultString.contains("Ergebnis:")) {
        final String result = resultString.split("Ergebnis:")[1];
        team.setTeamResult(result.contains("Rang: ") ? result.split("Rang: ")[1] : result);
      }
      return team;
    } catch (ArrayIndexOutOfBoundsException ex) {
      ex.printStackTrace();
    }
    return null;
  }

  static void handleMembers(Document doc, Team team) {
    final Elements members = doc.select("section.league-team-members").select("div.section-content").select("li");
    for (Element playerElement : members) {
      val elements = playerElement.select("a");
      val idString = elements.attr("href").split("/users/")[1].split("-")[0];
      val name = elements.text().equals("") ? null : elements.text();
      val roleString = playerElement.select("div.txt-subtitle").text();
      val summonerName = playerElement.select("div.txt-info").select("span").get(0).text();

      if (Player.has(Integer.parseInt(idString))) {
        val player = Player.get(new Player(Integer.parseInt(idString), name, Teamrole.valueOf(roleString.toUpperCase())), team);
        if (!player.getTeam().equals(team)) {
          // Spieler hat gewechselt
          team.addPlayer(player);
        }

        val accounts = player.getAccounts();
        if (accounts.stream().noneMatch(account -> account.getName().equals(summonerName))) {
          // Account hat sich geändert
          final Account account = RiotAccountURLGenerator.fromName(summonerName);
          if (account != null) {
            if (accounts.stream().noneMatch(account1 -> account1.getPuuid() != null && account1.getPuuid().equals(account.getPuuid()))) {
              accounts.forEach(account1 -> account1.setActive(false));
              player.addAccount(account);
            }
          }
        } else {
          // erweiterte Initialisierung
          for (Account account1 : player.getAccounts()) {
            if (account1.getPuuid() == null) {
              final Account account = RiotAccountURLGenerator.fromName(summonerName);
              if (account != null && accounts.stream()
                  .filter(account2 -> account2.getPuuid() != null)
                  .noneMatch(account2 -> account2.getPuuid().equals(account.getPuuid()))) {
                accounts.forEach(account2 -> account1.setActive(true));
              }

              // Gleich geblieben
            } else if (account1.isValueable() || account1.getLastUpdate() != null &&
                Util.getCalendar(account1.getLastUpdate()).get(Calendar.MONTH) != Util.getCalendar(new Date()).get(Calendar.MONTH)) {
              RiotAccountURLGenerator.fromName(summonerName);
            }
          }
        }
      } else {
        final Player player = team.addPlayer(new Player(Integer.parseInt(idString), name, Teamrole.valueOf(roleString.toUpperCase())));
        final Set<Account> accounts = player.getAccounts();
        if (accounts.stream().noneMatch(account -> account.getName().equals(summonerName))) {
          final Account account = Account.get(new Account(summonerName));
          player.addAccount(account);
        }
      }
    }
  }

  public static Team handleSeason(Team team) {
    final Logger logger = Logger.getLogger("Team-Erstellung");
    final long last = updated.containsKey(team) ? updated.get(team) : 0;
    if (System.currentTimeMillis() - last > 300_000L) {
      try {
        final HTML html = Data.getInstance().getRequester().requestHTML("https://www.primeleague.gg/leagues/teams/" + team.getTurneyId());
        final Document doc = Jsoup.parse(html.toString());
        handleSeason(Data.getInstance().getCurrentSeason(), doc, team);
        updated.put(team, System.currentTimeMillis());
        logger.info("Team " + team.getId() + ": " + team.getTeamName() + " aktualisiert");
        return team;
      } catch (FileNotFoundException exception) {
        logger.attention("Team wurde nicht gefunden.");
      } catch (IOException exception) {
        logger.severe(exception.getMessage());
      }
      return null;
    }
    return team;
  }

  private static void handleSeason(Season season, Document doc, Team team) {
    for (Element stageElement : doc.select("section.league-team-stage")) {
      final String stageTypeString = stageElement.select("div.section-title").text();
      final StageType stageType = StageType.valueOf(stageTypeString.toUpperCase());
      final Stage stage = Stage.find(season, stageType);

      final Elements divisionLink = stageElement.select("ul.content-icon-info-l").select("a");
      final String idString = divisionLink.attr("href").split("/")[8].split("-")[0];
      final League league = stage.addLeague(new League(Short.parseShort(idString), divisionLink.text()));
      league.addTeam(team);
    }
  }


  static void loadMatches(List<Team> teams, Season season) {
    for (Team team : teams) {
      if (team != null && team.getTurneyId() != 0) {
        try {
          final HTML html = Data.getInstance().getRequester().requestHTML("https://www.primeleague.gg/leagues/teams/" + team.getTurneyId());
          final Document doc = Jsoup.parse(html.toString());
          loadMatchesOfTeam(doc, season);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private static void loadMatchesOfTeam(Document doc, Season season) {
    final Logger logger = Logger.getLogger("Load Match");
    for (Element stageElement : doc.select("section.league-team-stage")) {
      final String stageTypeString = stageElement.select("div.section-title").text();
      final StageType stageType = StageType.valueOf(stageTypeString.toUpperCase());
      final String matchdayPrefix = stageType.equals(StageType.GRUPPENPHASE) ? "Spieltag " : "Runde ";
      final Stage stage = Stage.find(season, stageType);

      final Elements divisionLink = stageElement.select("ul.content-icon-info-l").select("a");
      final String idString = divisionLink.attr("href").split("/")[8].split("-")[0];
      final League league = League.find(Short.parseShort(idString));

      final Elements matchElements = stageElement.select("ul.league-stage-matches").select("li");
      for (int i = 0; i < matchElements.size(); i++) {
        final Element matchElement = matchElements.get(i);
        final Elements selectTitle = matchElement.select("div.txt-info");
        final String titleText = selectTitle.text();
        int id = titleText.contains("(Spieltag ") ?
            Integer.parseInt(titleText.split("Spieltag ")[1].substring(0, 1)) : i + 1;
        if (titleText.contains("Tiebreak")) {
          id = 8;
        }
        if (Matchday.has(matchdayPrefix + id, stage)) {
          final Matchday matchday = Matchday.find(matchdayPrefix + id, stage);
          if (matchElement.selectFirst("tr") != null) {
            MatchLoader.loadMatch(league, matchday, matchElement);
          } else {
            logger.attention("Matchlist empty for some reason");
          }
        } else {
          logger.severe("Matchday was not created.");
        }


      }
    }
  }
}
