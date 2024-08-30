package com.ratifire.devrate.util.zoom.network;

import com.ratifire.devrate.util.zoom.authentication.ZoomAuthHelper;
import com.ratifire.devrate.util.zoom.exception.ZoomApiException;
import com.ratifire.devrate.util.zoom.exception.ZoomAuthException;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Client for interacting with the Zoom API.
 */
@Component
@AllArgsConstructor
public class ZoomApiClient {

  private static final Logger logger = LoggerFactory.getLogger(ZoomApiClient.class);
  private static final String BEARER_AUTHORIZATION = "Bearer %s";

  private final RestTemplate restTemplate;
  private ZoomAuthHelper zoomAuthHelper;

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
      HttpEntity<?> httpEntity = createHttpEntity(jsonRequest);
      T body = restTemplate.postForEntity(url, httpEntity, response).getBody();
      return Optional.ofNullable(body);
    } catch (Throwable ex) {
      logger.error("Error occurred while sending POST request to URL: {}: {}", url,
          ex.getMessage());
      return Optional.empty();
    }
  }

  private HttpEntity<?> createHttpEntity(String jsonRequest) throws ZoomAuthException {
    HttpHeaders authHeader = createBearerAuthHeader(zoomAuthHelper
        .getAuthenticationToken());
    return new HttpEntity<>(jsonRequest, authHeader);
  }

  private HttpHeaders createBearerAuthHeader(String token) {
    HttpHeaders headers = createHttpHeader();
    String authToken = String.format(BEARER_AUTHORIZATION, token);
    headers.set(HttpHeaders.AUTHORIZATION, authToken);
    return headers;
  }

  private HttpHeaders createHttpHeader() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }

  /**
   * Performs a DELETE request to the specified URL.
   *
   * @param url the URL to which the DELETE request is sent.
   */
  public void delete(String url) throws ZoomApiException {
    try {
      HttpHeaders authHeader = createBearerAuthHeader(zoomAuthHelper.getAuthenticationToken());
      HttpEntity<?> httpEntity = new HttpEntity<>(authHeader);
      restTemplate.exchange(url, HttpMethod.DELETE, httpEntity, Void.class);
    } catch (Throwable ex) {
      logger.error("Error occurred while sending DELETE request to URL: {}: {}",
          url, ex.getMessage());
    }
  }
}
