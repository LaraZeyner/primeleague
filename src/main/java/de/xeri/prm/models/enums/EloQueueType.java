package de.xeri.prm.models.enums;

import java.util.Arrays;

/**
 * Created by Lara on 29.04.2022 for web
 */
public enum EloQueueType {
  RANKED_SOLO_5x5,
  RANKED_FLEX_SR;


  public static boolean has(String queue) {
    return Arrays.stream(EloQueueType.values()).anyMatch(eloQueueType -> eloQueueType.toString().equals(queue));
  }
}
