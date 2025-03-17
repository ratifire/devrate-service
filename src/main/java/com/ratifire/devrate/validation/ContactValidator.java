package com.ratifire.devrate.validation;

import com.ratifire.devrate.dto.ContactDto;
import com.ratifire.devrate.enums.ContactType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Validator for the {@link ContactDto} class, ensuring that the contact matches the expected
 * format based on the ContactType.
 */
public class ContactValidator implements
    ConstraintValidator<ValidContact, ContactDto> {

  private static final Pattern EMAIL_PATTERN = Pattern.compile(
      "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
  private static final Pattern TELEGRAM_PATTERN = Pattern.compile(
      "https://t\\.me/[a-zA-Z0-9_]+/?$");
  private static final Pattern LINKEDIN_PATTERN = Pattern.compile(
      "https://www\\.linkedin\\.com/in/[a-zA-Z0-9-]+/?$");
  private static final Pattern GITHUB_PATTERN = Pattern.compile(
      "https://github\\.com/[a-zA-Z0-9-]+/?$");
  private static final Pattern BEHANCE_PATTERN = Pattern.compile(
      "https://www\\.behance\\.net/[a-zA-Z0-9-]+/?$");
  private final Map<ContactType, Predicate<String>> validatorsByContactType;

  public ContactValidator() {
    this.validatorsByContactType = initializeValidators();
  }

  /**
   * Validates the given ContactDto based on its ContactType.
   *
   * @param contactDto the contact DTO to validate
   * @param context    the context in which the constraint is evaluated
   * @return True if the contact is valid or empty, false otherwise
   */
  @Override
  public boolean isValid(ContactDto contactDto, ConstraintValidatorContext context) {
    ContactType type = contactDto.getType();
    String value = contactDto.getValue();

    if (value.isEmpty()) {
      return true;
    }

    Predicate<String> validator = validatorsByContactType.get(type);
    return validator != null && validator.test(value);
  }

  private Map<ContactType, Predicate<String>> initializeValidators() {
    Map<ContactType, Predicate<String>> map = new EnumMap<>(ContactType.class);
    map.put(ContactType.EMAIL, value -> EMAIL_PATTERN.matcher(value).matches());
    map.put(ContactType.TELEGRAM_LINK, value -> TELEGRAM_PATTERN.matcher(value).matches());
    map.put(ContactType.LINKEDIN_LINK, value -> LINKEDIN_PATTERN.matcher(value).matches());
    map.put(ContactType.GITHUB_LINK, value -> GITHUB_PATTERN.matcher(value).matches());
    map.put(ContactType.BEHANCE_LINK, value -> BEHANCE_PATTERN.matcher(value).matches());
    return map;
  }
}