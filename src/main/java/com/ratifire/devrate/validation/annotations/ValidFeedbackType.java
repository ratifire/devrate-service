package com.ratifire.devrate.validation.annotations;

import com.ratifire.devrate.validation.validators.FeedbackTypeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation for validating feedback types.
 */
@Constraint(validatedBy = FeedbackTypeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFeedbackType {

  /**
   * The default validation message.
   *
   * @return the default message
   */
  String message() default "Invalid feedback type.";

  /**
   * Grouping constraints.
   *
   * @return the groups
   */
  Class<?>[] groups() default {};

  /**
   * Additional data to be carried with the annotation.
   *
   * @return the payload
   */
  Class<? extends Payload>[] payload() default {};
}