package de.xeri.prm.models.enums;

/**
 * Created by Lara on 07.04.2022 for web
 */
public enum QueueType {
  OTHER(-2),
  CLASH(700),
  TOURNEY(0);

  private final int queueId;

  QueueType(int queueId) {
    this.queueId = queueId;
  }

  public int getQueueId() {
    return queueId;
  }
}
