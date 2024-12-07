package com.ratifire.devrate.security.token.validation;

import com.nimbusds.jwt.JWTClaimsSet;
import com.ratifire.devrate.security.model.enums.CognitoTypeToken;

/**
 * Interface for validating JWT token claims.
 */
public interface TokenClaimsValidator {

  /**
   * Validates the claims within a JWT token.
   */
  boolean validate(JWTClaimsSet claimsSet);

  /**
   * Retrieves the type of token this validator is designed to validate.
   */
  CognitoTypeToken getTokenType();

}