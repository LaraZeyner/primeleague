package de.xeri.prm.servlet.datatables.side;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by Lara on 22.05.2022 for web
 */
@Data
public class PickRow implements Serializable {
  private static final long serialVersionUID = -4244063970089148306L;

  private final String pickWe;
  private final String ban;
  private final String pickEnemy;
}
