package com.ratifire.devrate.controller;


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

}
