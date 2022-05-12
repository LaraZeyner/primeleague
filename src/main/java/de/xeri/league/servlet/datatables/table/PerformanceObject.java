package de.xeri.league.servlet.datatables.table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Lara on 11.05.2022 for web
 */
@Getter
@Setter
@AllArgsConstructor
public class PerformanceObject {
  private String championName;
  private String presence;
  private String gamesComp;
  private String games;
  private String wins;
  private String lead;
  private String goldEfficency;

}
