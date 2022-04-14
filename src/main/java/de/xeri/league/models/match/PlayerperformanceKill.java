package de.xeri.league.models.match;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
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

import de.xeri.league.models.enums.KillRole;
import de.xeri.league.models.enums.KillType;
import de.xeri.league.models.ids.PlayerperformanceKillId;
import de.xeri.league.util.Data;
import de.xeri.league.util.Util;

@Entity(name = "Playerperformance_Kill")
@Table(name = "playerperformance_kill", indexes = @Index(name = "playerperformance", columnList = "playerperformance"))
@IdClass(PlayerperformanceKillId.class)
public class PlayerperformanceKill implements Serializable {
  @Transient
  private static final long serialVersionUID = 8813809418539948714L;
  //<editor-fold desc="Queries">
  private static Set<PlayerperformanceKill> data;

  public static void save() {
    if (data != null) data.forEach(Data.getInstance().getSession()::saveOrUpdate);
  }

  public static Set<PlayerperformanceKill> get() {
    if (data == null)
      data = new LinkedHashSet<>((List<PlayerperformanceKill>) Util.query("Playerperformance_Kill"));
    return data;
  }

  public static int lastId() {
    final List<PlayerperformanceKill> kills = Util.query("Playerperformance_Kills ORDER BY id DESC");
    return kills.isEmpty() ? 0 : kills.get(0).getId();
  }

  public static PlayerperformanceKill get(PlayerperformanceKill neu, Playerperformance performance) {
    get();
    if (find(performance, neu.getTime()) == null) {
      performance.getKillEvents().add(neu);
      neu.setPlayerperformance(performance);
      data.add(neu);
    }
    return find(performance, neu.getTime());
  }


  public static PlayerperformanceKill find(Game game, int killTime) {
    return data.stream().filter(entry -> entry.getPlayerperformance().getTeamperformance().getGame().equals(game) &&
            entry.getTime() == killTime).findFirst().orElse(null);
  }

  public static PlayerperformanceKill find(Playerperformance playerperformance, int killTime) {
    return data.stream().filter(entry -> entry.getPlayerperformance().equals(playerperformance) && entry.getTime() == killTime)
        .findFirst().orElse(null);
  }
  //</editor-fold>

  @Id
  @Column(name = "kill_id", nullable = false)
  private int id;

  @Id
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "playerperformance")
  private Playerperformance playerperformance;

  @Column(name = "kill_time", nullable = false)
  private int time;

  @Embedded
  private KillPosition position;

  @Column(name = "kill_bounty", nullable = false)
  private short bounty;

  @Enumerated(EnumType.STRING)
  @Column(name = "kill_role", nullable = false, length = 6)
  private KillRole role;

  @Enumerated(EnumType.STRING)
  @Column(name = "kill_type", nullable = false, length = 11)
  private KillType type;

  @Column(name = "kill_streak", nullable = false)
  private byte streak = 0;



  // default constructor
  public PlayerperformanceKill() {
  }

  public PlayerperformanceKill(int id, int time, Position position, short bounty, KillRole role, KillType type, byte streak) {
    this.id = id;
    this.time = time;
    this.position = new KillPosition(position.getX(), position.getY());
    this.bounty = bounty;
    this.role = role;
    this.type = type;
    this.streak = streak;
  }

  //<editor-fold desc="getter and setter">
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Playerperformance getPlayerperformance() {
    return playerperformance;
  }

  public void setPlayerperformance(Playerperformance playerperformance) {
    this.playerperformance = playerperformance;
  }

  public int getTime() {
    return time;
  }

  public void setTime(int time) {
    this.time = time;
  }

  public Position getPosition() {
    return new Position(position.getX(), position.getY());
  }

  public short getBounty() {
    return bounty;
  }

  public KillRole getRole() {
    return role;
  }

  public KillType getType() {
    return type;
  }

  public void setType(KillType type) {
    this.type = type;
  }

  public byte getStreak() {
    return streak;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PlayerperformanceKill)) return false;
    final PlayerperformanceKill that = (PlayerperformanceKill) o;
    return getId() == that.getId() && getTime() == that.getTime() && getBounty() == that.getBounty() && getStreak() == that.getStreak() && getPlayerperformance().equals(that.getPlayerperformance()) && getPosition().equals(that.getPosition()) && getRole() == that.getRole() && getType() == that.getType();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getPlayerperformance(), getTime(), getPosition(), getBounty(), getRole(), getType(), getStreak());
  }

  @Override
  public String toString() {
    return "PlayerperformanceKill{" +
        "id=" + id +
        ", playerperformance=" + playerperformance +
        ", time=" + time +
        ", position=" + position +
        ", bounty=" + bounty +
        ", role=" + role +
        ", type=" + type +
        ", streak=" + streak +
        '}';
  }
  //</editor-fold>
}