package de.xeri.prm.models.match.neu;

import java.io.Serializable;
import java.util.Objects;

import org.hibernate.Hibernate;

public class RatingId implements Serializable {
  private static final transient long serialVersionUID = -6140522775423191660L;

  private StatSubcategory category;
  private DisplaystatType type;
  private DisplaystatSubtype subType;

  // default constructor
  public RatingId() {
  }

  public RatingId(StatSubcategory category, DisplaystatType type, DisplaystatSubtype subType) {
    this.category = category;
    this.type = type;
    this.subType = subType;
  }

  public DisplaystatSubtype getSubType() {
    return subType;
  }

  public void setSubType(DisplaystatSubtype subType) {
    this.subType = subType;
  }

  public DisplaystatType getType() {
    return type;
  }

  public void setType(DisplaystatType type) {
    this.type = type;
  }

  public StatSubcategory getCategory() {
    return category;
  }

  public void setCategory(StatSubcategory displaystat) {
    this.category = displaystat;
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, subType, category);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    RatingId entity = (RatingId) o;
    return Objects.equals(this.type, entity.type) &&
        Objects.equals(this.subType, entity.subType) &&
        Objects.equals(this.category, entity.category);
  }

  @Override
  public String toString() {
    return "DisplaystatRatingId{" +
        "category='" + category + '\'' +
        ", type=" + type +
        ", subType=" + subType +
        '}';
  }
}