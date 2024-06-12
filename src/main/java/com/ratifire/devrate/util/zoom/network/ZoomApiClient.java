package com.ratifire.devrate.util.zoom.network;

import com.ratifire.devrate.util.zoom.authentication.ZoomAuthHelper;
import com.ratifire.devrate.util.zoom.exception.ZoomApiException;
import com.ratifire.devrate.util.zoom.exception.ZoomAuthException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Client for interacting with the Zoom API.
 */
@Component
@AllArgsConstructor
public class ZoomApiClient {

  private static final String BEARER_AUTHORIZATION = "Bearer %s";

  private ZoomAuthHelper zoomAuthHelper;
  private final RestTemplate restTemplate;

  /**
   * Performs a POST request to the specified URL with the provided JSON request payload
   * and returns the response of the specified type.
   *
   * @param url the URL to which the POST request is sent.
   * @param jsonRequest the JSON request payload to be sent in the request body.
   * @param response the class of the response type.
   * @param <T> the type of the response.
   * @return the response object of the specified type.
   * @throws ZoomApiException if an error occurs while performing the POST request.
   */
  public <T> T post(String url, String jsonRequest, Class<T> response) throws ZoomApiException {
    try {
      HttpEntity<?> httpEntity = createHttpEntity(jsonRequest);
      return restTemplate.postForEntity(url, httpEntity, response).getBody();
    } catch (Throwable ex) {
      throw new ZoomApiException(ex.getMessage(), ex);
    }
  }

  private HttpEntity<?> createHttpEntity(String jsonRequest) throws ZoomAuthException {
    HttpHeaders authHeader = createBearerAuthHeader(zoomAuthHelper
        .getAuthenticationToken());
    return new HttpEntity<>(jsonRequest, authHeader);
  }

  private static HttpHeaders createBearerAuthHeader(String token) {
    HttpHeaders headers = createHttpHeader();
    String authToken = String.format(BEARER_AUTHORIZATION, token);
    headers.set(HttpHeaders.AUTHORIZATION, authToken);
    return headers;
  }

  private static HttpHeaders createHttpHeader() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }
}
