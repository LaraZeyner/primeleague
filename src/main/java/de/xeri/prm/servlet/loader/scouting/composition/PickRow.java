package de.xeri.prm.servlet.loader.scouting.composition;

import java.io.Serializable;

import javax.faces.event.ValueChangeEvent;

import lombok.Data;
import lombok.NonNull;

/**
 * Created by Lara on 22.05.2022 for web
 */
@Data
public class PickRow implements Serializable {
  private static final long serialVersionUID = -4244063970089148306L;

  @NonNull private String pickWe;
  @NonNull private String banWe;
  @NonNull private String banEnemy;
  @NonNull private String pickEnemy;

  public void PickWeChangeEvent(ValueChangeEvent event) {
    this.pickWe = String.valueOf(event.getNewValue());
    System.out.println(pickWe);
  }

  public void BanWeChangeEvent(ValueChangeEvent event) {
    this.banWe = String.valueOf(event.getNewValue());
  }

  public void BanEnemyChangeEvent(ValueChangeEvent event) {
    this.banEnemy = String.valueOf(event.getNewValue());
  }

  public void PickEnemyChangeEvent(ValueChangeEvent event) {
    this.pickEnemy = String.valueOf(event.getNewValue());
  }


}
