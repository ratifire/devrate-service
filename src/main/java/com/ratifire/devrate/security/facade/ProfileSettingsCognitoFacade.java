package com.ratifire.devrate.security.facade;

import com.ratifire.devrate.security.model.dto.EmailChangeDto;
import com.ratifire.devrate.security.model.dto.PasswordChangeDto;
import com.ratifire.devrate.security.model.enums.AccountLanguage;
import com.ratifire.devrate.security.service.account.ProfileSettingsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * AWS Cognito facade class.
 */
@RequiredArgsConstructor
@Component
@Profile("!local")
public class ProfileSettingsCognitoFacade implements ProfileSettingsFacade {

  private final ProfileSettingsService profileSettingsService;

  @Override
  public void changeEmail(HttpServletRequest request, HttpServletResponse response,
      EmailChangeDto emailChangeDto) {
    profileSettingsService.changeEmail(request, response, emailChangeDto);
  }

  @Override
  public void changePassword(HttpServletRequest request, PasswordChangeDto passwordChangeDto) {
    profileSettingsService.changePassword(request, passwordChangeDto);
  }

  @Override
  public void updateAccountLanguage(AccountLanguage language) {
    profileSettingsService.updateAccountLanguage(language);
  }

  @Override
  public void updateEmailSubscriptionFlag(boolean isEnabled) {
    profileSettingsService.updateEmailSubscriptionFlag(isEnabled);
  }
}