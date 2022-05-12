package de.xeri.prm.models.league;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.prm.models.enums.Elo;
import de.xeri.prm.models.ids.SeasonEloId;
import de.xeri.prm.manager.Data;
import de.xeri.prm.util.HibernateUtil;
import org.hibernate.annotations.NamedQuery;

@Entity(name = "SeasonElo")
@Table(name = "season_elo", indexes = @Index(name = "season", columnList = "season"))
@IdClass(SeasonEloId.class)
@NamedQuery(name = "SeasonElo.findAll", query = "FROM SeasonElo s")
@NamedQuery(name = "SeasonElo.findBy", query = "FROM SeasonElo s WHERE account = :account AND season = :season")
public class SeasonElo implements Serializable {

  @Transient
  private static final long serialVersionUID = 3295530440973862452L;

  public static Set<SeasonElo> get() {
    return new LinkedHashSet<>(HibernateUtil.findList(SeasonElo.class));
  }

  public static SeasonElo get(SeasonElo neu, Season season, Account account) {
    if (has(season, account)) {
      final SeasonElo elo = find(season, account);
      elo.setMmr(neu.getMmr());
      elo.setWins(neu.getWins());
      elo.setLosses(neu.getLosses());
      return elo;
    }
    season.addSeaonElo(neu);
    account.addSeasonElo(neu);
    Data.getInstance().save(neu);
    return neu;
  }

  public static boolean has(Season season, Account account) {
    return HibernateUtil.has(SeasonElo.class, new String[]{"account", "season"}, new Object[]{account, season});
  }

  public static SeasonElo find(Season season, Account account) {
    return HibernateUtil.find(SeasonElo.class, new String[]{"account", "season"}, new Object[]{account, season});
  }

  @Id
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "account")
  private Account account;

  @Id
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "season")
  private Season season;

  @Column(name = "mmr", nullable = false)
  private short mmr;

  @Column(name = "wins", nullable = false)
  private short wins;

  @Column(name = "losses", nullable = false)
  private short losses;

  // default constructor
  public SeasonElo() {
  }

  public SeasonElo(short mmr, short wins, short losses) {
    this.mmr = mmr;
    this.wins = wins;
    this.losses = losses;
  }

  public Elo getElo() {
    return Elo.getDivision(mmr);
  }

  public String getRank() {
    int lp = mmr - getElo().getMmr();
    if (getElo().equals(Elo.GRANDMASTER)) lp+= 500;
    if (getElo().equals(Elo.CHALLENGER)) lp+= 1000;
    return getElo() + " " + lp + " LP";
  }

  public short getGames() {
    return (short) (wins + losses);
  }

  public float getRatio() {
    return wins * 1f / getGames();
  }

  //<editor-fold desc="getter and setter">
  public short getLosses() {
    return losses;
  }

  public void setLosses(short losses) {
    this.losses = losses;
  }

  public short getWins() {
    return wins;
  }

  public void setWins(short wins) {
    this.wins = wins;
  }

  public short getMmr() {
    return mmr;
  }

  public void setMmr(short mmr) {
    this.mmr = mmr;
  }

  public Season getSeason() {
    return season;
  }

  void setSeason(Season season) {
    this.season = season;
  }

  public Account getAccount() {
    return account;
  }

  void setAccount(Account account) {
    this.account = account;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SeasonElo)) return false;
    final SeasonElo seasonElo = (SeasonElo) o;
    return getMmr() == seasonElo.getMmr() && getWins() == seasonElo.getWins() && getLosses() == seasonElo.getLosses() && getAccount().equals(seasonElo.getAccount()) && getSeason().equals(seasonElo.getSeason());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getAccount(), getSeason(), getMmr(), getWins(), getLosses());
  }

  @Override
  public String toString() {
    return "SeasonElo{" +
        "account=" + account +
        ", season=" + season +
        ", mmr=" + mmr +
        ", wins=" + wins +
        ", losses=" + losses +
        '}';
  }
  //</editor-fold>
}