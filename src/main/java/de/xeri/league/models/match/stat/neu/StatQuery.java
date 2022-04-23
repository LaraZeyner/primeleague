package de.xeri.league.models.match.stat.neu;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.xeri.league.util.Data;

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
        .append("= :")
        .append(limitation.getColumn());

    limitations.add(limitation);
  }

  // LIMIT BEFORE ADD
  public void add(Stat stat, OutputType outputType) {
    if (stat instanceof TimeStat) {
      final String query = queryTime(stat, outputType, 0);
      stat.setResult(request(query));
    }
    if (!stats.isEmpty()) {
      selects.append(", ");
    }
    selects.append(outputType.getQuery())
        .append(stat.getAttribute())
        .append(")");

    stats.add(stat);
  }

  public String query(int limit) {
    final String name = Data.getInstance().getSession().getMetamodel().entity(aClass).getName();
    return selects + " FROM " + name + joins + limits + (limit > 0 ? " LIMIT " + limit : "");
  }

  public String queryTime(Stat stat, OutputType type, int limit) {
    final String entity = Data.getInstance().getSession().getMetamodel().entity(stat.getSourceClass()).getName();
    return "SELECT " + type.getQuery() + stat.getAttribute() + ") FROM " + entity + limits;
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
