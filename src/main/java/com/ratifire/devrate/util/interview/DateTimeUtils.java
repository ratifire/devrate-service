package com.ratifire.devrate.util.interview;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

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
   * Calculates the duration of an interview in minutes based on the start time and end time.
   *
   * @param startTime the start time of the interview as a ZonedDateTime
   * @param endTime   the end time of the interview as a string in ISO-8601 format
   * @return the duration of the interview in minutes
   * @throws IllegalArgumentException if the end time string cannot be parsed into a ZonedDateTime
   */
  public static long calculateDurationInterviewInMinutes(ZonedDateTime startTime, String endTime) {
    try {
      ZonedDateTime endTimeParsed = ZonedDateTime.parse(endTime);
      Duration duration = Duration.between(startTime, endTimeParsed);
      return duration.toMinutes();
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Invalid end time format: " + endTime, e);
    }
  }

}