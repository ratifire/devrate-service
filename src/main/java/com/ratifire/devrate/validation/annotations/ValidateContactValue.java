package com.ratifire.devrate.validation.annotations;

import com.ratifire.devrate.validation.validators.ContactValueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ContactValueValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateContactValue {

  String message() default "Invalid contact value";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}