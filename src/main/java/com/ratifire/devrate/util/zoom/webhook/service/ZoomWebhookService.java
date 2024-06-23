package com.ratifire.devrate.util.zoom.webhook.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ratifire.devrate.util.zoom.webhook.exception.ZoomWebhookException;
import com.ratifire.devrate.util.zoom.webhook.model.WebHookRequest;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Service class for handling Zoom webhook events.
 * This service processes various webhook events sent by Zoom.
 */
@Service
@RequiredArgsConstructor
public class ZoomWebhookService {

  private final ZoomWebhookAuthService zoomWebhookAuthService;

  /**
   * Handles all webhook events from Zoom.
   *
   * @param payload The payload containing event data.
   * @param headers The headers of the request.
   * @return ResponseEntity with the result of processing the event.
   * @throws JsonProcessingException If an error occurs during JSON processing.
   * @throws ZoomWebhookException If the event type is unknown or an error occurs during processing.
   */
  public ResponseEntity<String> handleZoomWebhook(WebHookRequest payload, Map<String,
      String> headers) throws JsonProcessingException, ZoomWebhookException {
    zoomWebhookAuthService.validateToken(headers.get("authorization"));

    String event = payload.getEvent();
    return switch (event) {
      case "endpoint.url_validation" -> zoomWebhookAuthService.handleUrlValidationEvent(payload);
      case "meeting.ended" -> handleMeetingEndedEvent(payload);
      default -> throw new ZoomWebhookException("Unknown event type");
    };
  }

  /**
   * Handles meeting ended events.
   *
   * @param payload The payload containing event data.
   * @return ResponseEntity with the status of the meeting.
   */
  private ResponseEntity<String> handleMeetingEndedEvent(WebHookRequest payload) {
    WebHookRequest.Payload.Meeting payloadObject = payload.getPayload().getMeeting();
    if (payloadObject == null) {
      return new ResponseEntity<>("Error: The payload or meeting details are missing.",
          HttpStatus.BAD_REQUEST);
    }

    String meetingId = payloadObject.getId();
    String endTime = payloadObject.getEndTime();

    return new ResponseEntity<>("Meeting id: " + meetingId
        + " ended at: " + endTime, HttpStatus.OK);
  }
}