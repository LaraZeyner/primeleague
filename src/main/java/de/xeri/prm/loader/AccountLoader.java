package de.xeri.prm.loader;

import java.io.FileNotFoundException;
import java.io.IOException;

import de.xeri.prm.manager.Data;
import de.xeri.prm.models.league.Team;
import de.xeri.prm.util.io.json.HTML;
import de.xeri.prm.util.logger.Logger;
import org.jsoup.Jsoup;

/**
 * Created by Lara on 14.04.2022 for web
 */
public final class AccountLoader {
  private static final Logger logger = Logger.getLogger("Account-Erstellung");

  public static void load() {
    try {
      for (Team team : Team.get()) {
        final int teamTid = team.getTurneyId();
        final HTML html = Data.getInstance().getRequester().requestHTML("https://www.primeleague.gg/leagues/teams/" + teamTid);
        TeamLoader.handleMembers(Jsoup.parse(html.toString()), Team.findTid(teamTid));
      }
    } catch (FileNotFoundException exception) {
      logger.warning("Account konnte nicht gefunden werden");
    } catch (IOException exception) {
      logger.severe(exception.getMessage());
    }
    Data.getInstance().commit();
  }

  public static void updateTeams() {
    Team.get().stream().filter(Team::isValueable).forEach(AccountLoader::updateAccounts);
    Data.getInstance().commit();
  }

  private static void updateAccounts(Team team) {
    final int id = team.getTurneyId();
    TeamLoader.handleTeam(id);
  }
}
