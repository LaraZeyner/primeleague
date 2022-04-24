package de.xeri.league.models.match.stat;

/**
 * Created by Lara on 22.04.2022 for web
 */
public class Limitation {


  private final String column;
  private final Operator operator;
  private final Object value;

  public Limitation(String column, Object value) {
    this.column = column;
    this.value = value;
    this.operator = Operator.EQUALS;
  }

  public Limitation(String column, Object value, Operator operator) {
    this.column = column;
    this.value = value;
    this.operator = operator;
  }

  public String getColumn() {
    return column;
  }

  public Operator getOperator() {
    return operator;
  }

  public Object getValue() {
    return value;
  }
}
