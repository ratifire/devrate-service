package com.ratifire.devrate.service.snspublisher;

/**
 * Contract for publishing messages to an Amazon SNS topic.
 */
public interface SnsPublisherService {

  /**
   * Publishes a message to the SNS topic indicating that a meeting has started.
   *
   * @param meetingUrl the meeting join URL to include in the message payload
   * @param fileName   the name of the file
   */
  void publishMeetingStarting(String meetingUrl, String fileName);
}
