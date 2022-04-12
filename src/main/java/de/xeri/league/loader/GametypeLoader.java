package de.xeri.league.loader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.xeri.league.models.match.Gametype;
import de.xeri.league.models.dynamic.LeagueMap;
import de.xeri.league.util.Data;
import de.xeri.league.util.io.json.JSON;
import de.xeri.league.util.io.JSONList;
import de.xeri.league.util.io.JSONParser;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Lara on 29.03.2022 for TRUES
 */
public final class GametypeLoader {
  private static final JSON json = Data.getInstance().getRequester()
      .requestJSON("https://static.developer.riotgames.com/docs/lol/queues.json");
  private static final Map<String, LeagueMap> maps = new HashMap<>();
  private static final Map<Short, Gametype> gameTypes = new HashMap<>();

  public static void createTypes() {
    final List<LeagueMap> mapList = (List<LeagueMap>) Data.getInstance().getSession().createQuery("from LeagueMap").list();
    mapList.forEach(map -> maps.put(map.getName(), map));

    final List<Gametype> typeList = (List<Gametype>) Data.getInstance().getSession().createQuery("from Gametype").list();
    typeList.forEach(type -> gameTypes.put(type.getId(), type));

    final JSONArray types = ((JSONList) JSONParser.from(json)).getArray();
    manageEntry((short) -1, "Summoner's Rift", "Turnament");
    for (int i=0; i < types.length(); ++i) {
      final JSONObject modeObject = types.getJSONObject(i);
      if (modeObject.isNull("notes") || !modeObject.getString("notes").toLowerCase().contains("deprecated")) {
        final short queue = (short) modeObject.getInt("queueId");
        final String mapName = modeObject.getString("map");
        final String name = modeObject.isNull("description") ? null : modeObject.getString("description")
            .replace(" games", "");

        manageEntry(queue, mapName, name);
      }
    }

    maps.forEach((s, map) -> Data.getInstance().getSession().saveOrUpdate(map));
    gameTypes.forEach((i, gametype) -> Data.getInstance().getSession().saveOrUpdate(gametype));
  }

  private static void manageEntry(short queue, String mapName, String name) {
    if (mapName.equals("Custom games")) {
      mapName = "Summoner's Rift";
      name = "Custom";
    }


    final LeagueMap map = maps.get(mapName) == null ? new LeagueMap(mapName) : maps.get(mapName);
    final Gametype gametype = gameTypes.get(queue) == null ? new Gametype(queue, name) : gameTypes.get(queue);

    map.addGametype(gametype);
    maps.put(mapName, map);
    gameTypes.put(queue, gametype);
  }
}
