package com.ratifire.devrate.security.service;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.ratifire.devrate.security.configuration.properties.CognitoRegistrationProperties;
import com.ratifire.devrate.security.helper.CognitoAuthenticationHelper;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class SsoService {

  private static final Logger logger = LoggerFactory.getLogger(SsoService.class);
  private final CognitoRegistrationProperties cognitoProperties;


  public Map<String, String> handleCallback(String code) {

    RestTemplate restTemplate = new RestTemplate();

    String clientId = cognitoProperties.getClientId();
    String clientSecret = cognitoProperties.getClientSecret();
    String authHeader = "Basic " + Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes(
        StandardCharsets.UTF_8));

    HttpHeaders headers = new HttpHeaders();
    headers.set( "Content-Type", "application/x-www-form-urlencoded");
    headers.set("Authorization", authHeader);

    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("grant_type", "authorization_code");
    body.add("code", code);
    body.add("redirect_uri", "http://localhost:8080/auth/callback");


    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

    logger.info("Request body: {}", body);
    logger.info("Headers: {}", headers);
    try {
      ResponseEntity<Map> response = restTemplate.exchange(
          "https://devrate.auth.eu-north-1.amazoncognito.com/oauth2/token",
          HttpMethod.POST,
          request,
          Map.class
      );

      logger.info("Response: {}", response.getBody());
      return response.getBody();

    } catch (HttpClientErrorException e) {
      e.printStackTrace();
      logger.error("HTTP Error: {}", e.getResponseBodyAsString(), e);
      throw e;
    } catch (RestClientException e) {
      logger.error("Request failed: {}", e.getMessage());
      throw e;
    }

  }
}