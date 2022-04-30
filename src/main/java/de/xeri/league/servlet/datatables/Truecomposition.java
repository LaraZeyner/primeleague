package de.xeri.league.servlet.datatables;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import de.xeri.league.servlet.datatables.services.PickBanService;

/**
 * Created by Lara on 24.04.2022 for web
 */
@Named
@ViewScoped
public class Truecomposition implements Serializable {

  private List<String> compositionWeaknesses;
  private List<String> compositionStrengths;
  private List<String> possiblePicks;
  private List<String> compositionAttributes;

  @Inject
  private PickBanService service;

}
