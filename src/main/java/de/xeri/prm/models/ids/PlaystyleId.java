package de.xeri.prm.models.ids;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;

import de.xeri.prm.models.enums.GamePhase;
import org.hibernate.Hibernate;

@Embeddable
public class PlaystyleId implements Serializable {

  @Transient
  private static final long serialVersionUID = -8664163729547080120L;

  @Column(name = "champion", nullable = false)
  private short champion;

  @Enumerated(EnumType.STRING)
  @Column(name = "game_phase", nullable = false, length = 12)
  private GamePhase gamePhase;

  //<editor-fold desc="getter and setter">
  public GamePhase getGamePhase() {
    return gamePhase;
  }

  public void setGamePhase(GamePhase gamePhase) {
    this.gamePhase = gamePhase;
  }

  public short getChampion() {
    return champion;
  }

  public void setChampion(short champion) {
    this.champion = champion;
  }

  @Override
  public int hashCode() {
    return Objects.hash(gamePhase, champion);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    final PlaystyleId entity = (PlaystyleId) o;
    return Objects.equals(this.gamePhase, entity.gamePhase) &&
        Objects.equals(this.champion, entity.champion);
  }

  @Override
  public String toString() {
    return "PlaystyleId{" +
        "champion='" + champion + '\'' +
        ", gamePhase=" + gamePhase +
        '}';
  }
  //</editor-fold>
}