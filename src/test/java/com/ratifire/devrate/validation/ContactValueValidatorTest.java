package com.ratifire.devrate.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ratifire.devrate.dto.ContactDto;
import com.ratifire.devrate.enums.ContactType;
import com.ratifire.devrate.validation.validators.ContactValueValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ContactValueValidatorTest {

  @InjectMocks
  private ContactValueValidator contactValueValidator;

  @Mock
  private ConstraintValidatorContext context;

  static Stream<Arguments> validContactProvider() {
    return Stream.of(
        Arguments.of(ContactType.EMAIL, "test@example.com"),
        Arguments.of(ContactType.PHONE_NUMBER, "+12345678901"),
        Arguments.of(ContactType.TELEGRAM_LINK, "https://t.me/testuser"),
        Arguments.of(ContactType.LINKEDIN_LINK, "https://www.linkedin.com/in/testuser"),
        Arguments.of(ContactType.GITHUB_LINK, "https://github.com/testuser"),
        Arguments.of(ContactType.BEHANCE_LINK, "https://www.behance.net/testuser"),
        Arguments.of(ContactType.EMAIL, "")
    );
  }

  static Stream<Arguments> invalidContactProvider() {
    return Stream.of(
        Arguments.of(ContactType.EMAIL, "testexample.com"),
        Arguments.of(ContactType.EMAIL, "test@examplecom"),
        Arguments.of(ContactType.EMAIL, "test@example.c"),
        Arguments.of(ContactType.EMAIL, "@example.com"),
        Arguments.of(ContactType.EMAIL, "test@.com"),
        Arguments.of(ContactType.PHONE_NUMBER, "12345678901"),
        Arguments.of(ContactType.PHONE_NUMBER, "+1234"),
        Arguments.of(ContactType.PHONE_NUMBER, "+1234567890123456"),
        Arguments.of(ContactType.PHONE_NUMBER, "+12 345678901"),
        Arguments.of(ContactType.TELEGRAM_LINK, "https://invalid.telegram./testuser"),
        Arguments.of(ContactType.LINKEDIN_LINK, "https://invalid.linkedin/in/testuser"),
        Arguments.of(ContactType.GITHUB_LINK, "https://invalid.github/testuser"),
        Arguments.of(ContactType.BEHANCE_LINK, "https://invalid.behance/testuser")
    );
  }

  @ParameterizedTest
  @MethodSource("validContactProvider")
  void validContactsTest(ContactType type, String value) {
    ContactDto contactDto = new ContactDto(1L, type, value);
    assertTrue(contactValueValidator.isValid(contactDto, context));
  }

  @ParameterizedTest
  @MethodSource("invalidContactProvider")
  void invalidContactsTest(ContactType type, String value) {
    ContactDto contactDto = new ContactDto(1L, type, value);
    assertFalse(contactValueValidator.isValid(contactDto, context));
  }
}