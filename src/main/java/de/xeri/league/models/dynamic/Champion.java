package de.xeri.league.models.dynamic;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.league.models.enums.Abilitytype;
import de.xeri.league.models.enums.Championclass;
import de.xeri.league.models.enums.FightStyle;
import de.xeri.league.models.enums.FightType;
import de.xeri.league.models.enums.Subclass;
import de.xeri.league.models.match.ChampionSelection;
import de.xeri.league.models.match.playerperformance.Playerperformance;
import de.xeri.league.models.others.ChampionRelationship;
import de.xeri.league.models.others.Playstyle;
import de.xeri.league.util.Data;
import de.xeri.league.util.HibernateUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.annotations.Type;

@Entity(name = "Champion")
@Table(name = "champion", indexes = {
    @Index(name = "champion_title", columnList = "champion_title", unique = true),
    @Index(name = "resource", columnList = "resource"),
    @Index(name = "champion_name", columnList = "champion_name", unique = true)
})
@NamedQuery(name = "Champion.findAll", query = "FROM Champion c")
@NamedQuery(name = "Champion.findById", query = "FROM Champion c WHERE id = :pk")
@NamedQuery(name = "Champion.findBy", query = "FROM Champion c WHERE name = :name")
@Getter
@NoArgsConstructor
public class Champion implements Serializable {

  @Transient
  private static final long serialVersionUID = 1840290655581732760L;

  public static Set<Champion> get() {
    return new LinkedHashSet<>(HibernateUtil.findList(Champion.class));
  }

  public static Champion get(Champion neu) {
    if (has(neu.getId())) {
      return find(neu.getId());
    }
    Data.getInstance().save(neu);
    return neu;
  }

  public static boolean has(short id) {
    return HibernateUtil.has(Champion.class, id);
  }

  public static boolean has(String name) {
    return HibernateUtil.has(Champion.class, new String[]{"name"}, new Object[]{name});
  }

  public static Champion find(String name) {
    return HibernateUtil.find(Champion.class, new String[]{"name"}, new Object[]{name});
  }

  public static Champion find(int id) {
    return HibernateUtil.find(Champion.class, id);
  }

  @Id
  @Column(name = "champion_id", nullable = false)
  private short id;

  @Column(name = "champion_name", nullable = false, length = 16)
  private String name;

  @Column(name = "champion_title", nullable = false, length = 30)
  private String title;

  @Enumerated(EnumType.STRING)
  @Column(name = "subclass", length = 14)
  private Subclass subclass;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "resource")
  private Resource resource;

  @Column(name = "attack", nullable = false)
  private byte attack;

  @Column(name = "defense", nullable = false)
  private byte defense;

  @Column(name = "spell", nullable = false)
  private byte spell;

  @Column(name = "health", nullable = false)
  private short health;

  @Column(name = "secondary", nullable = false)
  private short secondary;

  @Column(name = "move_speed", nullable = false)
  private short moveSpeed;

  @Column(name = "resist", nullable = false)
  private BigDecimal resist;

  @Column(name = "attack_range", nullable = false)
  private short attackRange;

  @Column(name = "health_regen", nullable = false)
  @Type(type = "big_decimal")
  private BigDecimal healthRegen;

  @Column(name = "spell_regen", nullable = false)
  @Type(type = "big_decimal")
  private BigDecimal spellRegen;

  @Column(name = "damage", nullable = false, columnDefinition = "TINYINT UNSIGNED NUL NULL")
  private short damage;

  @Column(name = "attack_speed", nullable = false)
  @Type(type = "big_decimal")
  private BigDecimal attackSpeed;

  @Enumerated(EnumType.STRING)
  @Column(name = "fight_type", length = 9)
  private FightType fightType;

  @Enumerated(EnumType.STRING)
  @Column(name = "fight_style", length = 4)
  private FightStyle fightStyle;

  @OneToMany(mappedBy = "fromChampion")
  private final Set<ChampionRelationship> championRelationshipsFrom = new LinkedHashSet<>();

  @OneToMany(mappedBy = "toChampion")
  private final Set<ChampionRelationship> championRelationshipsTo = new LinkedHashSet<>();

  @OneToMany(mappedBy = "champion")
  private final Set<Wincondition> winconditions = new LinkedHashSet<>();

  @OneToMany(mappedBy = "championOwn")
  private final Set<Playerperformance> playerperformancesOwn = new LinkedHashSet<>();

  @OneToMany(mappedBy = "championEnemy")
  private final Set<Playerperformance> playerperformancesEnemy = new LinkedHashSet<>();

  @OneToMany(mappedBy = "champion")
  private final Set<Playstyle> playstyles = new LinkedHashSet<>();

  @OneToMany(mappedBy = "champion")
  private final Set<ChampionSelection> championSelections = new LinkedHashSet<>();

  @OneToMany(mappedBy = "champion", orphanRemoval = true)
  private final Set<Ability> abilities = new LinkedHashSet<>();

  @ElementCollection(targetClass = Championclass.class)
  @JoinTable(name = "champion_class",
      joinColumns = @JoinColumn(name = "champion", nullable = false),
      indexes = @Index(name = "idx_championclass", columnList = "champion, championclass", unique = true))
  @Enumerated(EnumType.STRING)
  @Column(name = "championclass")
  private final Set<Championclass> classes = new LinkedHashSet<>();

  public Champion(short id, String name) {
    this.id = id;
    this.name = name;
  }

  public void setStats(byte attack, byte defense, byte spell, short health, short secondary, short moveSpeed, double resist,
                       short attackRange, double healthRegen, double spellRegen, short damage, double attackSpeed) {
    this.attack = attack;
    this.defense = defense;
    this.spell = spell;
    this.health = health;
    this.secondary = secondary;
    this.moveSpeed = moveSpeed;
    this.resist = BigDecimal.valueOf(resist);
    this.attackRange = attackRange;
    this.healthRegen = BigDecimal.valueOf(healthRegen);
    this.spellRegen = BigDecimal.valueOf(spellRegen);
    this.damage = damage;
    this.attackSpeed = BigDecimal.valueOf(attackSpeed);
  }

  public Ability addAbility(Ability ability) {
    this.abilities.add(ability);
    ability.setChampion(this);
    return ability;
  }

  public void addPlaystyle(Playstyle playstyle) {
    this.playstyles.add(playstyle);
    playstyle.setChampion(this);
  }

  public void addPlayerperformance(Playerperformance performance, boolean own) {
    if (own) {
      this.playerperformancesOwn.add(performance);
      performance.setChampionOwn(this);
    } else {
      this.playerperformancesEnemy.add(performance);
      performance.setChampionEnemy(this);
    }
  }

  public void addWincondition(Wincondition wincondition) {
    this.winconditions.add(wincondition);
    wincondition.setChampion(this);
  }

  public void addRelationship(ChampionRelationship relationship, boolean from) {
    if (from) {
      this.championRelationshipsFrom.add(relationship);
      relationship.setFromChampion(this);
    } else {
      this.championRelationshipsTo.add(relationship);
      relationship.setToChampion(this);
    }
  }

  public List<Abilitytype> getKeyspells()  {
    return abilities.stream()
        .filter(ability -> !ability.getAbilityType().equals(Abilitytype.PASSIVE) && ability.getCooldown() != null)
        .collect(Collectors.toMap(
            Ability::getAbilityType,
            ability -> ability.getCooldown().contains("/") ? Arrays.stream(ability.getCooldown().split("/"))
                .mapToDouble(Double::parseDouble)
                .max().orElse(0) :
                Double.parseDouble(ability.getCooldown()),
            (a, b) -> b))
        .entrySet().stream()
        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
        .map(Map.Entry::getKey)
        .collect(Collectors.toList())
        .subList(0, 2);
  }

  //<editor-fold desc="getter and setter">
  public double getAttackSpeed() {
    return this.attackSpeed.doubleValue();
  }

  public void setDamage(short damage) {
    this.damage = damage;
  }

  public double getSpellRegen() {
    return this.spellRegen.doubleValue();
  }

  public double getHealthRegen() {
    return healthRegen.doubleValue();
  }

  public double getResist() {
    return resist.doubleValue();
  }

  public void setResource(Resource resource) {
    this.resource = resource;
  }

  public void setSubclass(Subclass subclass) {
    this.subclass = subclass;
  }

  public void setTitle(String championTitle) {
    title = championTitle;
  }

  public void setName(String championName) {
    name = championName;
  }

  public void setId(short id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Champion)) return false;
    final Champion champion = (Champion) o;
    return this.getId() == champion.getId() && this.getName().equals(champion.getName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.getId(), this.getName(), this.getTitle(), this.getSubclass(), this.getResource(), this.getAttack(), this.getDefense(), this.getSpell(), this.getHealth(), this.getSecondary(), this.getMoveSpeed(), this.getResist(), this.getAttackRange(), this.getHealthRegen(), this.getSpellRegen(), this.getDamage(), this.getAttackSpeed(), this.getFightType(), this.getFightStyle());
  }

  @Override
  public String toString() {
    return "Champion{" +
        "id=" + this.id +
        ", name='" + this.name + '\'' +
        ", title='" + this.title + '\'' +
        ", subclass=" + this.subclass +
        ", resource=" + this.resource +
        ", attack=" + this.attack +
        ", defense=" + this.defense +
        ", spell=" + this.spell +
        ", health=" + this.health +
        ", secondary=" + this.secondary +
        ", moveSpeed=" + this.moveSpeed +
        ", resist=" + this.resist +
        ", attackRange=" + this.attackRange +
        ", healthRegen=" + this.healthRegen +
        ", spellRegen=" + this.spellRegen +
        ", damage=" + this.damage +
        ", attackSpeed=" + this.attackSpeed +
        ", fightType=" + this.fightType +
        ", fightStyle=" + this.fightStyle +
        '}';
  }
  //</editor-fold>
}