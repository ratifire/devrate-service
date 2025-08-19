package com.ratifire.devrate.service.snspublisher;

import io.awspring.cloud.sns.core.SnsTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Service responsible for publishing messages to the Amazon SNS topic {@code meetingStartingTopic}.
 */
@Service
@RequiredArgsConstructor
@Profile("!local")
public class AwsSnsPublisherService implements SnsPublisherService {

  private final SnsTemplate snsTemplate;
  private static final String TOPIC_NAME = "meetingStartingTopic";

  @Override
  public void publishMeetingStarting(String url) {
    String message = String.format("{\"url\":\"%s\"}", url);
    snsTemplate.convertAndSend(TOPIC_NAME, message);
  }
}
