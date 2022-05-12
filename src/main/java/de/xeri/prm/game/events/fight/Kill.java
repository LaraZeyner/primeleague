package de.xeri.prm.game.events.fight;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.xeri.prm.game.events.location.Position;
import de.xeri.prm.models.enums.EventTypes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Lara on 01.05.2022 for web
 */
@Getter
@Setter
@AllArgsConstructor
public class Kill {

  public static Kill getKillFromEvent(JSONObject event) {
    val type = EventTypes.valueOf(event.getString("type"));
    if (type.equals(EventTypes.CHAMPION_KILL)) {
      final int timestamp = event.getInt("timestamp");
      val positionObject = event.getJSONObject("position");
      final int xCoordinate = positionObject.getInt("x");
      final int yCoordinate = positionObject.getInt("y");
      val position = new Position((short) xCoordinate, (short) yCoordinate);

      final int victim = event.getInt("victimId");
      final int killer = event.getInt("killerId");
      final int shutdown = event.has("shutdownBounty") ? event.getInt("shutdownBounty") : 0;
      final int gold = shutdown + event.getInt("bounty");
      Map<Integer, Integer> participants = new HashMap<>();
      if (event.has("assistingParticipantIds")) {
        participants = event.getJSONArray("assistingParticipantIds").toList()
            .stream().map(id -> (Integer) id).collect(Collectors.toMap(Function.identity(), e -> 0));
      }

      val damageReceived = event.getJSONArray("victimDamageReceived");
      handleDamageValues(participants, damageReceived);
      if (event.has("victimDamageDealt")) {
        val damageDealt = event.getJSONArray("victimDamageDealt");
        handleDamageValues(participants, damageDealt);
      }

      if (victim != 0 && !participants.containsKey(victim)) {
        participants.put(victim, 0);
      }

      if (killer != 0 && !participants.containsKey(killer)) {
        participants.put(killer, 0);
      }

      if (killer != 0 || !participants.isEmpty()) {
        return new Kill(timestamp, position, killer, victim, participants, gold);
      }
    }
    return null;
  }
  private static void handleDamageValues(Map<Integer, Integer> participants, JSONArray damage) {
    for (int i = 0; i < damage.length(); i++) {
      val damageObject = damage.getJSONObject(i);
      final int magicalDamage = damageObject.getInt("magicDamage");
      final int physicalDamage = damageObject.getInt("physicalDamage");
      final int trueDamage = damageObject.getInt("trueDamage");
      final int totalDamage = magicalDamage + physicalDamage + trueDamage;

      final int partId = damageObject.getInt("participantId");

      if (participants.containsKey(partId)) {
        participants.put(partId, participants.get(partId) + totalDamage);
      } else if (partId != 0) {
        participants.put(partId, totalDamage);
      }
    }
  }

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
