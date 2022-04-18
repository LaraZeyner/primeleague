package de.xeri.league.models.league;

import java.io.Serializable;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import de.xeri.league.util.Data;
import de.xeri.league.util.Util;

@Entity(name = "Season")
@Table(name = "season", indexes = {
    @Index(name = "idx_season_name", columnList = "season_name", unique = true),
    @Index(name = "idx_season_start", columnList = "season_start", unique = true),
    @Index(name = "idx_season_end", columnList = "season_end", unique = true)
})
public class Season implements Serializable {

  @Transient
  private static final long serialVersionUID = -3593638320708107825L;

  private static Set<Season> data;

  public static Season current() {
    final Calendar now = Calendar.getInstance();
    return Season.get().stream().filter(season -> season.getSeasonStart().after(now) && season.getSeasonEnd().before(now))
        .findFirst().orElse(null);
  }

  public static void save() {
    if (data != null) data.forEach(Data.getInstance().getSession()::saveOrUpdate);
  }

  public static Set<Season> get() {
    if (data == null) data = new LinkedHashSet<>((List<Season>) Util.query("Season"));
    return data;
  }

  public static Season get(Season neu) {
    get();
    if (find(neu.getId()) == null) data.add(neu);
    return find(neu.getId());
  }

  public static Season find(int id) {
    get();
    return data.stream().filter(entry -> entry.getId() == (short) id).findFirst().orElse(null);
  }

  public static Season find(String name) {
    get();
    return data.stream().filter(entry -> entry.getSeasonName().equals(name)).findFirst().orElse(null);
  }

  @Id
  @Column(name = "season_id", nullable = false)
  private short id;

  @Column(name = "season_name", nullable = false, length = 17)
  private String seasonName;

  @Temporal(TemporalType.DATE)
  @Column(name = "season_start", nullable = false)
  private Calendar seasonStart;

  @Temporal(TemporalType.DATE)
  @Column(name = "season_end", nullable = false)
  private Calendar seasonEnd;

  @OneToMany(mappedBy = "season")
  private final Set<Stage> stages = new LinkedHashSet<>();

  @OneToMany(mappedBy = "season")
  private final Set<SeasonElo> seasonElos = new LinkedHashSet<>();

  // default constructor
  public Season() {
  }

  public Season(short id, String seasonName, Calendar seasonStart, Calendar seasonEnd) {
    this.id = id;
    this.seasonName = seasonName;
    this.seasonStart = seasonStart;
    this.seasonEnd = seasonEnd;
  }

  public Stage addStage(Stage stage) {
    return Stage.get(stage, this);
  }

  public void addSeaonElo(SeasonElo seasonElo) {
    seasonElos.add(seasonElo);
    seasonElo.setSeason(this);
  }

  public boolean isCurrent() {
    final Calendar now = Calendar.getInstance();
    return seasonStart.after(now) && seasonEnd.before(now);
  }

  //<editor-fold desc="getter and setter">
  public Set<SeasonElo> getSeasonElos() {
    return seasonElos;
  }

  public Set<Stage> getStages() {
    return stages;
  }

  public Calendar getSeasonEnd() {
    return seasonEnd;
  }

  public void setSeasonEnd(Calendar seasonEnd) {
    this.seasonEnd = seasonEnd;
  }

  public Calendar getSeasonStart() {
    return seasonStart;
  }

  public void setSeasonStart(Calendar seasonStart) {
    this.seasonStart = seasonStart;
  }

  public String getSeasonName() {
    return seasonName;
  }

  public void setSeasonName(String seasonName) {
    this.seasonName = seasonName;
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
    if (!(o instanceof Season)) return false;
    final Season season = (Season) o;
    return getId() == season.getId() && getSeasonName().equals(season.getSeasonName()) && getSeasonStart().equals(season.getSeasonStart()) && getSeasonEnd().equals(season.getSeasonEnd()) && getStages().equals(season.getStages()) && getSeasonElos().equals(season.getSeasonElos());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getSeasonName(), getSeasonStart(), getSeasonEnd());
  }

  @Override
  public String toString() {
    return "Season{" +
        "id=" + id +
        ", seasonName='" + seasonName + '\'' +
        ", seasonStart=" + seasonStart +
        ", seasonEnd=" + seasonEnd +
        '}';
  }
  //</editor-fold>
}