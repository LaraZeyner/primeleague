package de.xeri.prm.util.logger;

import de.xeri.prm.util.logger.jcolor.AnsiFormat;
import de.xeri.prm.util.logger.jcolor.Attribute;
import static de.xeri.prm.util.logger.jcolor.Attribute.BOLD;
import static de.xeri.prm.util.logger.jcolor.Attribute.DIM;

/**
 * Created by Lara on 15.04.2022 for web
 */
public class ColorFormat {
  private final AnsiFormat format;

  public static final ColorFormat DEFAULT = new ColorFormat(Color.VIOLET.lighter(0.5).text(), DIM());
  public static final ColorFormat OFF = new ColorFormat(Color.RED.text(), BOLD());
  public static final ColorFormat SEVERE = new ColorFormat(Color.RED.text(), BOLD());
  public static final ColorFormat WARNING = new ColorFormat(Color.ORANGE.text(), BOLD());
  public static final ColorFormat WARNING_LIGHT = new ColorFormat(Color.YELLOW.text(), DIM());
  public static final ColorFormat INFO = new ColorFormat(Color.GREY.lighter(0.5).text(), DIM());
  public static final ColorFormat CONFIG = new ColorFormat(Color.YELLOW.darker().text(), DIM());
  public static final ColorFormat LOG = new ColorFormat(Color.GREEN.text(), DIM());

  public static final ColorFormat FINE = new ColorFormat(Color.LIGHTBLUE.text(), BOLD());
  public static final ColorFormat FINER = new ColorFormat(Color.BLUEVIOLET.text(), DIM());
  public static final ColorFormat FINEST = new ColorFormat(Color.LIGHTBLUE.lighter().text(), DIM());
  public static final ColorFormat ALL = new ColorFormat(Color.GREEN.text(), BOLD());

  public static final ColorFormat EXCEPTION_WEAK = new ColorFormat(Color.MAGENTA.text(), DIM());
  public static final ColorFormat EXCEPTION_STRONG = new ColorFormat(Color.MAGENTA.back(), Color.GREY.lighter().text(), BOLD());


  public ColorFormat(Attribute... attributes) {
    this.format = new AnsiFormat(attributes) ;
  }

  public String format(String text) {
    return format.format(text);
  }


}