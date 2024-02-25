package com.ratifire.devrate.controller;


import com.ratifire.devrate.service.resetPassword.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;



/**
 * Controller class responsible for handling password reset-related requests.
 * This controller provides endpoints for requesting a password reset link and resetting the password with a token.
 */
@RestController
@RequestMapping("/api/auth/password-reset")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;


    /**
     * Endpoint for requesting a password reset link.
     *
     * @param email The email address associated with the user account for which the password is to be reset.
     * @return ResponseEntity indicating the status of the password reset request.
     */
    @PostMapping("/request")
    public boolean requestPasswordReset(@RequestParam String email) {
        return passwordResetService.requestPasswordReset(email);
    }

    /**
     * Endpoint for resetting the password using a token.
     *
     * @param code      The token received in the password reset link.
     * @param newPassword The new password to be set for the user account.
     * @return ResponseEntity indicating the status of the password reset operation.
     */
    @PostMapping("/{code}")
    public boolean resetPassword(@PathVariable String code, @RequestBody String newPassword) {
        return passwordResetService.resetPassword(code, newPassword);
    }
}
