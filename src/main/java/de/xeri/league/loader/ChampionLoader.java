package de.xeri.league.loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import de.xeri.league.models.dynamic.Ability;
import de.xeri.league.models.dynamic.Abilitystyle;
import de.xeri.league.models.dynamic.Champion;
import de.xeri.league.models.dynamic.Resource;
import de.xeri.league.models.enums.Abilitytype;
import de.xeri.league.models.enums.Championclass;
import de.xeri.league.manager.Data;
import de.xeri.league.util.io.DataType;
import de.xeri.league.util.io.JSONElement;
import de.xeri.league.util.io.JSONParser;
import de.xeri.league.util.io.json.JSON;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Lara on 29.03.2022 for TRUES
 */
public final class ChampionLoader {
  private static final JSON json = Data.getInstance().getRequester()
      .requestJSON("http://ddragon.leagueoflegends.com/cdn/12.6.1/data/en_US/championFull.json");

  public static void createChampions() {
    final JSONObject championsData = ((JSONElement) JSONParser.from(json)).getObject("data");

    for (String id : championsData.keySet()) {
      final JSONElement parser = JSONParser.from(championsData);
      final JSONObject championObject = championsData.getJSONObject(id);
      final short cId = Short.parseShort(championObject.getString("key"));
      final String name = championObject.getString("name");
      final Champion ch = new Champion(cId, name);

      final String title = championObject.getString("title");
      ch.setTitle(title);

      final JSONObject info = championObject.getJSONObject("info");
      final byte attack = (byte) info.getInt("attack");
      final byte defense = (byte) info.getInt("defense");
      final byte magic = (byte) info.getInt("magic");
      final JSONObject stats = championObject.getJSONObject("stats");
      final short health = (short) calculateSubStat(stats.getDouble("hp"), stats.getDouble("hpperlevel"));
      final double healthRegeneration = calculateSubStat(stats.getDouble("hpregen"), stats.getDouble("hpregenperlevel"));
      final short secondary = (short) calculateSubStat(stats.getDouble("mp"), stats.getDouble("mpperlevel"));
      final double secondaryRegeneration = calculateSubStat(stats.getDouble("mpregen"), stats.getDouble("mpregenperlevel"));
      final short moveSpeed = (short) stats.getInt("movespeed");
      final double resist = calculateSubStat(stats.getDouble("armor"), stats.getDouble("armorperlevel"));
      final short range = (short) stats.getInt("attackrange");
      final short damage = (short) calculateSubStat(stats.getDouble("attackdamage"), stats.getDouble("attackdamageperlevel"));
      final byte attackSpeed = (byte) calculateSubStat(stats.getDouble("attackspeed"), stats.getDouble("attackspeedperlevel") / 100);
      ch.setStats(attack, defense, magic, health, secondary, moveSpeed, resist, range, healthRegeneration, secondaryRegeneration, damage,
          attackSpeed);

      final String resourceString = String.valueOf(parser.getSubParameter(DataType.STRING, id + ".partype"));
      final Resource resource = Resource.get(new Resource(resourceString));
      ch.setResource(resource);
      resource.addChampion(ch);

      final Champion champion = Champion.get(ch);

      // Class
      championObject.getJSONArray("tags").forEach(clazz -> Arrays.stream(Championclass.values())
          .filter(championclazz -> championclazz.getName().equalsIgnoreCase(String.valueOf(clazz)))
          .findFirst().ifPresent(championclazz -> champion.getClasses().add(championclazz)));

      // Passive
      final JSONObject passiveObject = championObject.getJSONObject("passive");
      manageAbility(champion, passiveObject, Abilitytype.PASSIVE);

      // Abilities
      final JSONArray spells = championObject.getJSONArray("spells");
      if (!spells.isEmpty()) manageAbility(champion, spells.getJSONObject(0), Abilitytype.Q_SPELL);
      if (spells.length() > 1) manageAbility(champion, spells.getJSONObject(1), Abilitytype.W_SPELL);
      if (spells.length() > 2) manageAbility(champion, spells.getJSONObject(2), Abilitytype.E_SPELL);
      if (spells.length() > 3) manageAbility(champion, spells.getJSONObject(3), Abilitytype.ULTIMATE);
    }
  }

  private static double calculateSubStat(double base, double scale) {
    return base + scale * 12;
  }

  private static void manageAbility(Champion champion, JSONObject spell, Abilitytype abilitytype) {
    final String cooldown = abilitytype.equals(Abilitytype.PASSIVE) ? null : spell.getString("cooldownBurn");
    final String cost = abilitytype.equals(Abilitytype.PASSIVE) ? null : spell.getString("costBurn");
    final String range = abilitytype.equals(Abilitytype.PASSIVE) ? null : spell.getString("rangeBurn");
    final String value = abilitytype.equals(Abilitytype.PASSIVE) ? null : spell.getJSONArray("effectBurn").getString(1);

    final Ability ability = Ability.get(new Ability(champion, abilitytype, cooldown, cost, range, value));
    ability.setAbilityRange(range);
    ability.setCooldown(cooldown);
    ability.setResourceCost(cost);
    ability.setValue(value);

    updateTags(spell, ability);
    champion.addAbility(ability);
  }

  private static void updateTags(JSONObject spell, Ability ability) {
    final String description = spell.getString("description");
    final List<String> tags = new ArrayList<>();
    if (description.contains("<status>")) {
      final String[] strings1 = description.split("status>");
      IntStream.range(0, strings1.length).filter(i -> i % 2 == 1)
          .mapToObj(i -> strings1[i]).map(s2 -> s2.split("</status>"))
          .filter(strings -> strings.length > 0).map(strings -> strings[0])
          .forEach(tags::add);
    }

    if (description.contains("<")) {
      Arrays.stream(description.split("<"))
          .filter(s -> s.contains(">")).map(s -> s.split(">")).map(split -> split[0])
          .filter(s1 -> !s1.startsWith("/") && !s1.equals("status") &&
              tags.stream().noneMatch(s1::equalsIgnoreCase))
          .forEach(tags::add);
    }

    if (spell.has("leveltip"))
      spell.getJSONObject("leveltip").getJSONArray("label").forEach(label -> tags.add(String.valueOf(label)));

    for (String tag : tags) {
      tag = updateTagName(tag);
      if (!Abilitystyle.has(tag) && tag.length() > 3 &&
          !tag.contains("KEYWORD") &&
          !tag.contains("CHARGE") &&
          !tag.contains("STATS") &&
          !tag.contains("FURY") &&
          !tag.contains("COOLDOWN") &&
          !tag.contains("SPELLNAME") &&
          !tag.contains("VOIDLING") &&
          !tag.contains("BULLET") &&
          !tag.contains("DISTANCE") &&
          !tag.contains("SPIDERLING") &&
          !tag.contains("OF") &&
          !tag.contains("WALKERS") &&
          !tag.contains("SLAM") &&
          !tag.contains("FACTION") &&
          !tag.contains("RANGE") &&
          !tag.contains("SOULFLARE") &&
          !tag.contains("FLIGHT") &&
          !tag.contains("MONSTER") &&
          !tag.contains("MAINTEXT") &&
          !tag.contains("EVOLUTION") &&
          !tag.contains("SPELL") &&
          !tag.contains("DAISY") &&
          !tag.contains("REDUCE") &&
          !tag.contains("ARROWS") &&
          !tag.contains("TRAP") &&
          !tag.contains("COST") &&
          !tag.contains("FONT COLOR")) {
        final Abilitystyle abilitystyle = Abilitystyle.get(new Abilitystyle(tag));
        ability.addAbilitystyle(abilitystyle);
      }
    }
  }

  private static String updateTagName(String tag) {
    tag = tag.toUpperCase();
    final boolean percentage = (tag.contains("%") || tag.contains("PERCENT"));
    tag = tag.replace("PERCENT", "")
        .replace("PERCENTAGE", "")
        .replace("%", "");

    tag = tag.replace("ACTIVE", "")
        .replace("DRAIN", "")
        .replace("DURATION", "")
        .replace("LENGTH", "")
        .replace("AD", "PHYSICAL DAMAGE")
        .replace("AP", "MAGICAL DAMAGE")
        .replace("PASSIVE", "")
        .replace("MINIMUM", "")
        .replace("SCALE", "")
        .replace("MAXIMUM", "")
        .replace("BASE", "")
        .replace("TOTAL", "")
        .replace("(", "")
        .replace(")", "")
        .replace("<", "")
        .replace("/", "")
        .replace("EMPOWERED", "")
        .replace("ARMOR AND MAGIC RESISTANCE", "RESISTANCES")
        .replace("ARMOR AND MAGIC RESIST", "RESISTANCES")
        .replace("DEFENSE", "RESISTANCES")
        .replace("REDUCTION", "PENETRATION")
        .replace("RESISTS", "RESISTANCES")
        .replace("ADAPTIVE FORCE", "DAMAGE")
        .replace("GAIN", "")
        .replace("RATIO", "")
        .replace("BONUS", "")
        .replace("SHRED", "PENETRATION")
        .replace("MAGIC PEN", "MAGIC PENETRATION")
        .replace("WIDTH", "");


    if (tag.contains("DAMAGE")) {
      if (tag.contains("PHYSIC")) tag = "PHYSICAL DAMAGE";
      if (tag.contains("MAGIC")) tag = "MAGICAL DAMAGE";
      else tag = "DAMAGE";
    }
    if (tag.contains("ATTACK SPEED")) tag = "ATTACK SPEED";
    if (tag.contains("MOVE SPEED")) tag = "MOVE SPEED";
    if (tag.contains("HEAL")) tag = "HEALING";
    if (tag.contains("MANA")) tag = "MANA";
    if (tag.contains("ENERGY")) tag = "ENERGY";
    if (tag.contains("RESISTANCES PEN")) tag = "PENETRATION";
    if (tag.contains("SLOW")) tag = "SLOW";
    if (tag.contains("ROOT")) tag = "ROOT";
    if (tag.contains("GOLD") || tag.contains("PLUNDER")) tag = "GOLD";
    if (tag.contains("ARMOR PEN")) tag = "ARMOR PENETRATION";
    if (tag.contains("SHIELD")) tag = "SHIELD";
    if (tag.contains("MAGIC PEN")) tag = "MAGIC PENETRATION";
    if (tag.contains("LIFESTEAL") || tag.contains("VAMP")) tag = "LIFESTEAL";
    if (tag.contains("KNOCK") || tag.contains("PULL") || tag.contains("DRAG")) tag = "AIRBORNE";
    if (tag.contains("SLOW")) tag = "SLOW";
    if (tag.startsWith(" ")) tag = tag.substring(1);
    if (tag.endsWith(" ")) tag = tag.substring(0, tag.length() - 1);
    if (percentage) tag = "PERCENTAGE " + tag;
    return tag;
  }


}
