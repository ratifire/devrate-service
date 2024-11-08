package com.ratifire.devrate.security.service;

import static com.amazonaws.services.cognitoidp.model.AuthFlowType.REFRESH_TOKEN_AUTH;
import static com.amazonaws.services.cognitoidp.model.AuthFlowType.USER_PASSWORD_AUTH;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.cognitoidp.model.ConfirmForgotPasswordRequest;
import com.amazonaws.services.cognitoidp.model.ConfirmSignUpRequest;
import com.amazonaws.services.cognitoidp.model.ForgotPasswordRequest;
import com.amazonaws.services.cognitoidp.model.GlobalSignOutRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.NotAuthorizedException;
import com.amazonaws.services.cognitoidp.model.SignUpRequest;
import com.ratifire.devrate.security.configuration.CognitoRegistrationProperties;
import com.ratifire.devrate.security.exception.RefreshTokenExpiredException;
import com.ratifire.devrate.security.helper.CognitoAuthenticationHelper;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for interacting with AWS Cognito API.
 */
@Service
@RequiredArgsConstructor
public class CognitoApiClientService {

  private final AWSCognitoIdentityProvider cognitoClient;
  private final CognitoAuthenticationHelper cognitoAuthHelper;
  private final CognitoRegistrationProperties cognitoConfig;


  /**
   * Registers a new user in Cognito with the provided email, password, user ID, and role.
   *
   * @param email    The user's email, which will serve as the username.
   * @param password The user's password.
   * @param userId   The unique identifier for the user.
   * @param role     The user's role.
   */
  public void register(String email, String password, long userId, String role) {
    SignUpRequest signUpRequest = new SignUpRequest()
        .withClientId(cognitoConfig.getClientId())
        .withSecretHash(cognitoAuthHelper.generateSecretHash(email))
        .withUsername(email)
        .withPassword(password)
        .withUserAttributes(List.of(
            createAttribute("email", email),
            createAttribute("custom:userId", String.valueOf(userId)),
            createAttribute("custom:role", role)
        ));
    cognitoClient.signUp(signUpRequest).getUserSub();
  }

  /**
   * Confirms user registration in Cognito using the provided confirmation code.
   *
   * @param email            The user's email (username).
   * @param confirmationCode The confirmation code sent to the user's email.
   */
  public void confirmRegistration(String email, String confirmationCode) {
    ConfirmSignUpRequest confirmSignUpRequest = new ConfirmSignUpRequest()
        .withClientId(cognitoConfig.getClientId())
        .withSecretHash(cognitoAuthHelper.generateSecretHash(email))
        .withUsername(email)
        .withConfirmationCode(confirmationCode);
    cognitoClient.confirmSignUp(confirmSignUpRequest);
  }

  /**
   * Authenticates an existing user in Cognito with the provided email and password.
   *
   * @param email    The user's email (username).
   * @param password The user's password.
   * @return An {@link AuthenticationResultType} object containing authentication details such as
   *     access token, refresh token, and ID token.
   */
  public AuthenticationResultType login(String email, String password) {
    String secretHash = cognitoAuthHelper.generateSecretHash(email);
    InitiateAuthRequest authRequest = new InitiateAuthRequest()
        .withClientId(cognitoConfig.getClientId())
        .withAuthFlow(USER_PASSWORD_AUTH)
        .withAuthParameters(Map.of(
            "USERNAME", email,
            "PASSWORD", password,
            "SECRET_HASH", secretHash));
    return cognitoClient.initiateAuth(authRequest).getAuthenticationResult();
  }

  /**
   * Logs out a user by invalidating their current session using the provided access token.
   *
   * @param accessToken The access token of the user to be logged out.
   */
  public void logout(String accessToken) {
    GlobalSignOutRequest signOutRequest = new GlobalSignOutRequest()
        .withAccessToken(accessToken);
    cognitoClient.globalSignOut(signOutRequest);

  }

  /**
   * Initiates the password reset process by sending a confirmation code to the user's email.
   *
   * @param email The user's email (username).
   */
  public void initiatePasswordReset(String email) {
    ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest()
        .withClientId(cognitoConfig.getClientId())
        .withSecretHash(cognitoAuthHelper.generateSecretHash(email))
        .withUsername(email);
    cognitoClient.forgotPassword(forgotPasswordRequest);
  }

  /**
   * Completes the password reset process using the provided confirmation code and new password.
   *
   * @param email            The user's email (username).
   * @param confirmationCode The confirmation code sent to the user's email.
   * @param newPassword      The new password for the user.
   */
  public void confirmPasswordReset(String email, String confirmationCode, String newPassword) {
    ConfirmForgotPasswordRequest confirmRequest = new ConfirmForgotPasswordRequest()
        .withClientId(cognitoConfig.getClientId())
        .withSecretHash(cognitoAuthHelper.generateSecretHash(email))
        .withUsername(email)
        .withConfirmationCode(confirmationCode)
        .withPassword(newPassword);
    cognitoClient.confirmForgotPassword(confirmRequest);
  }

  /**
   * Refreshes the access token using the provided refresh token and email (username).
   *
   * @param email        The email (username) of the user for whom the token is being refreshed.
   * @param refreshToken The refresh token issued during initial authentication, used to request a
   *                     new access token.
   * @return An {@link AuthenticationResultType} object containing the new access token, ID token,
   *     and other authentication details.
   */
  public AuthenticationResultType refreshAccessToken(String email, String refreshToken) {
    try {
      String secretHash = cognitoAuthHelper.generateSecretHash(email);
      InitiateAuthRequest authRequest = new InitiateAuthRequest()
          .withClientId(cognitoConfig.getClientId())
          .withAuthFlow(REFRESH_TOKEN_AUTH)
          .withAuthParameters(Map.of(
              "REFRESH_TOKEN", refreshToken,
              "USERNAME", email,
              "SECRET_HASH", secretHash
          ));
      return cognitoClient.initiateAuth(authRequest).getAuthenticationResult();
    } catch (NotAuthorizedException e) {
      throw new RefreshTokenExpiredException("The refresh token has expired. Please login again.");
    }
  }

  private AttributeType createAttribute(String name, String value) {
    return new AttributeType().withName(name).withValue(value);
  }
}