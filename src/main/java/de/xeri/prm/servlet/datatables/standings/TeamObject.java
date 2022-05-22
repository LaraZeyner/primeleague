package de.xeri.prm.servlet.datatables.standings;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Lara on 09.05.2022 for web
 */
@Getter
@Setter
@AllArgsConstructor
public class TeamObject implements Serializable {
  private static final transient long serialVersionUID = -4712718507389889491L;

  private int place;
  private int tId;
  private String name;
  private String abbreviation;
  private String logoUrl;
  private String winsPerMatch;
  private String teamScore;
  private String bilance;
  private String kills;
  private String killDiff;
  private String killsPerMatch;
  private String gold;
  private String goldDiff;
  private String goldPerMatch;
  private String creeps;
  private String creepDiff;
  private String creepPerMatch;
  private String objectives;
  private String objectivesPerMatch;
  private String towers;
  private String towersPerMatch;
  private String drakes;
  private String drakesPerMatch;
  private String inhibs;
  private String inhibsPerMatch;
  private String heralds;
  private String heraldsPerMatch;
  private String barons;
  private String baronsPerMatch;
  private String matchTime;
  private String matchTimeWins;
  private String matchTimeLosses;

}
