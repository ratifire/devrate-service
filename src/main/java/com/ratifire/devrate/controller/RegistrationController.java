package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.SignUpDto;
import com.ratifire.devrate.service.registration.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class responsible for handling registration-related requests. This controller provides
 * endpoints for user registration.
 */
@RestController
@RequestMapping("/signup")
@RequiredArgsConstructor
public class RegistrationController {

  private final RegistrationService registrationService;

  /**
   * Endpoint for user registration. Accepts POST requests with user details and registers a new
   * user.
   *
   * @param signUpDto DTO containing new user's details such as username, password, etc.
   * @return The registered User entity if successful, or null if the registration fails.
   */
  @PostMapping
  public SignUpDto registerUser(@RequestBody @Valid SignUpDto signUpDto) {
    return registrationService.registerUser(signUpDto);
  }

  /**
   * Endpoint to confirm the email using the provided confirmation code.
   *
   * @param id   The unique identifier of the user for whom the email is being confirmed.
   * @param code The confirmation code to be checked against the stored code.
   * @return {@code true} if the email confirmation code is successfully confirmed;
   *         {@code false} otherwise.
   */
  @Operation(summary = "Email confirmation",
      description = "Confirming the user's email by matching the code", tags = {"Registration"})
  @GetMapping("/{id}/{code}")
  public boolean confirm(@PathVariable Long id, @PathVariable String code) {
    return registrationService.isCodeConfirmed(id, code);
  }
}
