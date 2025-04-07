package com.ratifire.devrate.util;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import lombok.NoArgsConstructor;

/**
 * Utility class for date and time related operations.
 */
@NoArgsConstructor
public class DateTimeUtils {

  /**
   * Checks if a given LocalDate is within the specified range.
   *
   * @param dateTime the LocalDate to check
   * @param from     the start of the range (inclusive)
   * @param to       the end of the range (inclusive)
   * @return true if date in range, otherwise false
   */
  public static boolean isWithinRange(
      ZonedDateTime dateTime,
      LocalDate from,
      LocalDate to) {
    LocalDate eventDate = dateTime.toLocalDate();
    return !eventDate.isBefore(from) && !eventDate.isAfter(to);
  }
}