package de.xeri.prm.servlet;

import javax.faces.bean.SessionScoped;
import javax.inject.Named;

import de.xeri.prm.manager.Data;
import de.xeri.prm.models.league.League;
import lombok.Getter;

/**
 * Created by Lara on 09.05.2022 for web
 */
@Named
@SessionScoped
@Getter
public class Loadup {
  private final League division = Data.getInstance().getCurrentGroup();

}
