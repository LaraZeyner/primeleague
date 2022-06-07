package de.xeri.prm.servlet.datatables.scheduling;

import lombok.Data;
import lombok.NonNull;

/**
 * Created by Lara on 05.06.2022 for web
 */
@Data
public class Poolentry {
  @NonNull private String toplaner;
  @NonNull private String jungler;
  @NonNull private String midlaner;
  @NonNull private String botlaner;
  @NonNull private String support;
}
