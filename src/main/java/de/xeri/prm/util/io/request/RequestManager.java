package de.xeri.prm.util.io.request;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.xeri.prm.util.io.json.HTML;
import de.xeri.prm.util.io.json.JSON;
import de.xeri.prm.util.logger.Logger;

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
      return tryRiotJSON(urlString, 0, logger);
    } else {
      Logger.getLogger("JSON").severe("No URL requested");
    }
    return null;
  }

  private JSON tryRiotJSON(String urlString, int attempt, Logger logger) {
    try {
      return handleJSONString(urlString.replace(" ", "%20"));
    } catch (IOException exception) {
      if (exception.getMessage().contains("HTTP response code: 429")) {
        logger.warning("Rate limit exceeded");
      } else if (exception.getMessage().contains("HTTP response code: 503 for URL")) {
        logger.fine("Service unavailable - Retrying");
        try {
          Thread.sleep(5_000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        if (attempt < 5) {
          return tryRiotJSON(urlString, attempt + 1, logger);
        }
      } else {
        logger.severe("ERROR!: " + exception.getMessage());
      }
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
      final HTML html = handleHTMLString(urlString);
      if (html.toString().contains("webmaster has already been notified")) {
        return handleHTMLString(urlString);
      }
      return html;
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
