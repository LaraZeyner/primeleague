package de.xeri.league.models.match;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.league.models.enums.QueueType;
import de.xeri.league.util.Data;
import de.xeri.league.util.HibernateUtil;
import de.xeri.league.util.logger.Logger;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NamedQuery;

@Entity(name = "ScheduledGame")
@Table(name = "scheduledgame")
@NamedQuery(name = "ScheduledGame.findAll", query = "FROM ScheduledGame s")
@NamedQuery(name = "ScheduledGame.findById", query = "FROM ScheduledGame s WHERE id = :pk")
@NamedQuery(name = "ScheduledGame.findByMode", query = "FROM ScheduledGame s WHERE queueType = :queue")
@Getter
@NoArgsConstructor
public class ScheduledGame implements Serializable {

  @Transient
  private static final long serialVersionUID = 9033305101470944563L;

  public static ScheduledGame get(ScheduledGame neu) {
    final ScheduledGame game = find(neu.getId());
    if (game != null) {
      return game;
    }
    if (!Game.has(neu.getId())) {
      if (neu.getId().startsWith("EUW")) {
        Logger.getLogger("Scheduled-Game-Creation").info("Spiel erstellt", neu.getId());
        Data.getInstance().save(neu);
        return neu;
      } else {
        Logger.getLogger("Scheduled-Game-Creation").attention("Spiel auf anderem Server", neu.getId());
        return null;
      }

    }
    return null;
  }

  public static boolean has(String id) {
    return HibernateUtil.has(ScheduledGame.class, id);
  }

  public static ScheduledGame find(String id) {
    return HibernateUtil.find(ScheduledGame.class, id);
  }

  public static List<ScheduledGame> findMode(QueueType queue) {
    return HibernateUtil.findList(ScheduledGame.class, new String[]{"queue"}, new Object[]{queue}, "findByMode");
  }

  @Id
  @Column(name = "game_id", nullable = false, length = 16)
  private String id;

  @Enumerated(EnumType.STRING)
  @Column(name = "queuetype")
  private QueueType queueType;

  @Column(name = "prioritized", nullable = false)
  private boolean prioritized;

  public ScheduledGame(String id, QueueType queueType, boolean prioritized) {
    this.id = id;
    this.queueType = queueType;
    this.prioritized = prioritized;
  }

  //<editor-fold desc="getter and setter">
  public void setQueueType(QueueType queueType) {
    this.queueType = queueType;
  }

  public void setPrioritized(boolean prioritized) {
    this.prioritized = prioritized;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ScheduledGame)) return false;
    final ScheduledGame scheduledGame = (ScheduledGame) o;
    return getId().equals(scheduledGame.getId()) && getQueueType() == scheduledGame.getQueueType();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getQueueType());
  }

  @Override
  public String toString() {
    return "ScheduledGame{" +
        "id='" + id + '\'' +
        ", queueType=" + queueType +
        '}';
  }
  //</editor-fold>
}