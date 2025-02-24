package com.ratifire.devrate.util.zoom.webhook.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
  private final ObjectMapper objectMapper;

  /**
   * Handles all webhook events from Zoom.
   *
   * @param requestBody The payload containing event data.
   * @param headers The headers of the request.
   * @return String with the result of processing the event.
   * @throws JsonProcessingException If an error occurs during JSON processing.
   * @throws ZoomWebhookException    If the event type is unknown or an error occurs during
   *                                 processing.
   */
  public ResponseEntity<String> handleZoomWebhook(String requestBody, Map<String, String> headers)
      throws JsonProcessingException, ZoomWebhookException {

    String signature = headers.get("x-zm-signature");
    String timestamp = headers.get("x-zm-request-timestamp");
    zoomWebhookAuthService.validateSignature(signature, requestBody, timestamp);

    WebHookRequest payload = objectMapper.readValue(requestBody, WebHookRequest.class);
    String event = payload.getEvent();

    switch (event) {
      case "endpoint.url_validation":
        return new ResponseEntity<>(zoomWebhookAuthService.handleUrlValidationEvent(payload),
            HttpStatus.OK);
      case "meeting.ended":
        try {
          // add logic for interview completion (if will be needed in the future)
          return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
      default:
        throw new ZoomWebhookException("Unknown event type");
    }
  }
}