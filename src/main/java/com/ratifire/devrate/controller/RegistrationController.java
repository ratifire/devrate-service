package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.SignUpDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.exception.UserAlreadyExistException;
import com.ratifire.devrate.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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
   * @return ResponseEntity with HTTP status 200 (OK) and newly created user's details upon
   * successful registration. Throws UserAlreadyExistException with HTTP status 409 (Conflict) if
   * inputted email is already registered.
   */
  @PostMapping
  @ResponseStatus
  public ResponseEntity<User> registerUser(@RequestBody SignUpDto signUpDto) {
    if (registrationService.isUserExistByEmail(signUpDto.getEmail())) {
      throw new UserAlreadyExistException("User is already registered!");
    }

    User registeredUser = registrationService.registerUser(signUpDto);
    return ResponseEntity.ok().body(registeredUser);
  }
}
