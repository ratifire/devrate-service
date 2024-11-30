package com.ratifire.devrate.security.service;

import com.ratifire.devrate.security.configuration.properties.CognitoRegistrationProperties;
import com.ratifire.devrate.security.helper.CognitoOAuthHelper;
import jakarta.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class CognitoOAuthService {

  private static final Logger logger = LoggerFactory.getLogger(CognitoOAuthService.class);
  private final CognitoOAuthHelper cognitoOAuthHelper;
  private final RestTemplate restTemplate;

  /**
   * Generates the authorization URL for the specified provider (Google, LinkedIn, etc.).
   *
   * @param session     the current session to store the unique state
   * @param providerName the name of the identity provider
   * @return the authorization URL
   */

  public String generateAuthorizationUrl(HttpSession session, String providerName) {
    String state = UUID.randomUUID().toString();
    session.setAttribute("oauth2State", state);

    String authorizationUrl = cognitoOAuthHelper.buildAuthorizationUrl(providerName, state);
    logger.info("Generated Authorization URL for {}: {}", providerName, authorizationUrl);
    return authorizationUrl;
  }

  public Map<String, String> exchangeAuthorizationCodeForTokens(String authorizationCode) {
    try {
      String tokenUrl = cognitoOAuthHelper.buildTokenUrl();
      var tokenRequest = cognitoOAuthHelper.buildTokenExchangeRequest(authorizationCode);

      ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, tokenRequest, String.class);
      logger.info("Token exchange successful. Response: {}", response.getBody());
      return Map.of("response", response.getBody());
    } catch (HttpClientErrorException e) {
      logger.error("HTTP Error: {}", e.getResponseBodyAsString(), e);
      throw e;
    }
  }
}