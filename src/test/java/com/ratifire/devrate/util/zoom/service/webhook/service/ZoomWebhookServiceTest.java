package com.ratifire.devrate.util.zoom.service.webhook.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratifire.devrate.service.interview.InterviewCompletionService;
import com.ratifire.devrate.util.zoom.exception.ZoomApiException;
import com.ratifire.devrate.util.zoom.webhook.exception.ZoomWebhookException;
import com.ratifire.devrate.util.zoom.webhook.model.WebHookRequest;
import com.ratifire.devrate.util.zoom.webhook.service.ZoomWebhookAuthService;
import com.ratifire.devrate.util.zoom.webhook.service.ZoomWebhookService;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link ZoomWebhookService}.
 */
@ExtendWith(MockitoExtension.class)
public class ZoomWebhookServiceTest {

  @InjectMocks
  private ZoomWebhookService zoomWebhookService;

  @Mock
  private ZoomWebhookAuthService zoomWebhookAuthService;

  @Mock
  private InterviewCompletionService interviewCompletionService;

  @Mock
  private ObjectMapper objectMapper;

  private Map<String, String> headers;
  private String requestBody;

  /**
   * Setup method executed before each test method.
   */
  @BeforeEach
  public void init() {
    headers = new HashMap<>();
    headers.put("x-zm-signature", "valid_signature");
    headers.put("x-zm-request-timestamp", "1623855600000");
  }

  @Test
  void handleZoomWebhookUrlValidationEventShouldReturnEncryptedToken()
      throws JsonProcessingException, ZoomWebhookException {
    requestBody = "{\"event\":\"endpoint.url_validation\",\"payload\""
        + ":{\"plain_token\":\"4f7d4bca6e9f441e8eae7e872b76c3bc\"}}";

    WebHookRequest payload = WebHookRequest.builder()
        .event("endpoint.url_validation")
        .payload(WebHookRequest.Payload.builder()
            .plainToken("4f7d4bca6e9f441e8eae7e872b76c3bc")
            .build())
        .build();

    when(objectMapper.readValue(requestBody, WebHookRequest.class))
        .thenReturn(payload);
    when(zoomWebhookAuthService.handleUrlValidationEvent(payload))
        .thenReturn("encrypted_token");

    String result = zoomWebhookService.handleZoomWebhook(requestBody, headers).getBody();

    assertEquals("encrypted_token", result);
    verify(zoomWebhookAuthService, times(1))
        .validateSignature(anyString(), anyString(), anyString());
    verify(zoomWebhookAuthService, times(1))
        .handleUrlValidationEvent(payload);
  }

  @Test
  void handleZoomWebhookUnknownEventShouldThrowException()
      throws JsonProcessingException, ZoomWebhookException {
    requestBody = "{\"event\":\"unknown_event\"}";

    WebHookRequest payload = WebHookRequest.builder()
        .event("unknown_event")
        .build();

    when(objectMapper.readValue(requestBody, WebHookRequest.class))
        .thenReturn(payload);

    ZoomWebhookException exception = assertThrows(ZoomWebhookException.class, () ->
        zoomWebhookService.handleZoomWebhook(requestBody, headers)
    );

    assertEquals("Unknown event type", exception.getMessage());
    verify(zoomWebhookAuthService, times(1))
        .validateSignature(eq("valid_signature"),
            eq(requestBody), eq("1623855600000"));
    verifyNoInteractions(interviewCompletionService);
  }

  @Test
  void handleZoomWebhookMeetingEndedEventShouldCallInterviewCompletionService()
      throws JsonProcessingException, ZoomWebhookException, ZoomApiException {
    requestBody = "{\"event\":\"meeting.ended\",\"payload\":{\"object\""
        + ":{\"id\":\"89723154070\"}}}";

    WebHookRequest.Payload.Meeting expectedMeeting = WebHookRequest.Payload
        .Meeting.builder()
        .id("89723154070")
        .build();

    WebHookRequest payload = WebHookRequest.builder()
        .event("meeting.ended")
        .payload(WebHookRequest.Payload.builder()
            .meeting(expectedMeeting)
            .build())
        .build();

    when(objectMapper.readValue(requestBody, WebHookRequest.class))
        .thenReturn(payload);

    zoomWebhookService.handleZoomWebhook(requestBody, headers);

    verify(zoomWebhookAuthService, times(1))
        .validateSignature(anyString(), anyString(), anyString());
    verify(interviewCompletionService, times(1))
        .finalizeInterviewProcess(payload.getPayload().getMeeting());
  }

  @Test
  void handleZoomWebhookInvalidSignatureShouldThrowException() throws ZoomWebhookException {
    requestBody = "{\"event\":\"meeting.ended\",\"payload\":{\"object\":"
        + "{\"id\":\"89723154070\"}}}";
    headers.put("x-zm-signature", "invalid_signature");

    doThrow(new ZoomWebhookException("Unauthorized: Invalid signature."))
        .when(zoomWebhookAuthService).validateSignature(anyString(), anyString(), anyString());

    ZoomWebhookException exception = assertThrows(ZoomWebhookException.class, () ->
        zoomWebhookService.handleZoomWebhook(requestBody, headers)
    );

    assertEquals("Unauthorized: Invalid signature.", exception.getMessage());
    verify(zoomWebhookAuthService, times(1)).validateSignature(anyString(),
        anyString(), anyString());
    verifyNoInteractions(interviewCompletionService);
  }

  @Test
  void handleZoomWebhookMalformedJsonShouldThrowException()
      throws JsonProcessingException, ZoomWebhookException {
    requestBody = "malformed_json";

    when(objectMapper.readValue(requestBody, WebHookRequest.class))
        .thenThrow(new JsonProcessingException("Invalid JSON") {});

    JsonProcessingException exception = assertThrows(JsonProcessingException.class, () ->
        zoomWebhookService.handleZoomWebhook(requestBody, headers)
    );

    assertEquals("Invalid JSON", exception.getMessage());
    verify(zoomWebhookAuthService, times(1))
        .validateSignature(anyString(), anyString(), anyString());
    verifyNoInteractions(interviewCompletionService);
  }
}