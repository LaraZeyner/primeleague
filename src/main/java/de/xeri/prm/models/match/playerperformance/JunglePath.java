package de.xeri.prm.models.match.playerperformance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.prm.game.events.location.JungleCamp;
import de.xeri.prm.game.events.location.Position;
import de.xeri.prm.models.match.Teamperformance;
import de.xeri.prm.util.Const;
import de.xeri.prm.manager.Data;
import de.xeri.prm.util.HibernateUtil;
import de.xeri.prm.util.Util;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.val;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.NamedQuery;

/**
 * Created by Lara on 05.05.2022 for web
 */
@Entity(name = "JunglePath")
@Table(name = "path")
@NamedQuery(name = "JunglePath.findAll", query = "FROM JunglePath j")
@NamedQuery(name = "JunglePath.findBy", query = "FROM JunglePath j WHERE name = :name")
@Getter
@Setter
@NoArgsConstructor
public class JunglePath implements Serializable {
  @Transient
  private static final long serialVersionUID = 1152823523613046551L;

  //<editor-fold desc="Queries">
  public static Set<JunglePath> get() {
    return new LinkedHashSet<>(HibernateUtil.findList(JunglePath.class));
  }

  public static JunglePath get(Teamperformance teamperformance, Position positionAt2, Position positionAt3, Position positionAt4,
                               Position positionAt5, Position positionAt6, Position positionAt7) {
    if (has(positionAt2, positionAt3, positionAt4, positionAt5, positionAt6, positionAt7)) {
      return find(positionAt2, positionAt3, positionAt4, positionAt5, positionAt6, positionAt7);
    }
    val neu = new JunglePath(positionAt2, positionAt3, positionAt4, positionAt5, positionAt6, positionAt7,
        teamperformance.isFirstPick());
    neu.getTeamperformances().add(teamperformance);
    teamperformance.setJunglePath(neu);
    Data.getInstance().save(neu);
    return find(positionAt2, positionAt3, positionAt4, positionAt5, positionAt6, positionAt7);
  }


  public static boolean has(Position positionAt2, Position positionAt3, Position positionAt4, Position positionAt5,
                            Position positionAt6, Position positionAt7) {
    return find(positionAt2, positionAt3, positionAt4, positionAt5, positionAt6, positionAt7) != null;
  }

  public static boolean has(String name) {
    return HibernateUtil.has(JunglePath.class, new String[]{"name"}, new Object[]{name});
  }

  public static JunglePath find(String name) {
    return HibernateUtil.find(JunglePath.class, new String[]{"name"}, new Object[]{name});
  }

  public static JunglePath find(Position positionAt2, Position positionAt3, Position positionAt4, Position positionAt5,
                                Position positionAt6, Position positionAt7) {
    return perform(positionAt2, positionAt3, positionAt4, positionAt5, positionAt6, positionAt7);
  }

  private static JunglePath perform(Position positionAt2, Position positionAt3, Position positionAt4, Position positionAt5,
                                    Position positionAt6, Position positionAt7) {
    return get().stream()
        .filter(junglePath -> Util.distance(new Position(junglePath.getXAt2(), junglePath.getYAt2()), positionAt2) < Const.PATH_SIMILARITY)
        .filter(junglePath -> Util.distance(new Position(junglePath.getXAt3(), junglePath.getYAt3()), positionAt3) < Const.PATH_SIMILARITY)
        .filter(junglePath -> Util.distance(new Position(junglePath.getXAt4(), junglePath.getYAt4()), positionAt4) < Const.PATH_SIMILARITY)
        .filter(junglePath -> Util.distance(new Position(junglePath.getXAt5(), junglePath.getYAt5()), positionAt5) < Const.PATH_SIMILARITY)
        .filter(junglePath -> Util.distance(new Position(junglePath.getXAt6(), junglePath.getYAt6()), positionAt6) < Const.PATH_SIMILARITY)
        .filter(junglePath -> Util.distance(new Position(junglePath.getXAt7(), junglePath.getYAt7()), positionAt7) < Const.PATH_SIMILARITY)
        .findFirst().orElse(null);

  }
  //</editor-fold>

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "path_id", nullable = false)
  private int id;

  @Column(name = "path_name", nullable = false, length = 109)
  private String name;

  @Column(name = "minute_2_x")
  private short xAt2;

  @Column(name = "minute_2_y")
  private short yAt2;

  @Column(name = "minute_3_x")
  private short xAt3;

  @Column(name = "minute_3_y")
  private short yAt3;

  @Column(name = "minute_4_x")
  private short xAt4;

  @Column(name = "minute_4_y")
  private short yAt4;

  @Column(name = "minute_5_x")
  private short xAt5;

  @Column(name = "minute_5_y")
  private short yAt5;

  @Column(name = "minute_6_x")
  private short xAt6;

  @Column(name = "minute_6_y")
  private short yAt6;

  @Column(name = "minute_7_x")
  private short xAt7;

  @Column(name = "minute_7_y")
  private short yAt7;

  @OneToMany(mappedBy = "junglePath")
  @LazyCollection(LazyCollectionOption.EXTRA)
  @OrderColumn
  private final Set<Teamperformance> teamperformances = new LinkedHashSet<>();

  public JunglePath(Position positionAt2, Position positionAt3, Position positionAt4, Position positionAt5,
                    Position positionAt6, Position positionAt7, boolean blueSide) {
    this.xAt2 = (short) positionAt2.getX();
    this.yAt2 = (short) positionAt2.getY();
    this.xAt3 = (short) positionAt3.getX();
    this.yAt3 = (short) positionAt3.getY();
    this.xAt4 = (short) positionAt4.getX();
    this.yAt4 = (short) positionAt4.getY();
    this.xAt5 = (short) positionAt5.getX();
    this.yAt5 = (short) positionAt5.getY();
    this.xAt6 = (short) positionAt6.getX();
    this.yAt6 = (short) positionAt6.getY();
    this.xAt7 = (short) positionAt7.getX();
    this.yAt7 = (short) positionAt7.getY();
    val strings = new ArrayList<String>();
    if (!JungleCamp.getClosestCampName(positionAt2, blueSide).equals("")) {
      strings.add(JungleCamp.getClosestCampName(positionAt2, blueSide));
    }
    if (!JungleCamp.getClosestCampName(positionAt3, blueSide).equals("")) {
      strings.add(JungleCamp.getClosestCampName(positionAt3, blueSide));
    }
    if (!JungleCamp.getClosestCampName(positionAt4, blueSide).equals("")) {
      strings.add(JungleCamp.getClosestCampName(positionAt4, blueSide));
    }
    if (!JungleCamp.getClosestCampName(positionAt5, blueSide).equals("")) {
      strings.add(JungleCamp.getClosestCampName(positionAt5, blueSide));
    }
    if (!JungleCamp.getClosestCampName(positionAt6, blueSide).equals("")) {
      strings.add(JungleCamp.getClosestCampName(positionAt6, blueSide));
    }
    if (!JungleCamp.getClosestCampName(positionAt7, blueSide).equals("")) {
      strings.add(JungleCamp.getClosestCampName(positionAt7, blueSide));
    }

    this.name = strings.stream().collect(Collectors.joining(" - ", "", ""));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof JunglePath)) return false;
    final JunglePath that = (JunglePath) o;
    return name.equals(that.getName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), xAt2, yAt2, xAt3, yAt3, xAt4, yAt4, xAt5, yAt5, xAt6, yAt6, xAt7, yAt7);
  }

  @Override
  public String toString() {
    return "JunglePath{" +
        "id=" + id +
        ", xAt2=" + xAt2 +
        ", yAt2=" + yAt2 +
        ", xAt3=" + xAt3 +
        ", yAt3=" + yAt3 +
        ", xAt4=" + xAt4 +
        ", yAt4=" + yAt4 +
        ", xAt5=" + xAt5 +
        ", yAt5=" + yAt5 +
        ", xAt6=" + xAt6 +
        ", yAt6=" + yAt6 +
        ", xAt7=" + xAt7 +
        ", yAt7=" + yAt7 +
        '}';
  }
}
