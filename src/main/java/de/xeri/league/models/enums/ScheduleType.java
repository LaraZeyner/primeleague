package de.xeri.league.models.enums;

/**
 * Created by Lara on 29.03.2022 for TRUES
 */
public enum ScheduleType {

  SIGN_IN(1_638_000_000),
  VORRUNDE_1(2_700_000),
  VORRUNDE_2(2_700_000),
  VORRUNDE_3(2_700_000),
  VORRUNDE_4(2_700_000),
  VORRUNDE_5(2_700_000),
  VORRUNDE_6(2_700_000),
  VORRUNDE_7(2_700_000),
  VORRUNDE_8(2_700_000),
  VORRUNDE_9(2_700_000),
  VORRUNDE_10(2_700_000),
  SPIELTAG_1(5_400_000),
  SPIELTAG_2(5_400_000),
  SPIELTAG_3(5_400_000),
  SPIELTAG_4(5_400_000),
  SPIELTAG_5(5_400_000),
  SPIELTAG_6(5_400_000),
  SPIELTAG_7(5_400_000),
  SPIELTAG_8(5_400_000),
  TIEBREAKER(2_700_000),
  PLAYOFF_1(8_100_000),
  PLAYOFF_2(8_100_000),
  PLAYOFF_3(8_100_000),
  PLAYOFF_4(8_100_000),
  PLAYOFF_5(8_100_000),
  CLASH(8_100_000),
  SCRIM_BO1(2_700_000),
  SCRIM_BO2(5_400_000),
  SCRIM_BO3(8_100_000),
  SCRIM_BO4(10_800_000),
  SCRIM_BO5(13_500_000),
  ABWESEND(86_400_000);

  private final int duration;

  ScheduleType(int duration) {
    this.duration = duration;
  }

  public int getDuration() {
    return duration;
  }
}
