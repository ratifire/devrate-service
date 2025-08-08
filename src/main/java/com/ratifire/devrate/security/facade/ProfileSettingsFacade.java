package com.ratifire.devrate.security.facade;

import com.ratifire.devrate.security.model.dto.EmailChangeDto;
import com.ratifire.devrate.security.model.dto.PasswordChangeDto;
import com.ratifire.devrate.security.model.enums.AccountLanguage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Profile settings facade interface.
 */
public interface ProfileSettingsFacade {

  void changeEmail(HttpServletRequest request, HttpServletResponse response,
      EmailChangeDto emailChangeDto);

  void changePassword(HttpServletRequest request, PasswordChangeDto passwordChangeDto);

  void updateAccountLanguage(@RequestParam("lang") AccountLanguage language);

  void updateEmailSubscriptionFlag(@RequestParam("enabled") boolean isEnabled);

}