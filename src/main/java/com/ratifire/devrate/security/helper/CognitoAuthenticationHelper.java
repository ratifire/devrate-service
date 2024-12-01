package com.ratifire.devrate.security.helper;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.ratifire.devrate.security.configuration.properties.CognitoRegistrationProperties;
import com.ratifire.devrate.security.exception.SecretHashGenerationException;
import java.security.Key;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Provides helper methods for Cognito authentication.
 */
@Component
@RequiredArgsConstructor
public class CognitoAuthenticationHelper {

  private static final Logger logger = LoggerFactory.getLogger(CognitoAuthenticationHelper.class);
  private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
  private final CognitoRegistrationProperties cognitoRegistrationProperties;

  /**
   * Generates a secret hash using the user's identifier (email or sub) and client credentials.
   *
   * @param identifier The user's identifier, which can be either an email or sub.
   * @return A Base64-encoded secret hash string.
   * @throws SecretHashGenerationException if the secret hash generation fails.
   */
  public String generateSecretHash(String identifier) {
    try {
      byte[] clientSecret = cognitoRegistrationProperties.getClientSecret().getBytes(UTF_8);
      byte[] clientId = cognitoRegistrationProperties.getClientId().getBytes(UTF_8);
      byte[] identifierBytes = identifier.getBytes(UTF_8);

      Key signingKey = new SecretKeySpec(clientSecret, HMAC_SHA256_ALGORITHM);
      Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
      mac.init(signingKey);

      mac.update(identifierBytes);
      mac.update(clientId);

      byte[] hashBytes = mac.doFinal();
      return Base64.getEncoder().encodeToString(hashBytes);

    } catch (Exception e) {
      logger.error("Failed to generate secret hash for cognito for user: {}", identifier);
      throw new SecretHashGenerationException("Failed to generate secret hash");
    }
  }
}