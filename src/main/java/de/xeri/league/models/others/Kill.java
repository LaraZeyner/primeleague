package de.xeri.league.models.others;

import java.util.Map;

import de.xeri.league.models.match.location.Position;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Lara on 01.05.2022 for web
 */
@Getter
@Setter
@AllArgsConstructor
public class Kill {
  private int timestamp;
  private Position position;
  private int killer;
  private int victim;
  private Map<Integer, Integer> participants;
  private int gold;

  public boolean isInvolved(int pId) {
    return killer == pId || victim == pId || participants.containsKey(pId);
  }
}
