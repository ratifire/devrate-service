package com.ratifire.devrate.security.token.validation;

import com.nimbusds.jwt.JWTClaimsSet;
import com.ratifire.devrate.security.configuration.properties.CognitoProviderProperties;
import com.ratifire.devrate.security.configuration.properties.CognitoRegistrationProperties;
import com.ratifire.devrate.security.model.enums.CognitoTypeToken;
import com.ratifire.devrate.security.util.TokenUtil;
import org.springframework.stereotype.Component;

/**
 * Validator for validating claims of access tokens from AWS Cognito.
 */
@Component
public class AccessTokenClaimsValidator extends BaseTokenClaimsValidator {

  private static final String EXPECTED_TOKEN_USE = "access";

  /**
   * Constructs an AccessTokenClaimsValidator with the specified properties for validating access
   * tokens.
   */
  public AccessTokenClaimsValidator(CognitoProviderProperties cognitoProviderProperties,
      CognitoRegistrationProperties cognitoRegistrationProperties) {
    super(cognitoProviderProperties, cognitoRegistrationProperties);
  }

  @Override
  public boolean validate(JWTClaimsSet claimsSet) {
    return validateCommonClaims(claimsSet, EXPECTED_TOKEN_USE)
        && isClientIdValid(claimsSet);
  }

  @Override
  public CognitoTypeToken getTokenType() {
    return CognitoTypeToken.ACCESS_TOKEN;
  }

  private boolean isClientIdValid(JWTClaimsSet claimsSet) {
    return TokenUtil.extractStringClaim(claimsSet, "client_id")
        .map(cognitoRegistrationProperties.getClientId()::equals)
        .orElse(false);
  }
}