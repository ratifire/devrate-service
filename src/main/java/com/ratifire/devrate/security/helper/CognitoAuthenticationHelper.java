package com.ratifire.devrate.security.helper;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.ratifire.devrate.security.configuration.CognitoRegistrationProperties;
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
   * Generates a secret hash using the user's username and client credentials.
   *
   * @param userEmail The user's username (email).
   * @return A Base64-encoded secret hash string.
   * @throws SecretHashGenerationException if the secret hash generation fails.
   */
  public String generateSecretHash(String userEmail) {
    try {
      byte[] clientSecret = cognitoRegistrationProperties.getClientSecret().getBytes(UTF_8);
      byte[] clientId = cognitoRegistrationProperties.getClientId().getBytes(UTF_8);
      byte[] email = userEmail.getBytes(UTF_8);

      Key signingKey = new SecretKeySpec(clientSecret, HMAC_SHA256_ALGORITHM);
      Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
      mac.init(signingKey);

      mac.update(email);
      mac.update(clientId);

      byte[] hashBytes = mac.doFinal();
      return Base64.getEncoder().encodeToString(hashBytes);

    } catch (Exception e) {
      logger.error("Failed to generate secret hash for cognito");
      throw new SecretHashGenerationException("Failed to generate secret hash for cognito");
    }
  }
}