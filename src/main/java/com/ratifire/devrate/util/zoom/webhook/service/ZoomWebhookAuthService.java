package com.ratifire.devrate.util.zoom.webhook.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratifire.devrate.util.zoom.webhook.exception.ZoomWebhookException;
import com.ratifire.devrate.util.zoom.webhook.model.TokenResponse;
import com.ratifire.devrate.util.zoom.webhook.model.WebHookRequest;
import java.nio.charset.StandardCharsets;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service class for handling Zoom webhook authentication and URL validation events.
 */
@Service
@RequiredArgsConstructor
public class ZoomWebhookAuthService {

  @Value("${zoom.secret}")
  private String zoomSecret;

  private final ObjectMapper objectMapper;

  /**
   * Validates the signature of the Zoom webhook.
   *
   * @param signature The signature received in the request headers.
   * @param requestBody The raw body of the request.
   * @param timestamp The timestamp received in the request headers.
   * @throws ZoomWebhookException If the signature is invalid.
   */
  public void validateSignature(String signature, String requestBody, String timestamp)
      throws ZoomWebhookException {
    String message = String.format("v0:%s:%s", timestamp, requestBody);
    String computedSignature = "v0=" + generateHmacSha256(zoomSecret, message);

    if (!computedSignature.equals(signature)) {
      throw new ZoomWebhookException("Unauthorized: Invalid signature.");
    }
  }

  /**
   * Handles URL validation events.
   *
   * @param payload The payload containing event data.
   * @return String with the encrypted token.
   * @throws JsonProcessingException If an error occurs during JSON processing.
   */
  public String handleUrlValidationEvent(WebHookRequest payload)
      throws JsonProcessingException {
    String plainToken = payload.getPayload().getPlainToken();
    String encryptedToken = generateHmacSha256(zoomSecret, plainToken);
    TokenResponse model = new TokenResponse(plainToken, encryptedToken);

    return objectMapper.writeValueAsString(model);
  }

  /**
   * Generates an HMAC SHA-256 hash.
   *
   * @param secret The secret key.
   * @param plainToken The plain token.
   * @return The encrypted token.
   */
  private String generateHmacSha256(String secret, String plainToken) {
    try {
      Mac sha256Hmac = Mac.getInstance("HmacSHA256");
      SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
          "HmacSHA256");
      sha256Hmac.init(secretKeySpec);
      byte[] hash = sha256Hmac.doFinal(plainToken.getBytes(StandardCharsets.UTF_8));
      return bytesToHex(hash);
    } catch (Exception e) {
      throw new RuntimeException("Failed to calculate HMAC SHA-256 hash", e);
    }
  }

  /**
   * Converts a byte array to a hexadecimal string.
   *
   * @param bytes The byte array.
   * @return The hexadecimal string.
   */
  private String bytesToHex(byte[] bytes) {
    StringBuilder result = new StringBuilder();
    for (byte b : bytes) {
      result.append(String.format("%02x", b));
    }
    return result.toString();
  }
}