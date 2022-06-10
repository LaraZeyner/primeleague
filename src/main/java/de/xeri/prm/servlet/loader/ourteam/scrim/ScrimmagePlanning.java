package de.xeri.prm.servlet.loader.ourteam.scrim;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * Created by Lara on 02.06.2022 for web
 */
@Data
public class ScrimmagePlanning implements Serializable {
  private Date start;
  private int games;
}
