package de.xeri.league.util.io.request;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.xeri.league.util.BreakManager;
import de.xeri.league.util.Const;
import de.xeri.league.util.io.json.HTML;
import de.xeri.league.util.io.json.JSON;
import de.xeri.league.util.logger.Logger;

/**
 * Created by Lara on 29.03.2022 for TRUES
 */
public class Provider {
  private List<Long> requests;
  private long timeframe;
  private int range;

  public Provider() {
    this.requests = null;
  }

  public Provider(int range, long timeframe) {
    this.requests = new ArrayList<>();
    this.range = range;
    this.timeframe = timeframe;
  }

  public List<Long> getRequests() {
    return requests;
  }

  public int getRange() {
    return range;
  }

  public JSON requestJSON(String urlString) throws IOException {
    final Logger logger = Logger.getLogger("Request-Riot");
    try {
      if (isAllowed(logger)) {
        return new JSON(urlString);
      }
    } catch (InterruptedException exception) {
      logger.severe(exception.getMessage());
    }
    return null;
  }

  public HTML requestHTML(String urlString) throws IOException {
    final Logger logger = Logger.getLogger("Request-PRM");
    try {
      if (isAllowed(logger)) {
        if (requests != null) requests.add(System.currentTimeMillis());
        return new HTML(new URL(urlString));
      }
    } catch (InterruptedException exception) {
      Logger.getLogger("Requester").severe(exception.getMessage());
    }
    return null;
  }

  private boolean isAllowed(Logger logger) throws InterruptedException {
    if (checkRequestLimit()) {
      long current = System.currentTimeMillis();
      if (requests.size() == range) {
        if (current - requests.get(0) > timeframe * 1000) {
          requests.remove(0);
        } else {
          final long half = requests.get(range / 2 - 1);
          long waitingTime = (timeframe * 1000) - (current + 1000 - half);
          if (waitingTime > 0) {
            System.out.println(Const.TIMEOUT_MESSAGE + "(" + waitingTime + "ms)");
            final long until = waitingTime + System.currentTimeMillis();
            if (System.currentTimeMillis() <= until) {
              logger.attention("noch " + BreakManager.loop(waitingTime/1000) + " Spiele - ");
            }
            waitingTime = until - System.currentTimeMillis();
            logger.attention(Const.TIMEOUT_MESSAGE + "(" + waitingTime + "ms)");
            TimeUnit.SECONDS.sleep(waitingTime / 1000);
          }

          requests = requests.subList(range / 2, requests.size());
        }
      }
      current = System.currentTimeMillis();
      requests.add(current);
    }
    return true;
  }

  private boolean checkRequestLimit() {
    return (requests != null && range > 0);
  }

}
