package de.xeri.prm.servlet.datatables.table;

import javax.annotation.PostConstruct;
import javax.faces.bean.SessionScoped;
import javax.inject.Named;

import de.xeri.prm.manager.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Lara on 09.05.2022 for web
 */
@Named
@SessionScoped
@Getter
@Setter
public class Infos {
  private String leagueName;

  @PostConstruct
  public void init() {
    this.leagueName = Data.getInstance().getCurrentGroup().getName();
  }
}
