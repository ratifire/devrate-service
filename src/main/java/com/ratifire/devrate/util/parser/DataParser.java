package com.ratifire.devrate.util.parser;

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DataParser {

  /**
   * Parses the given string and converts it to a long value.
   *
   * @param value the string representation of the number
   * @return the parsed long value
   * @throws IllegalArgumentException if the string is not a valid number
   */
  public static long parseToLong(String value) {
    try {
      return Long.parseLong(value);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid number format: " + value, e);
    }
  }

  /**
   * Parses a date and time string in ISO-8601 format to a ZonedDateTime object.
   *
   * @param dateTime the date and time as a string in ISO-8601 format
   * @return the parsed ZonedDateTime object
   * @throws IllegalArgumentException if the date and time string cannot be parsed into a
   *                                  ZonedDateTime
   */
  public static ZonedDateTime parseToZonedDateTime(String dateTime) {
    try {
      return ZonedDateTime.parse(dateTime);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Invalid date and time format: " + dateTime, e);
    }
  }
}