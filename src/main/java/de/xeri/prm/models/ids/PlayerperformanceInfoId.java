package de.xeri.prm.models.ids;

import java.io.Serializable;

import lombok.Data;

@Data
public class PlayerperformanceInfoId implements Serializable {
  private static final transient long serialVersionUID = -2861272963952309777L;

  private int playerperformance;
  private short minute;
}