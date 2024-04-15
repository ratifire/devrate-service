package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.UserRegistrationDto;
import com.ratifire.devrate.service.registration.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class responsible for handling registration-related requests. This controller provides
 * endpoints for user registration.
 */
@RestController
@RequestMapping("/auth/signup")
@RequiredArgsConstructor
public class RegistrationController {

  private final RegistrationService registrationService;

  /**
   * Endpoint for user registration. Accepts POST requests with user details and registers a new
   * user.
   *
   * @param userRegistrationDto DTO containing new user's details such as username, password, etc.
   */
  @PostMapping
  public void registerUser(@RequestBody @Valid UserRegistrationDto userRegistrationDto) {
    registrationService.registerUser(userRegistrationDto);
  }

  /**
   * Endpoint to confirm the email using the provided confirmation code.
   *
   * @param code Unique code used for confirming user registration.
   * @return ResponseEntity with the user ID and HTTP status CREATED (201) upon successful
   *         confirmation, or the corresponding error HTTP status in case of failure.
   */
  @PutMapping("/{code}")
  public ResponseEntity<Long> confirm(@PathVariable String code) {
    long userId = registrationService.confirmRegistration(code);
    return ResponseEntity.status(HttpStatus.CREATED).body(userId);
  }
}
