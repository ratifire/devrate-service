package com.ratifire.devrate.security.validation.validator;

import com.nimbusds.jwt.JWTClaimsSet;
import com.ratifire.devrate.security.configuration.CognitoProviderProperties;
import com.ratifire.devrate.security.configuration.CognitoRegistrationProperties;
import com.ratifire.devrate.security.model.CognitoTypeToken;
import com.ratifire.devrate.security.util.TokenUtil;
import org.springframework.stereotype.Component;

/**
 * Validator for validating claims of id tokens from AWS Cognito.
 */
@Component
public class IdTokenClaimsValidator extends BaseTokenClaimsValidator {

  private static final String EXPECTED_TOKEN_USE = "id";

  /**
   * Constructs an IdTokenClaimsValidator with the specified properties for validating access
   * tokens.
   */
  public IdTokenClaimsValidator(CognitoProviderProperties cognitoProviderProperties,
      CognitoRegistrationProperties cognitoRegistrationProperties) {
    super(cognitoProviderProperties, cognitoRegistrationProperties);
  }

  @Override
  public boolean validate(JWTClaimsSet claimsSet) {
    return validateCommonClaims(claimsSet, EXPECTED_TOKEN_USE)
        && isAudienceValid(claimsSet);
  }

  @Override
  public CognitoTypeToken getTokenType() {
    return CognitoTypeToken.ID_TOKEN;
  }

  private boolean isAudienceValid(JWTClaimsSet claimsSet) {
    return TokenUtil.extractStringClaim(claimsSet, "aud")
        .map(audience -> audience.equals(cognitoRegistrationProperties.getClientId()))
        .orElse(false);
  }
}