package com.ratifire.devrate.util.zoom.webhook.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ratifire.devrate.util.zoom.webhook.exception.ZoomWebhookException;
import com.ratifire.devrate.util.zoom.webhook.model.WebHookRequest;
import com.ratifire.devrate.util.zoom.webhook.service.ZoomWebhookService;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling Zoom webhook events.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/zoom")
public class ZoomWebhookController {

  private final ZoomWebhookService zoomWebhookService;

  /**
   * Endpoint for receiving Zoom webhook events.
   *
   * @param payload The payload containing event data.
   * @param headers The headers of the request.
   * @return ResponseEntity with the result of processing the event.
   * @throws JsonProcessingException If an error occurs during JSON processing.
   * @throws ZoomWebhookException If the event type is unknown or an error occurs
   *     during processing.
   */
  @PostMapping("/webhook/events")
  public ResponseEntity<String> handleZoomWebhook(
      @RequestBody WebHookRequest payload,
      @RequestHeader Map<String, String> headers) throws JsonProcessingException,
      ZoomWebhookException {
    return new ResponseEntity<>(zoomWebhookService.handleZoomWebhook(payload, headers),
        HttpStatus.OK);
  }
}