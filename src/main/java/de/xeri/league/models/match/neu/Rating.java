package de.xeri.league.models.match.neu;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.league.util.Data;
import de.xeri.league.util.HibernateUtil;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.NamedQuery;

@Entity(name = "Rating")
@Table(name = "rating")
@IdClass(RatingId.class)
@NamedQuery(name = "Rating.findAll", query = "FROM Rating r")
@NamedQuery(name = "Rating.findBy", query = "FROM Rating r WHERE category =:category AND type =: typ AND subType =: subtype")
public class Rating implements Serializable {

  public static Set<Rating> get() {
    return new LinkedHashSet<>(HibernateUtil.findList(Rating.class));
  }

  public static Rating get(Rating neu) {
    if (has(neu.getCategory(), neu.getType(), neu.getSubType())) {
      return find(neu.getCategory(), neu.getType(), neu.getSubType());
    }
    Data.getInstance().save(neu);
    return neu;
  }

  public static boolean has(StatSubcategory subcategory, DisplaystatType type, DisplaystatSubtype subType) {
    return HibernateUtil.has(Rating.class, new String[]{"category", "typ", "subtype"}, new Object[]{subcategory, type, subType});
  }

  public static Rating find(StatSubcategory subcategory, DisplaystatType type, DisplaystatSubtype subType) {
    return HibernateUtil.find(Rating.class, new String[]{"category", "typ", "subtype"}, new Object[]{subcategory, type, subType});
  }

  @Transient
  private static final long serialVersionUID = -7143298979632087784L;

  @Id
  @Enumerated(EnumType.STRING)
  @Column(name = "rating_category", nullable = false, length = 25)
  private StatSubcategory category;

  @Id
  @Enumerated(EnumType.STRING)
  @Column(name = "rating_type", nullable = false, length = 8)
  private DisplaystatType type;

  @Id
  @Enumerated(EnumType.STRING)
  @Column(name = "rating_subtype", nullable = false, length = 9)
  private DisplaystatSubtype subType;

  @Check(constraints = "rating_value <= 750")
  @Column(name = "rating_value", nullable = false)
  private short value = 300;

  // default constructor
  public Rating() {
  }

  public Rating(StatSubcategory category, DisplaystatType type, DisplaystatSubtype subType) {
    this.category = category;
    this.type = type;
    this.subType = subType;
  }

  //<editor-fold desc="getter and setter">
  public StatSubcategory getCategory() {
    return category;
  }

  public DisplaystatType getType() {
    return type;
  }

  public DisplaystatSubtype getSubType() {
    return subType;
  }

  public short getValue() {
    return value;
  }

  public void setValue(short value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Rating)) return false;
    final Rating that = (Rating) o;
    return getValue() == that.getValue() && getCategory().equals(that.getCategory()) && getType() == that.getType() && getSubType() == that.getSubType();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getCategory(), getType(), getSubType(), getValue());
  }

  @Override
  public String toString() {
    return "Rating{" +
        "category=" + category +
        ", type=" + type +
        ", subType=" + subType +
        ", value=" + value +
        '}';
  }
  //</editor-fold>
}