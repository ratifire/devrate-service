package com.ratifire.devrate.configuration;

import com.ratifire.devrate.util.zoom.authentication.ZoomAuthHelper;
import com.ratifire.devrate.util.zoom.exception.ZoomAuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

/**
 * Configures the Zoom API authorization header using a token from ZoomAuthHelper.
 */
@Configuration
@RequiredArgsConstructor
public class ZoomApiConfig {

  private static final String BEARER_AUTHORIZATION = "Bearer %s";
  private final ZoomAuthHelper zoomAuthHelper;

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  /**
   * Provides HttpHeaders with the Zoom API authorization token.
   *
   * @return HttpHeaders containing the authorization token.
   * @throws ZoomAuthException if token retrieval fails.
   */
  @Bean
  public HttpHeaders zoomAuthHeader() throws ZoomAuthException {
    String token = zoomAuthHelper.getAuthenticationToken();
    HttpHeaders headers = createHttpHeader();
    String authToken = String.format(BEARER_AUTHORIZATION, token);
    headers.set(HttpHeaders.AUTHORIZATION, authToken);
    return headers;
  }

  /**
   * Creates HttpHeaders with content type set to JSON.
   *
   * @return HttpHeaders with JSON content type.
   */
  private HttpHeaders createHttpHeader() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }
}
