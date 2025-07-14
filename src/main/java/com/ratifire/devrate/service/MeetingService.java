package com.ratifire.devrate.service;

import java.time.ZonedDateTime;

/**
 * Interface for services that handle meeting creation.
 */
public interface MeetingService {
  String createMeeting(String topic, String description, ZonedDateTime startTime);

  String createMeeting();
}
