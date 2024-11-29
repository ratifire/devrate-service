package com.ratifire.devrate.security.service;

import com.ratifire.devrate.security.configuration.properties.CognitoRegistrationProperties;
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
  private final CognitoRegistrationProperties properties;

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

    String redirectUri = properties.getRedirectUri();
    String cognitoAuthUrl = String.format(
        "https://%s/oauth2/authorize?response_type=code&client_id=%s&redirect_uri=%s&scope=%s&identity_provider=%s&state=%s",
        properties.getDomain(),
        properties.getClientId(),
        redirectUri,
        "openid profile email",
        providerName,
        state
    );

    logger.info("Generated Authorization URL for {}: {}", providerName, cognitoAuthUrl);
    return cognitoAuthUrl;
  }

  /**
   * Exchanges the authorization code for tokens.
   *
   * @param authorizationCode the authorization code received from Cognito
   * @return a map containing the tokens
   */
  public Map<String, String> exchangeAuthorizationCodeForTokens(String authorizationCode) {
    try {
      HttpEntity<MultiValueMap<String, String>> tokenRequest = buildTokenExchangeRequest(authorizationCode);
      String tokenUrl = String.format("https://%s/oauth2/token", properties.getDomain());

      RestTemplate restTemplate = new RestTemplate();
      ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, tokenRequest, String.class);

      logger.info("Token exchange successful. Response: {}", response.getBody());
      return Map.of("response", Objects.requireNonNull(response.getBody()));
    } catch (HttpClientErrorException e) {
      logger.error("HTTP Error: {}", e.getResponseBodyAsString(), e);
      throw e;
    } catch (RestClientException e) {
      logger.error("Token exchange request failed: {}", e.getMessage());
      throw e;
    }
  }

  private HttpEntity<MultiValueMap<String, String>> buildTokenExchangeRequest(String authorizationCode) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/x-www-form-urlencoded");
    headers.set("Authorization", generateAuthorizationHeader());

    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("grant_type", properties.getAuthorizationGrantType());
    body.add("code", authorizationCode);
    body.add("redirect_uri", properties.getRedirectUri());

    return new HttpEntity<>(body, headers);
  }

  private String generateAuthorizationHeader() {
    String clientId = properties.getClientId();
    String clientSecret = properties.getClientSecret();
    return "Basic " + Base64.getEncoder().encodeToString(
        (clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8)
    );
  }
}