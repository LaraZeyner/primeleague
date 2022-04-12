package de.xeri.league.util.io.json;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Lara on 29.03.2022 for TRUES
 */
public class JSON {
  private String json;
  private JSONType type;

  public JSON(String url) throws IOException {
    final Scanner scanner = new Scanner(new URL(url).openStream(), "UTF-8").useDelimiter("\\A");
    validate(scanner.hasNext() ? scanner.next() : "");
    scanner.close();
  }

  private void validate(String json) {
    if (json.isEmpty()) {
      Logger.getLogger("JSON").severe("Data not found!");
    } else {
      final char lastChar = json.charAt(json.length() - 3);
      if (json.startsWith("[") && (json.endsWith("]") || lastChar ==']')) {
        this.json = json;
        this.type = JSONType.JSONArray;
      } else if (json.startsWith("{") && (json.endsWith("}") || lastChar =='}')) {
        this.json = json;
        this.type = JSONType.JSONObject;
      } else {
        Logger.getLogger("JSON").severe("Data not a JSON!");
      }
    }
  }

  public Object get() {
    if (json != null) {
      if (type == JSONType.JSONObject) {
        return new JSONObject(json);
      } else if (type == JSONType.JSONArray) {
        return new JSONObject(json);
      } else {
        throw new IllegalArgumentException("JSON: Type does not exist!");
      }
    } else {
      Logger.getLogger("JSON").severe("Data not a JSON!");
    }
    return null;
  }

  public JSONType getType() {
    return type;
  }

  public JSONObject getJSONObject() {
    return type == JSONType.JSONObject ? new JSONObject(json) : null;
  }

  public JSONArray getJSONArray() {
    return type == JSONType.JSONArray ? new JSONArray(json) : null;
  }

}
