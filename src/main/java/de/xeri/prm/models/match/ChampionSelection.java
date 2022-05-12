package de.xeri.prm.models.match;

import java.io.Serializable;
import java.util.Objects;

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

import de.xeri.prm.manager.Data;
import de.xeri.prm.models.dynamic.Champion;
import de.xeri.prm.models.enums.SelectionType;
import de.xeri.prm.models.ids.ChampionSelectionId;
import de.xeri.prm.util.HibernateUtil;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.NamedQuery;

@Entity(name = "ChampionSelection")
@Table(name = "champion_selection", indexes = @Index(name = "champion", columnList = "champion"))
@IdClass(ChampionSelectionId.class)
@NamedQuery(name = "ChampionSelection.findAll", query = "FROM ChampionSelection c")
@NamedQuery(name = "ChampionSelection.findBy",
    query = "FROM ChampionSelection c WHERE game = :game AND selectionType = :type AND selectionOrder = :sOrder")
public class ChampionSelection implements Serializable {

  @Transient
  private static final long serialVersionUID = -2624837614626155375L;

  static ChampionSelection get(ChampionSelection neu, Game game, Champion champion, boolean check) {

    if (check && has(game, neu.getSelectionType(), neu.getSelectionOrder())) {
      return find(game, neu.getSelectionType(), neu.getSelectionOrder());
    }
    game.getChampionSelections().add(neu);
    neu.setGame(game);
    champion.getChampionSelections().add(neu);
    neu.setChampion(champion);
    Data.getInstance().save(neu);
    return neu;
  }

  public static boolean has(Game game, SelectionType type, byte order) {
    return HibernateUtil.has(ChampionSelection.class, new String[]{"game", "type", "sOrder"}, new Object[]{game, type, order});
  }

  public static ChampionSelection find(Game game, SelectionType type, byte order) {
    return HibernateUtil.find(ChampionSelection.class, new String[]{"game", "type", "sOrder"}, new Object[]{game, type, order});
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