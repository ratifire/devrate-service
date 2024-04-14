package com.ratifire.devrate.controller;

import com.ratifire.devrate.exception.EmailConfirmationCodeException;
import com.ratifire.devrate.exception.EmailConfirmationCodeExpiredException;
import com.ratifire.devrate.exception.EmailConfirmationCodeRequestException;
import com.ratifire.devrate.exception.InvalidCodeException;
import com.ratifire.devrate.exception.MissingFileDataException;
import com.ratifire.devrate.exception.UnsupportedFileTypeException;
import com.ratifire.devrate.exception.UserAvatarAlreadyExistException;
import com.ratifire.devrate.exception.UserAvatarNotFoundException;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler class for REST controllers.
 */
@RestControllerAdvice
public class HandlerException {

  private static final Logger log = LogManager.getLogger(HandlerException.class);

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
   * Controller advice method to handle exceptions of type Exception. Responds with an HTTP status
   * of INTERNAL_SERVER_ERROR and returns the error message from the exception.
   *
   * @param ex The exception to be handled.
   * @return A String containing the error message from the exception.
   */
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  public String handleExceptionErrors(Exception ex) {
    log.error("Handling Exception: {}", ex.getMessage(), ex);
    return "Oops! Something went wrong:( We're working to fix it! Please try again later:)";
  }

  /**
   * Exception handler method for handling MailSendException.
   *
   * <p>Handles instances of MailSendException by responding with an HTTP status
   * of BAD_REQUEST and returning the error message from the exception. Logs the
   * exception details for debugging purposes.</p>
   *
   * @param ex The MailSendException to be handled.
   * @return A ResponseEntity containing the error message from the exception
   *         with an HTTP status of BAD_REQUEST.
   */
  @ExceptionHandler(MailSendException.class)
  public ResponseEntity<String> handleMailSendException(MailSendException ex) {
    log.error("Handling MailSendException: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  /**
   * Exception handler method for handling InvalidCodeException.
   *
   * <p>Handles instances of InvalidCodeException by responding with an HTTP status
   * of BAD_REQUEST and returning the error message from the exception. Logs the
   * exception details for debugging purposes.</p>
   *
   * @param ex The InvalidCodeException to be handled.
   * @return A ResponseEntity containing the error message from the exception
   *         with an HTTP status of BAD_REQUEST.
   */
  @ExceptionHandler(InvalidCodeException.class)
  public ResponseEntity<?> handleInvalidCodeException(InvalidCodeException ex) {
    log.error("Invalid code: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body("Invalid or expired password reset code.");
  }

  /**
   * Exception handler method for handling EmailConfirmationCodeException.
   * This method is responsible for handling exceptions related to invalid email confirmation codes.
   *
   * @param ex The EmailConfirmationCodeException that has been thrown.
   * @return ResponseEntity with an error message and HTTP status NOT_FOUND (404).
   */
  @ExceptionHandler(EmailConfirmationCodeException.class)
  public ResponseEntity<String> handleConfirmationCodeException(EmailConfirmationCodeException ex) {
    log.error("Invalid email confirmation code: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  /**
   * Exception handler method for handling {@code EmailConfirmationCodeRequestException}.
   * This method handles exceptions related to invalid email confirmation code requests.
   *
   * @param ex The {@code EmailConfirmationCodeRequestException} that has been thrown.
   * @return ResponseEntity with an error message and HTTP status BAD_REQUEST (400).
   */
  @ExceptionHandler(EmailConfirmationCodeRequestException.class)
  public ResponseEntity<String> handleEmailConfirmationCodeRequestException(
      EmailConfirmationCodeRequestException ex) {
    log.error("Invalid request: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  /**
   * Handles the {@link EmailConfirmationCodeExpiredException} thrown when attempting to confirm
   * registration with an expired email confirmation code.
   *
   * @param ex The {@link EmailConfirmationCodeExpiredException} thrown.
   * @return A {@link ResponseEntity} with an appropriate HTTP status code and error message.
   */
  @ExceptionHandler(EmailConfirmationCodeExpiredException.class)
  public ResponseEntity<String> handleEmailConfirmationCodeExpiredException(
      EmailConfirmationCodeExpiredException ex) {
    log.error("Email confirmation code: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.GONE).body(ex.getMessage());
  }

  /**
   * Handles exceptions of type {@link UserAvatarAlreadyExistException}. This handler logs the
   * exception and responds with an HTTP 409 Conflict status, indicating that the user already has
   * an avatar, which prevents the creation of another one.
   *
   * @param ex the caught {@link UserAvatarAlreadyExistException} instance.
   * @return a {@link ResponseEntity} containing the error message and an HTTP 409 Conflict status.
   */
  @ExceptionHandler(UserAvatarAlreadyExistException.class)
  public ResponseEntity<String> handleUserAvatarAlreadyExists(UserAvatarAlreadyExistException ex) {
    log.error("Handling UserAvatarAlreadyExistException: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }

  /**
   * Handles exceptions of type {@link UserAvatarNotFoundException}. This handler logs the exception
   * and responds with an HTTP 404 Not Found status, indicating that the requested operation failed
   * because the user's avatar could not be found.
   *
   * @param ex the caught {@link UserAvatarNotFoundException} instance.
   * @return a {@link ResponseEntity} containing the error message and an HTTP 404 Not Found status.
   */
  @ExceptionHandler(UserAvatarNotFoundException.class)
  public ResponseEntity<String> handleUserAvatarNotFound(UserAvatarNotFoundException ex) {
    log.error("Handling UserAvatarNotFoundException: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  /**
   * Handles exceptions of type {@link MissingFileDataException}. This handler logs the exception
   * and responds with an HTTP 400 Bad Request status, indicating that the operation failed due to
   * missing or unreadable file data.
   *
   * @param ex the caught {@link MissingFileDataException} instance.
   * @return a {@link ResponseEntity} containing the error message and an HTTP 400 Bad Request
   *     status.
   */
  @ExceptionHandler(MissingFileDataException.class)
  public ResponseEntity<String> handleMissingFileData(MissingFileDataException ex) {
    log.error("Handling MissingFileDataException: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  /**
   * Handles exceptions of type {@link UnsupportedFileTypeException}. This handler logs the
   * exception and responds with an HTTP 415 Unsupported Media Type status, indicating that the
   * operation failed because the file type of the uploaded file is not supported.
   *
   * @param ex the caught {@link UnsupportedFileTypeException} instance.
   * @return a {@link ResponseEntity} containing the error message and an HTTP 415 Unsupported Media
   *     Type status.
   */
  @ExceptionHandler(UnsupportedFileTypeException.class)
  public ResponseEntity<String> handleUnsupportedFileType(UnsupportedFileTypeException ex) {
    log.error("Handling UnsupportedFileTypeException: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(ex.getMessage());
  }
}
