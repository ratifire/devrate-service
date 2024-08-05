package com.ratifire.devrate.util.interview;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Utility class for date and time related operations.
 */
public class DateTimeUtils {

  private DateTimeUtils() {
  }

  /**
   * Converts the given ZonedDateTime to UTC time zone.
   *
   * @param dateTime the ZonedDateTime to be converted to UTC
   * @return the ZonedDateTime in UTC time zone
   */
  public static ZonedDateTime convertToUtcTimeZone(ZonedDateTime dateTime) {
    return dateTime.withZoneSameInstant(ZoneId.of("UTC"));
  }

  /**
   * Checks if a given {@link LocalDate} is within the specified range.
   *
   * @param dateTime the {@link LocalDate} to check
   * @param from     the start of the range (inclusive)
   * @param to       the end of the range (inclusive)
   * @return {@code true} if {@code date} in range, otherwise {@code false}
   */
  public static boolean isWithinRange(LocalDate dateTime, LocalDate from, LocalDate to) {
    return !dateTime.isBefore(from) && !dateTime.isAfter(to);
  }
}