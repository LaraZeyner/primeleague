package de.xeri.prm.models.league;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import de.xeri.prm.manager.PrimeData;
import de.xeri.prm.util.HibernateUtil;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.NamedQuery;

@Entity(name = "Season")
@Table(name = "season", indexes = {
    @Index(name = "idx_season_name", columnList = "season_name", unique = true),
    @Index(name = "idx_season_start", columnList = "season_start", unique = true),
    @Index(name = "idx_season_end", columnList = "season_end", unique = true)
})
@NamedQuery(name = "Season.findAll", query = "FROM Season s")
@NamedQuery(name = "Season.findById", query = "FROM Season s WHERE id = :pk")
@NamedQuery(name = "Season.findBy", query = "FROM Season s WHERE seasonName = :name")
public class Season implements Serializable {

  @Transient
  private static final long serialVersionUID = -3593638320708107825L;

  public static Set<Season> get() {
    return new LinkedHashSet<>(HibernateUtil.findList(Season.class));
  }

  public static Season get(Season neu) {
    if (has(neu.getId())) {
      final Season season = find(neu.getId());
      season.setSeasonStart(neu.getSeasonStart());
      season.setSeasonEnd(neu.getSeasonEnd());
      return season;
    }
    PrimeData.getInstance().save(neu);
    return neu;
  }

  public static boolean has(short id) {
    return HibernateUtil.has(Season.class, id);
  }

  public static boolean has(String name) {
    return HibernateUtil.has(Season.class, new String[]{"name"}, new Object[]{name});
  }

  public static Season find(String name) {
    return HibernateUtil.find(Season.class, new String[]{"name"}, new Object[]{name});
  }

  public static Season find(short id) {
    return HibernateUtil.find(Season.class, id);
  }

  public static Season current() {
    final Calendar now = Calendar.getInstance();
    return Season.get().stream()
        .filter(season -> season.getSeasonStart().after(now) && season.getSeasonEnd().before(now))
        .findFirst().orElse(last());
  }

  public static Season last() {
    return Season.get().stream()
        .max(Comparator.comparingLong(season -> season.getSeasonEnd().getTimeInMillis()))
        .orElse(null);
  }

  @Id
  @Column(name = "season_id", nullable = false)
  private short id;

  @Column(name = "season_name", nullable = false, length = 21)
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
  @LazyCollection(LazyCollectionOption.EXTRA)
  @OrderColumn
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
    return getId() == season.getId() && getSeasonName().equals(season.getSeasonName()) && getSeasonStart().equals(season.getSeasonStart()) && getSeasonEnd().equals(season.getSeasonEnd());
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