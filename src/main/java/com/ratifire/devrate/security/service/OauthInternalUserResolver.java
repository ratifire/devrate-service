package com.ratifire.devrate.security.service;

import static com.ratifire.devrate.security.model.constants.CognitoConstant.NONE_VALUE;

import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.security.helper.CognitoAuthenticationHelper;
import com.ratifire.devrate.security.model.CognitoUserInfo;
import com.ratifire.devrate.security.model.enums.AccessLevel;
import com.ratifire.devrate.security.model.enums.AccountLanguage;
import com.ratifire.devrate.security.model.enums.RegistrationSourceType;
import com.ratifire.devrate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Service for resolving or creating internal user entities based on Cognito user information.
 */
@Service
@RequiredArgsConstructor
@Profile("!local")
public class OauthInternalUserResolver {

  private final UserService userService;
  private final CognitoUserSyncService cognitoUserSyncService;
  private final RegistrationService registrationService;
  private final CognitoAuthenticationHelper cognitoAuthenticationHelper;
  private final CognitoApiClientService cognitoApiClient;

  /**
   * Resolves an internal user by Cognito user info, creating a new user if none exists.
   *
   * @param userInfo Cognito user information used for resolution or creation
   * @return the resolved or newly created internal User entity
   */
  public User resolveOrCreateInternalUser(CognitoUserInfo userInfo) {
    User internalUser = userService.findByEmail(userInfo.email().toLowerCase());

    if (ObjectUtils.isNotEmpty(internalUser)) {
      cognitoUserSyncService.ensureInternalUserLinkedAndSyncedWithCognito(internalUser, userInfo);
      internalUser.setRegistrationSource(RegistrationSourceType.FEDERATED_IDENTITY);
      userService.updateByEntity(internalUser);
    } else {
      internalUser = createInternalUser(userInfo);
      cognitoApiClient.updateCognitoUserAttributes(
          userInfo.subject(),
          internalUser.getId(),
          AccessLevel.getDefaultRole(),
          true,
          NONE_VALUE);
    }

    return internalUser;
  }

  private User createInternalUser(CognitoUserInfo userInfo) {
    String email = userInfo.email().toLowerCase();
    UserDto userDto = UserDto.builder()
        .firstName(userInfo.firstName())
        .lastName(userInfo.lastName())
        .registrationSource(RegistrationSourceType.FEDERATED_IDENTITY)
        .accountLanguage(AccountLanguage.UKRAINE)
        .build();
    String password = cognitoAuthenticationHelper.generateRandomPassword();
    User newInternalUser = userService.create(userDto, email, password);
    registrationService.finalizeUserRegistration(newInternalUser, email);
    return newInternalUser;
  }

}