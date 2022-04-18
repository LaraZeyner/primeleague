package de.xeri.league.servlet.datatables;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;
import javax.inject.Named;

import de.xeri.league.models.dynamic.Champion;

/**
 * Created by Lara on 18.04.2022 for web
 */
@Named("dtBasicView")
@ViewScoped
public class ChampionView implements Serializable {

  private static final long serialVersionUID = -4641325248256874215L;
  private List<Champion> champions;

  @PostConstruct
  public void init() {
    champions = (List<Champion>) Champion.get();
  }

  public List<Champion> getChampions() {
    return champions;
  }
}