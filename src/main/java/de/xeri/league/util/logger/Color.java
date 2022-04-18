package de.xeri.league.util.logger;

import de.xeri.league.util.logger.jcolor.Attribute;

/**
 * Created by Lara on 17.04.2022 for web
 */
public class Color {
  private final int red;
  private final int green;
  private final int blue;

  public static final Color RED = new Color(255, 0, 0);
  public static final Color ORANGE = new Color(255, 128, 0);
  public static final Color YELLOW = new Color(255, 255, 0);
  public static final Color YELLOWGREEN = new Color(128, 255, 0);
  public static final Color GREEN = new Color(0, 255, 0);
  public static final Color LIGHTGREEN = new Color(0, 255, 128);
  public static final Color AQUA = new Color(0, 255, 255);
  public static final Color LIGHTBLUE = new Color(0, 128, 255);
  public static final Color BLUE = new Color(0, 0, 255);
  public static final Color BLUEVIOLET = new Color(128, 0, 255);
  public static final Color VIOLET = new Color(255, 0, 255);
  public static final Color MAGENTA = new Color(255, 0, 128);
  public static final Color GREY = new Color(128, 128, 128);

  public Color(int red, int green, int blue) {
    this.red = red;
    this.green = green;
    this.blue = blue;
  }

  public Color darker() {
    if (red == 128 && green == 128 && blue == 128) {
      return new Color(0, 0, 0);
    }
    return darker(0.25d);
  }

  public Color lighter() {
    if (red == 128 && green == 128 && blue == 128) {
      return new Color(255, 255, 255);
    }
    return lighter(0.25d);
  }

  public Color darker(double percent) {
    final double newRed = red - red * percent;
    final double newGreen = green - green * percent;
    final double newBlue = blue - blue * percent;
    return new Color((int) newRed, (int) newGreen, (int) newBlue);
  }

  public Color lighter(double percent) {
    final double newRed = red + (255 - red) * percent;
    final double newGreen = green + (255 - green) * percent;
    final double newBlue = blue + (255 - blue) * percent;
    return new Color((int) newRed, (int) newGreen, (int) newBlue);
  }
  
  public Attribute text() {
    return Attribute.TEXT_COLOR(red, green, blue);
  }

  public Attribute back() {
    return Attribute.BACK_COLOR(red, green, blue);
  }
}
