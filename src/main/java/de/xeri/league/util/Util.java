package de.xeri.league.util;

import java.awt.geom.Point2D;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.xeri.league.game.events.location.Position;
import org.hibernate.Session;

/**
 * Created by Lara on 06.04.2022 for web
 */
public final class Util {
  public static Calendar getCalendar(Date date) {
    final Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    return cal;
  }

  public static List query(String entityType) {
    final Session session = Data.getInstance().getSession();
    return session.createQuery("from " + entityType).list();
  }

  public static boolean inRange(Date date) {
    if (Data.getInstance().getStatLimit() == 0) return true;
    return date.after(new Date(System.currentTimeMillis() - Data.getInstance().getStatLimit() * 86_400_000L));
  }

  public static String capitalizeFirst(String str) {
    return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
  }

  public static double distance(Position a, Position b) {
    return Point2D.distance(a.getX(), a.getX(), b.getX(), b.getY());
  }
}
