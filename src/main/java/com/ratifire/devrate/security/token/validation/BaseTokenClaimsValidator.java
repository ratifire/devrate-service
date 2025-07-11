package com.ratifire.devrate.security.token.validation;

import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_EXPIRATION_TIME;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_ISSUER;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_TOKEN_USE;

import com.nimbusds.jwt.JWTClaimsSet;
import com.ratifire.devrate.security.configuration.properties.CognitoProviderProperties;
import com.ratifire.devrate.security.configuration.properties.CognitoRegistrationProperties;
import com.ratifire.devrate.security.exception.AuthTokenExpiredException;
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
    return TokenUtil.extractStringClaim(claimsSet, ATTRIBUTE_ISSUER)
        .map(cognitoProviderProperties.getIssuerUri()::equals)
        .orElse(false);
  }

  /**
   * Validates the expiration time ("exp") claim in the token.
   */
  private boolean isExpirationTimeValid(JWTClaimsSet claimsSet) {
    return TokenUtil.extractDateClaim(claimsSet, ATTRIBUTE_EXPIRATION_TIME)
        .map(this::verifyTokenExpiration)
        .orElse(false);
  }

  /**
   * Validates the "token_use" claim in the token.
   */
  private boolean isTokenUseValid(JWTClaimsSet claimsSet, String expectedTokenUse) {
    return TokenUtil.extractStringClaim(claimsSet, ATTRIBUTE_TOKEN_USE)
        .map(expectedTokenUse::equals)
        .orElse(false);
  }

  private boolean verifyTokenExpiration(Date exp) {
    if (new Date().after(exp)) {
      throw new AuthTokenExpiredException("Token has expired.");
    }
    return true;
  }
}