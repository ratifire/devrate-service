package com.ratifire.devrate.security.helper;

import static com.ratifire.devrate.security.model.constants.CognitoConstant.HMAC_SHA256_ALGORITHM;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.ratifire.devrate.security.configuration.properties.CognitoRegistrationProperties;
import com.ratifire.devrate.security.exception.SecretHashGenerationException;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Provides helper methods for Cognito authentication.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CognitoAuthenticationHelper {

  private static final int PASSWORD_BYTE_LENGTH = 64;
  private final CognitoRegistrationProperties properties;

  /**
   * Generates a secret hash using the user's identifier (email or subject) and client credentials.
   *
   * @param identifier The user's identifier, which can be either an email or subject.
   * @return A Base64-encoded secret hash string.
   * @throws SecretHashGenerationException if the secret hash generation fails.
   */
  public String generateSecretHash(String identifier) {
    try {
      byte[] clientSecret = properties.getClientSecret().getBytes(UTF_8);
      byte[] clientId = properties.getClientId().getBytes(UTF_8);
      byte[] identifierBytes = identifier.getBytes(UTF_8);

      Key signingKey = new SecretKeySpec(clientSecret, HMAC_SHA256_ALGORITHM);
      Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
      mac.init(signingKey);

      mac.update(identifierBytes);
      mac.update(clientId);

      byte[] hashBytes = mac.doFinal();
      return Base64.getEncoder().encodeToString(hashBytes);

    } catch (Exception e) {
      log.error("Failed to generate secret hash for cognito for user: {}", identifier);
      throw new SecretHashGenerationException("Failed to generate secret hash");
    }
  }

  /**
   * Generates a random password encoded using BCrypt.
   *
   * @return A BCrypt-encoded random password string.
   */
  public String generateRandomPassword() {
    SecureRandom random = new SecureRandom();
    byte[] randomBytes = new byte[PASSWORD_BYTE_LENGTH];
    random.nextBytes(randomBytes);

    String randomPassword = Base64.getEncoder().encodeToString(randomBytes);
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    return encoder.encode(randomPassword);
  }
}