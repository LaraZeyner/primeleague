package de.xeri.league.models.match.stat;

/**
 * Created by Lara on 22.04.2022 for web
 */
public abstract class Stat {
 /* protected final String type;
  protected final Class clazz;
  private final Map<String, Object> limit;
  protected final boolean minute;

  public Stat(String type, Class pClass, boolean minute) {
    this.type = type;
    this.clazz = pClass;
    this.limit = new HashMap<>();
    this.minute = minute;
  }

  public Stat where(String attribute, Object value) {
    limit.put(attribute, value);
    return this;
  }

  public String get(OutputType outputType, int digits, boolean percent) {
    final double value;
    if (outputType.equals(OutputType.AVG)) {
      value = avg();
    } else if (outputType.equals(OutputType.COUNT)) {
      value = count();
    } else if (outputType.equals(OutputType.MAX)) {
      value = max();
    } else if (outputType.equals(OutputType.MIN)) {
      value = min();
    } else if (outputType.equals(OutputType.SUM)) {
      value = sum();
    } else {
      return null;
    }
    return format(value, digits, percent);
  }

  public double avg() {
    return performMath("avg");
  }

  public double count() {
    return performMath("count");
  }

  public double max() {
    return performMath("max");
  }

  public double min() {
    return performMath("min");
  }

  public double sum() {
    return performMath("sum");
  }

  public List<Double> list() {
    final Session session = Data.getInstance().getSession();
    final EntityType storedEntity = session.getMetamodel().entity(clazz);
    final String entityClassName = storedEntity.getName();

    if (limit.isEmpty()) {
      return (List<Double>) session.createQuery("SELECT " + type + " from " + entityClassName).getSingleResult();
    }

    final Query query = session.createQuery("SELECT " + type + " from " + entityClassName + buildString());
    for (String key : limit.keySet()) {
      query.setParameter(key, String.valueOf(limit.get(key)));
    }
    return (List<Double>) query.list();
  }

  private double performMath(String category) {
    final Session session = Data.getInstance().getSession();
    final EntityType storedEntity = session.getMetamodel().entity(clazz);
    final String entityClassName = storedEntity.getName();

    if (limit.isEmpty()) {
      return (double) session.createQuery("SELECT " + category + "(" + type + ") from " + entityClassName).getSingleResult();
    }

    final Query query = session.createQuery("SELECT " + category + "(" + type + ") from " + entityClassName + buildString());
    for (String key : limit.keySet()) {
      query.setParameter(key, String.valueOf(limit.get(key)));
    }
    return (double) query.getSingleResult();
  }



  public String getType() {
    return type;
  }

  public Map<String, Object> getLimit() {
    return limit;
  }

  String format(Double value, int digits, boolean percent) {
    final String valueString = String.valueOf(value);
    if (valueString.contains("\\.")) {
      String digitsBefore = valueString.split("\\.")[0];
      if (digitsBefore.equals("0")) digitsBefore = "";
      final String digitsAfter = valueString.split("\\.")[1];
      final int lengthBefore = digitsBefore.length();
      final String preComma;
      final String postComma;
      if (percent) {
        preComma = digitsBefore + (digitsAfter.length() == 1 ? digitsAfter.charAt(0) + "0" : digitsAfter.substring(0, 2));
        postComma = (preComma.length() < digits) ? "." +
            (digitsAfter.length() >= digits - preComma.length() ? digitsAfter.substring(0, digits - preComma.length()) : digitsAfter) +
            "%" : "%";
      } else if (lengthBefore >= digits) {
        preComma = digitsBefore;
        postComma = "";
      } else {
        preComma = digitsBefore;
        postComma = "." + digitsAfter.substring(0, digits - digitsBefore.length());
      }
      return preComma + postComma;
    }
    return valueString;
  }*/
}
