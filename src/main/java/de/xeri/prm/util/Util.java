package de.xeri.prm.util;

import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import de.xeri.prm.game.events.location.Position;
import de.xeri.prm.manager.PrimeData;
import de.xeri.prm.models.match.ratings.RatingSubcategory;
import de.xeri.prm.models.match.ratings.StatSubcategory;
import org.hibernate.Session;

/**
 * Created by Lara on 06.04.2022 for web
 */
public final class Util {
  public static String uncapitalizeFirst(String str) {
    return str.substring(0, 1).toLowerCase() + str.substring(1);
  }

  public static double getDouble(BigDecimal decimal) {
    return decimal != null ? decimal.doubleValue() : 0;
  }

  public static double div(double divident, double divisor) {
    return div(divident, divisor, 0);
  }

  public static double div(double divident, double divisor, boolean re) {
    if (re) {
      return div(divident, divisor, divident);
    }
    return div(divident, divisor, 0);
  }

  public static double div(double divident, double divisor, double result) {
    if (divisor == 0) {
      return result;
    }
    return divident / divisor;
  }

  public static Calendar getCalendar(Date date) {
    final Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    return cal;
  }

  public static List query(String entityType) {
    final Session session = PrimeData.getInstance().getSession();
    return session.createQuery("from " + entityType).list();
  }

  public static boolean inRange(Date date) {
    if (PrimeData.getInstance().getStatLimit() == 0) return true;
    return date.after(new Date(System.currentTimeMillis() - PrimeData.getInstance().getStatLimit() * 86_400_000L));
  }

  public static String capitalizeFirst(String str) {
    return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
  }

  public static double distance(Position a, Position b) {
    return Point2D.distance(a.getX(), a.getX(), b.getX(), b.getY());
  }

  public static int getInt(Object object) {
    return object != null ? (int) (((Long) object).longValue()) : 0;
  }

  public static int longToInt(Long l) {
    return Integer.parseInt(String.valueOf(l));
  }

  public static String until(Date start, String prefix) {
    long distance = Math.abs((System.currentTimeMillis() - start.getTime()) / 1000);

    final int seconds = (int) (distance % 60);
    String secondsString = ("00" + seconds).substring(("00" + seconds).length() - 2);
    final int minutes = (int) ((distance / 60) % 60);
    String minutesString = ("00" + minutes).substring(("00" + minutes).length() - 2);
    final int hours = (int) ((distance / 3_600) % 24);
    String hoursString = ("00" + hours).substring(("00" + hours).length() - 2);
    final int days = (int) (distance / 86_400);

    StringBuilder str = new StringBuilder(prefix);
    if (days > 2) {
      return new SimpleDateFormat("dd.MM. HH:mm").format(start);
    } else if (days > 1) {
      str.append(days).append("d ").append(hoursString).append(":").append(minutesString).append(":").append(secondsString);
    } else if (hours > 1) {
      str.append(days * 24 + hours).append(":").append(minutesString).append(":").append(secondsString);
    } else if (minutes > 1) {
      str.append(hours * 60 + minutes).append(":").append(secondsString);
    } else {
      str.append(minutes * 60 + seconds).append("s");
    }

    return str.toString();
  }

  public static Date getDate(LocalDateTime dateToConvert) {
    return Date.from(dateToConvert.atZone(ZoneId.systemDefault()).toInstant());
  }

  public static LocalDateTime getLocalDate(Date dateToConvert) {
    return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
  }

  public static List<String> subvalues(RatingSubcategory... doubles) {
    return Arrays.stream(doubles)
        .mapToInt(aDouble -> (int) Math.round(aDouble.get() * 25))
        .mapToObj(round -> round + "")
        .collect(Collectors.toList());
  }

  public static List<String> subkeys(StatSubcategory... subcategories) {
    return Arrays.stream(subcategories)
        .map(StatSubcategory::getName)
        .collect(Collectors.toList());
  }

}
