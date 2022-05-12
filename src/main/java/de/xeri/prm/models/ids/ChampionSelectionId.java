package de.xeri.prm.models.ids;

import java.io.Serializable;
import java.util.Objects;

import de.xeri.prm.models.enums.SelectionType;
import org.hibernate.Hibernate;

public class ChampionSelectionId implements Serializable {
  private static final long serialVersionUID = -8480023592581283042L;

  private String game;
  private SelectionType selectionType;
  private byte selectionOrder;

  //<editor-fold desc="getter and setter">
  public byte getSelectionOrder() {
    return selectionOrder;
  }

  public void setSelectionOrder(byte selectionOrder) {
    this.selectionOrder = selectionOrder;
  }

  public SelectionType getSelectionType() {
    return selectionType;
  }

  public void setSelectionType(SelectionType selectionType) {
    this.selectionType = selectionType;
  }

  public String getGame() {
    return game;
  }

  public void setGame(String game) {
    this.game = game;
  }

  @Override
  public int hashCode() {
    return Objects.hash(game, selectionType, selectionOrder);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    final ChampionSelectionId entity = (ChampionSelectionId) o;
    return Objects.equals(this.game, entity.game) &&
        Objects.equals(this.selectionType, entity.selectionType) &&
        Objects.equals(this.selectionOrder, entity.selectionOrder);
  }

  @Override
  public String toString() {
    return "ChampionSelectionId{" +
        "game='" + game + '\'' +
        ", selectionType=" + selectionType +
        ", selectionOrder=" + selectionOrder +
        '}';
  }
  //</editor-fold>
}