package de.xeri.league.models.ids;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import org.hibernate.Hibernate;

@Embeddable
public class OutputId implements Serializable {

  @Transient
  private static final long serialVersionUID = -6674954314830967032L;

  @Column(name = "output_category", nullable = false, length = 25)
  private String outputCategory;

  @Column(name = "output_subcategory", nullable = false)
  private byte outputSubcategory;

  @Column(name = "output_row", nullable = false)
  private byte outputRow;

  @Column(name = "output_column", nullable = false)
  private byte outputColumn;

  //<editor-fold desc="getter and setter">
  public byte getOutputColumn() {
    return outputColumn;
  }

  public void setOutputColumn(byte outputColumn) {
    this.outputColumn = outputColumn;
  }

  public byte getOutputRow() {
    return outputRow;
  }

  public void setOutputRow(byte outputRow) {
    this.outputRow = outputRow;
  }

  public byte getOutputSubcategory() {
    return outputSubcategory;
  }

  public void setOutputSubcategory(byte outputSubcategory) {
    this.outputSubcategory = outputSubcategory;
  }

  public String getOutputCategory() {
    return outputCategory;
  }

  public void setOutputCategory(String outputCategory) {
    this.outputCategory = outputCategory;
  }

  @Override
  public int hashCode() {
    return Objects.hash(outputColumn, outputRow, outputCategory, outputSubcategory);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    final OutputId entity = (OutputId) o;
    return Objects.equals(this.outputColumn, entity.outputColumn) &&
        Objects.equals(this.outputRow, entity.outputRow) &&
        Objects.equals(this.outputCategory, entity.outputCategory) &&
        Objects.equals(this.outputSubcategory, entity.outputSubcategory);
  }

  @Override
  public String toString() {
    return "OutputId{" +
        "outputCategory='" + outputCategory + '\'' +
        ", outputSubcategory=" + outputSubcategory +
        ", outputRow=" + outputRow +
        ", outputColumn=" + outputColumn +
        '}';
  }
  //</editor-fold>
}