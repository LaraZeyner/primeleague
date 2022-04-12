package de.xeri.league.models.ids;

import java.io.Serializable;
import java.util.Objects;

public class SeasonEloId implements Serializable {
  private static final long serialVersionUID = 8396135638998617448L;

  private String account;
  private short season;

  // default constructor
  public SeasonEloId() {
  }

  public SeasonEloId(String account, short season) {
    this.account = account;
    this.season = season;
  }

  //<editor-fold desc="getter and setter">
  public short getSeason() {
    return season;
  }

  public void setSeason(short season) {
    this.season = season;
  }

  public String getAccount() {
    return account;
  }

  public void setAccount(String account) {
    this.account = account;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SeasonEloId)) return false;
    final SeasonEloId seasonEloId = (SeasonEloId) o;
    return getSeason() == seasonEloId.getSeason() && getAccount().equals(seasonEloId.getAccount());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getAccount(), getSeason());
  }

  @Override
  public String toString() {
    return "SeasonEloId{" +
        "account='" + account + '\'' +
        ", season='" + season + '\'' +
        '}';
  }
  //</editor-fold>
}