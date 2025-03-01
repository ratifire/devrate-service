package com.ratifire.devrate.security.helper;

import static com.amazonaws.services.cognitoidp.model.AuthFlowType.REFRESH_TOKEN_AUTH;
import static com.amazonaws.services.cognitoidp.model.AuthFlowType.USER_PASSWORD_AUTH;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.ConfirmForgotPasswordRequest;
import com.amazonaws.services.cognitoidp.model.ConfirmSignUpRequest;
import com.amazonaws.services.cognitoidp.model.ForgotPasswordRequest;
import com.amazonaws.services.cognitoidp.model.GlobalSignOutRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.ResendConfirmationCodeRequest;
import com.amazonaws.services.cognitoidp.model.SignUpRequest;
import com.ratifire.devrate.security.configuration.properties.CognitoRegistrationProperties;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Helper class for building AWS Cognito API requests.
 */
@Component
@RequiredArgsConstructor
public class CognitoApiClientRequestHelper {

  private static final String USERNAME = "USERNAME";
  private static final String REFRESH_TOKEN = "REFRESH_TOKEN";
  private static final String PASSWORD = "PASSWORD";
  private static final String SECRET_HASH = "SECRET_HASH";
  private final CognitoAuthenticationHelper cognitoAuthHelper;
  private final CognitoRegistrationProperties cognitoConfig;

  /**
   * Builds a request to register a new user in AWS Cognito.
   *
   * @param email    the email of the user (used as the username in Cognito).
   * @param password the password of the user.
   * @param userId   the unique identifier of the user.
   * @param role     the role assigned to the user.
   * @return a {@link SignUpRequest} object configured with the provided details.
   */
  public SignUpRequest buildRegisterRequest(String email, String password,
      long userId, String role) {
    return new SignUpRequest()
        .withClientId(cognitoConfig.getClientId())
        .withSecretHash(cognitoAuthHelper.generateSecretHash(email))
        .withUsername(email)
        .withPassword(password)
        .withUserAttributes(List.of(
            createAttribute("email", email),
            createAttribute("custom:userId", String.valueOf(userId)),
            createAttribute("custom:role", role)
        ));
  }

  /**
   * Builds a request to confirm a user's registration in AWS Cognito.
   *
   * @param email            the email of the user (username).
   * @param confirmationCode the confirmation code sent to the user.
   * @return a {@link ConfirmSignUpRequest} object configured with the provided details.
   */
  public ConfirmSignUpRequest buildConfirmRegistrationRequest(String email,
      String confirmationCode) {
    return new ConfirmSignUpRequest()
        .withClientId(cognitoConfig.getClientId())
        .withSecretHash(cognitoAuthHelper.generateSecretHash(email))
        .withUsername(email)
        .withConfirmationCode(confirmationCode);
  }

  /**
   * Builds a request to resend the registration confirmation code to a user in AWS Cognito.
   *
   * @param email the email of the user (used as the username in Cognito).
   * @return a {@link ResendConfirmationCodeRequest} object configured with the provided email.
   */
  public ResendConfirmationCodeRequest buildResendRegistrationCodeRequest(String email) {
    return new ResendConfirmationCodeRequest()
        .withClientId(cognitoConfig.getClientId())
        .withSecretHash(cognitoAuthHelper.generateSecretHash(email))
        .withUsername(email);
  }

  /**
   * Builds a request to initiate a password reset for a user in AWS Cognito.
   *
   * @param email the email of the user (username).
   * @return a {@link ForgotPasswordRequest} object configured with the provided details.
   */
  public ForgotPasswordRequest buildResetPasswordRequest(String email) {
    return new ForgotPasswordRequest()
        .withClientId(cognitoConfig.getClientId())
        .withSecretHash(cognitoAuthHelper.generateSecretHash(email))
        .withUsername(email);
  }

  /**
   * Builds a request to confirm a password reset for a user in AWS Cognito.
   *
   * @param email            the email of the user (username).
   * @param confirmationCode the confirmation code sent to the user.
   * @param newPassword      the new password for the user.
   * @return a {@link ConfirmForgotPasswordRequest} object configured with the provided details.
   */
  public ConfirmForgotPasswordRequest buildConfirmResetPasswordRequest(String email,
      String confirmationCode, String newPassword) {
    return new ConfirmForgotPasswordRequest()
        .withClientId(cognitoConfig.getClientId())
        .withSecretHash(cognitoAuthHelper.generateSecretHash(email))
        .withUsername(email)
        .withConfirmationCode(confirmationCode)
        .withPassword(newPassword);
  }

  /**
   * Builds a request to log in a user using their email and password.
   *
   * @param email    the email of the user (username).
   * @param password the password of the user.
   * @return an {@link InitiateAuthRequest} object configured with the provided details.
   */
  public InitiateAuthRequest buildLoginRequest(String email, String password) {
    return new InitiateAuthRequest()
        .withClientId(cognitoConfig.getClientId())
        .withAuthFlow(USER_PASSWORD_AUTH)
        .withAuthParameters(Map.of(
            USERNAME, email,
            PASSWORD, password,
            SECRET_HASH, cognitoAuthHelper.generateSecretHash(email)
        ));
  }

  /**
   * Builds a request to refresh authentication tokens using a refresh token.
   *
   * @param sub          the unique identifier (subject) of the user whose tokens are being
   *                     refreshed.
   * @param refreshToken the refresh token issued during initial authentication.
   * @return an {@link InitiateAuthRequest} object configured with the provided details.
   */
  public InitiateAuthRequest buildRefreshAuthRequest(String sub, String refreshToken) {
    return new InitiateAuthRequest()
        .withClientId(cognitoConfig.getClientId())
        .withAuthFlow(REFRESH_TOKEN_AUTH)
        .withAuthParameters(Map.of(
            REFRESH_TOKEN, refreshToken,
            USERNAME, sub,
            SECRET_HASH, cognitoAuthHelper.generateSecretHash(sub)
        ));
  }

  /**
   * Builds a request to log out a user by invalidating their current session.
   *
   * @param accessToken the access token of the user's current session.
   * @return a {@link GlobalSignOutRequest} object configured with the provided details.
   */
  public GlobalSignOutRequest buildLogoutRequest(String accessToken) {
    return new GlobalSignOutRequest()
        .withAccessToken(accessToken);
  }

  private AttributeType createAttribute(String name, String value) {
    return new AttributeType()
        .withName(name)
        .withValue(value);
  }
}