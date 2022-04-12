package de.xeri.league.models.io;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity(name = "Input")
@Table(name = "input")
public class Input implements Serializable {

  @Transient
  private static final long serialVersionUID = 3686292181607927659L;

  @Id
  @Column(name = "input_category", nullable = false, length = 25)
  private String id;

  @Column(name = "input_value1", length = 100)
  private String inputValue1;

  @Column(name = "input_value2", length = 100)
  private String inputValue2;

  @Column(name = "input_value3", length = 100)
  private String inputValue3;

  @Column(name = "input_value4", length = 100)
  private String inputValue4;

  @Column(name = "input_value5", length = 100)
  private String inputValue5;

  @Column(name = "input_value6", length = 100)
  private String inputValue6;

  @Column(name = "input_value7", length = 100)
  private String inputValue7;

  @Column(name = "input_value8", length = 100)
  private String inputValue8;

  @Column(name = "input_value9", length = 100)
  private String inputValue9;

  @Column(name = "input_value10", length = 100)
  private String inputValue10;

  @Column(name = "input_value11", length = 100)
  private String inputValue11;

  @Column(name = "input_value12", length = 100)
  private String inputValue12;

  @Column(name = "input_value13", length = 100)
  private String inputValue13;

  //<editor-fold desc="getter and setter">
  public String getInputValue13() {
    return inputValue13;
  }

  public void setInputValue13(String inputValue13) {
    this.inputValue13 = inputValue13;
  }

  public String getInputValue12() {
    return inputValue12;
  }

  public void setInputValue12(String inputValue12) {
    this.inputValue12 = inputValue12;
  }

  public String getInputValue11() {
    return inputValue11;
  }

  public void setInputValue11(String inputValue11) {
    this.inputValue11 = inputValue11;
  }

  public String getInputValue10() {
    return inputValue10;
  }

  public void setInputValue10(String inputValue10) {
    this.inputValue10 = inputValue10;
  }

  public String getInputValue9() {
    return inputValue9;
  }

  public void setInputValue9(String inputValue9) {
    this.inputValue9 = inputValue9;
  }

  public String getInputValue8() {
    return inputValue8;
  }

  public void setInputValue8(String inputValue8) {
    this.inputValue8 = inputValue8;
  }

  public String getInputValue7() {
    return inputValue7;
  }

  public void setInputValue7(String inputValue7) {
    this.inputValue7 = inputValue7;
  }

  public String getInputValue6() {
    return inputValue6;
  }

  public void setInputValue6(String inputValue6) {
    this.inputValue6 = inputValue6;
  }

  public String getInputValue5() {
    return inputValue5;
  }

  public void setInputValue5(String inputValue5) {
    this.inputValue5 = inputValue5;
  }

  public String getInputValue4() {
    return inputValue4;
  }

  public void setInputValue4(String inputValue4) {
    this.inputValue4 = inputValue4;
  }

  public String getInputValue3() {
    return inputValue3;
  }

  public void setInputValue3(String inputValue3) {
    this.inputValue3 = inputValue3;
  }

  public String getInputValue2() {
    return inputValue2;
  }

  public void setInputValue2(String inputValue2) {
    this.inputValue2 = inputValue2;
  }

  public String getInputValue1() {
    return inputValue1;
  }

  public void setInputValue1(String inputValue1) {
    this.inputValue1 = inputValue1;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Input)) return false;
    final Input input = (Input) o;
    return getId().equals(input.getId()) && getInputValue1().equals(input.getInputValue1()) && Objects.equals(getInputValue2(), input.getInputValue2()) && Objects.equals(getInputValue3(), input.getInputValue3()) && Objects.equals(getInputValue4(), input.getInputValue4()) && Objects.equals(getInputValue5(), input.getInputValue5()) && Objects.equals(getInputValue6(), input.getInputValue6()) && Objects.equals(getInputValue7(), input.getInputValue7()) && Objects.equals(getInputValue8(), input.getInputValue8()) && Objects.equals(getInputValue9(), input.getInputValue9()) && Objects.equals(getInputValue10(), input.getInputValue10()) && Objects.equals(getInputValue11(), input.getInputValue11()) && Objects.equals(getInputValue12(), input.getInputValue12()) && Objects.equals(getInputValue13(), input.getInputValue13());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getInputValue1(), getInputValue2(), getInputValue3(), getInputValue4(), getInputValue5(), getInputValue6(), getInputValue7(), getInputValue8(), getInputValue9(), getInputValue10(), getInputValue11(), getInputValue12(), getInputValue13());
  }

  @Override
  public String toString() {
    return "Input{" +
        "id='" + id + '\'' +
        ", inputValue1='" + inputValue1 + '\'' +
        ", inputValue2='" + inputValue2 + '\'' +
        ", inputValue3='" + inputValue3 + '\'' +
        ", inputValue4='" + inputValue4 + '\'' +
        ", inputValue5='" + inputValue5 + '\'' +
        ", inputValue6='" + inputValue6 + '\'' +
        ", inputValue7='" + inputValue7 + '\'' +
        ", inputValue8='" + inputValue8 + '\'' +
        ", inputValue9='" + inputValue9 + '\'' +
        ", inputValue10='" + inputValue10 + '\'' +
        ", inputValue11='" + inputValue11 + '\'' +
        ", inputValue12='" + inputValue12 + '\'' +
        ", inputValue13='" + inputValue13 + '\'' +
        '}';
  }
  //</editor-fold>
}