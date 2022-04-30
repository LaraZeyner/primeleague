package de.xeri.league.servlet.datatables;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import de.xeri.league.servlet.datatables.objects.PickBanObject;
import de.xeri.league.servlet.datatables.services.PickBanService;

/**
 * Created by Lara on 24.04.2022 for web
 */
@Named
@ViewScoped
public class PicksAndBans implements Serializable {

  private List<PickBanObject> pickBans;

  @Inject
  private PickBanService service;

  @PostConstruct
  public void init() {
    pickBans = service.getPickBans();
  }

  public List<PickBanObject> getPickBans() {
    return pickBans;
  }

  public void setPickBans(List<PickBanObject> pickBans) {
    this.pickBans = pickBans;
  }
}
