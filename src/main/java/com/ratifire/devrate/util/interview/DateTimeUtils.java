package com.ratifire.devrate.util.interview;

import java.time.LocalDateTime;
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
   * Checks if a given {@link LocalDateTime} is within the specified range.
   *
   * @param dateTime the {@link LocalDateTime} to check
   * @param from the start of the range (inclusive)
   * @param to the end of the range (inclusive)
   * @return {@code true} if {@code dateTime} is greater than or equal to {@code from}
   *         and less than or equal to {@code to}; {@code false} otherwise
   */
  public static boolean isWithinRange(LocalDateTime dateTime, LocalDateTime from,
      LocalDateTime to) {
    return !dateTime.isBefore(from) && !dateTime.isAfter(to);
  }
}