package de.xeri.league.models.enums;

import java.util.Arrays;

/**
 * Created by Lara on 07.04.2022 for web
 */
public enum LogAction {
  CONFIRM("scheduling_confirm"),
  DISQUALIFIED("disqualify"),
  EXPIRED("scheduling_expired"),
  FAILED("lineup_fail"),
  MISSING("lineup_missing"),
  NOT_READY("lineup_notready"),
  READY("lineup_player_ready"),
  REQUEST("hosting_request"),
  REPORT("report"),
  PLAYED("played"),
  SUBMIT("lineup_submit"),
  STATUS_CHANGED("change_status"),
  TIME_CHANGED("change_time"),
  SCORE_CHANGED("change_score"),
  SUGGEST("scheduling_suggest");

  private final String displaymessage;

  LogAction(String displaymessage) {
    this.displaymessage = displaymessage;
  }

  public static LogAction getAction(String message) {
    if (message.equals("scheduling_autoconfirm")) {
      return LogAction.CONFIRM;
    }
    return Arrays.stream(values()).filter(logAction -> logAction.getDisplaymessage().equals(message))
        .findFirst().orElse(null);
  }

  public String getDisplaymessage() {
    return displaymessage;
  }
}
