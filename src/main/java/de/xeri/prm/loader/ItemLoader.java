package de.xeri.prm.loader;

import java.util.List;
import java.util.stream.Collectors;

import de.xeri.prm.manager.PrimeData;
import de.xeri.prm.models.dynamic.Item;
import de.xeri.prm.models.dynamic.ItemStat;
import de.xeri.prm.models.dynamic.Itemstyle;
import de.xeri.prm.models.enums.ItemType;
import de.xeri.prm.util.io.DataType;
import de.xeri.prm.util.io.JSONElement;
import de.xeri.prm.util.io.JSONParser;
import de.xeri.prm.util.io.json.HTML;
import de.xeri.prm.util.io.json.JSON;
import lombok.val;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;

/**
 * Created by Lara on 29.03.2022 for TRUES
 */
public final class ItemLoader {
  private static final JSON json = PrimeData.getInstance().getRequester().requestJSON("http://ddragon.leagueoflegends.com/cdn/" + PrimeData.getInstance().getCurrentVersion() + "/data/en_US/item.json");

  public static void createItems() {
    val itemsObject = ((JSONElement) JSONParser.from(json)).getObject("data");

    for (String id : itemsObject.keySet()) {// create Item
      val itemObject = itemsObject.getJSONObject(id);
      String name = itemObject.getString("name");
      String description = "";
      if (name.contains("<br>")) {
        description = name.substring(name.split("<br>")[0].length());
        name = name.split("<br>")[0];
      }
      if (name.contains("</")) {
        name = name.split(">")[1].split("<")[0];
      }

      if (!Item.has(name) || Item.find(name).getId() == Short.parseShort(id)) {
        description = description + itemObject.getString("description");
        val finalDescription = determineDescription(description);
        String shortDescription = itemObject.getString("plaintext");
        if (shortDescription.equals("")) shortDescription = description.length() > 250 ? description.substring(0, 250) : description;
        final short cost = (short) ((int) JSONParser.from(itemObject).getSubParameter(DataType.INTEGER, "gold.total"));
        val tags = JSONParser.from(itemObject).getArray("tags");
        val type = getItemType(description, cost, tags, itemObject);
        val item = Item.get(new Item(Short.parseShort(id), type, name, finalDescription, shortDescription, cost));

        // Stats
        val doc = Jsoup.parse(description);
        val statString = doc.select("maintext").select("stats").html();
        val stats = new HTML(statString).find("br", null, false).stream().map(HTML::toString).collect(Collectors.toList());

        for (String s : stats) {
          stats.set(stats.indexOf(s), s.replace("<attention>", "")
              .replace("</attention>", "")
              .replace("<ornnbonus>", "")
              .replace("</ornnbonus>", "")
              .replace("\n", "")
              .replace("  ", " "));
        }

        for (String statId : stats) {
          val statStr = (statId.startsWith(" ")) ? statId.substring(1) : statId;
          val statStringOfStat = statStr.split(" ")[0];
          if (!statStringOfStat.isEmpty()) {
            val statNameKey = statStr.substring(statStringOfStat.length() + 1);

            final double statDouble = (statStringOfStat.endsWith("%")) ?
                Double.parseDouble(statStringOfStat.replace("%", "")) / 100 : Double.parseDouble(statStringOfStat);

            val stat = createItemStat(itemObject, statNameKey, statDouble);
            item.addItemStat(stat, statDouble);
          }
        }

        // Styles
        for (Object tag : tags.toList()) {
          val tagString = String.valueOf(tag);
          val itemstyle = Itemstyle.get(new Itemstyle(tagString));
          item.addItemStyle(itemstyle);
        }
      }
    }
  }

  private static ItemStat createItemStat(JSONObject itemObject, String statNameKey, double statDouble) {
    val itemStat = ItemStat.get(new ItemStat(statNameKey));
    if (!ItemStat.has(statNameKey) || ItemStat.has(statNameKey) && ItemStat.find(statNameKey).getName() == null) {
      final JSONObject statsListing = JSONParser.from(itemObject).getObject("stats");
      final List<String> possibleMatches = statsListing.keySet().stream()
          .filter(statName -> statsListing.getDouble(statName) == statDouble)
          .collect(Collectors.toList());
      if (possibleMatches.size() == 1 && itemStat.getName() == null) itemStat.setName(possibleMatches.get(0));
    }
    return itemStat;
  }

  private static String determineDescription(String description) {
    String text = new HTML(description).readTag("mainText").toString();
    text = (text.split("</stats>").length > 1) ? text.split("</stats>")[1] : "";
    text = text.replace("<br>", "\n")
        .replace("<li>", "\n -> ")
        .replace("<rules>", "")
        .replace("active>", "ac>")
        .replace("scaleAD>", "ad>")
        .replace("attackDamage>", "ad>")
        .replace("scaleAP>", "ap>")
        .replace("scaleArmor>", "ar>")
        .replace("attackSpeed>", "as>")
        .replace("passive>", "b>")
        .replace("attention>", "b>")
        .replace("p>", "b>")
        .replace("status>", "b>")
        .replace("flavorText>", "ft>")
        .replace("rarityGeneric>", "ge>")
        .replace("goldGain>", "go>")
        .replace("scaleHealth>", "hp>")
        .replace("rarityLegendary>", "lg>")
        .replace("healing>", "ls>")
        .replace("lifeSteal>", "ls>")
        .replace("scaleLethality>", "lt>")
        .replace("scaleLevel>", "lv>")
        .replace("scaleMana>", "ma>")
        .replace("magicDamage>", "md>")
        .replace("magicdamage>", "md>")
        .replace("keywordMajor>", "mj>")
        .replace("scaleMR>", "mr>")
        .replace("rarityMythic>", "m>")
        .replace("OnHit>", "oh>")
        .replace("physicalDamage>", "pd>")
        .replace("physicaldamage>", "pd>")
        .replace("shield>", "sh>")
        .replace("speed>", "sp>")
        .replace("keywordStealth>", "st>")
        .replace("trueDamage>", "td>")
        .replace("truedamage>", "td>")
        .replace("unique>", "uq>")
        .replace("</rules>", "");
    return text.replace("(based on level)", "");
  }

  private static ItemType getItemType(String description, short cost, JSONArray tags, JSONObject itemObject) {
    val from = JSONParser.from(itemObject).getArray("from");
    val into = JSONParser.from(itemObject).getArray("into");
    final ItemType type;
    if (tags.toList().contains("Boots")) {
      type = ItemType.BOOTS;
    } else if (tags.toList().contains("Consumable") && cost <= 500) {
      type = ItemType.CONSUMABLE;
    } else if (from == null || from.isEmpty()) {
      type = into == null || into.isEmpty() ? (tags.toList().contains("Trinket")) ? ItemType.TRINKET : ItemType.STARTING : ItemType.BASIC;
    } else if (into == null || into.isEmpty()) {
      type = (description.contains("Mythic Passive")) ? ItemType.MYTHIC : ItemType.LEGENDARY;
    } else {
      type = ItemType.EPIC;

    }
    return type;
  }

}
