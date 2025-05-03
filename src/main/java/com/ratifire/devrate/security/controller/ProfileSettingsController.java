package com.ratifire.devrate.security.controller;

import com.ratifire.devrate.security.model.dto.EmailChangeDto;
import com.ratifire.devrate.security.model.dto.PasswordChangeDto;
import com.ratifire.devrate.security.model.enums.AccountLanguage;
import com.ratifire.devrate.security.service.ProfileSettingsService;
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

  private final ProfileSettingsService profileSettingsService;

  @PatchMapping("/change-email")
  public void changeEmail(HttpServletRequest request, HttpServletResponse response,
      @Valid @RequestBody EmailChangeDto emailChangeDto) {
    profileSettingsService.changeEmail(request, response, emailChangeDto);
  }

  @PatchMapping("/change-password")
  public void changePassword(HttpServletRequest request,
      @Valid @RequestBody PasswordChangeDto passwordChangeDto) {
    profileSettingsService.changePassword(request, passwordChangeDto);
  }

  @PatchMapping("/account-language")
  public void updateAccountLanguage(@RequestParam("language") AccountLanguage language) {
    profileSettingsService.updateAccountLanguage(language);
  }

  @PatchMapping("/profile-deactivation")
  public void forceUserProfileDeactivation(HttpServletRequest request,
      HttpServletResponse response) {
    profileSettingsService.forceUserProfileDeactivation(request, response);
  }

  @PatchMapping("/email-subscription")
  public void updateEmailSubscriptionFlag(@RequestParam("enabled") boolean isEnabled) {
    profileSettingsService.updateEmailSubscriptionFlag(isEnabled);
  }

}