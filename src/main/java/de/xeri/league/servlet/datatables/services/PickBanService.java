package de.xeri.league.servlet.datatables.services;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.inject.Named;

import de.xeri.league.servlet.datatables.objects.PickBanObject;

/**
 * Created by Lara on 24.04.2022 for web
 */
@Named
@ApplicationScoped
public class PickBanService {

  private List<PickBanObject> pickBans;

  @PostConstruct
  public void init() {
    pickBans = new ArrayList<>();
    pickBans.add(new PickBanObject("Cho'Gath", true, "Vayne", "Shen", true));
    pickBans.add(new PickBanObject("Jarvan IV", true, "Jhin", "Viego", true));
    pickBans.add(new PickBanObject("Ahri", true, "Tahm Kench", "Viktor", true));
    pickBans.add(new PickBanObject("Jinx", true, "Xerath", "Senna", true));
    pickBans.add(new PickBanObject("Sett", true, "Veigar", "Galio", true));
  }

  public List<PickBanObject> getPickBans() {
    return pickBans;
  }
}
