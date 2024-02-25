package com.ratifire.devrate.service.resetPassword;

import com.ratifire.devrate.entity.EmailConfirmationCode;
import com.ratifire.devrate.exception.InvalidCodeException;
import com.ratifire.devrate.repository.EmailConfirmationCodeRepository;
import com.ratifire.devrate.service.email.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Service class responsible for generating and managing unique UUID confirmation codes for email confirmation.
 */
@Service
@AllArgsConstructor
public class EmailConfirmationUUIDService  {

    /**
     * Repository for accessing email confirmation code data in the database.
     */
    private final EmailConfirmationCodeRepository emailConfirmationCodeRepository;
    private final EmailService emailService;

    /**
     * Generates a unique UUID.
     *
     * @return A unique UUID string.
     */
    public String createUniqueUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * Saves the UUID confirmation code for a specific user in the database.
     *
     * @param userId The ID of the user associated with the confirmation code.
     * @param code   The confirmation code to be saved.
     */
    public void saveUUIDConfirmationCode(Long userId, String code) {
        LocalDateTime createdAt = LocalDateTime.now().plusHours(12); // Adding 12 hours to the current time
        EmailConfirmationCode emailConfirmationCode = EmailConfirmationCode.builder()
                .code(code)
                .createdAt(createdAt)
                .userId(userId)
                .build();
        emailConfirmationCodeRepository.save(emailConfirmationCode);
    }

    /**
     * Generates a unique UUID confirmation code for a specific user, persists it in the database, and returns the code.
     *
     * @param userId The ID of the user for whom the confirmation code is generated.
     * @return The generated confirmation code.
     */
    public String generateAndPersistUUIDCode(Long userId) {
        deleteConfirmedCodesByUserId(userId);
        String code = createUniqueUUID();
        saveUUIDConfirmationCode(userId, code);
        return code;
    }

    /**
     * Deletes the confirmed {@link EmailConfirmationCode} entity with the specified ID.
     * <p>
     //     * @param confirmedCodeId The ID of the confirmed email confirmation code to be deleted.
     */
    public void deleteConfirmedCodesByUserId(Long userId) {
        emailConfirmationCodeRepository.deleteByUserId(userId);
    }

    // TODO: Limit password reset attempts to avoid brute-force attacks
    public void verifyCode(String code) throws InvalidCodeException {
        // TODO: Check the code and increase the failed attempt counter if the code is incorrect
        // If the number of failed attempts exceeds the specified threshold, throw an exception
    }

    /**
     * Sends an email with a password reset link to the user.
     *
     * @param email The user's email address.
     * @param code The unique password reset code.
     */
    public boolean sendPasswordResetEmail(String email, String code) {
        String resetLink = "https://devrate.com/reset-password/" + code;
        SimpleMailMessage resetEmail = new SimpleMailMessage();
        resetEmail.setTo(email);
        resetEmail.setSubject("Password Reset");
        resetEmail.setText("To reset your password, please click the link below:\n" + resetLink);
        emailService.sendEmail(resetEmail, false);
        return true;
    }

    /**
     * Sends an email confirmation about the password change.
     *
     * @param email The user's email address.
     */
    public boolean sendPasswordChangeConfirmation(String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Successfully Reset");
        LocalDateTime now = LocalDateTime.now();
        String formattedDateTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String text = "Your password has been successfully changed on " + formattedDateTime + ".";
        message.setText(text);
        emailService.sendEmail(message, false);
        return true;
    }

    /**
     * Finds and retrieves the UUID confirmation code from the database based on the provided code.
     *
     * @param code The confirmation code to search for.
     * @return The EmailConfirmationCode entity associated with the provided code.
     * @throws InvalidCodeException If the code is invalid or expired.
     */
    public EmailConfirmationCode findUUIDCode(String code) {
        return emailConfirmationCodeRepository.findByCode(code)
                .orElseThrow(() -> new InvalidCodeException("Invalid or expired password reset code."));
    }
}