package com.ratifire.devrate.service.resetPassword;

import com.ratifire.devrate.entity.EmailConfirmationCode;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.UserSecurity;
import com.ratifire.devrate.service.UserSecurityService;
import com.ratifire.devrate.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for password reset logic. This service handles password reset requests
 * and performs necessary operations such as sending reset links and updating user passwords.
 */
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserService userService;
    private final UserSecurityService userSecurityService;
    private final EmailConfirmationUUIDService emailConfirmationUUIDService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Sends a password reset link to the user's email address.
     *
     * @param email The email address associated with the user account.
     */
    @Transactional
    public boolean requestPasswordReset(String email) {
        User user = userService.findUserByEmail(email);

        String code = emailConfirmationUUIDService.generateAndPersistUUIDCode(user.getId());

        emailConfirmationUUIDService.sendPasswordResetEmail(email, code);
        return true;
    }

    /**
     * Resets the password of the user associated with the provided confirmation code.
     *
     * @param code        The confirmation code received in the password reset link.
     * @param newPassword The new password to be set for the user account.
     */
    @Transactional
    public boolean resetPassword(String code, String newPassword) {
        EmailConfirmationCode emailConfirmationCode = emailConfirmationUUIDService.findUUIDCode(code);
        User user = userService.getById(emailConfirmationCode.getUserId());
        UserSecurity userSecurity = userSecurityService.findByUserId(user.getId());

        String encodedPassword = passwordEncoder.encode(newPassword);

        UserSecurity updatedUserSecurity = userSecurity.toBuilder()
                .password(encodedPassword)
                .build();

        userSecurityService.save(updatedUserSecurity);

        emailConfirmationUUIDService.deleteConfirmedCodesByUserId(user.getId());
        emailConfirmationUUIDService.sendPasswordChangeConfirmation(user.getEmail());
        return true;
    }
}
