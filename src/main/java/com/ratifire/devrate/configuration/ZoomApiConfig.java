package com.ratifire.devrate.configuration;

import com.ratifire.devrate.util.zoom.authentication.ZoomAuthHelper;
import com.ratifire.devrate.util.zoom.exception.ZoomAuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Configuration
@RequiredArgsConstructor
public class ZoomApiConfig {

    private static final String BEARER_AUTHORIZATION = "Bearer %s";
    private final ZoomAuthHelper zoomAuthHelper;

    @Bean
    public HttpHeaders zoomAuthHeader() throws ZoomAuthException {
        String token = zoomAuthHelper.getAuthenticationToken();
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
}
