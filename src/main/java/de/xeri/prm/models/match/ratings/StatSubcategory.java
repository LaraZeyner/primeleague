package de.xeri.prm.models.match.ratings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by Lara on 25.04.2022 for web
 */
@Getter
@AllArgsConstructor
@ToString
public enum StatSubcategory {
  OBJECTIVE_PRESSURE(StatCategory.VISION_AND_OBJECTIVECONTROL, "Objective Pressure", StatText.OBJECTIVE_PRESSURE),
  TOPSIDE_OBJECTIVES(StatCategory.VISION_AND_OBJECTIVECONTROL, "Topside Objectives", StatText.TOPSIDE_OBJECTIVES),
  BOTSIDE_OBJECTIVES(StatCategory.VISION_AND_OBJECTIVECONTROL, "Botside Objectives", StatText.BOTSIDE_OBJECTIVES),
  WARDING(StatCategory.VISION_AND_OBJECTIVECONTROL, "Visionkontrolle", StatText.WARDING),
  CONTROLWARDS(StatCategory.VISION_AND_OBJECTIVECONTROL, "Control Wards", StatText.CONTROLWARDS),

  TURRET_PRESSURE(StatCategory.ROAMING_AND_DIVING, "Turret Kontrolle", StatText.TURRET_PRESSURE),
  MACRO(StatCategory.ROAMING_AND_DIVING, "Macro Verständnis", StatText.MACRO),
  ROAMING(StatCategory.ROAMING_AND_DIVING, "Roameffizienz", StatText.ROAMING),
  GANKING(StatCategory.ROAMING_AND_DIVING, "Gankeffizienz", StatText.GANKING),
  DIVING(StatCategory.ROAMING_AND_DIVING, "Dives", StatText.DIVING),

  DAMAGE(StatCategory.AGGRESSION_AND_FIGHTING, "Schaden", StatText.DAMAGE),
  PLAYMAKING(StatCategory.AGGRESSION_AND_FIGHTING, "Playmaking", StatText.PLAYMAKING),
  CATCHING(StatCategory.AGGRESSION_AND_FIGHTING, "Picks", StatText.CATCHING),
  SNOWBALLING(StatCategory.AGGRESSION_AND_FIGHTING, "Snowballpotential", StatText.SNOWBALLING),
  STRONG_PHASE(StatCategory.AGGRESSION_AND_FIGHTING, "Starke Spielphase", StatText.STRONG_PHASE),

  TEAMFIGHTING(StatCategory.COMBAT_AND_INCOME, "Teamfighting", StatText.TEAMFIGHTING),
  SKIRMISHING(StatCategory.COMBAT_AND_INCOME, "Skrimishing", StatText.SKIRMISHING),
  EARLY_INCOME(StatCategory.COMBAT_AND_INCOME, "Einkommen Earlygame", StatText.EARLY_INCOME),
  INCOME(StatCategory.COMBAT_AND_INCOME, "Einkommen", StatText.INCOME),
  ITEMIZATION(StatCategory.COMBAT_AND_INCOME, "Itemization", StatText.ITEMIZATION),

  EARLY_SURVIVAL(StatCategory.SURVIVABILITY_AND_UTILITY, "Survival Earlygame", StatText.EARLY_SURVIVAL),
  SURVIVAL(StatCategory.SURVIVABILITY_AND_UTILITY, "Survival Allgemein", StatText.SURVIVAL),
  TEAM_UTILITY(StatCategory.SURVIVABILITY_AND_UTILITY, "Team Utility", StatText.TEAM_UTILITY),
  WAVE_RESOURCE_MANAGEMENT(StatCategory.SURVIVABILITY_AND_UTILITY, "Ganks - Timewaste", StatText.WAVE_RESOURCEMANAGEMENT),
  ISOLATION(StatCategory.SURVIVABILITY_AND_UTILITY, "Isolation", StatText.ISOLATION),

  RESETS(StatCategory.LANING, "Resets", StatText.RESETS),
  PRE_FIRST_BASE(StatCategory.LANING, "Vor Reset", StatText.PRE_FIRST_BASE),
  POST_FIRST_BASE(StatCategory.LANING, "Nach Reset", StatText.POST_FIRST_BASE),
  LANE_BILANCE(StatCategory.LANING, "Lane Bilanz", StatText.LANE_BILANCE),
  PLAYSTYLE(StatCategory.LANING, "Spellnutzung", StatText.PLAYSTYLE),

  GIVING_UP(StatCategory.MENTALITY_AND_ADAPTION, "Surrender", StatText.GIVING_UP),
  CONSISTENCY(StatCategory.MENTALITY_AND_ADAPTION, "Konstanz", StatText.CONSISTENCY),
  VERSATILTITY(StatCategory.MENTALITY_AND_ADAPTION, "Versatilität", StatText.VERSATILTITY),
  ADAPTION(StatCategory.MENTALITY_AND_ADAPTION, "Anpassung", StatText.ADAPTION),
  STATS(StatCategory.MENTALITY_AND_ADAPTION, "Stats", StatText.STATS);

  private final StatCategory category;
  private final String name;
  private final String description;

}
