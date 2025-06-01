package com.ratifire.devrate.security.service;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.ratifire.devrate.security.configuration.properties.Oauth2Properties;
import com.ratifire.devrate.security.exception.OauthException;
import com.ratifire.devrate.security.exception.TokenGenerationException;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

/**
 * Service for generating and validating signed JWT tokens used as OAuth state parameters.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OauthStateTokenService {

  private final Oauth2Properties oauth2Properties;

  /**
   * Generates a signed JWT token to be used as OAuth state.
   */
  public String generateSignedStateToken() {
    try {
      JWTClaimsSet jwtClaimsSet = buildJwtClaimsSet();
      JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
      JWSSigner signer = new MACSigner(oauth2Properties.getStateSecret().getBytes(UTF_8));

      SignedJWT signedJwt = new SignedJWT(header, jwtClaimsSet);
      signedJwt.sign(signer);

      return signedJwt.serialize();
    } catch (Exception e) {
      log.error("Failed to generate signed state JWT", e);
      throw new TokenGenerationException("Failed to generate signed state JWT", e);
    }
  }

  /**
   * Validates the provided OAuth state token.
   *
   * @param stateToken the JWT state token to validate
   * @throws OauthException if the token is invalid or expired
   */
  public void validateStateToken(String stateToken) {
    if (!isValid(stateToken)) {
      throw new OauthException("State token is invalid or expired");
    }
  }

  /**
   * Checks if the provided JWT state token is valid by verifying its signature and expiration
   * time.
   *
   * @param jwt the JWT state token to validate
   * @return true if the token is valid and not expired, false otherwise
   */
  private boolean isValid(String jwt) {
    try {
      SignedJWT signedJwt = SignedJWT.parse(jwt);
      MACVerifier macVerifier = new MACVerifier(oauth2Properties.getStateSecret().getBytes(UTF_8));

      if (!signedJwt.verify(macVerifier)) {
        return false;
      }

      Date expirationTime = signedJwt.getJWTClaimsSet().getExpirationTime();
      return ObjectUtils.isNotEmpty(expirationTime) && new Date().before(expirationTime);

    } catch (Exception e) {
      return false;
    }
  }

  private JWTClaimsSet buildJwtClaimsSet() {
    Date expirationTime = new Date(
        System.currentTimeMillis() + oauth2Properties.getStateLifeTime() * 1000L);
    return new JWTClaimsSet.Builder()
        .jwtID(UUID.randomUUID().toString())
        .issueTime(new Date())
        .expirationTime(expirationTime)
        .build();
  }

}