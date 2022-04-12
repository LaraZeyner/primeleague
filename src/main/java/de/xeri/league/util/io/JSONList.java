package de.xeri.league.util.io;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Lara on 30.03.2022 for TRUES
 */
public class JSONList extends JSONParserImpl {
  private final JSONArray json;

  public JSONList(JSONArray json) {
    this.json = json;
  }

  public JSONObject getObject(String query, JSONObject object) {
    return (object.has(query)) ? object.getJSONObject(query) : null;
  }

  public JSONArray getArray() {
    return json;
  }
}
