package de.xeri.prm.util.io;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Lara on 30.03.2022 for TRUES
 */
public class JSONElement extends JSONParserImpl {
  private final JSONObject json;

  public JSONElement(JSONObject json) {
    this.json = json;
  }

  public JSONArray getArray(String query) {
    return (json.has(query)) ? json.getJSONArray(query) : null;
  }

  public JSONObject getObject(String query) {
    return (json.has(query)) ? json.getJSONObject(query) : null;
  }

  private JSONObject getObject(String query, JSONObject object) {
    return (object.has(query)) ? object.getJSONObject(query) : null;
  }

  public boolean contains(String query, List<String> values) {
    final JSONArray array = getArray(query);
    for (Object element : array) for (String value : values) if (String.valueOf(element).equals(value)) return true;
    return false;
  }

  public JSONObject getSubObject(String query) {
    final String[] objects = query.split("\\.");
    JSONObject jsonObject = getObject(objects[0]);

    if (jsonObject != null && objects.length > 1) {
      for (int i = 1, objectsLength = objects.length; i < objectsLength; i++) {
        if (jsonObject != null) jsonObject = getObject(objects[i], jsonObject);
        else return null;
      }
    }
    return jsonObject;
  }

  public Object getSubParameter(DataType type, String query) {
    final String[] objects = query.split("\\.");
    JSONObject jsonObject = getObject(objects[0]);
    if (objects.length > 1) {
      for (int i = 1; i < objects.length-1; i++) {
        if (jsonObject != null) jsonObject = getObject(objects[i], jsonObject);
        else return null;
      }
      return get(type, objects[objects.length-1], jsonObject);
    } else {
      return json.get(objects[0]);
    }
  }

  public Object get(DataType type, String query) {
    return get(type, query, json);
  }

  private Object get(DataType type, String query, JSONObject object) {
    if (object.has(query)) {
      if (type == DataType.BOOLEAN) {
        return object.getBoolean(query);
      } else if (type == DataType.DOUBLE) {
        return object.getDouble(query);
      } else if (type == DataType.INTEGER) {
        return object.getInt(query);
      } else if (type == DataType.LONG) {
        return object.getLong(query);
      } else if (type == DataType.STRING) {
        return object.getString(query);
      }
      return object.get(query);
    }
    return null;
  }
}
