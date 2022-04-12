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
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.league.models.enums.QueueType;
import de.xeri.league.util.Data;
import de.xeri.league.util.Util;
import org.hibernate.annotations.Check;

@Entity(name = "ScheduledGame")
@Table(name = "scheduledgame")
public class ScheduledGame implements Serializable {

  @Transient
  private static final long serialVersionUID = 9033305101470944563L;

  private static Set<ScheduledGame> data;

  public static void save() {
    if (data != null) data.forEach(Data.getInstance().getSession()::saveOrUpdate);
  }

  public static Set<ScheduledGame> get() {
    if (data == null) data = new LinkedHashSet<>((List<ScheduledGame>) Util.query("ScheduledGame"));
    return data;
  }

  public static ScheduledGame get(ScheduledGame neu) {
    get();
    if (find(neu.getId()) == null) data.add(neu);
    return find(neu.getId());
  }

  public static ScheduledGame find(String id) {
    get();
    return data.stream().filter(entry -> entry.getId().equals(id)).findFirst().orElse(null);
  }

  @Id
  @Check(constraints = "game_id REGEXP ('^EUW')")
  @Column(name = "game_id", nullable = false, length = 16)
  private String id;

  @Enumerated(EnumType.STRING)
  @Column(name = "queuetype")
  private QueueType queueType;

  // default constructor
  public ScheduledGame() {
  }

  public ScheduledGame(String id, QueueType queueType) {
    this.id = id;
    this.queueType = queueType;
  }

  //<editor-fold desc="getter and setter">
  public QueueType getQueueType() {
    return queueType;
  }

  public void setQueueType(QueueType queueType) {
    this.queueType = queueType;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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