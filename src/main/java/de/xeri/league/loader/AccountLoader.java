package de.xeri.league.loader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.stream.Collectors;

import de.xeri.league.models.league.Player;
import de.xeri.league.models.league.Team;
import de.xeri.league.util.Data;
import de.xeri.league.util.io.json.HTML;
import de.xeri.league.util.logger.Logger;
import org.jsoup.Jsoup;

/**
 * Created by Lara on 14.04.2022 for web
 */
public final class AccountLoader {
  private static final Logger logger = Logger.getLogger("Account-Erstellung");

  // TODO: 16.04.2022 Look for account updates
  public static void load() {
    try {
      for (Player player : Player.get().stream().filter(player -> player.getAccounts().isEmpty()).collect(Collectors.toList())) {
        final int teamTid = player.getTeam().getTeamTid();
        final HTML html = Data.getInstance().getRequester().requestHTML("https://www.primeleague.gg/leagues/teams/" + teamTid);
        TeamLoader.handleMembers(Jsoup.parse(html.toString()), Team.find(teamTid));
      }
    } catch (FileNotFoundException exception) {
      logger.warning("Account konnte nicht gefunden werden");
    } catch (IOException exception) {
      logger.severe(exception.getMessage());
    }
  }

  public static void updateTeams() {
    Team.get().stream().filter(Team::isValueable).forEach(AccountLoader::updateAccounts);
  }

  private static void updateAccounts(Team team) {
    final int id = team.getTeamTid();
    try {
      final HTML html = Data.getInstance().getRequester().requestHTML("https://www.primeleague.gg/leagues/teams/" + id);
      TeamLoader.handleTeamCore(Jsoup.parse(html.toString()), id);
      TeamLoader.handleMembers(Jsoup.parse(html.toString()), team);
    } catch (IOException e) {
      e.printStackTrace();
    }

  }
}