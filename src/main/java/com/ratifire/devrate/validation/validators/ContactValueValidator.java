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

public class ContactValueValidator implements
    ConstraintValidator<ValidateContactValue, ContactDto> {

  private static final Pattern EMAIL_PATTERN = Pattern.compile(
      "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
  private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?\\d{10,15}$");
  private static final String TELEGRAM_BASE_URL = "https://t.me/";
  private static final String LINKEDIN_BASE_URL = "https://www.linkedin.com/in/";
  private static final String GITHUB_BASE_URL = "https://github.com/";
  private static final String BEHANCE_BASE_URL = "https://www.behance.net/";
  private static final Map<ContactType, Predicate<String>> VALIDATORS = new EnumMap<>(
      ContactType.class);

  static {
    VALIDATORS.put(ContactType.EMAIL, value -> EMAIL_PATTERN.matcher(value).matches());
    VALIDATORS.put(ContactType.PHONE_NUMBER, value -> PHONE_PATTERN.matcher(value).matches());
    VALIDATORS.put(ContactType.TELEGRAM_LINK, value -> value.startsWith(TELEGRAM_BASE_URL));
    VALIDATORS.put(ContactType.LINKEDIN_LINK,
        value -> value.startsWith(LINKEDIN_BASE_URL));
    VALIDATORS.put(ContactType.GITHUB_LINK, value -> value.startsWith(GITHUB_BASE_URL));
    VALIDATORS.put(ContactType.BEHANCE_LINK, value -> value.startsWith(BEHANCE_BASE_URL));
  }

  @Override
  public boolean isValid(ContactDto contactDto, ConstraintValidatorContext context) {
    ContactType type = contactDto.getType();
    String value = contactDto.getValue();

    if (type == null || value == null) {
      return false;
    }

    Predicate<String> validator = VALIDATORS.get(type);
    return validator != null && validator.test(value);
  }
}
