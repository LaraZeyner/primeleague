package de.xeri.prm.models.match.playerperformance;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.prm.models.dynamic.Summonerspell;
import de.xeri.prm.models.ids.PlayerperformanceSummonerspellId;
import de.xeri.prm.manager.Data;
import de.xeri.prm.util.HibernateUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NamedQuery;

@Entity(name = "PlayerperformanceSummonerspell")
@Table(name = "playerperformance_summonerspell", indexes = @Index(name = "summonerspell", columnList = "summonerspell"))
@IdClass(PlayerperformanceSummonerspellId.class)
@NamedQuery(name = "PlayerperformanceSummonerspell.findAll", query = "FROM PlayerperformanceSummonerspell p")
@NamedQuery(name = "PlayerperformanceSummonerspell.findBy",
    query = "FROM PlayerperformanceSummonerspell p WHERE playerperformance = :playerperformance AND summonerspell = :spell")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PlayerperformanceSummonerspell implements Serializable {
  @Transient
  private static final long serialVersionUID = 330336380785350420L;

  public static Set<PlayerperformanceSummonerspell> get() {
    return new LinkedHashSet<>(HibernateUtil.findList(PlayerperformanceSummonerspell.class));
  }

  public static PlayerperformanceSummonerspell get(PlayerperformanceSummonerspell neu) {
    if (has(neu.getPlayerperformance(), neu.getSummonerspell())) {
      return find(neu.getPlayerperformance(), neu.getSummonerspell());
    }
    neu.getPlayerperformance().getSummonerspells().add(neu);
    neu.getSummonerspell().getPlayerperformances().add(neu);
    Data.getInstance().save(neu);
    return neu;
  }

  public static boolean has(Playerperformance playerperformance, Summonerspell spell) {
    return HibernateUtil.has(PlayerperformanceSummonerspell.class, new String[]{"playerperformance", "spell"},
        new Object[]{playerperformance, spell});
  }

  public static PlayerperformanceSummonerspell find(Playerperformance playerperformance, Summonerspell spell) {
    return HibernateUtil.find(PlayerperformanceSummonerspell.class, new String[]{"playerperformance", "spell"},
        new Object[]{playerperformance, spell});
  }
  
  @Id
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "playerperformance")
  private Playerperformance playerperformance;
  
  @Id
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "summonerspell")
  private Summonerspell summonerspell;

  @Column(name = "usages", nullable = false)
  private byte usages;

  //<editor-fold desc="getter and setter">
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PlayerperformanceSummonerspell)) return false;
    final PlayerperformanceSummonerspell playerperformanceSummonerspell = (PlayerperformanceSummonerspell) o;
    return getUsages() == playerperformanceSummonerspell.getUsages() && getPlayerperformance().equals(playerperformanceSummonerspell.getPlayerperformance()) && getSummonerspell().equals(playerperformanceSummonerspell.getSummonerspell());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getPlayerperformance(), getSummonerspell(), getUsages());
  }

  @Override
  public String toString() {
    return "PlayerperformanceSummonerspell{" +
        "playerperformance=" + playerperformance +
        ", summonerspell=" + summonerspell +
        ", usages=" + usages +
        '}';
  }
  //</editor-fold>
}