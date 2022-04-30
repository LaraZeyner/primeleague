package de.xeri.league.servlet.datatables.objects;

/**
 * Created by Lara on 24.04.2022 for web
 */
public class PickBanObject {
  private String pick;
  private boolean confirmed;
  private String ban;
  private String pickOtherTeam;
  private boolean confirmedOther;

  public PickBanObject(String pick, boolean confirmed, String ban, String pickOtherTeam, boolean confirmedOther) {
    this.pick = pick;
    this.confirmed = confirmed;
    this.ban = ban;
    this.pickOtherTeam = pickOtherTeam;
    this.confirmedOther = confirmedOther;
  }

  public String getPick() {
    return pick;
  }

  public void setPick(String pick) {
    this.pick = pick;
  }

  public boolean isConfirmed() {
    return confirmed;
  }

  public void setConfirmed(boolean confirmed) {
    this.confirmed = confirmed;
  }

  public String getBan() {
    return ban;
  }

  public void setBan(String ban) {
    this.ban = ban;
  }

  public String getPickOtherTeam() {
    return pickOtherTeam;
  }

  public void setPickOtherTeam(String pickOtherTeam) {
    this.pickOtherTeam = pickOtherTeam;
  }

  public boolean isConfirmedOther() {
    return confirmedOther;
  }

  public void setConfirmedOther(boolean confirmedOther) {
    this.confirmedOther = confirmedOther;
  }
}
