package de.xeri.prm.loader;

import java.util.Arrays;

import de.xeri.prm.models.dynamic.Summonerspell;
import de.xeri.prm.manager.PrimeData;
import de.xeri.prm.util.io.JSONElement;
import de.xeri.prm.util.io.JSONParser;
import de.xeri.prm.util.io.json.JSON;
import org.json.JSONObject;

/**
 * Created by Lara on 29.03.2022 for TRUES
 */
public final class SpellLoader {
  private static final JSON json = PrimeData.getInstance().getRequester().requestJSON("http://ddragon.leagueoflegends.com/cdn/" + PrimeData.getInstance().getCurrentVersion() + "/data/en_US/summoner.json");

  public static void createItems() {
    final JSONObject spells = ((JSONElement) JSONParser.from(json)).getObject("data");

    for (String spellKey : spells.keySet()) {
      final JSONObject itemObject = spells.getJSONObject(spellKey);
      final String name = itemObject.getString("name");
      final byte id = Byte.parseByte(itemObject.getString("key"));
      if (JSONParser.from(itemObject).contains("modes", Arrays.asList("CLASSIC", "ARAM"))) {
        Summonerspell.get(new Summonerspell(id, name));
      }
    }
  }
}
