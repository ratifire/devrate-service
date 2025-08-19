package com.ratifire.devrate.service;

import io.awspring.cloud.sns.core.SnsTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SnsPublisherService {

  private final SnsTemplate snsTemplate;
  private static final String TOPIC_NAME = "meetingStartingTopic";

  public void publishMeetingStarting(String url) {
    String message = String.format("{\"url\":\"%s\"}", url);
    snsTemplate.convertAndSend(TOPIC_NAME, message);
  }
}
