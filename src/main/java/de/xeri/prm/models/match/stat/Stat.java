package de.xeri.prm.models.match.stat;

/**
 * Created by Lara on 22.04.2022 for web
 */
public class Stat {
  private final String displayName;
  private final Class sourceClass;
  private final String attribute;
  private final int digits;
  private final boolean percent;
  private Double result;

  public Stat(String displayName, Class sourceClass, String attribute, int digits, boolean percent) {
    this.displayName = displayName;
    this.sourceClass = sourceClass;
    this.attribute = attribute;
    this.digits = digits;
    this.percent = percent;
  }

  public String getDisplayName() {
    return displayName;
  }

  public Class getSourceClass() {
    return sourceClass;
  }

  public String getAttribute() {
    return attribute;
  }

  public void setResult(double result) {
    this.result = result;
  }

  public Double getResult() {
    return result;
  }

  public int getDigits() {
    return digits;
  }

  public String format() {
    final String valueString = String.valueOf(result);
    if (valueString.contains("\\.")) {
      String digitsBefore = valueString.split("\\.")[0];
      if (digitsBefore.equals("0")) digitsBefore = "";
      final String digitsAfter = valueString.split("\\.")[1];
      final int lengthBefore = digitsBefore.length();
      final String preComma;
      final String postComma;
      if (percent) {
        preComma = digitsBefore + (digitsAfter.length() == 1 ? digitsAfter.charAt(0) + "0" : digitsAfter.substring(0, 2));
        postComma = (preComma.length() < digits) ? "." +
            (digitsAfter.length() >= digits - preComma.length() ? digitsAfter.substring(0, digits - preComma.length()) : digitsAfter) +
            "%" : "%";
      } else if (lengthBefore >= digits) {
        preComma = digitsBefore;
        postComma = "";
      } else {
        preComma = digitsBefore;
        postComma = "." + digitsAfter.substring(0, digits - digitsBefore.length());
      }
      return preComma + postComma;
    }
    return valueString;
  }
}
