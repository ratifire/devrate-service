package com.ratifire.devrate.util.interview;

/**
 * Utility class for handling operations related to meeting.
 */
public class MeetingUtils {

  private MeetingUtils() {
  }

  /**
   * Parses the given meeting ID string and converts it to a long value.
   *
   * @param meetingId the string representation of the meeting ID
   * @return the parsed long value of the meeting ID
   * @throws IllegalArgumentException if the meeting ID string is not a valid number
   */
  public static long parseMeetingId(String meetingId) {
    try {
      return Long.parseLong(meetingId);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid meeting ID format: " + meetingId, e);
    }
  }
}