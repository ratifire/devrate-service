package com.ratifire.devrate.security.configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.SecurityContext;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AWS Cognito client configuration class.
 */
@Configuration
@RequiredArgsConstructor
public class CognitoApiClientConfig {

  private static final long CACHE_TIME_TO_LIVE = 1200000L;   // 20 minutes
  private static final long REFRESH_AHEAD_TIME = 120000L;    // 2 minutes before cache expiration
  private static final long CACHE_REFRESH_TIMEOUT = 300000L; // 5 minutes for cache refresh timeout
  private static final long RATE_LIMIT_MIN_INTERVAL = 30000L;// 30 seconds rate limit interval
  private final AwsProperties awsProperties;

  /**
   * Creates and configures an {@link AWSCognitoIdentityProvider} bean using provided AWS
   * credentials.
   *
   * @return A configured {@link AWSCognitoIdentityProvider} instance.
   */
  @Bean
  public AWSCognitoIdentityProvider cognitoIdentityProvider() {
    BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsProperties.getAccessKeyId(),
        awsProperties.getSecretKey());
    AWSStaticCredentialsProvider provider = new AWSStaticCredentialsProvider(awsCredentials);
    Regions regions = Regions.fromName(awsProperties.getRegion());
    return AWSCognitoIdentityProviderClientBuilder.standard()
        .withCredentials(provider)
        .withRegion(regions)
        .build();
  }

  /**
   * Creates a {@link JWKSource} bean configured with the Cognito JWK Set URI for token validation.
   *
   * @return A configured {@link JWKSource} instance for fetching JWKs.
   */
  @Bean
  public JWKSource<SecurityContext> jwkSource(CognitoProviderProperties properties)
      throws MalformedURLException {
    URI jwkUri = URI.create(properties.getJwkSetUri());
    URL jwkUrl = jwkUri.toURL();
    return JWKSourceBuilder.create(jwkUrl)
        .cache(CACHE_TIME_TO_LIVE, CACHE_REFRESH_TIMEOUT)
        .refreshAheadCache(REFRESH_AHEAD_TIME, true)
        .rateLimited(RATE_LIMIT_MIN_INTERVAL)
        .build();
  }
}