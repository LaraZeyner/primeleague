package de.xeri.league.models.match.neu;

/**
 * Created by Lara on 25.04.2022 for web
 */
// TODO Es soll für ALLE Spiele funktionieren
public final class StatText {
  public static final String OBJECTIVE_PRESSURE = "Generelle Kontrolle über Objectives <br /> " +
      "Stats: <br />" +
      " - Objective direkt nach Spawn <br />" +
      " - Gestohlene Objectives und Contest-Rate <br />" +
      " - Schaden gegen Objectives <br />" +
      " - Scuttlekontrolle gesamt <br />" +
      " - Jungler-Takedowns vor Objective";
  public static final String TOPSIDE_OBJECTIVES = "Rift Herald und Baron Kontrolle <br /> " +
      "Stats: <br />" +
      " - Baron-Zeit <br />" +
      " - Baron-Takedowns und Versuche <br />" +
      " - Baron-Powerplay <br />" +
      " - Herald-Turrets <br />" +
      " - Herald-Multicharge";
  public static final String BOTSIDE_OBJECTIVES = "Drachen Kontrolle <br /> " +
      "Stats: <br />" +
      " - Drachen-Zeit <br />" +
      " - Drachen-Takedowns <br />" +
      " - Elder-Zeit <br />" +
      " - Erster Drache <br />" +
      " - Soulrate und Perfekte Soul";
  public static final String WARDING = "Sichtwertung und Trinket <br />" +
      "Stats: <br />" +
      " - Vision Score + Advantage <br />" +
      " - Trinket Effizienz <br />" +
      " - 1. Ward platziert <br />" +
      " - Wards gecleart <br />" +
      " - Trinket-Wechsel Zeit";
  public static final String CONTROLWARDS = "Controlwards <br />" +
      "Stats: <br />" +
      " - Control-Wards platziert <br />" +
      " - Control-Wards beschützt <br />" +
      " - Control-Wards im gegnerisch. Jungle <br />" +
      " - 1. Control-Ward gekauft <br />" +
      " - Durchschnittliche Control-Ward Zeit";


  public static final String TURRET_PRESSURE = "Tower-Kontrolle und Splitpushing < br /> " +
      "Stats: <br />" +
      " - Erster Turm - Lead <br />" +
      " - Turret Platings <br />" +
      " - Turret Takedowns Earlygame <br />" +
      " - Turrets splitpushed <br />" +
      " - Towerbeteiligung";
  public static final String MACRO = "Macro < br /> " +
      "Stats: <br />" +
      " - Teleport Kills <br />" +
      " - Jungle Camps genommen <br />" +
      " - Midgame XP-Effizienz <br />" +
      " - Midgame Gold-Effizienz <br />" +
      " - Lategame XP/Gold-Lead";
  public static final String ROAMING = "Roameffizienz < br /> " +
      "Stats: <br />" +
      " - Miniongewinn pro Roam <br />" +
      " - XP-Effizienz pro Roam <br />" +
      " - Gold-Effizienz pro Roam <br />" +
      " - Roam-Nutzen <br />" +
      " - Objective Damage während Roam";
  public static final String GANKING = "Ganks < br /> " +
      "Stats: <br />" +
      " - Team Invades + Buffs genommen<br />" +
      " - Ganks Earlygame <br />" +
      " - Ganks gesamt/Proximity <br />" +
      " - Ganks Priorität (Top/Mid/Bot) <br />" +
      " - Gank Setups";
  public static final String DIVING = "Dives < br /> " +
      "Stats: <br />" +
      " - Dives Erfolgrate <br />" +
      " - Dives Disengagerate <br />" +
      " - Dives erfolgreich <br />" +
      " - Dives beschützt <br />" +
      " - Dives gestorben";


  public static final String DAMAGE = "Schadenbreakdown < br /> " +
      "Stats: <br />" +
      " - Teamschaden <br />" +
      " - Teamtankyness <br />" +
      " - Teamdurability <br />" +
      " - Healing <br />" +
      " - Zeit in Combat";
  public static final String PLAYMAKING = "Playmakingpotential < br /> " +
      "Stats: <br />" +
      " - Aggressiver Flash <br />" +
      " - Allins nach Levelup <br />" +
      " - Solo-Kills <br />" +
      " - Outplays <br />" +
      " - First-Blood Teilnahme";
  public static final String CATCHING = "Pickmaking < br /> " +
      "Stats: <br />" +
      " - Bounty erhalten <br />" +
      " - Assassination <br />" +
      " - Pick gemacht <br />" +
      " - Ambush <br />" +
      " - Duell Winrate";
  public static final String SNOWBALLING = "Snowball-Potential < br /> " +
      "Stats: <br />" +
      " - Kills Earlygame <br />" + // into win tag
      " - Tode Earlygame <br />" +
      " - Wins wenn ahead <br />" +
      " - Bounty Drop <br />" +
      " - Leadausbau"; // prozentual
  public static final String STRONG_PHASE = "Starke Spielphase < br /> " +
      "Stats: <br />" +
      " - Größter Lead Minute <br />" +
      " - Niedrigester Lead Minute <br />" +
      " - Comeback, wenn ahead <br />" +
      " - Comeback, wenn behind <br />" +
      " - XP-Lead";


  public static final String TEAMFIGHTING = "Teamfighting < br /> " +
      "Stats: <br />" +
      " - Multikills <br />" +
      " - Todesreihenfolge <br />" +
      " - Erfolgsrate <br />" +
      " - Aces early + cleane Fights <br />" +
      " - Schadensanteil";
  public static final String SKIRMISHING = "Skirmishing < br /> " +
      "Stats: <br />" +
      " - Anzahl Skrimishes <br />" +
      " - Killbilanz in Skirmishes <br />" +
      " - Erfolgsrate <br />" +
      " - Schaden pro Skrimish <br />" +
      " - Schadensanteil";
  public static final String EARLY_INCOME = "Gold Earlygame < br /> " +
      "Stats: <br />" +
      " - Early Lane Lead <br />" +
      " - Lane Lead <br />" +
      " - Erstes Full-Item <br />" +
      " - Early Creepscore <br />" +
      " - Farm/Supitem Efficiency";
  public static final String INCOME = "Einkommen < br /> " +
      "Stats: <br />" +
      " - Creeps pro Minute <br />" +
      " - XP pro Minute <br />" +
      " - Gold pro Minute <br />" +
      " - Creep Advantage <br />" +
      " - Support Quest";
  public static final String ITEMIZATION = "Items < br /> " +
      "Stats: <br />" +
      " - Legendary Items <br />" +
      " - Items gekauft <br />" +
      " - Mejais Zeit <br />" +
      " - Grievous Wounds/Pen Zeit" +
      " - Startitem Verkauf";


  public static final String SURVIVAL = "Survival Allgemein < br /> " +
      "Stats: <br />" +
      " - Spielzeit Anteil <br />" +
      " - ohne Sterben <br />" +
      " - knapp überlebt <br />" +
      " - Solo Tode <br />" +
      " - Risiko (Todesposition)";
  public static final String EARLY_SURVIVAL = "Survival Early < br /> " +
      "Stats: <br />" +
      " - 1. Tod <br />" +
      " - 1. Base durch Recall <br />" +
      " - Lane lead Verlust durch Tode <br />" +
      " - Lane lead Verlust ohne Tod" +
      " - Earlygame Tode";
  public static final String TEAM_UTILITY = "Team Utility < br /> " +
      "Stats: <br />" +
      " - Schaden geschildet <br />" +
      " - Crowd Control <br />" +
      " - Gegner unter Kontrolle <br />" +
      " - Verbündete gerettet <br />" +
      " - Utility Score";
  public static final String GANK_SURVIVAL_TIMEWASTING = "Gank überleben < br /> " +
      "Stats: <br />" +
      " - Gank Setup / Jungle Gap <br />" +
      " - Jungle Timewaste <br />" +
      " - Roams entdeckt <br />" +
      " - Gepickt worden <br />" +
      " - Gank Tode";
  public static final String ISOLATION = "Lane isolieren < br /> " +
      "Stats: <br />" +
      " - Minion Effizienz < br />" +
      " - XP Effizienz < br />" +
      " - Wards genutzt < br />" +
      " - Damage Trading < br />" +
      " - Reset Häufigkeit";


  public static final String PRE_FIRST_BASE = "Vor 1. Reset < br /> " +
      "Stats: <br />" +
      " - 1. Reset <br />" +
      " - Gegner unter Kontrolle <br />" +
      " - Buffs initial und Scuttlerotation <br />" +
      " - XP/Gold Lead <br />" +
      " - 1. Reset Gold";
  public static final String POST_FIRST_BASE = "Nach 1. Reset < br /> " +
      "Stats: <br />" +
      " - Resourcenverwendung <br />" +
      " - Consumables genutzt <br />" +
      " - 2. Reset Zeit <br />" +
      " - Gegner unter Kontrolle <br />" +
      " - Damage Percentage";
  public static final String LANE_BILANCE = "Laning Bilanz < br /> " +
      "Stats: <br />" +
      " - XP/Gold-Lead <br />" +
      " - Takedowns-Lead <br />" +
      " - Objective-Lead <br />" +
      " - Turret Plate-Lead <br />" +
      " - Gegner unter Kontrolle-Lead";
  public static final String PLAYSTYLE = "Spielweise < br /> " +
      "Stats: <br />" + // Lane Position
      " - Kills Position <br />" +
      " - Tode Position <br />" +
      " - Keyspells und Ultimate <br />" +
      " - Spellbilanz getroffen <br />" +
      " - Spellbilanz ausgewichen";
  public static final String RESETS = "Resets < br /> " +
      "Stats: <br />" +
      " - Resets durch Tod - Rate <br />" +
      " - Durchschnittliche Zeit <br />" +
      " - Durchschnittliche Goldmenge <br />" +
      " - Goldverlust durch Reset <br />" +
      " - Resets mit Team";


  public static final String GIVING_UP = "Forfeight < br /> " +
      "Stats: <br />" +
      " - FF-Rate <br />" +
      " - Lane lead, wenn gestorben <br />" +
      " - Farming advantage von Behind <br />" +
      " - Warding von Behind <br />" +
      " - Deaths von Behind";
  public static final String CONSISTENCY = "Konstanz < br /> " +
      "Stats: <br />" +
      " - Early Levelup-Lead <br />" +
      " - CS Diff von ahead <br />" +
      " - CS Diff von behind <br />" +
      " - XP Diff von ahead <br />" +
      " - XP Diff von behind";
  public static final String VERSATILTITY = "Versatilität < br /> " +
      "Stats: <br />" +
      " - Vision <br />" +
      " - Roaming <br />" +
      " - Aggression <br />" +
      " - Fighting <br />" +
      " - Survivability";
  public static final String ADAPTION = "Anpassung < br /> " +
      "Stats: <br />" +
      " - Anti-Healing <br />" +
      " - Penetration <br />" +
      " - Schaden <br />" +
      " - Resistenzen <br />" +
      " - Farmstop";
  public static final String STATS = "Stats < br /> " +
      "Stats: <br />" +
      " - Winrate <br />" +
      " - Kill Participation <br />" +
      " - Blue<br />" +
      " - Red<br />" +
      " - K D A";
}
