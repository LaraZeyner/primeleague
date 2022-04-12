package de.xeri.league.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.xeri.league.models.dynamic.Item;
import de.xeri.league.models.dynamic.ItemStat;
import de.xeri.league.models.dynamic.Item_Stat;
import de.xeri.league.models.dynamic.Itemstyle;
import de.xeri.league.models.enums.ItemType;
import de.xeri.league.util.Data;
import de.xeri.league.util.io.DataType;
import de.xeri.league.util.io.json.HTML;
import de.xeri.league.util.io.json.JSON;
import de.xeri.league.util.io.JSONElement;
import de.xeri.league.util.io.JSONParser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Created by Lara on 29.03.2022 for TRUES
 */
public final class ItemLoader {
  private static final JSON json = Data.getInstance().getRequester().requestJSON("http://ddragon.leagueoflegends.com/cdn/12.6.1/data/en_US/item.json");
  private static final List<Item_Stat> stats = new ArrayList<>();

  public static void addStat(Item_Stat stat) {
    stats.add(stat);
  }

  public static void createItems() {
    final List<ItemStat> itemStatList = (List<ItemStat>) Data.getInstance().getSession().createQuery("from Itemstat").list();
    final Map<String, ItemStat> databaseItemstats = itemStatList.stream()
        .collect(Collectors.toMap(ItemStat::getId, stat -> stat, (a, b) -> b));

    final List<Item> itemList = (List<Item>) Data.getInstance().getSession().createQuery("from Item").list();
    final Map<String, Item> databaseItems = new HashMap<>();

    final List<Itemstyle> itemStyleList = (List<Itemstyle>) Data.getInstance().getSession().createQuery("from Itemstyle").list();
    final Map<String, Itemstyle> databaseItemstyles = itemStyleList.stream()
        .collect(Collectors.toMap(Itemstyle::getName, style -> style, (a, b) -> b));

    final JSONObject items = ((JSONElement) JSONParser.from(json)).getObject("data");

    items.keySet().forEach(id -> {
      // create Item
      final JSONObject itemObject = items.getJSONObject(id);
      final String name = itemObject.getString("name");
      if (!databaseItems.containsKey(name)) {
        final String description = itemObject.getString("description");
        final String finalDescription = determineDescription(description);
        String shortDescription = itemObject.getString("plaintext");
        if (shortDescription.equals("")) shortDescription = description.length() > 250 ? description.substring(0, 250) : description;
        final short cost = (short) ((int) JSONParser.from(itemObject).getSubParameter(DataType.INTEGER, "gold.total"));
        final JSONArray tags = JSONParser.from(itemObject).getArray("tags");
        final ItemType type = getItemType(description, cost, tags, itemObject);
        final Item item = itemList.stream().filter(i -> i.getId() == Short.parseShort(id))
            .findFirst().orElse(new Item(Short.parseShort(id), type, name, finalDescription, shortDescription, cost));

        // Stats
        final Document doc = Jsoup.parse(description);
        final String statString = doc.select("maintext").select("stats").html();
        final List<String> stats = new HTML(statString).find("br", null, false)
            .stream().map(HTML::toString).collect(Collectors.toList());
        stats.forEach(stat -> stats.set(stats.indexOf(stat), stat.replace("<attention>", "")
            .replace("</attention>", "").replace("\n", "").replace("  ", " ")));
        stats.forEach(statId -> {
          final String statStringOfStat = statId.split(" ")[0];
          if (!statStringOfStat.isEmpty()) {
            final String statNameKey = statId.substring(statStringOfStat.length() + 1);
            final double statDouble = (statStringOfStat.endsWith("%")) ?
                Double.parseDouble(statStringOfStat.replace("%", "")) / 100 : Double.parseDouble(statStringOfStat);


            if (!databaseItemstats.containsKey(statNameKey) ||
                databaseItemstats.containsKey(statNameKey) && databaseItemstats.get(statNameKey).getName() == null) {
              final JSONObject statsListing = JSONParser.from(itemObject).getObject("stats");
              final List<String> possibleMatches = statsListing.keySet().stream()
                  .filter(statName -> statsListing.getDouble(statName) == statDouble)
                  .collect(Collectors.toList());
              if (!databaseItemstats.containsKey(statNameKey)) {
                final ItemStat stat = new ItemStat(statNameKey, null);
                if (possibleMatches.size() == 1) stat.setName(possibleMatches.get(0));
                databaseItemstats.put(statNameKey, stat);
              }
            }

            item.addItemStat(databaseItemstats.get(statNameKey), statDouble);
          }
        });

        // Styles
        tags.toList().forEach(tag -> {
          final String tagString = String.valueOf(tag);
          if (!databaseItemstyles.containsKey(tagString)) {
            databaseItemstyles.put(tagString, new Itemstyle(tagString));
          }
          final Itemstyle itemstyle = databaseItemstyles.get(tagString);
          item.addItemStyle(itemstyle);
        });
        databaseItems.put(name, item);
      }
    });
    databaseItemstats.forEach((s, itemStat) -> Data.getInstance().getSession().saveOrUpdate(itemStat));
    databaseItemstyles.forEach((s, itemstyle) -> Data.getInstance().getSession().saveOrUpdate(itemstyle));
    databaseItems.forEach((s, item) -> Data.getInstance().getSession().saveOrUpdate(item));
    stats.forEach(stat -> Data.getInstance().getSession().saveOrUpdate(stat));
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
