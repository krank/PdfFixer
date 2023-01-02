package com.urverkspel.app;

public class ProgressBar {

  int width = 0;
  int minValue = 0;
  int maxValue = 0;
  int value = 0;
  String prefix = "";

  public ProgressBar(int width, int minValue, int maxValue, String prefix) {
    this.minValue = minValue;
    this.maxValue = maxValue;
    this.width = width;
    this.value = minValue;
    this.prefix = prefix;
  }

  public void Print(Boolean includePercent, Boolean includeNumbers) {
    
    // Calculate the percent & bar width
    int percent = (int) (((double) value / (double) maxValue) * 100);
    int barWidth = (int) (((double) value / (double) maxValue) * width);

    // Create the bar
    String bar = "=".repeat(barWidth);
    // Pad the bar with spaces
    bar = String.format("%1$-" + width + "s", bar);

    // Create the percent & numbers string
    String percentString = includeNumbers ? " " + percent + "%" : "";
    String numbersString = includeNumbers ? value + "/" + maxValue : "";
    
    // Print the bar
    System.out.print("\r" + prefix + "[" + bar + "]" + percentString + " (" + numbersString + ")");
  }

  public void SetValue(int value) {
    this.value = value;
  }

}
