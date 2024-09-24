package com.ratifire.devrate.util.zoom.webhook.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratifire.devrate.entity.interview.Interview;
import com.ratifire.devrate.service.interview.InterviewCompletionService;
import com.ratifire.devrate.service.interview.InterviewService;
import com.ratifire.devrate.util.zoom.webhook.exception.ZoomWebhookException;
import com.ratifire.devrate.util.zoom.webhook.model.WebHookRequest;
import java.time.ZonedDateTime;
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
  private final ObjectMapper objectMapper;
  private final InterviewService interviewService;

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
  public String handleZoomWebhook(String requestBody, Map<String, String> headers)
      throws JsonProcessingException, ZoomWebhookException {

    String signature = headers.get("x-zm-signature");
    String timestamp = headers.get("x-zm-request-timestamp");
    zoomWebhookAuthService.validateSignature(signature, requestBody, timestamp);

    WebHookRequest payload = objectMapper.readValue(requestBody, WebHookRequest.class);
    String event = payload.getEvent();
    return switch (event) {
      case "endpoint.url_validation" -> zoomWebhookAuthService.handleUrlValidationEvent(payload);
      case "meeting.ended" -> {
        long meetingId = Long.parseLong(payload.getPayload().getMeeting().getId());

        Interview interview = interviewService.getInterviewByMeetingId(meetingId);
        ZonedDateTime scheduledStartTime = interview.getStartTime();
        String endTime = payload.getPayload().getMeeting().getEndTime();

        if (endTime == null) {
          throw new ZoomWebhookException("End time is missing in the webhook payload.");
        }
        ZonedDateTime webhookTime = ZonedDateTime.parse(payload.getPayload()
            .getMeeting().getEndTime());

        if (webhookTime.isBefore(scheduledStartTime.plusMinutes(10))) {
          yield "Too early to process the meeting. Ignoring webhook.";
        }

        yield interviewCompletionService.completeInterviewProcess(payload
            .getPayload().getMeeting());

      }
      default -> throw new ZoomWebhookException("Unknown event type");
    };
  }
}