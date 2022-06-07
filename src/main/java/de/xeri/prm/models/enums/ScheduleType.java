package de.xeri.prm.models.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Created by Lara on 29.03.2022 for TRUES
 */
@Getter
@RequiredArgsConstructor
@ToString
public enum ScheduleType {
  SIGN_IN(1_638_000_000, "Sign In"),
  VORRUNDE_1(2_700_000, "Vorrunde 1"),
  VORRUNDE_2(2_700_000, "Vorrunde 2"),
  VORRUNDE_3(2_700_000, "Vorrunde 3"),
  VORRUNDE_4(2_700_000, "Vorrunde 4"),
  VORRUNDE_5(2_700_000, "Vorrunde 5"),
  VORRUNDE_6(2_700_000, "Vorrunde 6"),
  VORRUNDE_7(2_700_000, "Vorrunde 7"),
  VORRUNDE_8(2_700_000, "Vorrunde 8"),
  VORRUNDE_9(2_700_000, "Vorrunde 9"),
  VORRUNDE_10(2_700_000, "Vorrunde 10"),
  SPIELTAG_1(5_400_000, "Spieltag 1"),
  SPIELTAG_2(5_400_000, "Spieltag 2"),
  SPIELTAG_3(5_400_000, "Spieltag 3"),
  SPIELTAG_4(5_400_000, "Spieltag 4"),
  SPIELTAG_5(5_400_000, "Spieltag 5"),
  SPIELTAG_6(5_400_000, "Spieltag 6"),
  SPIELTAG_7(5_400_000, "Spieltag 7"),
  SPIELTAG_8(5_400_000, "Spieltag 8"),
  TIEBREAKER(2_700_000, "Tiebreaker"),
  PLAYOFF_1(8_100_000, "Playoff 1"),
  PLAYOFF_2(8_100_000, "Playoff 2"),
  PLAYOFF_3(8_100_000, "Playoff 3"),
  PLAYOFF_4(8_100_000, "Playoff 4"),
  PLAYOFF_5(8_100_000, "Playoff 5"),
  CLASH(8_100_000, "Clash"),
  SCRIM_BO1(2_700_000, "Best of 1"),
  SCRIM_BO2(5_400_000, "Best of 2"),
  SCRIM_BO3(8_100_000, "Best of 3"),
  SCRIM_BO4(10_800_000, "Best of 4"),
  SCRIM_BO5(13_500_000, "Best of 5");

  private final int duration;
  private final String displayname;
}
