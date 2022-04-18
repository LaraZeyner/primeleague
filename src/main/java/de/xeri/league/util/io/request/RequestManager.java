package de.xeri.league.util.io.request;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import de.xeri.league.util.io.json.HTML;
import de.xeri.league.util.io.json.JSON;

/**
 * Created by Lara on 29.03.2022 for TRUES
 */
public class RequestManager {
  private final Map<String, Provider> providers;

  public RequestManager() {
    this.providers = new HashMap<>();
  }

  public Map<String, Provider> getProviders() {
    return providers;
  }

  public JSON requestRiotJSON(String urlString) {
    if (urlString.startsWith("http")) {
      final String logName;
      if (urlString.contains("summoner") | urlString.contains("league/v4/entries")) {
        logName = "Account-Request";
      } else if (urlString.contains("match")) {
        logName = "Match-Request";
      } else {
        logName = "Riot-Request";
      }

      final Logger logger = Logger.getLogger(logName);
      try {
        return handleJSONString(urlString.replace(" ", "%20"));
      } catch (FileNotFoundException ex) {
        logger.warning(logName.equals("Account-Request") ? "Name wurde geändert" : logName.split("-")[0] + " nicht gefunden");
      } catch (IOException ex) {
        if (ex.getMessage().contains("Server returned HTTP response code: 429 for URL")) {
          logger.severe("Rate limit exceeded");
        } else {
          logger.severe("ERROR!: " + ex.getMessage());
        }
      }
    } else {
      Logger.getLogger("JSON").severe("No URL requested");
    }
    return null;
  }

  public JSON requestJSON(String urlString) {
    if (urlString.startsWith("http")) {
      try {
        return handleJSONString(urlString.replace(" ", "%20"));
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    } else {
      Logger.getLogger("JSON").severe("No URL requested");
    }
    return null;
  }

  public HTML requestHTML(String urlString) throws IOException {
    if (urlString.startsWith("http")) {
      return handleHTMLString(urlString);
    } else {
      Logger.getLogger("HTML").severe("No URL requested");
    }
    return null;
  }

  private JSON handleJSONString(String urlString) throws IOException {
    String providerString = urlString.split("/")[2];
    if (providerString.contains("api.riotgames.com")) {
      providerString = "riotgames";
    }
    if (!providers.containsKey(providerString)) {
      final Provider provider = !providerString.contains("riotgames") ? new Provider() :
          new Provider(100, 120L);
      providers.put(providerString, provider);
    }
    return providers.get(providerString).requestJSON(urlString);

  }

  private HTML handleHTMLString(String urlString) throws IOException {
    final String providerString = urlString.split("/")[2];
    final Provider provider = (providers.containsKey(providerString)) ? providers.get(providerString) :
        new Provider();
    return provider.requestHTML(urlString);
  }

}
