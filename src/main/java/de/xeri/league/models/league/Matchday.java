package de.xeri.league.models.league;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import de.xeri.league.models.enums.MatchdayType;
import de.xeri.league.util.Data;
import de.xeri.league.util.Util;

@Entity(name = "Matchday")
@Table(name = "matchday", indexes = {
    @Index(name = "matchday_start", columnList = "matchday_start", unique = true),
    @Index(name = "matchday_end", columnList = "matchday_end", unique = true),
    @Index(name = "idx_matchday", columnList = "stage, matchday_type", unique = true)
})
public class Matchday implements Serializable {

  @Transient
  private static final long serialVersionUID = 721589244740386057L;

  private static Set<Matchday> data;

  public static void save() {
    if (data != null) data.forEach(Data.getInstance().getSession()::saveOrUpdate);
  }

  public static Set<Matchday> get() {
    if (data == null) data = new LinkedHashSet<>((List<Matchday>) Util.query("Matchday"));
    return data;
  }

  public static Matchday get(Matchday neu, Stage stage) {
    get();
    final Matchday entry = find(neu.getMatchdayType(), stage.getId());
    if (entry == null) {
      stage.addMatchday(neu);
      data.add(neu);
    }
    return find(neu.getMatchdayType(), stage.getId());
  }

  public static Matchday find(MatchdayType type, short stageId) {
    get();
    return data.stream().filter(entry -> entry.getMatchdayType().equals(type) && entry.getStage().getId() == stageId)
        .findFirst().orElse(null);
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "matchday_id", nullable = false)
  private short id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "stage")
  private Stage stage;

  @Enumerated(EnumType.STRING)
  @Column(name = "matchday_type", nullable = false, length = 11)
  private MatchdayType matchdayType;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "matchday_start", nullable = false)
  private Date matchdayStart;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "matchday_end", nullable = false)
  private Date matchdayEnd;

  @OneToMany(mappedBy = "matchday")
  private final Set<TurnamentMatch> matches = new LinkedHashSet<>();

  // default constructor
  public Matchday() {
  }

  public Matchday(MatchdayType matchdayType, Date matchdayStart, Date matchdayEnd) {
    this.matchdayType = matchdayType;
    this.matchdayStart = matchdayStart;
    this.matchdayEnd = matchdayEnd;
  }

  public void addMatch(TurnamentMatch match) {
    matches.add(match);
    match.setMatchday(this);
  }

  //<editor-fold desc="getter and setter">
  public Set<TurnamentMatch> getMatches() {
    return matches;
  }

  public Date getMatchdayEnd() {
    return matchdayEnd;
  }

  public void setMatchdayEnd(Date matchdayEnd) {
    this.matchdayEnd = matchdayEnd;
  }

  public Date getMatchdayStart() {
    return matchdayStart;
  }

  public void setMatchdayStart(Date matchdayStart) {
    this.matchdayStart = matchdayStart;
  }

  public MatchdayType getMatchdayType() {
    return matchdayType;
  }

  public void setMatchdayType(MatchdayType matchdayType) {
    this.matchdayType = matchdayType;
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
    return getId() == matchday.getId() && getStage().equals(matchday.getStage()) && getMatchdayType() == matchday.getMatchdayType() && getMatchdayStart().equals(matchday.getMatchdayStart()) && getMatchdayEnd().equals(matchday.getMatchdayEnd()) && getMatches().equals(matchday.getMatches());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getMatchdayType(), getMatchdayStart(), getMatchdayEnd());
  }

  @Override
  public String toString() {
    return "Matchday{" +
        "id=" + id +
        ", matchdayType=" + matchdayType +
        ", matchdayStart=" + matchdayStart +
        ", matchdayEnd=" + matchdayEnd +
        ", matches=" + matches.size() +
        '}';
  }
  //</editor-fold>
}