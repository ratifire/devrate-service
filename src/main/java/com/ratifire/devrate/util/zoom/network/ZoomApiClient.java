package com.ratifire.devrate.util.zoom.network;

import java.util.Optional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Client for interacting with the Zoom API.
 */
@Component
@AllArgsConstructor
public class ZoomApiClient {

  private final RestTemplate restTemplate;
  private final HttpHeaders zoomAuthHeader;
  private static final Logger logger = LoggerFactory.getLogger(ZoomApiClient.class);

  /**
   * Performs a POST request to the specified URL with the provided JSON request payload and returns
   * the response of the specified type.
   *
   * @param url         the URL to which the POST request is sent.
   * @param jsonRequest the JSON request payload to be sent in the request body.
   * @param response    the class of the response type.
   * @param <T>         the type of the response.
   * @return the response object of the specified type.
   */
  public <T> Optional<T> post(String url, String jsonRequest, Class<T> response) {
    try {
      HttpEntity<?> httpEntity = new HttpEntity<>(jsonRequest, zoomAuthHeader);
      T body = restTemplate.postForEntity(url, httpEntity, response).getBody();
      return Optional.ofNullable(body);
    } catch (RestClientException ex) {
      logger.error("Error occurred while sending POST request to URL: {}: {}", url,
          ex.getMessage());
      return Optional.empty();
    }
  }
}
