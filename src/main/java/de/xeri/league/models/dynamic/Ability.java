package de.xeri.league.models.dynamic;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.league.models.enums.Abilitytype;
import de.xeri.league.util.Data;
import de.xeri.league.util.HibernateUtil;
import org.hibernate.annotations.NamedQuery;

@Entity(name = "Ability")
@Table(name = "ability", indexes = @Index(name = "uq_ability", columnList = "champion, ability_type", unique = true))
@NamedQuery(name = "Ability.findAll", query = "FROM Ability a")
@NamedQuery(name = "Ability.findBy", query = "FROM Ability a WHERE champion = :champion AND abilityType = :type")
public class Ability implements Serializable {

  @Transient
  private static final long serialVersionUID = -716852779822608536L;

  public static Set<Ability> get() {
    return new LinkedHashSet<>(HibernateUtil.findList(Ability.class));
  }

  public static Ability get(Ability neu) {
    if (has(neu.getChampion(), neu.getAbilityType())) {
      return find(neu.getChampion(), neu.getAbilityType());
    }
    Data.getInstance().save(neu);
    return neu;
  }

  public static boolean has(Champion champion, Abilitytype abilitytype) {
    return HibernateUtil.has(Ability.class, new String[]{"champion", "type"}, new Object[]{champion, abilitytype});
  }

  public static Ability find(Champion champion, Abilitytype abilitytype) {
    return HibernateUtil.find(Ability.class, new String[]{"champion", "type"}, new Object[]{champion, abilitytype});
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ability_id", nullable = false)
  private short id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "champion")
  private Champion champion;

  @Enumerated(EnumType.STRING)
  @Column(name = "ability_type", nullable = false)
  private Abilitytype abilityType;

  @Column(name = "cooldown", length = 24)
  private String cooldown;

  @Column(name = "resource_cost", length = 24)
  private String resourceCost;

  @Column(name = "ability_range", length = 24)
  private String abilityRange;

  @Column(name = "value", length = 29)
  private String value;

  @ManyToMany
  @JoinTable(name = "ability_style",
      joinColumns = @JoinColumn(name = "ability"),
      inverseJoinColumns = @JoinColumn(name = "abilitystyle"),
      indexes = @Index(name = "idx_championclass", columnList = "ability, abilitystyle", unique = true))
  private final Set<Abilitystyle> abilitystyles = new LinkedHashSet<>();

  // default constructor
  public Ability() {
  }

  public Ability(Champion champion, Abilitytype abilityType, String cooldown, String resourceCost, String abilityRange, String value) {
    this.champion = champion;
    this.abilityType = abilityType;
    this.cooldown = cooldown;
    this.resourceCost = resourceCost;
    this.abilityRange = abilityRange;
    this.value = value;
  }

  public void addAbilitystyle(Abilitystyle abilitystyle) {
    if (!abilitystyles.contains(abilitystyle)) {
      abilitystyles.add(abilitystyle);
      abilitystyle.getAbilities().add(this);
    }
  }

  //<editor-fold desc="getter and setter">
  public Set<Abilitystyle> getAbilitystyles() {
    return abilitystyles;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getAbilityRange() {
    return abilityRange;
  }

  public void setAbilityRange(String abilityRange) {
    this.abilityRange = abilityRange;
  }

  public String getResourceCost() {
    return resourceCost;
  }

  public void setResourceCost(String resourceCost) {
    this.resourceCost = resourceCost;
  }

  public String getCooldown() {
    return cooldown;
  }

  public void setCooldown(String cooldown) {
    this.cooldown = cooldown;
  }

  public Abilitytype getAbilityType() {
    return abilityType;
  }

  public void setAbilityType(Abilitytype abilityType) {
    this.abilityType = abilityType;
  }

  public Champion getChampion() {
    return champion;
  }

  public void setChampion(Champion champion) {
    this.champion = champion;
  }

  public short getId() {
    return id;
  }

  public void setId(short id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Ability)) return false;
    final Ability ability = (Ability) o;
    return getId() == ability.getId() && getChampion().equals(ability.getChampion()) && getAbilityType() == ability.getAbilityType() && Objects.equals(getCooldown(), ability.getCooldown()) && Objects.equals(getResourceCost(), ability.getResourceCost()) && Objects.equals(getAbilityRange(), ability.getAbilityRange()) && Objects.equals(getValue(), ability.getValue()) && getAbilitystyles().equals(ability.getAbilitystyles());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getChampion(), getAbilityType(), getCooldown(), getResourceCost(), getAbilityRange(), getValue());
  }

  @Override
  public String toString() {
    return "Ability{" +
        "id=" + id +
        ", champion=" + champion +
        ", abilityType=" + abilityType +
        ", cooldown='" + cooldown + '\'' +
        ", resourceCost='" + resourceCost + '\'' +
        ", abilityRange='" + abilityRange + '\'' +
        ", value='" + value + '\'' +
        ", abilitystyles=" + abilitystyles.size() +
        '}';
  }
  //</editor-fold>
}