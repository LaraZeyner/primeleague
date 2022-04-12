package de.xeri.league.models.io;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.xeri.league.models.ids.OutputId;

@Entity(name = "Output")
@Table(name = "output")
public class Output implements Serializable {

  @Transient
  private static final long serialVersionUID = -763195302229141705L;

  @EmbeddedId
  private OutputId id;

  @Column(name = "value", length = 100)
  private String value;

  @Column(name = "updated", nullable = false)
  private boolean updated;

  //<editor-fold desc="getter and setter">
  public boolean isUpdated() {
    return updated;
  }

  public void setUpdated(boolean updated) {
    this.updated = updated;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public OutputId getId() {
    return id;
  }

  public void setId(OutputId id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Output)) return false;
    final Output output = (Output) o;
    return isUpdated() == output.isUpdated() && getId().equals(output.getId()) && getValue().equals(output.getValue());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getValue(), isUpdated());
  }

  @Override
  public String toString() {
    return "Output{" +
        "id=" + id +
        ", value='" + value + '\'' +
        ", updated=" + updated +
        '}';
  }
  //</editor-fold>
}