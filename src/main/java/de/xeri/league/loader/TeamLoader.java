package de.xeri.league.loader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

import de.xeri.league.models.enums.MatchdayType;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by Lara on 04.04.2022 for web
 */
public class TeamLoader {

  public static Team handleTeam(int turnamentId) {
    return handleTeam(turnamentId, Season.current(), null, null);
  }

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

    for (Element playerElement : members) {
      final Elements elements = playerElement.select("a");
      final String idString = elements.attr("href").split("/users/")[1].split("-")[0];
      final String name = elements.text();
      final String roleString = playerElement.select("div.txt-subtitle").text();
      final Player player = Player.get(new Player(Integer.parseInt(idString), name, Teamrole.valueOf(roleString.toUpperCase())));
      team.addPlayer(player);

      final String summonerName = playerElement.select("div.txt-info").select("span").get(0).text();
      final Account account = RiotAccountRequester.getAccountFromName(summonerName);
      if (account != null) player.addAccount(account);
    }
    return team;
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
  }
}
