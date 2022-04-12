package de.xeri.league.loader;

import java.util.Arrays;

import de.xeri.league.models.dynamic.Summonerspell;
import de.xeri.league.util.Data;
import de.xeri.league.util.io.JSONElement;
import de.xeri.league.util.io.JSONParser;
import de.xeri.league.util.io.json.JSON;
import org.json.JSONObject;

/**
 * Created by Lara on 29.03.2022 for TRUES
 */
public final class SpellLoader {
  private static final JSON json = Data.getInstance().getRequester().requestJSON("http://ddragon.leagueoflegends.com/cdn/12.6.1/data/en_US/summoner.json");

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
