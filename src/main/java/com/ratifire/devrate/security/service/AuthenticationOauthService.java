package com.ratifire.devrate.security.service;

import static com.ratifire.devrate.security.model.enums.CognitoTypeToken.ID_TOKEN;
import static com.ratifire.devrate.security.model.enums.CognitoTypeToken.REFRESH_TOKEN;

import com.amazonaws.services.cognitoidp.model.AWSCognitoIdentityProviderException;
import com.ratifire.devrate.dto.LoginResponseDto;
import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.security.exception.OauthException;
import com.ratifire.devrate.security.factory.LoginResponseFactory;
import com.ratifire.devrate.security.helper.AuthTokenHelper;
import com.ratifire.devrate.security.helper.CognitoApiClientRequestHelper;
import com.ratifire.devrate.security.model.CognitoUserInfo;
import com.ratifire.devrate.security.model.dto.OauthAuthorizationDto;
import com.ratifire.devrate.security.service.account.AccountLifecycleService;
import com.ratifire.devrate.security.util.TokenUtil;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;


/**
 * Service responsible for handling authentication and authorization via OAuth with AWS Cognito.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Profile("!local")
public class AuthenticationOauthService {

  private final CognitoApiClientService cognitoApiClient;
  private final AccountLifecycleService accountLifecycleService;
  private final OauthInternalUserResolver internalUserResolver;
  private final OauthStateTokenService stateTokenService;
  private final CognitoApiClientRequestHelper apiRequestHelper;
  private final AuthTokenHelper authTokenHelper;
  private final DataMapper<UserDto, User> userMapper;

  /**
   * Generates an OAuth redirect URL for the specified OAuth provider.
   *
   * @param oauthProvider the name of the OAuth provider
   * @return the constructed redirect URL for the specified OAuth provider
   */
  public String generateOauthRedirectUrl(String oauthProvider) {
    String signedStateToken = stateTokenService.generateSignedStateToken();
    return apiRequestHelper.buildOauthRedirectUrl(oauthProvider, signedStateToken);
  }

  /**
   * Handles the OAuth authorization flow by exchanging the authorization code for tokens,
   * processing the user information, refreshing authentication tokens, and setting the response
   * with updated tokens.
   *
   * @param response the HttpServletResponse to which the tokens will be added
   * @param request  the OauthAuthorizationDto containing the authorization code and other necessary
   *                 details for OAuth processing
   * @return a UserDto object representing the internal user mapped from the processed data
   */
  public LoginResponseDto handleOauthAuthorization(HttpServletResponse response,
      OauthAuthorizationDto request) {
    try {
      stateTokenService.validateStateToken(request.getState());

      Map<String, String> rawTokens =
          cognitoApiClient.fetchRawTokensFromCognito(request.getAuthorizationCode());

      String idToken = rawTokens.get(ID_TOKEN.getValue());
      String refreshToken = rawTokens.get(REFRESH_TOKEN.getValue());

      CognitoUserInfo cognitoUserInfo = TokenUtil.getCognitoUserInfoFromIdToken(idToken);

      User internalUser = internalUserResolver.resolveOrCreateInternalUser(cognitoUserInfo);

      if (Boolean.FALSE.equals(internalUser.getAccountActivated())) {
        return accountLifecycleService.handleInactiveAccountFlow(
            response, internalUser, cognitoUserInfo.cognitoUsername(), refreshToken, true);
      }

      authTokenHelper.setAuthTokensToResponse(
          response, cognitoUserInfo.cognitoUsername(), null, idToken, refreshToken, true, true);

      return LoginResponseFactory.success(userMapper.toDto(internalUser));

    } catch (HttpClientErrorException e) {
      log.error("HTTP Error during token exchange: {}", e.getMessage(), e);
      throw new OauthException(
          "OAuth authentication process failed during the exchange of code for tokens.", e);
    } catch (AWSCognitoIdentityProviderException e) {
      log.error("Cognito Error during OAuth authorization process: {}", e.getMessage(), e);
      throw new OauthException("OAuth authentication process failed. Cognito error: " + e);
    } catch (RuntimeException e) {
      log.error("Error during OAuth authorization process: {}", e.getMessage(), e);
      throw new OauthException("OAuth authentication process failed.", e);
    }
  }

}