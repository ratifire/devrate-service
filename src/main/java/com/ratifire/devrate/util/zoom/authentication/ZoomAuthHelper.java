package com.ratifire.devrate.util.zoom.authentication;

import com.ratifire.devrate.util.zoom.exception.ZoomAuthException;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Helper class for Zoom authentication.
 */
@Component
@RequiredArgsConstructor
public class ZoomAuthHelper {

  private static final int MINUTES_BEFORE_TOKEN_EXPIRED = 10;
  private long tokenExpiryTime;
  private ZoomAuthResponse authResponse;
  private final ZoomAuthRequest authRequest;
  private final RestTemplate restTemplate;

  /**
   * Retrieve authentication token.
   */
  public synchronized String getAuthenticationToken() throws ZoomAuthException {
    if (this.authResponse == null || checkIfTokenWillExpire()) {
      fetchToken();
    }
    return this.authResponse.getAccessToken();
  }

  /**
   * Determine where new token should be retrieved.
   */
  private boolean checkIfTokenWillExpire() {
    Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

    long differenceInMillis = this.tokenExpiryTime - now.getTimeInMillis();

    if (differenceInMillis < 0) {
      return true;
    }

    return TimeUnit.MILLISECONDS.toMinutes(differenceInMillis) < MINUTES_BEFORE_TOKEN_EXPIRED;
  }

  private void fetchToken() throws ZoomAuthException {
    String credentials = authRequest.getZoomClientId() + ":" + authRequest.getZoomClientSecret();
    String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(List.of(MediaType.APPLICATION_FORM_URLENCODED));
    headers.add("Authorization", "Basic " + encodedCredentials);
    headers.add("Host", "zoom.us");

    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("grant_type", "account_credentials");
    map.add("account_id", authRequest.getZoomAccountId());

    HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
    String url = authRequest.getZoomIssuerUrl() + "/token";
    try {
      this.authResponse = restTemplate.exchange(url, HttpMethod.POST, entity,
          ZoomAuthResponse.class).getBody();
    } catch (HttpClientErrorException ex) {
      ResponseEntity<String> res =
          new ResponseEntity<>(ex.getResponseBodyAsString(), ex.getStatusCode());
      String message = String.format("Unable to get authentication token due to %s. "
              + "Response code: %d", res.getBody(), res.getStatusCode().value());

      throw new ZoomAuthException(message, ex);
    }

    Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    this.tokenExpiryTime = now.getTimeInMillis() + (this.authResponse.getExpiresIn() - 10) * 1000;
  }
}
