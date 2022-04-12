package de.xeri.league.models.enums;

/**
 * Created by Lara on 25.03.2022 for TRUES
 */
public enum Matchstate {
  CREATED("Spiel erstellt"),
  SUGGESTED("Termin vorgeschlagen"),
  RESPONDED("offener Terminvorschlag"),

  SCHEDULED("Termin bestätigt"),
  LINEUP_SUBMITTED("Lineup bestätigt"),
  LINEUPS_SUBMITTED("Lineups bestätigt"),
  TEAM_READY("Team bereit"),
  TEAMS_READY("Teams bereit"),
  LOBBY_REQUESTED("Warte auf Spiel 1"),
  GAME_1_OPEN("Spiel 1 läuft"),
  GAME_1_ENDED("Warte auf Spiel 2"),
  GAME_2_OPEN("Spiel 2 läuft"),
  GAME_2_ENDED("Warte auf Spiel 3"),
  GAME_3_OPEN("Spiel 3 läuft"),
  CLOSED("Endergebnis gemeldet");

  private String text;

  Matchstate(String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }
}
