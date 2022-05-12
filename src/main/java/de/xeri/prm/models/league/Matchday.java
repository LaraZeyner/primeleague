package de.xeri.prm.models.league;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import de.xeri.prm.manager.Data;
import de.xeri.prm.util.HibernateUtil;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.NamedQuery;

@Entity(name = "Matchday")
@Table(name = "matchday", indexes = {
    @Index(name = "matchday_start", columnList = "matchday_start", unique = true),
    @Index(name = "matchday_end", columnList = "matchday_end", unique = true),
    @Index(name = "idx_matchday", columnList = "stage, matchday_type", unique = true)
})
@NamedQuery(name = "Matchday.findAll", query = "FROM Matchday m")
@NamedQuery(name = "Matchday.findById", query = "FROM Matchday m WHERE id = :pk")
@NamedQuery(name = "Matchday.findBy", query = "FROM Matchday m WHERE stage = :stage AND type = :type")
public class Matchday implements Serializable {

  @Transient
  private static final long serialVersionUID = 721589244740386057L;

  public static Set<Matchday> get() {
    return new LinkedHashSet<>(HibernateUtil.findList(Matchday.class));
  }

  public static Matchday get(Matchday neu, Stage stage) {
    if (has(neu.getType(), stage)) {
      return find(neu.getType(), stage);
    }
    stage.getMatchdays().add(neu);
    neu.setStage(stage);
    Data.getInstance().save(neu);
    return neu;
  }

  public static boolean has(String type, Stage stage) {
    return HibernateUtil.has(Matchday.class, new String[]{"stage", "type"}, new Object[]{stage, type});
  }

  public static boolean has(short id) {
    return HibernateUtil.has(Matchday.class, id);
  }

  public static Matchday find(String type, Stage stage) {
    return HibernateUtil.find(Matchday.class, new String[]{"stage", "type"}, new Object[]{stage, type});
  }

  public static Matchday find(short id) {
    return HibernateUtil.find(Matchday.class, id);
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "matchday_id", nullable = false)
  private short id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "stage")
  private Stage stage;

  @Column(name = "matchday_type", nullable = false, length = 11)
  private String type;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "matchday_start", nullable = false)
  private Date start;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "matchday_end", nullable = false)
  private Date end;

  @OneToMany(mappedBy = "matchday")
  @LazyCollection(LazyCollectionOption.EXTRA)
  @OrderColumn
  private final Set<TurnamentMatch> matches = new LinkedHashSet<>();

  // default constructor
  public Matchday() {
  }

  public Matchday(String type, Date start, Date end) {
    this.type = type;
    this.start = start;
    this.end = end;
  }

  void addMatch(TurnamentMatch match) {
    matches.add(match);
    match.setMatchday(this);
  }

  //<editor-fold desc="getter and setter">
  public Set<TurnamentMatch> getMatches() {
    return matches;
  }

  public Date getEnd() {
    return end;
  }

  public void setEnd(Date end) {
    this.end = end;
  }

  public Date getStart() {
    return start;
  }

  public void setStart(Date start) {
    this.start = start;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Stage getStage() {
    return stage;
  }

  void setStage(Stage stage) {
    this.stage = stage;
  }

  public short getId() {
    return id;
  }

  public void setId(short id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Matchday)) return false;
    final Matchday matchday = (Matchday) o;
    return getId() == matchday.getId() && getStage().equals(matchday.getStage()) && getType() == matchday.getType() && getStart().equals(matchday.getStart()) && getEnd().equals(matchday.getEnd()) && getMatches().equals(matchday.getMatches());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getType(), getStart(), getEnd());
  }

  @Override
  public String toString() {
    return "Matchday{" +
        "id=" + id +
        ", type=" + type +
        ", start=" + start +
        ", end=" + end +
        ", matches=" + matches.size() +
        '}';
  }
  //</editor-fold>
}