package de.xeri.league.loader;

import de.xeri.league.models.dynamic.Rune;
import de.xeri.league.models.dynamic.Runetree;
import de.xeri.league.util.Data;
import de.xeri.league.util.io.JSONList;
import de.xeri.league.util.io.JSONParser;
import de.xeri.league.util.io.json.JSON;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Lara on 29.03.2022 for TRUES
 */
public final class RuneLoader {
  private static final JSON json = Data.getInstance().getRequester()
      .requestJSON("http://ddragon.leagueoflegends.com/cdn/12.6.1/data/en_US/runesReforged.json");

  public static void createItems() {
    final JSONArray trees = ((JSONList) JSONParser.from(json)).getArray();
    for (int i = 0; i < trees.length(); i++) {
      final JSONObject tree = trees.getJSONObject(i);
      final short treeId = (short) tree.getInt("id");
      final String treeName = tree.getString("name");
      final String treeIcon = tree.getString("icon")
          .replace("perk-images/Styles/", "");
      final Runetree runetree = determineRunetree(treeId, treeName, treeIcon);

      final JSONArray slots = tree.getJSONArray("slots");
      for (int j = 0; j < slots.length(); j++) {
        final JSONObject slot = slots.getJSONObject(j);

        final JSONArray masteries = slot.getJSONArray("runes");
        for (int k = 0; k < masteries.length(); k++) {
          final JSONObject mastery = masteries.getJSONObject(k);
          final short runeId = (short) mastery.getInt("id");
          final byte runeSlot = (byte) (j * 10 + k + 1);
          final String runeName = mastery.getString("name");
          final String description = mastery.getString("longDesc")
              .replace("<br>", "\n")
              .replace("<lol-uikit-tooltipped-keyword key='LinkTooltip_Description_", "<")
              .replace("</lol-uikit-tooltipped-keyword>", "</key>");
          final String shortDescription = mastery.getString("shortDesc")
              .replace("<br>", "\n")
              .replace("<lol-uikit-tooltipped-keyword key='LinkTooltip_Description_", "<")
              .replace("</lol-uikit-tooltipped-keyword>", "</k>");
          final Rune rune = determineRune(runeId, runeSlot, runeName, description, shortDescription);
          runetree.addRune(rune);
        }
      }
    }
  }

  private static Rune determineRune(short runeId, byte runeSlot, String runeName, String description, String shortDescription) {
    final Rune rune = Rune.get(new Rune(runeId, runeSlot, runeName, description, shortDescription));
    rune.setSlot(runeSlot);
    rune.setName(runeName);
    rune.setDescription(description);
    rune.setShortDescription(shortDescription);
    return rune;
  }

  private static Runetree determineRunetree(short treeId, String treeName, String treeIcon) {
    final Runetree runetree = Runetree.get(new Runetree(treeId, treeName, treeIcon));
    runetree.setName(treeName);
    runetree.setIconURL(treeIcon);
    return runetree;
  }
}
