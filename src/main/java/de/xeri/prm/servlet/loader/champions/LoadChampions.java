package de.xeri.prm.servlet.loader.champions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import de.xeri.prm.manager.PrimeData;
import de.xeri.prm.models.dynamic.Champion;
import de.xeri.prm.models.enums.ChampionPlaystyle;
import de.xeri.prm.models.enums.FightStyle;
import de.xeri.prm.models.enums.FightType;
import de.xeri.prm.models.enums.RelationshipType;
import de.xeri.prm.models.enums.Subclass;
import de.xeri.prm.models.match.playerperformance.Playerperformance;
import de.xeri.prm.models.others.ChampionRelationship;
import de.xeri.prm.util.FacesUtil;
import lombok.Data;
import lombok.val;
import org.hibernate.query.Query;
import org.primefaces.model.DualListModel;

/**
 * Created by Lara on 31.05.2022 for web
 */
@ManagedBean
@SessionScoped
@Data
public class LoadChampions implements Serializable {
  private static final transient long serialVersionUID = 8793193815956627574L;

  private List<String> championClasses;
  private String championClass;
  private List<Playerperformance> playerperformances;

  private List<String> champions;
  private String champion;

  private DualListModel<String> synergies;
  private DualListModel<String> counters;

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

  private int allin;
  private int sustain;
  private int trade;
  private int waveclear;

  @PostConstruct
  public void init() {
    this.champions = new ArrayList<>(Champion.get()).stream().map(Champion::getName).collect(Collectors.toList());
    champions.sort(Comparator.comparing(s -> s));

    this.synergies = new DualListModel<>(new ArrayList<>(champions), new ArrayList<>());
    this.counters = new DualListModel<>(new ArrayList<>(champions), new ArrayList<>());

    this.fightTypes = Arrays.stream(FightType.values()).map(FightType::getDisplayName).collect(Collectors.toList());
    this.fightStyles = Arrays.stream(FightStyle.values()).map(FightStyle::getDisplayName).collect(Collectors.toList());
    this.playStyles = Arrays.stream(ChampionPlaystyle.values()).map(ChampionPlaystyle::getDisplayname).collect(Collectors.toList());
    this.championClasses = Arrays.stream(Subclass.values()).map(Subclass::toString).collect(Collectors.toList());
    championClasses.sort(Comparator.comparing(s -> s));
  }

  public void update() {
    try {
      this.selected = Champion.find(champion);
      this.overall = getPlaystyleOverall();
      this.earlygame = getPlaystyleEarly();
      this.pre6 = getPlaystylePre6();
      this.post6 = getPlaystylePost6();
      this.midgame = getPlaystyleMid();
      this.lategame = getPlaystyleLate();

      this.fightType = (isSelected() && selected.getFightType() != null) ? selected.getFightType().getDisplayName() : "Typ eingeben...";
      this.fightStyle = (isSelected() && selected.getFightStyle() != null) ? selected.getFightStyle().getDisplayName() : "Typ eingeben...";
      this.championClass = (isSelected() && selected.getSubclass() != null) ? selected.getSubclass().toString() : "Championklassifizierung nicht festgelegt";

      this.allin = selected.getAllin();
      this.sustain = selected.getSustain();
      this.trade = selected.getTrade();
      this.waveclear = (isSelected() && selected.getWaveClear() != null) ? selected.getWaveClear() : 0;

      if (isSelected()) {
        this.synergies = getRelationship(selected.getSynergies());
        this.counters = getRelationship(selected.getCounters());
      }

      final Query<Playerperformance> namedQuery = PrimeData.getInstance().getSession().getNamedQuery("Playerperformance.forChampion");
      namedQuery.setParameter("champion", selected);
      namedQuery.setMaxResults(27);
      this.playerperformances = namedQuery.list();

      FacesUtil.sendMessage("Champion geladen", "");
    } catch (Exception exception) {
      FacesUtil.sendException("Fehler beim Laden", exception);
    }
  }

  public void save() {
    try {
      if (isSelected()) {
        selected.setSubclass(Subclass.fromName(championClass));

        selected.setFightType(FightType.fromName(fightType));
        selected.setFightStyle(FightStyle.fromName(fightStyle));

        selected.setOverall(ChampionPlaystyle.fromName(overall));
        selected.setEarlygame(ChampionPlaystyle.fromName(earlygame));
        selected.setPre6(ChampionPlaystyle.fromName(pre6));
        selected.setPost6(ChampionPlaystyle.fromName(post6));
        selected.setMidgame(ChampionPlaystyle.fromName(midgame));
        selected.setLategame(ChampionPlaystyle.fromName(lategame));

        if (allin + sustain + trade <= 15) {
          selected.setAllin((byte) allin);
          selected.setSustain((byte) sustain);
          selected.setTrade((byte) trade);
        } else {
          allin = selected.getAllin();
          sustain = selected.getSustain();
          trade = selected.getTrade();
          FacesUtil.sendWarning("Fehler bei Winconditions", "Die Summe aller Werte in Winconditions darf nicht größer als 15 sein.");
        }

        selected.setWaveClear((byte) waveclear);

        handleRelationships(synergies, RelationshipType.SYNERGY);
        handleRelationships(counters, RelationshipType.COUNTER);

        PrimeData.getInstance().save(selected);
        PrimeData.getInstance().commit();
        FacesUtil.sendMessage("Änderungen gespeichert", "");
      } else {
        FacesUtil.sendWarning("kein Champion ausgewählt", "");
      }
    } catch (Exception exception) {
      FacesUtil.sendException("Fehler beim Speichern", exception);
    }

  }

  private DualListModel<String> getRelationship(List<Champion> source) {
    final List<String> current = source.stream().map(Champion::getName).collect(Collectors.toList());
    final List<String> other = new ArrayList<>(champions);
    other.remove(selected.getName());
    other.removeIf(current::contains);
    return new DualListModel<>(other, current);
  }

  private void handleRelationships(DualListModel<String> source, RelationshipType type) {
    val champs = type.equals(RelationshipType.SYNERGY) ? selected.getSynergies() : selected.getCounters();
    final List<String> target = source.getTarget();
    for (Champion champion : champs) {
      if (!target.contains(champion.getName())) {
        if (type.equals(RelationshipType.SYNERGY)) {
          selected.getChampionRelationshipsFrom().stream()
              .filter(championRelationship -> championRelationship.getRelationshipType().equals(type))
              .collect(Collectors.toList()).stream()
              .filter(championRelationship -> championRelationship.has(champion))
              .findFirst().ifPresent(ChampionRelationship::remove);
        }
        selected.getChampionRelationshipsTo().stream()
            .filter(championRelationship -> championRelationship.getRelationshipType().equals(type))
            .collect(Collectors.toList()).stream()
            .filter(championRelationship -> championRelationship.has(champion))
            .findFirst().ifPresent(ChampionRelationship::remove);
      }
    }

    for (String championName : target) {
      val other = Champion.find(championName.replace("&#39;", "'"));
      if (champs.stream().noneMatch(champion -> champion.equals(other))) {
        new ChampionRelationship(type, selected, other).create();
      }
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
