package de.xeri.prm.models.match.ratings;

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

import de.xeri.prm.manager.PrimeData;
import de.xeri.prm.util.HibernateUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.NamedQuery;

@Entity(name = "Rating")
@Table(name = "rating")
@IdClass(RatingId.class)
@NamedQuery(name = "Rating.findAll", query = "FROM Rating r")
@NamedQuery(name = "Rating.findBy", query = "FROM Rating r WHERE category =:category AND subType =: subtype")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Rating implements Serializable {

  public static Set<Rating> get() {
    return new LinkedHashSet<>(HibernateUtil.findList(Rating.class));
  }

  public static Rating get(Rating neu) {
    if (has(neu.getCategory(), neu.getSubType())) {
      return find(neu.getCategory(), neu.getSubType());
    }
    PrimeData.getInstance().save(neu);
    return neu;
  }

  public static boolean has(StatSubcategory subcategory, DisplaystatSubtype subType) {
    return HibernateUtil.has(Rating.class, new String[]{"category", "subtype"}, new Object[]{subcategory, subType});
  }

  public static Rating find(StatSubcategory subcategory, DisplaystatSubtype subType) {
    return HibernateUtil.find(Rating.class, new String[]{"category", "subtype"}, new Object[]{subcategory, subType});
  }

  @Transient
  private static final long serialVersionUID = -7143298979632087784L;

  @Id
  @Enumerated(EnumType.STRING)
  @Column(name = "rating_category", nullable = false, length = 25)
  private StatSubcategory category;

  @Id
  @Enumerated(EnumType.STRING)
  @Column(name = "rating_subtype", nullable = false, length = 9)
  private DisplaystatSubtype subType;

  @Check(constraints = "rating_value <= 750")
  @Column(name = "rating_value", nullable = false)
  private short value = 300;

  public Rating(StatSubcategory category, DisplaystatSubtype subType) {
    this.category = category;
    this.subType = subType;
  }

  public void setValue(short value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    final Rating rating = (Rating) o;
    return category != null && Objects.equals(category, rating.category) && subType != null && Objects.equals(subType, rating.subType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(category, subType);
  }
}