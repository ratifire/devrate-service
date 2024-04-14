package com.ratifire.devrate.controller;

import com.ratifire.devrate.exception.ContactNotFoundException;
import com.ratifire.devrate.exception.EducationNotFoundException;
import com.ratifire.devrate.exception.EmailConfirmationCodeException;
import com.ratifire.devrate.exception.EmailConfirmationCodeExpiredException;
import com.ratifire.devrate.exception.EmailConfirmationCodeRequestException;
import com.ratifire.devrate.exception.InvalidCodeException;
import com.ratifire.devrate.exception.LoginNotFoundException;
import com.ratifire.devrate.exception.RoleNotFoundException;
import com.ratifire.devrate.exception.UserNotFoundException;
import com.ratifire.devrate.exception.UserSecurityNotFoundException;
import com.ratifire.devrate.exception.WebSocketSessionNotFoundException;
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
   * Handles custom NoFoundExceptions by returning an HTTP status 400.
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({
      ContactNotFoundException.class,
      EducationNotFoundException.class,
      LoginNotFoundException.class,
      RoleNotFoundException.class,
      UserNotFoundException.class,
      UserSecurityNotFoundException.class,
      WebSocketSessionNotFoundException.class
  })
  public void handleNoFoundExceptions() {
  }

  /**
   * Global exception handler method to handle specific mail-related exceptions.
   *
   * @param ex The runtime exception being handled.
   * @return A {@link ResponseEntity} with an appropriate status code type of exception.
   */
  @ExceptionHandler({
      MailSendException.class,
      InvalidCodeException.class,
      EmailConfirmationCodeException.class,
      EmailConfirmationCodeRequestException.class,
      EmailConfirmationCodeExpiredException.class
  })
  public ResponseEntity<?> handleMailExceptions(RuntimeException ex) {
    switch (ex.getClass().getSimpleName()) {
      case "MailSendException", "EmailConfirmationCodeRequestException", "InvalidCodeException" -> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
      }
      case "EmailConfirmationCodeException" -> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
      }
      case "EmailConfirmationCodeExpiredException" -> {
        return ResponseEntity.status(HttpStatus.GONE).build();
      }
      default -> handleExceptionErrors(ex);
    }
    return handleExceptionErrors(ex);
  }

}
