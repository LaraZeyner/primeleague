package de.xeri.prm.servlet.loader.start;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import de.xeri.prm.servlet.loader.ourteam.scrim.Scrimmage;
import de.xeri.prm.servlet.loader.ourteam.scrim.ScrimmagePlanning;
import lombok.Data;
import org.primefaces.model.timeline.TimelineEvent;

/**
 * Created by Lara on 02.06.2022 for web
 */
@ManagedBean
@RequestScoped
@Data
public class LoadScrims implements Serializable {
  private static final long serialVersionUID = -5742800385855342527L;
  private List<Scrimmage> scrimmages;
  private List<ScrimmagePlanning> scrimmagePlannings;
  private List<TimelineEvent> events;

  @PostConstruct
  public void init() {
    this.scrimmagePlannings = new ArrayList<>();
  }
}
