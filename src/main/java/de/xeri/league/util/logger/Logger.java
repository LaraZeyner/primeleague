package de.xeri.league.util.logger;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by Lara on 11.04.2022 for web
 */
public class Logger {
  private final String method;
  private final String name;
  private Level level;
  private String msg = "";
  private Throwable thrown;
  private Object[] values;

  public Logger() {
    this("");
  }

  public Logger(String name, String method) {
    this.name = name;
    this.method = method;

  }

  public Logger(String name) {
    this.name = name;
    this.method = name + " ";
  }

  public static Logger getLogger() {
    return new Logger();
  }

  public static Logger getLogger(String name) {
    return new Logger(name);
  }

  public static Logger getLogger(String name, String method) {
    return new Logger(name, method);
  }

  public void log(String msg) {
    this.level = Level.LOG;
    this.msg = msg;
    doLog();
  }

  public void log(Level level, String msg) {
    this.level = level;
    this.msg = msg;
    doLog();
  }

  public void log(Level level, String msg, Object... values) {
    this.level = level;
    this.msg = msg;
    this.values = values;
    doLog();
  }

  public void log(String msg, Throwable thrown) {
    this.level = Level.OFF;
    this.msg = msg;
    this.thrown = thrown;
    doLog();
  }

  public void log(String msg, Throwable thrown, Object... values) {
    this.level = Level.OFF;
    this.msg = msg;
    this.thrown = thrown;
    this.values = values;
    doLog();
  }

  public void enter() {
    this.level = Level.FINER;
    this.msg = "JOIN";
    doLog();
  }

  public void enter(Object... values) {
    this.level = Level.FINER;
    this.msg = "JOIN";
    this.values = values;
    doLog();
  }

  public void exit() {
    this.level = Level.FINER;
    this.msg = "LEAVE";
    doLog();
  }

  public void exit(Object... values) {
    this.level = Level.FINER;
    this.msg = "LEAVE";
    this.values = values;
    doLog();
  }

  public void throwing(Throwable thrown) {
    this.level = Level.FINER;
    this.thrown = thrown;
    doLog();
  }

  public void throwing(Throwable thrown, Object... values) {
    this.level = Level.FINER;
    this.thrown = thrown;
    this.values = values;
    doLog();
  }

  public void throwing(String msg, Throwable thrown) {
    this.level = Level.FINER;
    this.thrown = thrown;
    this.msg = msg;
    doLog();
  }

  public void throwing(String msg, Throwable thrown, Object... values) {
    this.level = Level.FINER;
    this.thrown = thrown;
    this.msg = msg;
    this.values = values;
    doLog();
  }

  public void severe(String msg) {
    log(Level.SEVERE, msg);
  }

  public void severe(String msg, Throwable thrown) {
    log(Level.SEVERE, msg, thrown);
  }

  public void severe(String msg, Object... values) {
    log(Level.SEVERE, msg, values);
  }

  public void severe(String msg, Throwable thrown, Object... values) {
    log(Level.SEVERE, msg, thrown, values);
  }

  public void warning(String msg) {
    log(Level.WARNING, msg);
  }

  public void warning(String msg, Throwable thrown) {
    log(Level.WARNING, msg, thrown);
  }

  public void warning(String msg, Object... values) {
    log(Level.WARNING, msg, values);
  }

  public void attention(String msg) {
    log(Level.WARNING_LIGHT, msg);
  }

  public void attention(String msg, Object... values) {
    log(Level.WARNING_LIGHT, msg, values);
  }

  public void info(String msg) {
    log(Level.INFO, msg);
  }

  public void info(String msg, Object... values) {
    log(Level.INFO, msg);
    this.values = values;
  }

  public void config(String msg) {
    log(Level.CONFIG, msg);
  }

  public void config(String msg, Object... values) {
    log(Level.CONFIG, msg);
    this.values = values;
  }

  public void fine(String msg) {
    log(Level.FINE, msg);
  }

  public void fine(String msg, Object... values) {
    log(Level.FINE, msg);
    this.values = values;
  }

  public void finer(String msg) {
    log(Level.FINER, msg);
  }

  public void finer(String msg, Object... values) {
    log(Level.FINER, msg);
    this.values = values;
  }

  public void finest(String msg) {
    log(Level.FINEST, msg);
  }

  public void finest(String msg, Object... values) {
    log(Level.FINEST, msg);
    this.values = values;
  }


  private void doLog() {
    if (level.isVisible()) {

      final String dateString = new SimpleDateFormat("HH:mm:ss.SSS ").format(new Date());
      final String levelString = level.getColor().format(level.getName());
      final String where = !name.equals("") ? name + " " + method : "" + method;
      final String valuesString = values != null ? "{" + Arrays.toString(values) + "}" : "";
      final String message = level.getColor().format(msg);
      final StringBuilder outputString = new StringBuilder(ColorFormat.DEFAULT.format(dateString))
          .append(levelString)
          .append(ColorFormat.DEFAULT.format(" in " + where + valuesString + " : "))
          .append(message);
      if (thrown != null) {
        final ColorFormat colorFormat = level.intValue() > 500 ? ColorFormat.EXCEPTION_STRONG : ColorFormat.EXCEPTION_WEAK;
        final String exceptionString = colorFormat.format(Arrays.toString(thrown.getStackTrace()));
        outputString.append("\n")
            .append(exceptionString);
      }
      System.out.println(outputString);
    }
  }

}
