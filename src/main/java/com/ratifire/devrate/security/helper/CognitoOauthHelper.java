package com.ratifire.devrate.security.helper;

import com.ratifire.devrate.security.configuration.properties.CognitoRegistrationProperties;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * test sso.
 */
@Component
@RequiredArgsConstructor
public class CognitoOauthHelper {

  private final CognitoRegistrationProperties properties;

  /**
   * test sso.
   */
  public HttpEntity<MultiValueMap<String, String>> buildTokenExchangeRequest(
      String authorizationCode) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/x-www-form-urlencoded");
    headers.set("Authorization", generateAuthorizationHeader());
    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("grant_type", properties.getAuthorizationGrantType());
    body.add("code", authorizationCode);
    body.add("redirect_uri", properties.getRedirectUri());
    return new HttpEntity<>(body, headers);
  }

  /**
   * test sso.
   */
  public String buildAuthorizationUrl(String providerName, String state) {
    return UriComponentsBuilder.fromUriString("https://{domain}/oauth2/authorize")
        .queryParam("response_type", "code")
        .queryParam("client_id", properties.getClientId())
        .queryParam("redirect_uri", properties.getRedirectUri())
        .queryParam("scope", properties.getScope())
        .queryParam("identity_provider", providerName)
        .queryParam("state", state)
        .buildAndExpand(Map.of("domain", properties.getDomain()))
        .toUriString();
  }

  /**
   * test sso.
   */
  public String buildTokenUrl() {
    return UriComponentsBuilder.fromUriString("https://{domain}/oauth2/token")
        .buildAndExpand(Map.of("domain", properties.getDomain()))
        .toUriString();
  }

  /**
   * test sso.
   */
  private String generateAuthorizationHeader() {
    return "Basic " + Base64.getEncoder().encodeToString(
        (properties.getClientId() + ":" + properties.getClientSecret()).getBytes(
            StandardCharsets.UTF_8)
    );
  }
}