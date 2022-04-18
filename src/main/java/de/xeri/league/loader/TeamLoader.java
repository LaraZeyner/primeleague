package de.xeri.league.loader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.xeri.league.models.enums.StageType;
import de.xeri.league.models.enums.Teamrole;
import de.xeri.league.models.league.Account;
import de.xeri.league.models.league.League;
import de.xeri.league.models.league.Matchday;
import de.xeri.league.models.league.Player;
import de.xeri.league.models.league.Season;
import de.xeri.league.models.league.Stage;
import de.xeri.league.models.league.Team;
import de.xeri.league.util.Data;
import de.xeri.league.util.io.json.HTML;
import de.xeri.league.util.io.riot.RiotAccountRequester;
import de.xeri.league.util.logger.Logger;
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

  public static Team handleTeam(int turnamentId) {
    return handleTeam(turnamentId, Season.current());
  }

  public static Team handleTeam(int turnamentId, Season season) {
    final Logger logger = Logger.getLogger("Team-Erstellung");
    try {
      final HTML html = Data.getInstance().getRequester().requestHTML("https://www.primeleague.gg/leagues/teams/" + turnamentId);
      final Document doc = Jsoup.parse(html.toString());
      final Team team = handleTeamCore(doc, turnamentId);
      handleMembers(doc, team);
      handleSeason(season, doc, team);
      amount++;
      logger.info(amount + ". Team " + team.getTeamName() + " hinzugefügt");
      return team;
    } catch (FileNotFoundException exception) {
      notFoundTeams.add(turnamentId);
      logger.warning("Team konnte nicht gefunden werden");
    } catch (IOException exception) {
      logger.severe(exception.getMessage());
    }
    return null;
  }

  static Team handleTeamCore(Document doc, int turnamentId) {
    final String title = doc.select("div.page-title").text();
    final Elements members = doc.select("section.league-team-members").select("div.section-content").select("li");
    if (title.startsWith("[DELETED]") && members.size() < 5) {
      return null;
    }
    final String teamName = title.split(" \\(")[0];
    final String teamAbbr = title.split(" \\(")[1].split("\\)")[0];
    final Team team = Team.get(new Team(turnamentId, teamName, teamAbbr));
    final String result = doc.select("ul.content-icon-info").select("li").get(1).text();
    team.setTeamResult(result.contains("Rang: ") ? result.split("Rang: ")[1] : result);
    return team;
  }

  static void handleMembers(Document doc, Team team) {
    final Elements members = doc.select("section.league-team-members").select("div.section-content").select("li");
    for (Element playerElement : members) {
      final Elements elements = playerElement.select("a");
      final String idString = elements.attr("href").split("/users/")[1].split("-")[0];
      final String name = elements.text();
      final String roleString = playerElement.select("div.txt-subtitle").text();
      final String summonerName = playerElement.select("div.txt-info").select("span").get(0).text();
      Player player = Player.find(Integer.parseInt(idString));
      if (player == null) {
        // Spieler neu im Team
        player = team.addPlayer(new Player(Integer.parseInt(idString), name, Teamrole.valueOf(roleString.toUpperCase())));
        final Set<Account> accounts = player.getAccounts();
        if (accounts.stream().noneMatch(account -> account.getName().equals(summonerName))) {
          final Account account = new Account(summonerName);
          player.addAccount(account);
        }
      } else {
        if (!player.getTeam().equals(team)) {
          // Spieler hat gewechselt
          team.addPlayer(player);
        }

        final Set<Account> accounts = player.getAccounts();
        if (accounts.stream().noneMatch(account -> account.getName().equals(summonerName))) {
          // Account hat sich geändert
          final Account account = RiotAccountRequester.getAccountFromName(summonerName);
          if (accounts.stream().noneMatch(account1 -> account1.getPuuid().equals(account.getPuuid()))) {
            accounts.forEach(account1 -> account1.setActive(false));
            player.addAccount(account);
          }
        } else {
          // erweiterte Initialisierung
          for (Account account1 : player.getAccounts()) {
            if (account1.getPuuid() == null) {
              final Account account = RiotAccountRequester.getAccountFromName(summonerName);
              if (accounts.stream().noneMatch(account2 -> account2.getPuuid().equals(account.getPuuid()))) {
                accounts.forEach(account2 -> account1.setActive(false));
              }
              // Gleich geblieben
            } else if (account1.isValueable()) {
              RiotAccountRequester.getAccountFromName(summonerName);
            }
          }
        }
      }
    }
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
      if (team.getTeamTid() != 0) {
        try {
          final HTML html = Data.getInstance().getRequester().requestHTML("https://www.primeleague.gg/leagues/teams/" + team.getTeamTid());
          final Document doc = Jsoup.parse(html.toString());
          loadMatchesOfTeam(team, doc, season);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private static void loadMatchesOfTeam(Team team, Document doc, Season season) {
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
        final Element matchElement = matchElements.get(i).selectFirst("tr");
        final Matchday matchday = stage.addMatchday(new Matchday(matchdayPrefix + (i + 1), null, null));
        MatchLoader.loadMatch(league, matchday, matchElement);
      }
    }
  }


/*
  public static Team handleTeam(int turnamentId, Season season, Document doc, String name) {
    final Logger logger = Logger.getLogger("Team-Erstellung");
    try {
      final HTML html = Data.getInstance().getRequester().requestHTML("https://www.primeleague.gg/leagues/teams/" + turnamentId);
      doc = Jsoup.parse(html.toString());
      final Team team = handleTeamCore(doc, turnamentId);
      handleSeason(season, doc);
      return team;

    } catch (FileNotFoundException exception) {
      for (Element day : doc.select("section.league-group-matches").select("li")) {
        for (final Element element : day.select("td.col-3")) {
          final String teamName = element.select("img").attr("title");
          if (Arrays.stream(name.split("-")).filter(word -> word.length() > 3).allMatch(teamName.toLowerCase()::contains)) {
            return Team.get(new Team(turnamentId, teamName, element.text()));
          }
        }
      }
      logger.warning("Team konnte nicht gefunden werden");
    } catch (IOException exception) {
      logger.severe(exception.getMessage());
    }
    return null;
  }

  private static Team handleTeamCore(Document doc, int turnamentId) {
    final String title = doc.select("div.page-title").text();
    final Elements members = doc.select("section.league-team-members").select("div.section-content").select("li");
    if (isDeleted(title, members)) {
      return null;
    }
    final Team team = Team.get(new Team(turnamentId, title.split(" \\(")[0], title.split(" \\(")[1].split("\\)")[0]));
    final String result = doc.select("ul.content-icon-info").select("li").get(1).text();
    team.setTeamResult(result.contains("Rang: ") ? result.split("Rang: ")[1] : result);

    handleMembers(members, team);
    return team;
  }

  static void handleMembers(Elements members, Team team) {
    for (Element playerElement : members) {
      final Elements elements = playerElement.select("a");
      final String idString = elements.attr("href").split("/users/")[1].split("-")[0];
      final String name = elements.text();
      final String roleString = playerElement.select("div.txt-subtitle").text();
      final Player player = team.addPlayer(new Player(Integer.parseInt(idString), name, Teamrole.valueOf(roleString.toUpperCase())));
      final String summonerName = playerElement.select("div.txt-info").select("span").get(0).text();
      final Stream<Account> accountStream = player.getAccounts().stream();
      if (player.getAccounts() == null || accountStream.noneMatch(account -> account.getName().equals(summonerName))) {
        final Account account = RiotAccountRequester.getAccountFromName(summonerName);
        if (player.getAccounts() != null && accountStream.noneMatch(account1 -> account1.getPuuid().equals(account.getPuuid()))) {
          player.getAccounts().forEach(account1 -> account1.setActive(false));
        }

        if (account != null) player.addAccount(account);
      }
    }
  }

  private static void handleSeason(Season season, Document doc) {
    for (Element stage : doc.select("section.league-team-stage")) {
      if (stage.select("div.section-title").text().contains("Kalibrierung")) {
        final String idString = stage.select("ul.content-icon-info-l").select("a").attr("href").
            split("/group/")[1].split("-")[0];
        final League league = League.get(new League(Short.parseShort(idString), "Kalibrierung"));

        final Elements matchElements = stage.select("ul.league-stage-matches").select("li");
        for (int i = 0; i < matchElements.size(); i++) {
          final Element matchElement = matchElements.get(i).selectFirst("tr");
          final Stage stage1 = Stage.find(season, StageType.KALIBRIERUNGSPHASE);
          final Matchday matchday = Matchday.get(new Matchday(MatchdayType.valueOf("RUNDE_" + (i + 1)), null, null), stage1);
          MatchLoader.loadMatch(league, matchday, matchElement);
        }
      }
    }
  }

  private static boolean isDeleted(String title, Elements members) {
    return title.startsWith("[DELETED]") && members.size() < 5;
  }*/
}
