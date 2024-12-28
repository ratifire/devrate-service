package com.ratifire.devrate.security.service;

import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_IS_PRIMARY_RECORD;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_PROVIDER_NAME;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.NONE_VALUE;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.cognitoidp.model.ListUsersResult;
import com.amazonaws.services.cognitoidp.model.ProviderUserIdentifierType;
import com.amazonaws.services.cognitoidp.model.UserType;
import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.security.exception.AuthenticationException;
import com.ratifire.devrate.security.helper.CognitoApiClientRequestHelper;
import com.ratifire.devrate.security.helper.CognitoAuthenticationHelper;
import com.ratifire.devrate.security.helper.RefreshTokenCookieHelper;
import com.ratifire.devrate.security.model.PoolUserInfo;
import com.ratifire.devrate.security.model.dto.OauthExchangeCodeRequest;
import com.ratifire.devrate.security.model.enums.AccessLevel;
import com.ratifire.devrate.security.util.TokenUtil;
import com.ratifire.devrate.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

/**
 * test sso.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationOauthService {

  private final CognitoApiClientService cognitoApiClient;
  private final UserService userService;
  private final CognitoApiClientRequestHelper apiRequestHelper;
  private final CognitoAuthenticationHelper authHelper;
  private final RefreshTokenCookieHelper refreshTokenCookieHelper;
  private final DataMapper<UserDto, User> userMapper;

  public String generateOauthRedirectUrl(HttpSession session, String provider) {
    String state = UUID.randomUUID().toString();
    session.setAttribute("oauth2State", state);
    return apiRequestHelper.buildOauthRedirectUrl(provider, state);
  }

  public UserDto exchangeAuthorizationCodeForTokens(HttpServletResponse response,
      OauthExchangeCodeRequest request) {
    try {
      Map<String, String> rawTokens =
          cognitoApiClient.exchangeAuthCodeForRawTokens(request.getCode());

      String accessToken = rawTokens.get("access_token");
      String idToken = rawTokens.get("id_token");
      String refreshToken = rawTokens.get("refresh_token");

      PoolUserInfo poolUserInfo = TokenUtil.getUserInfoFromIdToken(idToken);
      String poolUserEmail = poolUserInfo.email();
      String poolUserSubject = poolUserInfo.subject();
      String poolUserProvider = poolUserInfo.provider();
      String poolUserLinkedRecord = poolUserInfo.linkedRecord();
      String poolUserCognitoUsername = poolUserInfo.cognitoUsername();

      User internalUser = userService.findByEmail(poolUserEmail);

      if (ObjectUtils.isNotEmpty(internalUser)) {
        Optional<ProviderUserIdentifierType> poolPrimaryUserOptional = findPrimaryPoolUsersByEmail(
            poolUserEmail);

        if (poolPrimaryUserOptional.isEmpty()) {
          throw new AuthenticationException("User not found");
        }

        ProviderUserIdentifierType poolPrimaryUser = poolPrimaryUserOptional.get();
        String poolPrimaryUserSubject = poolPrimaryUser.getProviderAttributeValue();

        if (arePoolUsersLinked(poolUserLinkedRecord, poolPrimaryUserSubject)) {
          TokenUtil.setAuthTokensToHeaders(response, accessToken, idToken);
          refreshTokenCookieHelper.setRefreshTokenToCookie(response, refreshToken);
          return userMapper.toDto(internalUser);
        }

        linkUsersInPool(poolPrimaryUser, poolUserProvider, poolUserSubject);
        cognitoApiClient.updatePoolUserAttributes(poolUserSubject, internalUser.getId(),
            AccessLevel.getDefaultRole(), false, poolPrimaryUserSubject);

      } else {
        UserDto userDto = UserDto.builder()
            .firstName(poolUserInfo.firstName())
            .lastName(poolUserInfo.lastName())
            .country("Ukraine")
            .subscribed(false)
            .build();
        User newInternalUser = userService.create(userDto, poolUserEmail,
            authHelper.generateRandomPassword());

        cognitoApiClient.updatePoolUserAttributes(poolUserSubject, newInternalUser.getId(),
            AccessLevel.getDefaultRole(), true, NONE_VALUE);
      }

      AuthenticationResultType updatedTokens = cognitoApiClient.refreshAuthTokens(
          poolUserCognitoUsername, refreshToken);

      TokenUtil.setAuthTokensToHeaders(response, updatedTokens.getAccessToken(),
          updatedTokens.getIdToken());
      refreshTokenCookieHelper.setRefreshTokenToCookie(response, refreshToken);

      return userMapper.toDto(internalUser);
    } catch (HttpClientErrorException e) {
      log.error("HTTP Error during token exchange: {}", e.getResponseBodyAsString(), e);
      throw new AuthenticationException("Failed to exchange authorization code for tokens.");
    } catch (Exception e) {
      log.error("Error during token exchange or user processing: {}", e.getMessage(), e);
      throw new AuthenticationException("Authentication process failed.");
    }
  }

  private Optional<ProviderUserIdentifierType> findPrimaryPoolUsersByEmail(String email) {
    ListUsersResult response = cognitoApiClient.getListPoolUsersByEmail(email);
    return response.getUsers().stream()
        .filter(user -> user.getAttributes().stream()
            .anyMatch(attribute ->
                StringUtils.equals(attribute.getName(), ATTRIBUTE_IS_PRIMARY_RECORD) &&
                    StringUtils.equals(attribute.getValue(), "true")
            ))
        .findFirst()
        .map(this::mapToProviderUserIdentifier);
  }

  private ProviderUserIdentifierType mapToProviderUserIdentifier(UserType user) {
    ProviderUserIdentifierType providerUser = new ProviderUserIdentifierType();
    providerUser.setProviderName(getProviderName(user.getAttributes()));
    providerUser.setProviderAttributeValue(user.getUsername());
    return providerUser;
  }

  private String getProviderName(List<AttributeType> attributes) {
    return attributes.stream()
        .filter(attribute -> StringUtils.equals(attribute.getName(), ATTRIBUTE_PROVIDER_NAME))
        .map(AttributeType::getValue)
        .findFirst()
        .orElse("Cognito");
  }

  private boolean arePoolUsersLinked(String poolUserLinkedRecord, String primaryPollUserSubject) {
    if (StringUtils.isEmpty(poolUserLinkedRecord)) {
      return false;
    }

    if (StringUtils.equals(poolUserLinkedRecord, primaryPollUserSubject)) {
      return true;
    }
    throw new IllegalStateException(String.format(
        "Pool user linked record (%s) does not match the primary pool user subject (%s)",
        poolUserLinkedRecord, primaryPollUserSubject));
  }

  private void linkUsersInPool(ProviderUserIdentifierType destinationUser, String provider,
      String subject) {
    cognitoApiClient.linkProviderForUser(destinationUser, provider, subject);
  }
}