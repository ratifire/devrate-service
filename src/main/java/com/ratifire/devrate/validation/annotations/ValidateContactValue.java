package com.ratifire.devrate.validation.annotations;

import com.ratifire.devrate.validation.validators.ContactValueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation for validating contact values.
 */
@Constraint(validatedBy = ContactValueValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateContactValue {

  /**
   * The default validation message.
   *
   * @return the default message
   */
  String message() default "";

  /**
   * Allows the specification of validation groups, to which this constraint belongs.
   *
   * @return the array of groups
   */
  Class<?>[] groups() default {};

  /**
   * Can be used by clients of the Bean Validation API to assign custom payload objects to a
   * constraint.
   *
   * @return the array of payload classes
   */
  Class<? extends Payload>[] payload() default {};
}