package de.xeri.league.servlet;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * Created by Lara on 03.04.2022 for web
 */
@ManagedBean
@SessionScoped
public class Calculator implements Serializable {
  private static final long serialVersionUID = -5615839722863505140L;
  private int calc1;
  private int calc2;

  //default constructor
  public Calculator() {
  }

  public Calculator(int calc1, int calc2) {
    this.calc1 = calc1;
    this.calc2 = calc2;
  }

  public int add() {
    return calc1 + calc2;
  }

  //<editor-fold desc="getter and setter">
  public int getCalc1() {
    return calc1;
  }

  public void setCalc1(int calc1) {
    this.calc1 = calc1;
  }

  public int getCalc2() {
    return calc2;
  }

  public void setCalc2(int calc2) {
    this.calc2 = calc2;
  }
  //</editor-fold>
}
