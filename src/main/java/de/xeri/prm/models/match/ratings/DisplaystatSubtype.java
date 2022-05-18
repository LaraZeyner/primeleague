package de.xeri.prm.models.match.ratings;

import de.xeri.prm.models.enums.Lane;

/**
 * Created by Lara on 25.04.2022 for web
 */
public enum DisplaystatSubtype {
  TOP, JUNGLE, MIDDLE, BOTTOM, SUPPORT, ALLGEMEIN;

  public Lane getLane() {
    return this.equals(DisplaystatSubtype.ALLGEMEIN) ? Lane.UNKNOWN : this.equals(SUPPORT) ? Lane.UTILITY : Lane.valueOf(this.toString());
  }
}
