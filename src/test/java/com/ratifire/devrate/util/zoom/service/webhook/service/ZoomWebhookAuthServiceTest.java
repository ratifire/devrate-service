package com.ratifire.devrate.util.zoom.service.webhook.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratifire.devrate.util.zoom.webhook.exception.ZoomWebhookException;
import com.ratifire.devrate.util.zoom.webhook.model.TokenResponse;
import com.ratifire.devrate.util.zoom.webhook.model.WebHookRequest;
import com.ratifire.devrate.util.zoom.webhook.service.ZoomWebhookAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Unit tests for {@link ZoomWebhookAuthService}.
 */
@ExtendWith(MockitoExtension.class)
public class ZoomWebhookAuthServiceTest {

  @InjectMocks
  private ZoomWebhookAuthService zoomWebhookAuthService;

  @Mock
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    String zoomSecret = "testSecret";
    ReflectionTestUtils.setField(zoomWebhookAuthService, "zoomSecret", zoomSecret);
  }

  @Test
  void testValidateSignature_WithValidSignature_ShouldPass() {
    String requestBody = "{\"event\":\"test_event\"}";
    String timestamp = "1623855600";

    String expectedSignature =
        "v0=527040bd7a0b0bbdd3ff87c5f6b9b2297a626e09929c7920c9b500c9f05a18d5";

    assertDoesNotThrow(() -> zoomWebhookAuthService
        .validateSignature(expectedSignature, requestBody, timestamp));
  }

  @Test
  void testValidateSignature_WithInvalidSignature_ShouldThrowException() {
    String requestBody = "{\"event\":\"test_event\"}";
    String timestamp = "1623855600";

    String invalidSignature = "v0=abcdef1234567890abcdef1234567890abcdef12";

    ZoomWebhookException exception = assertThrows(ZoomWebhookException.class, () ->
        zoomWebhookAuthService.validateSignature(invalidSignature, requestBody, timestamp)
    );

    assertEquals("Unauthorized: Invalid signature.", exception.getMessage());
  }

  @Test
  void testHandleUrlValidationEventWithValidToken() throws JsonProcessingException {
    String plainToken = "plainToken";
    String expectedEncryptedToken = "f7c3bc1d808e04732adf679965ccc34ca7ae3441";

    WebHookRequest webHookRequest = WebHookRequest.builder()
        .event("endpoint.url_validation")
        .payload(WebHookRequest.Payload.builder().plainToken(plainToken).build())
        .build();

    String expectedResponse = "{\"plainToken\":\"plainToken\","
        + "\"encryptedToken\":\"" + expectedEncryptedToken + "\"}";
    when(objectMapper.writeValueAsString(any(TokenResponse.class)))
        .thenReturn(expectedResponse);

    String actualResponse = zoomWebhookAuthService
        .handleUrlValidationEvent(webHookRequest);
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void testHandleUrlValidationEventWithEmptyToken() throws JsonProcessingException {
    String plainToken = "";
    String expectedEncryptedToken =
        "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";

    WebHookRequest webHookRequest = WebHookRequest.builder()
        .event("endpoint.url_validation")
        .payload(WebHookRequest.Payload.builder().plainToken(plainToken).build())
        .build();

    String expectedResponse = "{\"plainToken\":\"\",\"encryptedToken\":\""
        + expectedEncryptedToken + "\"}";
    when(objectMapper.writeValueAsString(any(TokenResponse.class))).thenReturn(expectedResponse);

    String actualResponse = zoomWebhookAuthService.handleUrlValidationEvent(webHookRequest);
    assertEquals(expectedResponse, actualResponse);
  }
}