package de.xeri.league.servlet.datatables.table;

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
  private static final transient long serialVersionUID = -5954217254037670864L;

  private int tId;
  private String abbreviation;
  private String logoUrl;
  private String winsPerMatch;
  private String teamScore;
  private String bilance;

}
