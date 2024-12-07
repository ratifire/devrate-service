package com.ratifire.devrate.controller;

import com.ratifire.devrate.exception.FeedbackSubmissionLimitException;
import com.ratifire.devrate.exception.InterviewRequestDoesntExistException;
import com.ratifire.devrate.exception.MailException;
import com.ratifire.devrate.exception.ResourceAlreadyExistException;
import com.ratifire.devrate.exception.ResourceNotFoundException;
import com.ratifire.devrate.exception.SpecializationLinkedToInterviewException;
import com.ratifire.devrate.exception.UserSearchInvalidInputException;
import com.ratifire.devrate.security.exception.AuthTokenExpiredException;
import com.ratifire.devrate.security.exception.AuthenticationException;
import com.ratifire.devrate.security.exception.LogoutException;
import com.ratifire.devrate.security.exception.PasswordResetException;
import com.ratifire.devrate.security.exception.RefreshTokenException;
import com.ratifire.devrate.security.exception.RefreshTokenExpiredException;
import com.ratifire.devrate.security.exception.UserAlreadyExistsException;
import com.ratifire.devrate.security.exception.UserRegistrationException;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Global exception handler class for REST controllers.
 */
@RestControllerAdvice
public class HandlerException {

  private static final Logger log = LogManager.getLogger(HandlerException.class);
  private static final int EXPIRED_AUTH_TOKEN_HTTP_STATUS = 498;
  private static final int EXPIRED_REFRESH_TOKEN_HTTP_STATUS = 497;

  /**
   * Handles MethodArgumentNotValidException by returning a map of field errors with their
   * corresponding error messages.
   *
   * @param ex The MethodArgumentNotValidException to be handled.
   * @return A map containing field names as keys and corresponding error messages as values.
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public Map<String, String> handleArgumentErrors(MethodArgumentNotValidException ex) {
    log.error("Handling MethodArgumentNotValidException: {}", ex.getMessage(), ex);
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors()
        .forEach((error -> errors.put(error.getField(), error.getDefaultMessage())));
    return errors;
  }

  /**
   * Global exception handler method to handle exceptions of type {@link Exception}.
   *
   * @param ex The exception being handled.
   * @return A {@link ResponseEntity} with an appropriate error message and status code.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handleExceptionErrors(Exception ex) {
    log.error("Handling Exception: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("Oops! Something went wrong:( We're working to fix it! Please try again later:)");
  }

  /**
   * Handles InterviewRequestDoesntExistException by returning an HTTP status 204 (No content).
   */
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ExceptionHandler(InterviewRequestDoesntExistException.class)
  public void handleInterviewRequestDoesntExistException() {
  }

  /**
   * Handles AccessDeniedException by returning an HTTP status 403 (Forbidden).
   */
  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ExceptionHandler(AccessDeniedException.class)
  public void handleAccessDeniedException() {
  }

  /**
   * Handles NoResourceFoundException by returning an HTTP status 404.
   */
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(NoResourceFoundException.class)
  public void handleNoResourceFoundException() {
  }

  /**
   * Handles exceptions that extend ResourceNotFoundException by returning an HTTP status 400.
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(ResourceNotFoundException.class)
  public void handleNoFoundExceptions() {
  }

  /**
   * Handles the authorizing exceptions by returning an HTTP status 401.
   */
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler({AuthenticationException.class})
  public void handleAuthenticationExceptions() {
  }

  /**
   * Handles the logout exceptions by returning an HTTP status 401.
   */
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler({LogoutException.class})
  public void handleLogoutExceptions() {
  }

  /**
   * Handles the AuthTokenExpired exceptions by returning an HTTP status 498.
   */
  @ExceptionHandler(AuthTokenExpiredException.class)
  public void handleAuthTokenExpiredException(HttpServletResponse response) {
    response.setStatus(EXPIRED_AUTH_TOKEN_HTTP_STATUS);
  }

  /**
   * Handles the RefreshTokenExpired exceptions by returning an HTTP status 498.
   */
  @ExceptionHandler(RefreshTokenExpiredException.class)
  public void handleRefreshTokenExpiredException(HttpServletResponse response) {
    response.setStatus(EXPIRED_REFRESH_TOKEN_HTTP_STATUS);
  }

  /**
   * Handles exceptions related to user registration, password reset, and token refresh. This method
   * returns an HTTP 400 (Bad Request) status code for the specified exceptions.
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({
      UserRegistrationException.class,
      RefreshTokenException.class,
      PasswordResetException.class
  })
  public void handleBadRequestAuthenticationExceptions() {
  }

  /**
   * Handles the UserAlreadyExistsException by returning an HTTP status 409.
   */
  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler(UserAlreadyExistsException.class)
  public void handleUserAlreadyExistsExceptionExceptions() {
  }

  /**
   * Handles the disabled exception for not verified users by returning an HTTP status 403.
   */
  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ExceptionHandler({DisabledException.class})
  public void handleUserDisabledException() {
  }

  /**
   * Handles exceptions that extend ResourceAlreadyExistException by returning an HTTP status 409.
   */
  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler({ResourceAlreadyExistException.class,
      SpecializationLinkedToInterviewException.class})
  public void handleAlreadyExistExceptions() {
  }

  /**
   * Handles exceptions that extend SuperEmailException.
   *
   * @param exception The SuperEmailException instance to handle.
   * @return ResponseEntity with appropriate HTTP status based on the type of exception.
   */
  @ExceptionHandler(MailException.class)
  public ResponseEntity<?> handleMailExceptions(MailException exception) {
    return ResponseEntity.status(exception.getStatus()).build();
  }

  /**
   * Handles MethodArgumentTypeMismatchException by returning an HTTP status 400.
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public void handleMethodArgumentTypeMismatchException() {
  }

  /**
   * Handles HandlerMethodValidationException by returning an HTTP status 400.
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(HandlerMethodValidationException.class)
  public void handleHandlerMethodValidationException(HandlerMethodValidationException e) {
  }

  /**
   * Handles {@link UserSearchInvalidInputException} and returns a 400 Bad Request response.
   */
  @ExceptionHandler(UserSearchInvalidInputException.class)
  public ResponseEntity<String> handleUserSearchInvalidInputException(
      UserSearchInvalidInputException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  /**
   * Handles FeedbackFrequencyLimitReached by returning an HTTP status 429.
   */
  @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
  @ExceptionHandler(FeedbackSubmissionLimitException.class)
  public void handleFeedbackFrequencyLimitReached(FeedbackSubmissionLimitException e) {
  }
}