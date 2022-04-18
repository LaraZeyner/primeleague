package de.xeri.league.servlet.tabs;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 * Created by Lara on 18.04.2022 for web
 */
@Named
@RequestScoped
public class KnobView implements Serializable {

  private int value = 50;

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }

  public void onChange() {
    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "You have selected: " + value, null));
  }
}