package com.urverkspel.app;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RangeCollection {

  private List<Range> ranges = new ArrayList<>();

  public RangeCollection(String rangesString) {
    // parse the string into a list of spans
    // https://stackoverflow.com/questions/13011657/advanced-parsing-of-numeric-ranges-from-string

    // Check if the string is valid
    if (!isRangesStringValid(rangesString)) {
      throw new IllegalArgumentException("Invalid ranges expression: " + rangesString);
    }

    // Extract the ranges
    Pattern rangePattern = Pattern.compile(
        "# extract next integers/integer range value.    \n" +
            "([0-9]+)      # $1: 1st integer (Base).         \n" +
            "(?:           # Range for value (optional).     \n" +
            "  -           # Dash separates range integer.   \n" +
            "  ([0-9]+)    # $2: 2nd integer (Range)         \n" +
            ")?            # Range for value (optional). \n" +
            "(?:,|$)       # End on comma or string end.",
        Pattern.COMMENTS);

    Matcher m = rangePattern.matcher(rangesString);

    // Loop through the ranges
    while (m.find()) {
      Range r = new Range(Integer.parseInt(m.group(1)),
          m.group(2) != null ? Integer.parseInt(m.group(2)) : Integer.parseInt(m.group(1)));

      ranges.add(r);
    }
  }

  private Boolean isRangesStringValid(String rangesString) {
    Pattern validRangesString = Pattern.compile(
        "# Validate comma separated integers/integer ranges.\n" +
            "^             # Anchor to start of string.         \n" +
            "[0-9]+        # Integer of 1st value (required).   \n" +
            "(?:           # Range for 1st value (optional).    \n" +
            "  -           # Dash separates range integer.      \n" +
            "  [0-9]+      # Range integer of 1st value.        \n" +
            ")?            # Range for 1st value (optional).    \n" +
            "(?:           # Zero or more additional values.    \n" +
            "  ,           # Comma separates additional values. \n" +
            "  [0-9]+      # Integer of extra value (required). \n" +
            "  (?:         # Range for extra value (optional).  \n" +
            "    -         # Dash separates range integer.      \n" +
            "    [0-9]+    # Range integer of extra value.      \n" +
            "  )?          # Range for extra value (optional).  \n" +
            ")*            # Zero or more additional values.    \n" +
            "$             # Anchor to end of string.           ",
        Pattern.COMMENTS);

    Matcher m = validRangesString.matcher(rangesString);

    return m.matches();
  }

  public boolean contains(int value) {
    for (Range range : ranges) {
      if (range.contains(value)) {
        return true;
      }
    }
    return false;
  }

  private class Range {
    public int start;
    public int end;

    public Range(int start, int end) {
      this.start = start;
      this.end = end;
    }

    public boolean contains(int value) {
      return value >= start && value <= end;
    }

    @Override
    public String toString() {
      return start + "-" + end;
    }
  }
}
