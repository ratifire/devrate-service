package com.ratifire.devrate.security.service;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.cognitoidp.model.ConfirmForgotPasswordRequest;
import com.amazonaws.services.cognitoidp.model.ConfirmSignUpRequest;
import com.amazonaws.services.cognitoidp.model.ForgotPasswordRequest;
import com.amazonaws.services.cognitoidp.model.GlobalSignOutRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.ResendConfirmationCodeRequest;
import com.amazonaws.services.cognitoidp.model.SignUpRequest;
import com.ratifire.devrate.security.helper.CognitoApiClientRequestHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Service class for interacting with AWS Cognito API.
 */
@Service
@RequiredArgsConstructor
@Profile("!local")
public class CognitoApiClientService {

  private final AWSCognitoIdentityProvider cognitoClient;
  private final CognitoApiClientRequestHelper requestHelper;

  /**
   * Registers a new user in Cognito with the provided email, password, user ID, and role.
   *
   * @param email    The user's email, which will serve as the username.
   * @param password The user's password.
   * @param userId   The unique identifier for the user.
   * @param role     The user's role.
   */
  public void register(String email, String password, long userId, String role) {
    SignUpRequest request = requestHelper.buildRegisterRequest(email, password, userId, role);
    cognitoClient.signUp(request).getUserSub();
  }

  /**
   * Confirms user registration in Cognito using the provided confirmation code.
   *
   * @param email The user's email (username).
   * @param code  The confirmation code sent to the user's email.
   */
  public void confirmRegistration(String email, String code) {
    ConfirmSignUpRequest request = requestHelper.buildConfirmRegistrationRequest(email, code);
    cognitoClient.confirmSignUp(request);
  }

  /**
   * Resends the registration confirmation code to the specified user's email.
   *
   * @param email The user's email to which the confirmation code will be sent.
   */
  public void resendRegistrationCode(String email) {
    ResendConfirmationCodeRequest request = requestHelper.buildResendRegistrationCodeRequest(email);
    cognitoClient.resendConfirmationCode(request);
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
    InitiateAuthRequest request = requestHelper.buildLoginRequest(email, password);
    return cognitoClient.initiateAuth(request).getAuthenticationResult();
  }

  /**
   * Logs out a user by invalidating their current session using the provided access token.
   *
   * @param accessToken The access token of the user to be logged out.
   */
  public void logout(String accessToken) {
    GlobalSignOutRequest request = requestHelper.buildLogoutRequest(accessToken);
    cognitoClient.globalSignOut(request);
  }

  /**
   * Initiates the password reset process by sending a confirmation code to the user's email.
   *
   * @param email The user's email (username).
   */
  public void resetPassword(String email) {
    ForgotPasswordRequest request = requestHelper.buildResetPasswordRequest(email);
    cognitoClient.forgotPassword(request);
  }

  /**
   * Completes the password reset process using the provided confirmation code and new password.
   *
   * @param email            The user's email (username).
   * @param confirmationCode The confirmation code sent to the user's email.
   * @param newPassword      The new password for the user.
   */
  public void confirmResetPassword(String email, String confirmationCode, String newPassword) {
    ConfirmForgotPasswordRequest request =
        requestHelper.buildConfirmResetPasswordRequest(email, confirmationCode, newPassword);
    cognitoClient.confirmForgotPassword(request);
  }

  /**
   * Refreshes the user's authentication tokens using a refresh token.
   *
   * @param sub          the unique identifier (subject) of the user whose tokens are being
   *                     refreshed.
   * @param refreshToken the refresh token issued during the initial authentication process.
   * @return an {@link AuthenticationResultType} object containing the new access token, ID token,
   *     and other authentication details.
   */
  public AuthenticationResultType refreshAuthTokens(String sub, String refreshToken) {
    InitiateAuthRequest request = requestHelper.buildRefreshAuthRequest(sub, refreshToken);
    return cognitoClient.initiateAuth(request).getAuthenticationResult();
  }
}
