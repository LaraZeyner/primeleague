package de.xeri.league.util;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
}
