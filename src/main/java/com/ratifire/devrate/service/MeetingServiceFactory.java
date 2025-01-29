package com.ratifire.devrate.service;

import com.ratifire.devrate.util.mirotalk.MiroTalkMeetingService;
import com.ratifire.devrate.util.zoom.service.ZoomApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Factory class for providing the appropriate {@link MeetingService} implementation
 * based on the configured {@code serviceType} value.
 */
@Service
@RequiredArgsConstructor
public class MeetingServiceFactory {

  private final ZoomApiService zoomMeetingService;
  private final MiroTalkMeetingService miroTalkMeetingService;

  @Value("${meeting.service.type}")
  private String serviceType;

  /**
   * Returns the appropriate {@link MeetingService} implementation based on the {@code serviceType}.
   * If the {@code serviceType} is "Zoom", then {@link ZoomApiService} implementation.
   * If the {@code serviceType} is "MiroTalk", then {@link MiroTalkMeetingService} implementation.
   *
   * @return the corresponding {@link MeetingService} implementation.
   * @throws IllegalArgumentException if the {@code serviceType} is neither "Zoom" nor "MiroTalk".
   */
  public MeetingService getMeetingService() {
    return switch (serviceType) {
      case "Zoom" -> zoomMeetingService;
      case "MiroTalk" -> miroTalkMeetingService;
      default -> throw new IllegalArgumentException("Unsupported service type");
    };
  }
}