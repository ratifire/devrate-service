package com.ratifire.devrate.util.interview;

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

}