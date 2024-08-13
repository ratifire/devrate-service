package com.ratifire.devrate.util.zoom.webhook.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ratifire.devrate.entity.interview.Interview;
import com.ratifire.devrate.service.interview.InterviewService;
import com.ratifire.devrate.service.interview.InterviewSummaryService;
import com.ratifire.devrate.util.interview.MeetingUtils;
import com.ratifire.devrate.util.zoom.webhook.exception.ZoomWebhookException;
import com.ratifire.devrate.util.zoom.webhook.model.WebHookRequest;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
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
  private final InterviewSummaryService interviewSummaryService;
  private final InterviewService interviewService;
  private static final Logger logger = org.slf4j.LoggerFactory.getLogger(ZoomWebhookService.class);

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

    try {
      handleInterviewCompletion(meetingId, endTime);
    } catch (IllegalArgumentException e) {
      logger.error("Failed to save interview summary for meeting ID: " + meetingId + ". Reason: "
          + e.getMessage(), e);
      return new ResponseEntity<>("Invalid meeting details provided.",
          HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>("Meeting id: " + meetingId
        + " ended at: " + endTime, HttpStatus.OK);
  }

  /**
   * Handles the completion process of an interview by performing necessary operations after a
   * meeting has ended.
   *
   * @param meetingId The ID of the meeting that has ended. This ID is expected to be a string
   *                  representation of a long value.
   * @param endTime   The time when the meeting ended, in a string format. This is used to update
   *                  the interview summary.
   * @throws IllegalArgumentException If the meeting ID is not a valid long or if the interview is
   *                                  not found.
   */
  private void handleInterviewCompletion(String meetingId, String endTime) {
    long meetingIdLong = MeetingUtils.parseMeetingId(meetingId);
    Interview interview = interviewService.getInterviewByMeetingId(meetingIdLong);
    interviewSummaryService.saveInterviewSummary(interview, endTime);
  }
}