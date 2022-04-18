package de.xeri.league.util.logger;

import java.io.Serializable;

import de.xeri.league.util.Const;

/**
 * Created by Lara on 15.04.2022 for web
 */
public class Level implements Serializable {
  private static final long serialVersionUID = 4696525147809509943L;
  private final String name;
  private final int value;
  private final ColorFormat color;
  private final boolean visible;

  /**
   * OFF is a special level that can be used to turn off logging.
   * This level is initialized to <CODE>Integer.MAX_VALUE</CODE>.
   */
  public static final Level OFF = new Level("OFF", Integer.MAX_VALUE, ColorFormat.OFF);

  /**
   * serious failure, prevent normal execution. reasonably intelligible to end users
   * <p>
   * initialized to <CODE>1000</CODE>.
   */
  public static final Level SEVERE = new Level("SEVERE", 1000, ColorFormat.SEVERE);

  /**
   * indicating a potential problem, events that will
   * be of interest to end users or system managers
   * <p>
   * initialized to <CODE>900</CODE>.
   */
  public static final Level WARNING = new Level("WARNING", 900, ColorFormat.WARNING);

  /**
   * indicating a potential problem, events that will
   * be of interest to end users or system managers
   * <p>
   * initialized to <CODE>900</CODE>.
   */
  public static final Level WARNING_LIGHT = new Level("ATTENTION", 900, ColorFormat.WARNING_LIGHT);

  /**
   * informational messages, messages that will
   * make sense to end users and system administrators.
   * <p>
   * initialized to <CODE>800</CODE>.
   */
  public static final Level INFO = new Level("INFO", 800, ColorFormat.INFO);

  /**
   * static configuration information that may be associated with particular configurations.
   * <p>
   * This level is initialized to <CODE>700</CODE>.
   */
  public static final Level CONFIG = new Level("CONFIG", 700, ColorFormat.CONFIG);

  /**
   * OFF is a special level that can be used to turn off logging.
   * This level is initialized to <CODE>Integer.MAX_VALUE</CODE>.
   */
  public static final Level LOG = new Level("DEVELOPMENT", 600, ColorFormat.LOG);

  /**
   * tracing information, information that will be broadly interesting without specialized interest in the subsystem, minor (recoverable)
   * failures and issues indicating potential performance problems
   * <p>
   * initialized to <CODE>500</CODE>.
   */
  public static final Level FINE = new Level("FINE", 500, ColorFormat.FINE);

  /**
   * detailed tracing message.
   * logging calls for entering, returning, or throwing an exception
   * <p>
   * initialized to <CODE>400</CODE>.
   */
  public static final Level FINER = new Level("FINER", 400, ColorFormat.FINER);

  /**
   * highly detailed tracing message.
   * <p>
   * initialized to <CODE>300</CODE>.
   */
  public static final Level FINEST = new Level("FINEST", 300, ColorFormat.FINEST);

  /**
   * all messages should be logged.
   * <p>
   * initialized to <CODE>Integer.MIN_VALUE</CODE>.
   */
  public static final Level ALL = new Level("ALL", Integer.MIN_VALUE, ColorFormat.ALL);

  /**
   * Create a named Level with a given integer value and a
   * given localization resource name.
   * <p>
   *
   * @param name the name of the Level, for example "SEVERE".
   * @param value an integer value for the level.
   */
  protected Level(String name, int value, ColorFormat color) {
    this(name, value, value >= Const.LOG_LEVEL, color);

  }

  private Level(String name, int value, boolean visible, ColorFormat color) {
    this.name = name;
    this.value = value;
    this.visible = visible;
    this.color = color;
  }

  /**
   * Return the non-localized string name of the Level.
   *
   * @return non-localized name
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the color for the output
   *
   * @return color-code of the level type
   */
  public ColorFormat getColor() {
    return color;
  }

  /**
   * Returns wheather on not the message will be visible
   * @return boolean if visible
   */
  public boolean isVisible() {
    return visible;
  }

  /**
   * Returns a string representation of this Level.
   *
   * @return the non-localized name of the Level, for example "INFO".
   */
  @Override
  public final String toString() {
    return name;
  }

  /**
   * Get the integer value for this level.  This integer value
   * can be used for efficient ordering comparisons between
   * Level objects.
   *
   * @return the integer value for this level.
   */
  public final int intValue() {
    return value;
  }

  /**
   * Compare two objects for value equality.
   *
   * @return true if and only if the two objects have the same level value.
   */
  @Override
  public boolean equals(Object ox) {
    try {
      final Level lx = (Level) ox;
      return (lx.value == this.value);
    } catch (Exception ex) {
      return false;
    }
  }

  /**
   * Generate a hashcode.
   *
   * @return a hashcode based on the level value
   */
  @Override
  public int hashCode() {
    return this.value;
  }

}