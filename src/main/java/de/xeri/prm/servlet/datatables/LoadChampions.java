package de.xeri.prm.servlet.datatables;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import de.xeri.prm.models.dynamic.Champion;
import de.xeri.prm.models.enums.ChampionPlaystyle;
import de.xeri.prm.models.enums.FightStyle;
import de.xeri.prm.models.enums.FightType;
import lombok.Data;

/**
 * Created by Lara on 31.05.2022 for web
 */
@ManagedBean
@RequestScoped
@Data
public class LoadChampions implements Serializable {
  private static final transient long serialVersionUID = 8793193815956627574L;

  private List<String> champions;
  private String champion;
  private Champion selected;

  private List<String> fightTypes;
  private String fightType;

  private List<String> fightStyles;
  private String fightStyle;

  private List<String> playStyles;
  private String overall;
  private String earlygame;
  private String pre6;
  private String post6;
  private String midgame;
  private String lategame;

  @PostConstruct
  public void init() {
    this.champions = new ArrayList<>(Champion.get()).stream().map(Champion::getName).collect(Collectors.toList());
    champions.sort(Comparator.comparing(s -> s));

    this.fightTypes = Arrays.stream(FightType.values()).map(Enum::name).collect(Collectors.toList());
    this.fightStyles = Arrays.stream(FightStyle.values()).map(Enum::name).collect(Collectors.toList());
    this.playStyles = Arrays.stream(ChampionPlaystyle.values()).map(ChampionPlaystyle::getDisplayname).collect(Collectors.toList());
  }

  public void update() {
    this.selected = Champion.find(champion);
  }

  public void save() {
    if (isSelected()) {
      selected.setFightType(FightType.fromName(fightType));
      selected.setFightStyle(FightStyle.fromName(fightStyle));

      selected.setOverall(ChampionPlaystyle.fromName(overall));
      selected.setEarlygame(ChampionPlaystyle.fromName(earlygame));
      selected.setPre6(ChampionPlaystyle.fromName(pre6));
      selected.setPost6(ChampionPlaystyle.fromName(post6));
      selected.setMidgame(ChampionPlaystyle.fromName(midgame));
      selected.setLategame(ChampionPlaystyle.fromName(lategame));

      de.xeri.prm.manager.Data.getInstance().save(selected);
    }
  }

  private boolean isSelected() {
    return selected != null;
  }

  public String getTitle() {
    if (isSelected()) {
      return "   - " + selected.getTitle();
    }
    return "";
  }

  public String getSubclassName() {
    if (isSelected() && selected.getSubclass() != null) {
      return "   - " + selected.getSubclass().getDisplayName();
    }
    return "";
  }

  public String getClassName() {
    if (isSelected() && selected.getSubclass() != null) {
      return selected.getSubclass().getChampionclass().getDisplayName();
    }
    return "";
  }

  public String getResist() {
    if (isSelected()) {
      return String.valueOf(Math.round(selected.getResist()));
    }
    return "";
  }

  public String getRange() {
    if (isSelected()) {
      return String.valueOf(Math.round(selected.getRange()));
    }
    return "";
  }

  public String getHealth() {
    if (isSelected()) {
      return selected.getHealth() + " (" + Math.round(selected.getHealthRegen() * 10) / 10d + ")";
    }
    return "";
  }

  public String getResource() {
    if (isSelected()) {
      return selected.getSecondary() + " (" + Math.round(selected.getSpellRegen() * 10) / 10d + ")";
    }
    return "";
  }

  public String getAttackSpeed() {
    if (isSelected()) {
      return String.valueOf(Math.round(selected.getAttackSpeed() * 1000) / 1000d);
    }
    return "";
  }

  public String getWaveclear() {
    if (isSelected() && selected.getWaveClear() != null) {
      return String.valueOf(selected.getWaveClear());
    }
    return "";
  }

  public String getFightType() {
    if (isSelected() && selected.getFightType() != null) {
      return selected.getFightType().name();
    }
    return "Typ eingeben...";
  }

  public String getFightStyle() {
    if (isSelected() && selected.getFightStyle() != null) {
      return selected.getFightStyle().name();
    }
    return "Stil eingeben...";
  }

  public String getPlaystyleOverall() {
    if (isSelected() && selected.getOverall() != null) {
      return selected.getOverall().getDisplayname();
    }
    return "Playstyle eingeben...";
  }

  public String getPlaystyleEarly() {
    if (isSelected() && selected.getEarlygame() != null) {
      return selected.getEarlygame().getDisplayname();
    }
    return "Playstyle eingeben...";
  }

  public String getPlaystylePre6() {
    if (isSelected() && selected.getEarlygame() != null) {
      return selected.getPre6().getDisplayname();
    }
    return "Playstyle eingeben...";
  }

  public String getPlaystylePost6() {
    if (isSelected() && selected.getPost6() != null) {
      return selected.getPost6().getDisplayname();
    }
    return "Playstyle eingeben...";
  }

  public String getPlaystyleMid() {
    if (isSelected() && selected.getMidgame() != null) {
      return selected.getMidgame().getDisplayname();
    }
    return "Playstyle eingeben...";
  }

  public String getPlaystyleLate() {
    if (isSelected() && selected.getLategame() != null) {
      return selected.getLategame().getDisplayname();
    }
    return "Playstyle eingeben...";
  }

  /*public Champion getChampion(int id) {
    return champions.stream().filter(c -> c.getId() == id).findFirst().orElse(null);
  }*/
}
