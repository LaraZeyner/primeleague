package de.xeri.prm.servlet.datatables.draft;

import java.io.Serializable;

import lombok.Data;
import lombok.NonNull;

/**
 * Created by Lara on 22.05.2022 for web
 */
@Data
public class PickRow implements Serializable {
  private static final long serialVersionUID = -4244063970089148306L;

  @NonNull private String pickWe;
  @NonNull private String banWe;
  @NonNull private String banEnemy;
  @NonNull private String pickEnemy;
}
