package de.xeri.league.models.league;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import de.xeri.league.models.enums.LogAction;
import de.xeri.league.util.Data;
import de.xeri.league.util.HibernateUtil;
import org.hibernate.annotations.NamedQuery;

@Entity(name = "Matchlog")
@Table(name = "matchlog", indexes = {
    @Index(name = "idx_matchlog", columnList = "turnamentmatch, log_time", unique = true),
    @Index(name = "player", columnList = "player")
})
@NamedQuery(name = "Matchlog.findAll", query = "FROM Matchlog m")
@NamedQuery(name = "Matchlog.findById", query = "FROM Matchlog m WHERE id = :pk")
@NamedQuery(name = "Matchlog.findBy", query = "FROM Matchlog m WHERE match = :match AND logTime = :time")
public class Matchlog implements Serializable {

  @Transient
  private static final long serialVersionUID = 7774556170074025328L;

  // match, logtime
  public static Set<Matchlog> get() {
    return new LinkedHashSet<>(HibernateUtil.findList(Matchlog.class));
  }

  public static Matchlog get(Matchlog neu, TurnamentMatch match) {
    if (has(match, neu.getLogTime())) {
      return find(match, neu.getLogTime());
    }
    match.getLogEntries().add(neu);
    neu.setMatch(match);
    Data.getInstance().save(neu);
    return neu;
  }

  public static boolean has(TurnamentMatch match, Date logTime) {
    return HibernateUtil.has(Matchlog.class, new String[]{"match", "time"}, new Object[]{match, logTime});
  }

  public static boolean has(short id) {
    return HibernateUtil.has(Matchlog.class, id);
  }

  public static Matchlog find(TurnamentMatch match, Date logTime) {
    return HibernateUtil.find(Matchlog.class, new String[]{"match", "time"}, new Object[]{match, logTime});
  }

  public static Matchlog find(short id) {
    return HibernateUtil.find(Matchlog.class, id);
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "log_id", nullable = false)
  private int id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "turnamentmatch", nullable = false)
  private TurnamentMatch match;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "log_time", nullable = false)
  private Date logTime;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "player")
  private Player player;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "team")
  private Team team;

  @Enumerated(EnumType.STRING)
  @Column(name = "log_action", nullable = false, length = 143)
  private LogAction logAction;

  @Column(name = "log_details", nullable = false, length = 100)
  private String logDetails;

  // default constructor
  public Matchlog() {
  }

  public Matchlog(Date logTime, LogAction logAction, String logDetails) {
    this.logTime = logTime;
    this.logAction = logAction;
    this.logDetails = logDetails;
  }

  //<editor-fold desc="getter and setter">
  public String getLogDetails() {
    return logDetails;
  }

  public void setLogDetails(String logDetails) {
    this.logDetails = logDetails;
  }

  public LogAction getLogAction() {
    return logAction;
  }

  public void setLogAction(LogAction logAction) {
    this.logAction = logAction;
  }

  public Player getPlayer() {
    return player;
  }

  void setPlayer(Player player) {
    this.player = player;
  }

  public Date getLogTime() {
    return logTime;
  }

  public Team getTeam() {
    return team;
  }

  public void setTeam(Team team) {
    this.team = team;
  }

  public TurnamentMatch getMatch() {
    return match;
  }

  void setMatch(TurnamentMatch turnamentmatch) {
    this.match = turnamentmatch;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Matchlog)) return false;
    final Matchlog matchlog = (Matchlog) o;
    return getId() == matchlog.getId() && getMatch().equals(matchlog.getMatch()) && getLogTime().equals(matchlog.getLogTime()) && Objects.equals(getPlayer(), matchlog.getPlayer()) && Objects.equals(getTeam(), matchlog.getTeam()) && getLogAction() == matchlog.getLogAction() && getLogDetails().equals(matchlog.getLogDetails());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getMatch(), getLogTime(), getPlayer(), getTeam(), getLogAction(), getLogDetails());
  }

  @Override
  public String toString() {
    return "Matchlog{" +
        "id=" + id +
        ", turnamentmatch=" + match +
        ", logTime=" + logTime +
        ", player=" + player +
        ", team=" + team +
        ", logAction=" + logAction +
        ", logDetails='" + logDetails + '\'' +
        '}';
  }
  //</editor-fold>

}