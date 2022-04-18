package de.xeri.league.models.league;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
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

import de.xeri.league.models.enums.Elo;
import de.xeri.league.models.ids.SeasonEloId;
import de.xeri.league.util.Data;
import de.xeri.league.util.Util;

@Entity(name = "SeasonElo")
@Table(name = "season_elo", indexes = @Index(name = "season", columnList = "season"))
@IdClass(SeasonEloId.class)
public class SeasonElo implements Serializable {

  @Transient
  private static final long serialVersionUID = 3295530440973862452L;

  private static Set<SeasonElo> data;

  public static void save() {
    if (data != null) data.forEach(Data.getInstance().getSession()::saveOrUpdate);
  }

  public static Set<SeasonElo> get() {
    if (data == null) data = new LinkedHashSet<>((List<SeasonElo>) Util.query("SeasonElo"));
    return data;
  }

  public static SeasonElo get(SeasonElo neu, Season season, Account account) {
    get();
    if (find(season, account) == null) data.add(neu);
    return find(season, account);
  }

  public static SeasonElo find(Season season, Account account) {
    get();
    return data.stream().filter(entry -> entry.getSeason().equals(season) && entry.getAccount().equals(account)).findFirst().orElse(null);
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

  public String getRank() {
    final Elo division = Elo.getDivision(mmr);
    int lp = mmr - division.getMmr();
    if (division.equals(Elo.GRANDMASTER)) lp+= 500;
    if (division.equals(Elo.CHALLENGER)) lp+= 1000;
    return division + " " + lp + " LP";
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