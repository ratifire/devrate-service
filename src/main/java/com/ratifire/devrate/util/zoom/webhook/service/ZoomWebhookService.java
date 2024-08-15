package com.ratifire.devrate.util.zoom.webhook.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ratifire.devrate.service.interview.InterviewCompletionService;
import com.ratifire.devrate.util.zoom.webhook.exception.ZoomWebhookException;
import com.ratifire.devrate.util.zoom.webhook.model.WebHookRequest;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for handling Zoom webhook events.
 * This service processes various webhook events sent by Zoom.
 */
@Service
@RequiredArgsConstructor
public class ZoomWebhookService {

  private final ZoomWebhookAuthService zoomWebhookAuthService;
  private final InterviewCompletionService interviewCompletionService;

  /**
   * Handles all webhook events from Zoom.
   *
   * @param payload The payload containing event data.
   * @param headers The headers of the request.
   * @return String with the result of processing the event.
   * @throws JsonProcessingException If an error occurs during JSON processing.
   * @throws ZoomWebhookException    If the event type is unknown or an error occurs during
   *                                 processing.
   */
  public String handleZoomWebhook(WebHookRequest payload, Map<String, String> headers)
      throws JsonProcessingException, ZoomWebhookException {
    zoomWebhookAuthService.validateToken(headers.get("authorization"));

    String event = payload.getEvent();
    return switch (event) {
      case "endpoint.url_validation" -> zoomWebhookAuthService.handleUrlValidationEvent(payload);
      case "meeting.ended" ->
          interviewCompletionService.completeInterviewProcess(payload.getPayload().getMeeting());
      default -> throw new ZoomWebhookException("Unknown event type");
    };
  }
}