package com.ratifire.devrate.security.service;

import com.ratifire.devrate.security.helper.CognitoOAuthHelper;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CognitoOAuthService {

  private final CognitoOAuthHelper cognitoOAuthHelper;
  private final RestTemplateBuilder restTemplate;

  /**
   * Generates the authorization URL for the specified provider (Google, LinkedIn, etc.).
   *
   * @param session      the current session to store the unique state
   * @param providerName the name of the identity provider
   * @return the authorization URL
   */
  public String generateAuthorizationUrl(HttpSession session, String providerName) {
    String state = UUID.randomUUID().toString();
    session.setAttribute("oauth2State", state);

    String authorizationUrl = cognitoOAuthHelper.buildAuthorizationUrl(providerName, state);
    log.info("Generated Authorization URL for {}: {}", providerName, authorizationUrl);
    return authorizationUrl;
  }

  public Map<String, String> exchangeAuthorizationCodeForTokens(String authorizationCode) {
    try {
      String tokenUrl = cognitoOAuthHelper.buildTokenUrl();
      var tokenRequest = cognitoOAuthHelper.buildTokenExchangeRequest(authorizationCode);

      ResponseEntity<String> response = restTemplate.build()
          .postForEntity(tokenUrl, tokenRequest, String.class);
      log.info("Token exchange successful. Response: {}", response.getBody());
      return Map.of("response", Objects.requireNonNull(response.getBody()));
    } catch (HttpClientErrorException e) {
      log.error("HTTP Error: {}", e.getResponseBodyAsString(), e);
      throw e;
    }
  }
}