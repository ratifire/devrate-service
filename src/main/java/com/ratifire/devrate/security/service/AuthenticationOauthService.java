package com.ratifire.devrate.security.service;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminLinkProviderForUserRequest;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.cognitoidp.model.ListUsersRequest;
import com.amazonaws.services.cognitoidp.model.ListUsersResult;
import com.amazonaws.services.cognitoidp.model.ProviderUserIdentifierType;
import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.security.configuration.properties.CognitoRegistrationProperties;
import com.ratifire.devrate.security.exception.AuthenticationException;
import com.ratifire.devrate.security.helper.CognitoApiClientRequestHelper;
import com.ratifire.devrate.security.helper.CognitoAuthenticationHelper;
import com.ratifire.devrate.security.helper.RefreshTokenCookieHelper;
import com.ratifire.devrate.security.model.UserInfo;
import com.ratifire.devrate.security.model.dto.OauthExchangeCodeRequest;
import com.ratifire.devrate.security.model.enums.AccessLevel;
import com.ratifire.devrate.security.util.TokenUtil;
import com.ratifire.devrate.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

/**
 * test sso.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationOauthService {

  private final CognitoApiClientService apiClient;
  private final UserService userService;
  private final CognitoApiClientRequestHelper requestHelper;
  private final CognitoAuthenticationHelper authHelper;
  private final RefreshTokenCookieHelper refreshTokenCookieHelper;
  private final DataMapper<UserDto, User> userMapper;

  private final AWSCognitoIdentityProvider cognitoClient;
  private final CognitoRegistrationProperties cognitoConfig;


  public String generateOauthRedirectUrl(HttpSession session, String provider) {
    String state = UUID.randomUUID().toString();
    session.setAttribute("oauth2State", state);
    return requestHelper.buildOauthRedirectUrl(provider, state);
  }

  public UserDto exchangeAuthCodeForTokens(HttpServletResponse response,
      OauthExchangeCodeRequest request) {
    try {
      Map<String, String> rawTokens =
          apiClient.exchangeAuthCodeForRawTokens(request.getCode());

      String accessToken = rawTokens.get("access_token");
      String idToken = rawTokens.get("id_token");
      String refreshToken = rawTokens.get("refresh_token");

      UserInfo userInfo = TokenUtil.getUserInfoFromIdToken(idToken);

      User existingUser = userService.findByEmail(userInfo.email());
      if (existingUser == null) {
        UserDto userDto = UserDto.builder()
            .firstName(userInfo.firstName())
            .lastName(userInfo.lastName())
            .country("Ukraine")
            .subscribed(false)
            .build();
        User newUser = userService.create(userDto, userInfo.email(),
            authHelper.generateRandomPassword());

        apiClient.updateUserAttributes(
            userInfo.subject(), newUser.getId(), AccessLevel.getDefaultRole());

        AuthenticationResultType updatedTokens =
            apiClient.refreshAuthTokens(userInfo.subject(), refreshToken);

        TokenUtil.setAuthTokensToHeaders(response, updatedTokens.getAccessToken(),
            updatedTokens.getIdToken());
        refreshTokenCookieHelper.setRefreshTokenToCookie(response, refreshToken);

      } else {
        List<ProviderUserIdentifierType> allLinkedUsers = findAllLinkedUsersByEmail(
            userInfo.email());
        ProviderUserIdentifierType masterUser = findMasterRecord(allLinkedUsers, userInfo.email());

        if (isUserLinked(userInfo.subject(), existingUser.getId())) {
          TokenUtil.setAuthTokensToHeaders(response, accessToken, idToken);
          refreshTokenCookieHelper.setRefreshTokenToCookie(response, refreshToken);
        } else {

          linkUsers(allLinkedUsers, masterUser, existingUser.getEmail(), userInfo.provider(),
              userInfo.subject());
          apiClient.updateUserAttributes(userInfo.subject(), existingUser.getId(),
              AccessLevel.getDefaultRole());

          AuthenticationResultType updatedTokens = apiClient.refreshAuthTokens(userInfo.subject(),
              refreshToken);

          TokenUtil.setAuthTokensToHeaders(response, updatedTokens.getAccessToken(),
              updatedTokens.getIdToken());
          refreshTokenCookieHelper.setRefreshTokenToCookie(response, refreshToken);
        }
      }
      return userMapper.toDto(existingUser);
    } catch (HttpClientErrorException e) {
      log.error("HTTP Error during token exchange: {}", e.getResponseBodyAsString(), e);
      throw new AuthenticationException("Failed to exchange authorization code for tokens.");
    } catch (Exception e) {
      log.error("Error during token exchange or user processing: {}", e.getMessage(), e);
      throw new AuthenticationException("Authentication process failed.");
    }
  }

  private List<ProviderUserIdentifierType> findAllLinkedUsersByEmail(String email) {
    try {
      ListUsersRequest request = new ListUsersRequest();
      request.setUserPoolId(cognitoConfig.getUserPoolId());
      request.setFilter(String.format("email=\"%s\"", email));

      ListUsersResult response = cognitoClient.listUsers(request);

      return response.getUsers().stream()
          .map(user -> {
            ProviderUserIdentifierType providerUser = new ProviderUserIdentifierType();
            providerUser.setProviderName(getProviderNameFromAttributes(user.getAttributes()));
            providerUser.setProviderAttributeValue(user.getUsername());
            return providerUser;
          })
          .toList();
    } catch (Exception e) {
      log.error("Error fetching linked users for email {}: {}", email, e.getMessage(), e);
      throw new RuntimeException("Failed to fetch linked users for email: " + email, e);
    }
  }

  private String getProviderNameFromAttributes(List<AttributeType> attributes) {
    return attributes.stream()
        .filter(attribute -> "cognito:providerName".equals(attribute.name()))
        .map(AttributeType::value)
        .findFirst()
        .orElse("Cognito");
  }

  private ProviderUserIdentifierType findMasterRecord(
      List<ProviderUserIdentifierType> allLinkedUsers, String email) {
    return allLinkedUsers.stream()
        .filter(user -> isMasterRecord(user.providerAttributeValue()))
        .findFirst()
        .orElseThrow(() -> new RuntimeException("No master record found for email: " + email));
  }

  private boolean isMasterRecord(String subject) {
    try {
      AdminGetUserRequest request = AdminGetUserRequest.builder()
          .userPoolId(cognitoConfig.getUserPoolId())
          .username(subject)
          .build();

      AdminGetUserResponse response = cognitoClient.adminGetUser(request);

      return response.userAttributes().stream()
          .anyMatch(attribute ->
              "custom:isMasterRecord".equals(attribute.name()) &&
                  "true".equalsIgnoreCase(attribute.value())
          );
    } catch (Exception e) {
      log.error("Failed to check if user with subject {} is master record: {}", subject,
          e.getMessage(), e);
      throw new RuntimeException("Failed to check if user is master record", e);
    }
  }

  private boolean isUserLinked(String subject, long userId) {
    try {
      AdminGetUserRequest request = AdminGetUserRequest.builder()
          .userPoolId(cognitoConfig.getUserPoolId())
          .username(subject)
          .build();

      AdminGetUserResponse response = cognitoClient.adminGetUser(request);

      return response.userAttributes().stream()
          .anyMatch(attribute ->
              "custom:userId".equals(attribute.name()) &&
                  String.valueOf(userId).equals(attribute.value())
          );
    } catch (UserNotFoundException e) {
      log.warn("User with subject {} not found in Cognito", subject);
      return false;
    } catch (Exception e) {
      log.error("Error checking if user is linked: {}", e.getMessage(), e);
      throw new RuntimeException("Error checking user linkage", e);
    }
  }

  private void linkUsers(List<ProviderUserIdentifierType> allLinkedUsers,
      ProviderUserIdentifierType masterUser,
      String email, String provider, String subject) {
    try {
      if (allLinkedUsers.isEmpty()) {
        log.error("No linked users found for email: {}", email);
        throw new RuntimeException("Cannot link users: no existing records found");
      }

      AdminLinkProviderForUserRequest request = AdminLinkProviderForUserRequest.builder()
          .userPoolId(cognitoConfig.getUserPoolId())
          .destinationUser(masterUser)
          .sourceUser(
              ProviderUserIdentifierType.builder()
                  .providerName(provider)
                  .providerAttributeValue(subject)
                  .build())
          .build();

      cognitoClient.adminLinkProviderForUser(request);

      log.info("Successfully linked user with subject {} to provider {} and email {}",
          subject, provider, email);
    } catch (Exception e) {
      log.error("Error linking user with provider: {}", e.getMessage(), e);
      throw new RuntimeException("Error linking user with provider", e);
    }
  }
}