package de.xeri.league.models.match;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.league.models.dynamic.Champion;
import de.xeri.league.models.enums.SelectionType;
import de.xeri.league.models.ids.ChampionSelectionId;
import de.xeri.league.util.Data;
import de.xeri.league.util.Util;
import org.hibernate.annotations.Check;

@Entity(name = "ChampionSelection")
@Table(name = "champion_selection", indexes = @Index(name = "champion", columnList = "champion"))
@IdClass(ChampionSelectionId.class)
public class ChampionSelection implements Serializable {

  @Transient
  private static final long serialVersionUID = -2624837614626155375L;

  private static Set<ChampionSelection> data;

  public static void save() {
    if (data != null) data.forEach(Data.getInstance().getSession()::saveOrUpdate);
  }

  public static Set<ChampionSelection> get() {
    if (data == null) data = new LinkedHashSet<>((List<ChampionSelection>) Util.query("ChampionSelection"));
    return data;
  }

  public static ChampionSelection get(ChampionSelection neu, Game game) {
    get();
    final ChampionSelection entry = find(game, neu.getSelectionType(), neu.getSelectionOrder());
    if (entry == null) data.add(neu);
    return find(game, neu.getSelectionType(), neu.getSelectionOrder());
  }

  public static ChampionSelection find(Game game, SelectionType type, byte order) {
    get();
    return data.stream().filter(selection1 -> selection1.getGame().equals(game) && selection1.getSelectionType().equals(type) &&
        selection1.getSelectionOrder() == order).findFirst().orElse(null);
  }

  @Id
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "game")
  private Game game;

  @Id
  @Enumerated(EnumType.STRING)
  @Column(name = "selection_type", nullable = false, length = 4)
  private SelectionType selectionType;

  @Id
  @Check(constraints = "selection_order <= 10")
  @Column(name = "selection_order", nullable = false)
  private byte selectionOrder;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "champion")
  private Champion champion;

  public ChampionSelection() {
  }

  public ChampionSelection(SelectionType selectionType, byte selectionOrder) {
    this.selectionType = selectionType;
    this.selectionOrder = selectionOrder;
  }

  //<editor-fold desc="getter and setter">
  public Game getGame() {
    return game;
  }

  public void setGame(Game game) {
    this.game = game;
  }

  public SelectionType getSelectionType() {
    return selectionType;
  }

  public void setSelectionType(SelectionType selectionType) {
    this.selectionType = selectionType;
  }

  public byte getSelectionOrder() {
    return selectionOrder;
  }

  public void setSelectionOrder(byte selectionOrder) {
    this.selectionOrder = selectionOrder;
  }

  public Champion getChampion() {
    return champion;
  }

  public void setChampion(Champion champion) {
    this.champion = champion;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ChampionSelection)) return false;
    final ChampionSelection championSelection = (ChampionSelection) o;
    return getSelectionOrder() == championSelection.getSelectionOrder() && getGame().equals(championSelection.getGame()) && getSelectionType() == championSelection.getSelectionType() && getChampion().equals(championSelection.getChampion());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getGame(), getSelectionType(), getSelectionOrder(), getChampion());
  }

  @Override
  public String toString() {
    return "ChampionSelection{" +
        "game=" + game +
        ", selectionType=" + selectionType +
        ", selectionOrder=" + selectionOrder +
        ", champion=" + champion +
        '}';
  }
  //</editor-fold>
}