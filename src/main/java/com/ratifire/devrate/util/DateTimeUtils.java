package com.ratifire.devrate.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.NoArgsConstructor;

/**
 * Utility class for date and time related operations.
 */
@NoArgsConstructor
public class DateTimeUtils {

  /**
   * Converts the given ZonedDateTime to UTC time zone.
   *
   * @param dateTime the ZonedDateTime to be converted to UTC
   * @return the ZonedDateTime in UTC time zone
   */
  //TODO: Can we use Instant instead of ZonedDateTime?
  public static ZonedDateTime toUtc(ZonedDateTime dateTime) {
    return dateTime.withZoneSameInstant(ZoneId.of("UTC"));
  }

  /**
   * Checks if a given LocalDate is within the specified range.
   *
   * @param dateTime the LocalDate to check
   * @param from     the start of the range (inclusive)
   * @param to       the end of the range (inclusive)
   * @return true if date in range, otherwise false
   */
  public static boolean isWithinRange(LocalDate dateTime, LocalDate from, LocalDate to) {
    return !dateTime.isBefore(from) && !dateTime.isAfter(to);
  }
}