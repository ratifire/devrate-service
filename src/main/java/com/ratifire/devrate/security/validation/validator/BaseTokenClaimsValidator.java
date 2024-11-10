package com.ratifire.devrate.security.validation.validator;

import com.nimbusds.jwt.JWTClaimsSet;
import com.ratifire.devrate.security.configuration.CognitoProviderProperties;
import com.ratifire.devrate.security.configuration.CognitoRegistrationProperties;
import com.ratifire.devrate.security.util.TokenUtil;
import java.util.Date;
import lombok.RequiredArgsConstructor;

/**
 * Abstract base class for validating common JWT token claims.
 */
@RequiredArgsConstructor
public abstract class BaseTokenClaimsValidator implements TokenClaimsValidator {

  protected final CognitoProviderProperties cognitoProviderProperties;
  protected final CognitoRegistrationProperties cognitoRegistrationProperties;

  /**
   * Validates common claims within the JWT token.
   */
  protected boolean validateCommonClaims(JWTClaimsSet claimsSet, String expectedTokenUse) {
    return isIssuerValid(claimsSet)
        && isExpirationTimeValid(claimsSet)
        && isTokenUseValid(claimsSet, expectedTokenUse);
  }

  /**
   * Validates the "iss" (issuer) claim in the token.
   */
  private boolean isIssuerValid(JWTClaimsSet claimsSet) {
    return TokenUtil.extractStringClaim(claimsSet, "iss")
        .map(cognitoProviderProperties.getIssuerUri()::equals)
        .orElse(false);
  }

  /**
   * Validates the expiration time ("exp") claim in the token.
   */
  private boolean isExpirationTimeValid(JWTClaimsSet claimsSet) {
    return TokenUtil.extractDateClaim(claimsSet, "exp")
        .map(expiration -> new Date().before(expiration))
        .orElse(false);
  }

  /**
   * Validates the "token_use" claim in the token.
   */
  private boolean isTokenUseValid(JWTClaimsSet claimsSet, String expectedTokenUse) {
    return TokenUtil.extractStringClaim(claimsSet, "token_use")
        .map(expectedTokenUse::equals)
        .orElse(false);
  }
}