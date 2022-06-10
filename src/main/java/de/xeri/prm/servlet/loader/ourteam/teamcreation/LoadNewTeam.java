package de.xeri.prm.servlet.loader.ourteam.teamcreation;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import lombok.Data;
import org.primefaces.model.DualListModel;

/**
 * Created by Lara on 02.06.2022 for web
 */
@ManagedBean
@RequestScoped
@Data
public class LoadNewTeam implements Serializable {
  private String name;
  private String player1;
  private String player2;
  private String player3;
  private String player4;
  private String player5;

  private DualListModel<String> participants;


  private String type;

  @PostConstruct
  public void init() {
  }

  public void load() {

  }
}
