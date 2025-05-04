package com.ratifire.devrate.security.controller;

import com.ratifire.devrate.security.facade.ProfileSettingsFacade;
import com.ratifire.devrate.security.model.dto.EmailChangeDto;
import com.ratifire.devrate.security.model.dto.PasswordChangeDto;
import com.ratifire.devrate.security.model.enums.AccountLanguage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for handling profile settings endpoints.
 */
@RestController
@RequestMapping("/profile-settings")
@RequiredArgsConstructor
public class ProfileSettingsController {

  private final ProfileSettingsFacade profileSettingsFacade;

  @PatchMapping("/change-email")
  public void changeEmail(HttpServletRequest request, HttpServletResponse response,
      @Valid @RequestBody EmailChangeDto emailChangeDto) {
    profileSettingsFacade.changeEmail(request, response, emailChangeDto);
  }

  @PatchMapping("/change-password")
  public void changePassword(HttpServletRequest request,
      @Valid @RequestBody PasswordChangeDto passwordChangeDto) {
    profileSettingsFacade.changePassword(request, passwordChangeDto);
  }

  @PatchMapping("/account-language")
  public void updateAccountLanguage(@RequestParam("language") AccountLanguage language) {
    profileSettingsFacade.updateAccountLanguage(language);
  }

  @PatchMapping("/profile-deactivation")
  public void forceUserProfileDeactivation(HttpServletRequest request,
      HttpServletResponse response) {
    profileSettingsFacade.forceUserProfileDeactivation(request, response);
  }

  @PatchMapping("/email-subscription")
  public void updateEmailSubscriptionFlag(@RequestParam("enabled") boolean isEnabled) {
    profileSettingsFacade.updateEmailSubscriptionFlag(isEnabled);
  }

}