package de.xeri.league.loader;

import java.util.List;
import java.util.stream.Collectors;

import de.xeri.league.models.dynamic.Item;
import de.xeri.league.models.dynamic.ItemStat;
import de.xeri.league.models.dynamic.Itemstyle;
import de.xeri.league.models.enums.ItemType;
import de.xeri.league.util.Data;
import de.xeri.league.util.io.DataType;
import de.xeri.league.util.io.JSONElement;
import de.xeri.league.util.io.JSONParser;
import de.xeri.league.util.io.json.HTML;
import de.xeri.league.util.io.json.JSON;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Created by Lara on 29.03.2022 for TRUES
 */
public final class ItemLoader {
  private static final JSON json = Data.getInstance().getRequester().requestJSON("http://ddragon.leagueoflegends.com/cdn/12.6.1/data/en_US/item.json");

  public static void createItems() {
    final JSONObject items = ((JSONElement) JSONParser.from(json)).getObject("data");

    for (String id : items.keySet()) {// create Item
      final JSONObject itemObject = items.getJSONObject(id);
      final String name = itemObject.getString("name");
      if (!Item.has(name) || Item.find(name).getId() == Short.parseShort(id)) {
        final String description = itemObject.getString("description");
        final String finalDescription = determineDescription(description);
        String shortDescription = itemObject.getString("plaintext");
        if (shortDescription.equals("")) shortDescription = description.length() > 250 ? description.substring(0, 250) : description;
        final short cost = (short) ((int) JSONParser.from(itemObject).getSubParameter(DataType.INTEGER, "gold.total"));
        final JSONArray tags = JSONParser.from(itemObject).getArray("tags");
        final ItemType type = getItemType(description, cost, tags, itemObject);
        final Item item = Item.get(new Item(Short.parseShort(id), type, name, finalDescription, shortDescription, cost));

        // Stats
        final Document doc = Jsoup.parse(description);
        final String statString = doc.select("maintext").select("stats").html();
        final List<String> stats = new HTML(statString).find("br", null, false).stream()
            .map(HTML::toString)
            .collect(Collectors.toList());

        for (String s : stats) {
          stats.set(stats.indexOf(s), s.replace("<attention>", "")
              .replace("</attention>", "")
              .replace("\n", "")
              .replace("  ", " "));
        }

        for (String statId : stats) {
          final String statStr = (statId.startsWith(" ")) ? statId.substring(1) : statId;
          final String statStringOfStat = statStr.split(" ")[0];
          if (!statStringOfStat.isEmpty()) {
            final String statNameKey = statStr.substring(statStringOfStat.length() + 1);
            final double statDouble = (statStringOfStat.endsWith("%")) ?
                Double.parseDouble(statStringOfStat.replace("%", "")) / 100 : Double.parseDouble(statStringOfStat);

            final ItemStat stat = createItemStat(itemObject, statNameKey, statDouble);
            item.addItemStat(stat, statDouble);
          }
        }

        // Styles
        for (Object tag : tags.toList()) {
          final String tagString = String.valueOf(tag);
          final Itemstyle itemstyle = Itemstyle.get(new Itemstyle(tagString));
          item.addItemStyle(itemstyle);
        }
      }
    }
  }

  private static ItemStat createItemStat(JSONObject itemObject, String statNameKey, double statDouble) {
    final ItemStat stat = ItemStat.get(new ItemStat(statNameKey));
    if (!ItemStat.has(statNameKey) || ItemStat.has(statNameKey) && ItemStat.find(statNameKey).getName() == null) {
      final JSONObject statsListing = JSONParser.from(itemObject).getObject("stats");
      final List<String> possibleMatches = statsListing.keySet().stream()
          .filter(statName -> statsListing.getDouble(statName) == statDouble)
          .collect(Collectors.toList());
      if (possibleMatches.size() == 1 && stat.getName() == null) stat.setName(possibleMatches.get(0));
    }
    return stat;
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
    final JSONArray from = JSONParser.from(itemObject).getArray("from");
    final JSONArray into = JSONParser.from(itemObject).getArray("into");
    final ItemType type;
    if (tags.toList().contains("Boots")) type = ItemType.BOOTS;
    else if (tags.toList().contains("Consumable") && cost <= 500) type = ItemType.CONSUMABLE;
    else if (from == null || from.isEmpty()) {
      if (into == null || into.isEmpty()) type = (tags.toList().contains("Trinket")) ? ItemType.TRINKET : ItemType.STARTING;
      else type = ItemType.BASIC;
    } else {
      if (into == null || into.isEmpty()) type = (description.contains("Mythic Passive")) ? ItemType.MYTHIC : ItemType.LEGENDARY;
      else type = ItemType.EPIC;
    }
    return type;
  }

}
