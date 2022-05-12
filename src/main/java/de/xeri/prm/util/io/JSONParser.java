package de.xeri.prm.util.io;

import de.xeri.prm.util.io.json.JSON;
import de.xeri.prm.util.io.json.JSONType;
import org.json.JSONObject;

/**
 * Created by Lara on 29.03.2022 for TRUES
 */
public final class JSONParser {
  public static JSONElement from(JSONObject jsonObject) {
    return new JSONElement(jsonObject);
  }

  public static JSONParserImpl from(JSON jsonObject) {
    if (jsonObject.getType().equals(JSONType.JSONObject)) {
      return new JSONElement(jsonObject.getJSONObject());
    } else {
      return new JSONList(jsonObject.getJSONArray());
    }
  }
}
