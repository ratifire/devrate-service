package com.ratifire.devrate.service.snspublisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * No-operation implementation of {@link SnsPublisherService} used in the {@code local} profile.
 */
@Service
@Profile("local")
@Slf4j
public class NoOpSnsPublisherService implements SnsPublisherService {
  @Override
  public void publishMeetingStarting(String url) {
    log.info("[LOCAL] SNS publish skipped. Message would be: {\"url\":\"{}\"}", url);
  }
}
