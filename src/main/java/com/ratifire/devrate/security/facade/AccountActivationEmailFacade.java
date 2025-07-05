package com.ratifire.devrate.security.facade;

import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.security.service.EmailConfirmationCodeService;
import com.ratifire.devrate.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Facade component for managing the process of sending and resending account activation codes via
 * email.
 */
@Component
@RequiredArgsConstructor
public class AccountActivationEmailFacade {

  private final EmailConfirmationCodeService confirmationCodeService;
  private final EmailService emailService;

  public void sendNewActivationCode(User user) {
    String code = confirmationCodeService.createConfirmationCode(user.getId());
    emailService.sendAccountActivationCodeEmail(user.getEmail(), code);
  }

  public void resendActivationCode(User user) {
    confirmationCodeService.deleteConfirmedCodeByUserId(user.getId());
    sendNewActivationCode(user);
  }

}
