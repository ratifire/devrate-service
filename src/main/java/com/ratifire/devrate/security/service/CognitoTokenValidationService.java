package com.ratifire.devrate.security.service;

import static com.nimbusds.jose.JWSAlgorithm.RS256;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.ratifire.devrate.security.exception.AuthTokenExpiredException;
import com.ratifire.devrate.security.exception.TokenValidationException;
import com.ratifire.devrate.security.model.enums.CognitoTypeToken;
import com.ratifire.devrate.security.token.validation.TokenClaimsValidator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Service responsible for validating JWT tokens from AWS Cognito.
 */
@Service
@Profile("!local")
public class CognitoTokenValidationService {

  private static final Logger log = LoggerFactory.getLogger(CognitoTokenValidationService.class);
  private final JWKSource<SecurityContext> jwkSource;
  private final Map<CognitoTypeToken, TokenClaimsValidator> claimValidators;

  /**
   * Constructs a CognitoTokenValidationService with the specified JWK source and list of claim
   * validators.
   */
  public CognitoTokenValidationService(JWKSource<SecurityContext> jwkSource,
      List<TokenClaimsValidator> claimValidators) {
    this.jwkSource = jwkSource;
    this.claimValidators = initializeValidatorsMap(claimValidators);
  }

  /**
   * Validates the specified token by checking its signature and claims.
   *
   * @param token the JWT token to validate
   * @param type  the expected type of the token (ACCESS_TOKEN, ID_TOKEN)
   * @return true if the token is valid, otherwise false
   * @throws TokenValidationException if the token is invalid or validation fails
   */
  public boolean validateToken(String token, CognitoTypeToken type) {
    try {

      SignedJWT signedJwt = SignedJWT.parse(token);
      JWTClaimsSet claimsSet = createJwtProcessor().process(signedJwt, null);
      TokenClaimsValidator validator = claimValidators.get(type);
      return validator.validate(claimsSet);

    } catch (AuthTokenExpiredException e) {
      throw e;
    } catch (Exception e) {
      log.error("Token validation process was failed: {}", e.getMessage(), e);
      throw new TokenValidationException("Token validation process was failed");
    }
  }

  /**
   * Creates and configures a JWT processor for validating token signatures using Cognito's JWK
   * source.
   *
   * @return A configured DefaultJWTProcessor instance.
   */
  private DefaultJWTProcessor<SecurityContext> createJwtProcessor() {
    JWSKeySelector<SecurityContext> selector = new JWSVerificationKeySelector<>(RS256, jwkSource);
    DefaultJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
    jwtProcessor.setJWSKeySelector(selector);
    jwtProcessor.setJWTClaimsSetVerifier(null);
    return jwtProcessor;
  }

  private Map<CognitoTypeToken, TokenClaimsValidator> initializeValidatorsMap(
      List<TokenClaimsValidator> validators) {
    return validators.stream()
        .collect(Collectors.toMap(TokenClaimsValidator::getTokenType, Function.identity()));
  }
}
