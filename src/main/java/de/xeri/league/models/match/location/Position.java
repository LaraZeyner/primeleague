package de.xeri.league.models.match.location;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by Lara on 14.04.2022 for web
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Position implements Serializable {
  private static final transient long serialVersionUID = 8321719493844687789L;

  private int x;
  private int y;

}
