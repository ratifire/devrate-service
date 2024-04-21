package com.ratifire.devrate.controller;

import com.ratifire.devrate.exception.EmploymentRecordNotFoundException;
import com.ratifire.devrate.exception.InvalidCodeException;
import com.ratifire.devrate.exception.MailConfirmationCodeException;
import com.ratifire.devrate.exception.MailConfirmationCodeExpiredException;
import com.ratifire.devrate.exception.MailConfirmationCodeRequestException;
import com.ratifire.devrate.exception.MailException;
import com.ratifire.devrate.exception.ResourceAlreadyExistException;
import com.ratifire.devrate.exception.ResourceNotFoundException;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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
   * Handles exceptions that extend ResourceAlreadyExistException by returning an HTTP status 409.
   */
  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler(ResourceAlreadyExistException.class)
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
    return switch (exception) {
      case MailConfirmationCodeRequestException ignored ->
          ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
      case InvalidCodeException ignored ->
          ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
      case MailConfirmationCodeException ignored ->
          ResponseEntity.status(HttpStatus.NOT_FOUND).build();
      case MailConfirmationCodeExpiredException ignored ->
          ResponseEntity.status(HttpStatus.GONE).build();
      default -> handleExceptionErrors(exception);
    };
  }

  /**
   * Exception handler method for handling EmploymentRecordNotFoundException. This method is
   * responsible for handling exceptions related to work experience information not being found.
   *
   * @param ex The EmploymentRecordNotFoundException that has been thrown.
   * @return A ResponseEntity with HTTP status NOT_FOUND (404).
   */
  @ExceptionHandler(EmploymentRecordNotFoundException.class)
  public ResponseEntity<Void> handleEmploymentRecordNotFoundException(
      EmploymentRecordNotFoundException ex) {
    log.error("The user's employment record information could not be found with"
        + "the: {}", ex.getMessage());
    return ResponseEntity.notFound().build();
  }
}
