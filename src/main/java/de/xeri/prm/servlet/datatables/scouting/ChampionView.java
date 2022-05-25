package de.xeri.prm.servlet.datatables.scouting;

import java.io.Serializable;

import de.xeri.prm.models.dynamic.Champion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by Lara on 18.05.2022 for web
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChampionView implements Serializable {
  private static final long serialVersionUID = -9003855396420546142L;

  private short id;
  private Champion champion;
  private String name;
  private String presence;
  private int presenceNum;
  private int gamesCompetitive;
  private int gamesOther;
  private String wins;
}
