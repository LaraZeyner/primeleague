package de.xeri.league.models.match.stat;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.xeri.league.util.Data;
import org.hibernate.query.Query;

/**
 * Created by Lara on 22.04.2022 for web
 */
public class StatQuery {
  private final Set<Stat> stats = new HashSet<>();
  private final Set<Limitation> limitations = new HashSet<>();
  private final Class aClass;
  private final StringBuilder selects = new StringBuilder("SELECT ");
  private final StringBuilder joins = new StringBuilder(" INNER JOIN ");
  private final StringBuilder limits = new StringBuilder(" WHERE ");

  public StatQuery(Class aClass) {
    this.aClass = aClass;
  }

  public void limit(Limitation limitation) {
    if (!limitations.isEmpty()) {
      limits.append(" AND ");
    }
    limits.append(limitation.getColumn())
        .append(limitation.getOperator().getCode())
        .append(limitation.getColumn());

    limitations.add(limitation);
  }

  // LIMIT BEFORE
  public List<Object> list(Stat stat) {
    final String limitString = limitations.isEmpty() ? "" : limits.toString();
    final Query query = Data.getInstance().getSession()
        .createQuery("SELECT " + stat.getAttribute() + " FROM " + stat.getSourceClass() + limitString);
    for (Limitation limitation : limitations) {
      query.setParameter(limitation.getColumn(), limitation.getValue());
    }
    return query.list();
  }

  // LIMIT BEFORE
  public void add(Stat stat, OutputType outputType) {
    if (stat instanceof TimeStat) {
      final String query = queryTime(stat, outputType, 0);
      stat.setResult(request(query));
    }

    if (!outputType.equals(OutputType.LIST)) {
      if (!stats.isEmpty()) {
        selects.append(", ");
      }
      selects.append(outputType.getQuery())
          .append(stat.getAttribute())
          .append(")");

      stats.add(stat);
    }
  }

  public String query(int limit) {
    final String name = Data.getInstance().getSession().getMetamodel().entity(aClass).getName();
    final String limitString = limitations.isEmpty() ? "" : limits.toString();
    return selects + " FROM " + name + joins + limitString + (limit > 0 ? " LIMIT " + limit : "");
  }

  public String queryTime(Stat stat, OutputType type, int limit) {
    final String entity = Data.getInstance().getSession().getMetamodel().entity(stat.getSourceClass()).getName();
    final String limitString = limitations.isEmpty() ? "" : limits.toString();
    return "SELECT " + type.getQuery() + stat.getAttribute() + ") FROM " + entity + limitString;
  }

  public List<String> execute() {
    final List<Double> requestList = requestList(query(0));
    int i = 0;
    for (Stat stat : stats) {
      if (stat.getResult() == null) {
        stat.setResult(requestList.get(i));
        ++i;
      }
    }
    return stats.stream().map(Stat::format).collect(Collectors.toList());
  }

  private List<Double> requestList(String query) {
    return null;
  }

  private double request(String query) {
    return 0;
  }

}
