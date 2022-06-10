package de.xeri.prm.servlet.loader.profile;

import lombok.Data;
import lombok.NonNull;

/**
 * Created by Lara on 05.06.2022 for web
 */
@Data
public class Poolinfo {
  @NonNull private String champion;
  @NonNull private String details;
}
