package com.ratifire.devrate.validation.validators;

import com.ratifire.devrate.enums.FeedbackType;
import com.ratifire.devrate.validation.annotations.ValidFeedbackType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator to check if the feedback type is valid.
 */
public class FeedbackTypeValidator implements
    ConstraintValidator<ValidFeedbackType, String> {

  /**
   * Validates if the provided feedback type is non-null, non-empty, and exists in FeedbackType.
   *
   * @param value   the feedback type to validate
   * @param context the context in which the constraint is evaluated
   * @return true if the feedback type is valid, otherwise false
   */
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.isEmpty()) {
      return false;
    }
    return FeedbackType.getAll().contains(value);
  }
}