package de.xeri.prm.models.match;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.prm.manager.PrimeData;
import de.xeri.prm.models.enums.QueueType;
import de.xeri.prm.models.league.Team;
import de.xeri.prm.util.HibernateUtil;
import de.xeri.prm.util.logger.Logger;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.NamedQuery;

@Entity(name = "ScheduledGame")
@Table(name = "scheduledgame")
@NamedQuery(name = "ScheduledGame.findAll", query = "FROM ScheduledGame s")
@NamedQuery(name = "ScheduledGame.findById", query = "FROM ScheduledGame s WHERE id = :pk")
@NamedQuery(name = "ScheduledGame.findByMode", query = "FROM ScheduledGame s WHERE queueType = :queue")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class ScheduledGame implements Serializable {

  @Transient
  private static final long serialVersionUID = 9033305101470944563L;

  public static ScheduledGame get(ScheduledGame neu, Short teamId) {
    final ScheduledGame game = find(neu.getId());
    if (game != null && teamId != null) {
      game.setTeams(game.getTeams() == null ? teamId + "" : game.getTeams() + "," + teamId);
      PrimeData.getInstance().save(game);
      return game;
    }
    if (game == null && !Game.has(neu.getId())) {
      if (neu.getId().startsWith("EUW")) {
        if (teamId != null) {
          neu.setTeams(teamId + "");
        }
        PrimeData.getInstance().save(neu);
        Logger.getLogger("Scheduled-Game-Creation").info("Spiel erstellt", neu.getId());
        return neu;
      } else {
        Logger.getLogger("Scheduled-Game-Creation").attention("Spiel auf anderem Server", neu.getId());
      }
    }
    return game;
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

  @Column(name = "prioritized", length = 120)
  private String teams;

  public ScheduledGame(String id, QueueType queueType) {
    this.id = id;
    this.queueType = queueType;
  }

  public Map<Team, Integer> getTeamsMap() {
    Map<Team, Integer> map = new HashMap<>();
    if (teams != null && teams.contains(",")) {
      for (String s : teams.split(",")) {
        short id = Short.parseShort(s);
        final Team team = Team.find(id);
        map.put(team, map.containsKey(team) ? map.get(team) + 1 : 1);
      }
    }
    return map;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    final ScheduledGame that = (ScheduledGame) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}