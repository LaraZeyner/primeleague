package de.xeri.prm.util.io.json;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import de.xeri.prm.util.logger.Logger;
import lombok.val;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Lara on 29.03.2022 for TRUES
 */
public class JSON {
  private String json;
  private JSONType type;

  public JSON(String url) throws IOException {
    val scanner = new Scanner(new URL(url).openStream(), "UTF-8").useDelimiter("\\A");
    validate(scanner.hasNext() ? scanner.next() : "");
    scanner.close();
  }

  private void validate(String json) {
    val logger = Logger.getLogger("JSON");
    if (json.isEmpty()) {
      logger.warning("Data not found!");

    } else if (json.length() > 2) {
      final char lastChar = json.charAt(json.length() - 3);

      if (json.startsWith("[") && (json.endsWith("]") || lastChar ==']')) {
        this.json = json;
        this.type = JSONType.JSONArray;

      } else if (json.startsWith("{") && (json.endsWith("}") || lastChar =='}')) {
        this.json = json;
        this.type = JSONType.JSONObject;

      } else {
        logger.severe("Data not a JSON!", json);
      }

    } else if (json.equals("[]")) {
      this.json = json;
      this.type = JSONType.JSONArray;

    } else {
      logger.severe("Data not a JSON!", json);
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
