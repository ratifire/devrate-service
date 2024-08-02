package com.ratifire.devrate.validation.validators;

import com.ratifire.devrate.dto.ContactDto;
import com.ratifire.devrate.enums.ContactType;
import com.ratifire.devrate.validation.annotations.ValidateContactValue;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Validator for the {@link ContactDto} class, ensuring that the value of a contact matches the
 * expected format based on the ContactType.
 */
public class ContactValueValidator implements
    ConstraintValidator<ValidateContactValue, ContactDto> {

  private static final Pattern EMAIL_PATTERN = Pattern.compile(
      "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
  private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+\\d{10,15}$");
  private static final String TELEGRAM_BASE_URL = "https://t.me/";
  private static final String LINKEDIN_BASE_URL = "https://www.linkedin.com/in/";
  private static final String GITHUB_BASE_URL = "https://github.com/";
  private static final String BEHANCE_BASE_URL = "https://www.behance.net/";
  private final Map<ContactType, Predicate<String>> validatorsByContactType;

  public ContactValueValidator() {
    this.validatorsByContactType = initializeValidators();
  }

  /**
   * Validates the value of the given ContactDto based on its ContactType.
   *
   * @param contactDto the contact DTO to validate
   * @param context    the context in which the constraint is evaluated
   * @return True if the contact value is valid or empty, false otherwise
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
    map.put(ContactType.PHONE_NUMBER, value -> PHONE_PATTERN.matcher(value).matches());
    map.put(ContactType.TELEGRAM_LINK, value -> value.startsWith(TELEGRAM_BASE_URL));
    map.put(ContactType.LINKEDIN_LINK, value -> value.startsWith(LINKEDIN_BASE_URL));
    map.put(ContactType.GITHUB_LINK, value -> value.startsWith(GITHUB_BASE_URL));
    map.put(ContactType.BEHANCE_LINK, value -> value.startsWith(BEHANCE_BASE_URL));
    return map;
  }
}