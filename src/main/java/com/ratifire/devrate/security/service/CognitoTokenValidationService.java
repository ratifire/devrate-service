package com.ratifire.devrate.security.service;

import static com.nimbusds.jose.JWSAlgorithm.RS256;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.ratifire.devrate.security.exception.TokenValidationException;
import com.ratifire.devrate.security.model.CognitoTypeToken;
import com.ratifire.devrate.security.validation.validator.TokenClaimsValidator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Service responsible for validating JWT tokens from AWS Cognito.
 */
@Service
public class CognitoTokenValidationService {

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
    } catch (Exception e) {
      throw new TokenValidationException("Failed to validate token");
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
    return jwtProcessor;
  }

  private Map<CognitoTypeToken, TokenClaimsValidator> initializeValidatorsMap(
      List<TokenClaimsValidator> validators) {
    return validators.stream()
        .collect(Collectors.toMap(TokenClaimsValidator::getTokenType, Function.identity()));
  }
}