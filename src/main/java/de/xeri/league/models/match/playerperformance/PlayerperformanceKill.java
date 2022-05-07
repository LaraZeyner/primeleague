package de.xeri.league.models.match.playerperformance;

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
import de.xeri.league.game.events.location.KillPosition;
import de.xeri.league.game.events.location.Position;
import de.xeri.league.models.match.Game;
import de.xeri.league.util.Data;
import de.xeri.league.util.HibernateUtil;
import de.xeri.league.util.Util;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NamedQuery;

@Entity(name = "PlayerperformanceKill")
@Table(name = "playerperformance_kill", indexes = @Index(name = "kill_playerperformance", columnList = "playerperformance"))
@IdClass(PlayerperformanceKillId.class)
@NamedQuery(name = "PlayerperformanceKill.findAll", query = "FROM PlayerperformanceItem p")
@NamedQuery(name = "PlayerperformanceKill.findBy",
    query = "FROM PlayerperformanceKill p WHERE playerperformance = :playerperformance AND time = :time")
@NamedQuery(name = "PlayerperformanceKill.findByGame",
    query = "FROM PlayerperformanceKill p WHERE playerperformance.teamperformance.game = :game AND time = :time")
@Getter
@NoArgsConstructor
public class PlayerperformanceKill implements Serializable {
  @Transient
  private static final long serialVersionUID = 8813809418539948714L;

  //<editor-fold desc="Queries">
  public static Set<PlayerperformanceKill> get() {
    return new LinkedHashSet<>(HibernateUtil.findList(PlayerperformanceKill.class));
  }

  public static PlayerperformanceKill get(PlayerperformanceKill neu, Playerperformance playerperformance) {
    if (has(playerperformance, neu.getTime())) {
      return find(playerperformance, neu.getTime());
    }
    playerperformance.getKillEvents().add(neu);
    neu.setPlayerperformance(playerperformance);
    Data.getInstance().save(neu);
    return neu;
  }

  public static boolean has(Playerperformance playerperformance, int time) {
    return HibernateUtil.has(PlayerperformanceKill.class, new String[]{"playerperformance", "time"}, new Object[]{playerperformance, time});
  }

  public static boolean has(Game game, int time) {
    return HibernateUtil.has(PlayerperformanceKill.class, new String[]{"game", "time"}, new Object[]{game, time}, "findByGame");
  }

  public static PlayerperformanceKill find(Playerperformance playerperformance, int time) {
    return HibernateUtil.find(PlayerperformanceKill.class, new String[]{"playerperformance", "time"}, new Object[]{playerperformance, time});
  }

  public static PlayerperformanceKill find(Game game, int time) {
    return HibernateUtil.find(PlayerperformanceKill.class, new String[]{"game", "time"}, new Object[]{game, time}, "findByGame");
  }
  //</editor-fold>
  public static int lastId() {
    final List<PlayerperformanceKill> kills = Util.query("PlayerperformanceKill ORDER BY id DESC");
    return kills.isEmpty() ? 0 : kills.get(0).getId();
  }

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

  public PlayerperformanceKill(int id, int time, Position position, short bounty, KillRole role, KillType type, byte streak) {
    this.id = id;
    this.time = time;
    this.position = new KillPosition((short) position.getX(), (short) position.getY());
    this.bounty = bounty;
    this.role = role;
    this.type = type;
    this.streak = streak;
  }

  //<editor-fold desc="getter and setter">
  public void setPlayerperformance(Playerperformance playerperformance) {
    this.playerperformance = playerperformance;
  }

  public void setType(KillType type) {
    this.type = type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PlayerperformanceKill)) return false;
    final PlayerperformanceKill that = (PlayerperformanceKill) o;
    return getId() == that.getId() && getPlayerperformance().equals(that.getPlayerperformance());
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